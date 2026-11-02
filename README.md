# Rococo

Учебный fullstack-проект галереи: художники, музеи, картины и профиль пользователя.  
Стек: **Spring Boot** (auth + gateway), **SvelteKit** (UI), **PostgreSQL / Kafka / MinIO**, e2e на **JUnit 5 + Selenide + Retrofit + Allure**.

| Сервис | Порт | Назначение |
| --- | --- | --- |
| `rococo-auth` | 9000 | OAuth2 / login / register |
| `rococo-gateway` | 8080 | REST API, JWT, Kafka, S3 |
| `rococo-client` | 3000 | Frontend |
| Postgres | 5433 | `rococo_auth`, `rococo_gateway` |
| Kafka | 9092 | UI на 8085 |
| MinIO | 9100 / 9101 | buckets: painting, museum, artist, avatar |

<p align="center">
  <img src="e2e/src/test/resources/screenshots/artist/aivazovsky-list.png" alt="Список художников" width="48%" />
  <img src="e2e/src/test/resources/screenshots/artist/aivazovsky-card.png" alt="Карточка художника" width="48%" />
</p>

## Быстрый старт

Нужны: **JDK 21**, **Docker**, **Node.js 18+**, браузер для UI-тестов.

```bash
git clone git@github.com:mipkasnow/rococo.git
cd rococo

./localenv.sh   # только контейнер rococo-postgres
./kafka.sh      # compose-проект rococo-kafka
./minio.sh      # compose-проект rococo-minio
./ports.sh      # проверка портов, ничего не убивает

./gradlew :rococo-auth:bootRun
./gradlew :rococo-gateway:bootRun

cd rococo-client && npm install && npm run dev
```

Открыть UI: http://localhost:3000

Скрипты инфраструктуры трогают **только** именованные ресурсы Rococo (`rococo-postgres`, `rococo-kafka`, `rococo-minio`). Чужие контейнеры не останавливаются и не удаляются. `ports.sh` лишь сообщает, занят ли порт.

## Тесты

```bash
# unit / integration gateway
./gradlew :rococo-gateway:unitTests
./gradlew :rococo-gateway:integrationTests

# e2e (нужен поднятый стенд)
./gradlew :e2e:test
```

Allure-результаты: `e2e/build/allure-results`.  
Отчёт по API-таймингам после e2e: `e2e/build/reports/api-performance/index.html`.

## Jupiter extensions

Мета-аннотации собирают набор extensions для web/rest:

```java
@ExtendWith({
        TestMethodContextExtension.class,
        InjectDependenciesExtension.class,
        BrowserExtension.class,
        ApiLoginExtension.class,
        CookieExtension.class,
        AllureJunit5.class
})
public @interface WebTest {
}
```

`@ApiLogin` регистрирует пользователя через auth API, кладёт JWT в store и (для web) пишет `id_token` в localStorage:

```java
@Test
@ApiLogin
@Museums(count = 3)
@DisplayName("Список музеев совпадает с API, поиск находит Русский музей")
void museumsShouldMatchApiAndSearch() { /* ... */ }
```

Создание сущностей по аннотации — без boilerplate в тесте:

```java
@Override
public void beforeEach(ExtensionContext context) {
    AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), Artist.class)
            .ifPresent(artist -> {
                ArtistResponse created = artistApiClient.create(new ArtistCreateRequest(
                        value(artist.name(), 12),
                        value(artist.biography(), 20),
                        DataUrl.fromClasspath(PHOTO)
                ));
                context.getStore(NAMESPACE).put(context.getUniqueId(), created);
            });
}
```

Suite-хуки через root `ExtensionContext` + `CloseableResource` — один `beforeSuite` / `afterSuite` на весь прогон (очистка данных, HTML-отчёт по RPS):

```java
rootContext.getStore(ExtensionContext.Namespace.GLOBAL)
        .getOrComputeIfAbsent(
                this.getClass(),
                key -> {
                    beforeSuite(rootContext);
                    return (ExtensionContext.Store.CloseableResource) this::afterSuite;
                }
        );
```

OkHttp interceptor пишет каждый Retrofit-вызов в коллектор; после suite собирается интерактивный отчёт (RPS, latency, p95, группировка по ручкам).

## Пример отчёта RPS / latency

Скриншот реального прогона:

![RPS report](e2e/src/test/resources/samples/rps-stat-report.png)

Интерактивный HTML (открыть локально в браузере):  
[e2e/src/test/resources/samples/rps-stat-report.html](e2e/src/test/resources/samples/rps-stat-report.html)

## Модули

- `rococo-auth` — Authorization Server
- `rococo-gateway` — API, JPA/Flyway, Kafka, MinIO
- `rococo-client` — SvelteKit UI
- `e2e` — UI/API автотесты, JDBC-репозитории, Allure, API timing report
