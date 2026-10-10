package mipkasnow.dev.rococo.junit;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.AnnotationSupport;
import org.junit.platform.commons.support.HierarchyTraversalMode;
import org.junit.platform.commons.support.ModifierSupport;
import org.junit.platform.commons.support.ReflectionSupport;
import org.mockito.ArgumentMatchers;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.withSettings;

public class MockLoggerExtension implements BeforeAllCallback, BeforeEachCallback {

    private static final String TEST_SUFFIX = "Test";
    private static final Map<String, Logger> LOGGERS = new ConcurrentHashMap<>();
    private static final Map<Class<?>, List<Logger>> LOGGERS_BY_TEST_CLASS = new ConcurrentHashMap<>();
    private static MockedStatic<LoggerFactory> loggerFactory;

    @Override
    public void beforeAll(ExtensionContext context) {
        Class<?> testClass = context.getRequiredTestClass();
        List<Field> fields = AnnotationSupport.findAnnotatedFields(testClass, MockLogger.class);
        if (fields.isEmpty()) {
            return;
        }

        fields.forEach(MockLoggerExtension::validate);
        Class<?> target = serviceClass(testClass);

        synchronized (MockLoggerExtension.class) {
            Logger mock = LOGGERS.computeIfAbsent(target.getName(), name -> mock(Logger.class));
            installFactory();
            bindProductionLogger(target, mock);
            fields.forEach(field -> assign(field, mock));
            context.getRoot().getStore(ExtensionContext.Namespace.GLOBAL)
                    .getOrComputeIfAbsent(
                            MockLoggerExtension.class,
                            ignored -> (ExtensionContext.Store.CloseableResource) MockLoggerExtension::closeFactory
                    );
            LOGGERS_BY_TEST_CLASS.put(testClass, List.of(mock));
        }
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        List<Logger> mocks = LOGGERS_BY_TEST_CLASS.get(context.getRequiredTestClass());
        if (mocks == null) {
            return;
        }
        synchronized (MockLoggerExtension.class) {
            mocks.forEach(Mockito::reset);
        }
    }

    private static Class<?> serviceClass(Class<?> testClass) {
        String simpleName = testClass.getSimpleName();
        if (!simpleName.endsWith(TEST_SUFFIX) || simpleName.equals(TEST_SUFFIX)) {
            throw new IllegalStateException(testClass.getName() + " должен называться SomeServiceTest");
        }
        String serviceName = testClass.getPackageName() + "."
                + simpleName.substring(0, simpleName.length() - TEST_SUFFIX.length());
        return ReflectionSupport.tryToLoadClass(serviceName, testClass.getClassLoader())
                .getOrThrow(exception -> new IllegalStateException("Не найден класс " + serviceName, exception));
    }

    private static void installFactory() {
        if (loggerFactory != null) {
            return;
        }
        loggerFactory = mockStatic(LoggerFactory.class, withSettings().defaultAnswer(CALLS_REAL_METHODS));
        loggerFactory.when(() -> LoggerFactory.getLogger(ArgumentMatchers.<Class<?>>any()))
                .thenAnswer(MockLoggerExtension::loggerByClass);
        loggerFactory.when(() -> LoggerFactory.getLogger(anyString())).thenAnswer(MockLoggerExtension::loggerByName);
    }

    private static Logger loggerByClass(InvocationOnMock invocation) throws Throwable {
        Class<?> type = invocation.getArgument(0);
        Logger mocked = LOGGERS.get(type.getName());
        if (mocked != null) {
            return mocked;
        }
        return (Logger) invocation.callRealMethod();
    }

    private static Logger loggerByName(InvocationOnMock invocation) throws Throwable {
        Logger mocked = LOGGERS.get(invocation.getArgument(0, String.class));
        if (mocked != null) {
            return mocked;
        }
        return (Logger) invocation.callRealMethod();
    }

    private static void bindProductionLogger(Class<?> target, Logger mock) {
        Field logField = productionLoggerField(target);
        Logger actual = (Logger) ReflectionSupport.tryToReadFieldValue(logField, null)
                .getOrThrow(exception -> new IllegalStateException(
                        "Не удалось прочитать логгер " + target.getName(),
                        exception
                ));
        if (actual != mock) {
            throw new IllegalStateException(target.getName() + " уже инициализирован, логгер подменить нельзя");
        }
    }

    private static Field productionLoggerField(Class<?> target) {
        List<Field> fields = ReflectionSupport.findFields(
                target,
                field -> "log".equals(field.getName()) && field.getDeclaringClass().equals(target),
                HierarchyTraversalMode.BOTTOM_UP
        );
        if (fields.isEmpty()) {
            throw new IllegalStateException("В " + target.getName() + " нет static поля log");
        }
        Field field = fields.getFirst();
        if (ModifierSupport.isNotStatic(field) || !Logger.class.equals(field.getType())) {
            throw new IllegalStateException("Поле log в " + target.getName() + " должно быть static Logger");
        }
        return field;
    }

    private static void assign(Field field, Logger mock) {
        try {
            ReflectionSupport.makeAccessible(field).set(null, mock);
        } catch (IllegalAccessException exception) {
            throw new IllegalStateException("Не удалось записать мок логгера в " + field.getName(), exception);
        }
    }

    private static void validate(Field field) {
        if (ModifierSupport.isNotStatic(field)) {
            throw new IllegalStateException("@MockLogger ставится только на static поле " + field.getName());
        }
        if (ModifierSupport.isFinal(field)) {
            throw new IllegalStateException("@MockLogger не пишет в static final. Уберите final у " + field.getName());
        }
        if (!Logger.class.equals(field.getType())) {
            throw new IllegalStateException("@MockLogger требует поле типа org.slf4j.Logger: " + field.getName());
        }
    }

    private static void closeFactory() {
        synchronized (MockLoggerExtension.class) {
            if (loggerFactory != null) {
                loggerFactory.close();
                loggerFactory = null;
            }
            LOGGERS.clear();
            LOGGERS_BY_TEST_CLASS.clear();
        }
    }
}
