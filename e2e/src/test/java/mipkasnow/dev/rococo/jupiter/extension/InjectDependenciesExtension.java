package mipkasnow.dev.rococo.jupiter.extension;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ParametersAreNonnullByDefault
public class InjectDependenciesExtension implements BeforeAllCallback, TestInstancePostProcessor {

    private static final String CLIENT_PACKAGE = "mipkasnow.dev.rococo.api.client";

    private static final Map<Class<?>, Object> instances = new ConcurrentHashMap<>();

    @Override
    public void beforeAll(ExtensionContext context) {
        inject(context.getRequiredTestClass(), null);
    }

    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext context) {
        inject(testInstance.getClass(), testInstance);
    }

    private static void inject(Class<?> testClass, @Nullable Object testInstance) {
        boolean staticPhase = testInstance == null;
        for (Field field : fields(testClass)) {
            if (Modifier.isStatic(field.getModifiers()) != staticPhase || !injectable(field.getType())) {
                continue;
            }
            if (Modifier.isFinal(field.getModifiers())) {
                throw new IllegalStateException(
                        "Field '%s' in '%s' cannot be final".formatted(field.getName(), testClass.getName())
                );
            }
            setIfNull(field, testInstance, instance(field.getType()));
        }
    }

    private static boolean injectable(Class<?> type) {
        if (!CLIENT_PACKAGE.equals(type.getPackageName()) || !Modifier.isPublic(type.getModifiers()) || type.isMemberClass()) {
            return false;
        }
        try {
            return Modifier.isPublic(type.getDeclaredConstructor().getModifiers());
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    private static Object instance(Class<?> type) {
        return instances.computeIfAbsent(type, candidate -> {
            try {
                return candidate.getDeclaredConstructor().newInstance();
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException("Failed to create " + candidate.getName(), e);
            }
        });
    }

    private static void setIfNull(Field field, @Nullable Object target, Object dependency) {
        try {
            field.setAccessible(true);
            if (field.get(target) != null) {
                return;
            }
            field.set(target, dependency);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Failed to inject " + field, e);
        }
    }

    private static List<Field> fields(Class<?> testClass) {
        List<Field> fields = new ArrayList<>();
        Class<?> current = testClass;
        while (current != null && current != Object.class) {
            for (Field field : current.getDeclaredFields()) {
                if (!field.isSynthetic()) {
                    fields.add(field);
                }
            }
            current = current.getSuperclass();
        }
        return fields;
    }
}
