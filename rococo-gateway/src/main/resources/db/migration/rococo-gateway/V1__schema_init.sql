create table if not exists files
(
    id           uuid unique not null default gen_random_uuid(),
    object_key   varchar(255) not null,
    bucket       varchar(32)  not null,
    file_name    varchar(255) not null,
    content_type varchar(64)  not null,
    size_bytes   bigint       not null,
    sha256       varchar(64)  not null,
    primary key (id),
    constraint uq_files_bucket_object_key unique (bucket, object_key),
    constraint chk_files_bucket check (bucket in ('painting', 'museum', 'artist', 'avatar'))
);

create table if not exists users
(
    id             uuid unique not null default gen_random_uuid(),
    username       varchar(50) unique not null,
    firstname      varchar(255),
    lastname       varchar(255),
    avatar_file_id uuid,
    primary key (id),
    constraint fk_users_avatar_file foreign key (avatar_file_id) references files (id)
);

create index if not exists idx_users_avatar_file_id on users (avatar_file_id);

create table if not exists country
(
    id   uuid unique not null default gen_random_uuid(),
    name varchar(255) unique not null,
    primary key (id)
);

create table if not exists museum
(
    id            uuid unique not null default gen_random_uuid(),
    title         varchar(255) unique not null,
    description   varchar(1000),
    city          varchar(255),
    photo_file_id uuid not null,
    country_id    uuid not null,
    primary key (id),
    constraint fk_museum_country foreign key (country_id) references country (id),
    constraint fk_museum_photo_file foreign key (photo_file_id) references files (id)
);

create index if not exists idx_museum_country_id on museum (country_id);
create index if not exists idx_museum_photo_file_id on museum (photo_file_id);

create table if not exists artist
(
    id            uuid unique not null default gen_random_uuid(),
    name          varchar(255) unique not null,
    biography     varchar(2000) not null,
    photo_file_id uuid not null,
    primary key (id),
    constraint fk_artist_photo_file foreign key (photo_file_id) references files (id)
);

create index if not exists idx_artist_photo_file_id on artist (photo_file_id);

create table if not exists painting
(
    id              uuid unique not null default gen_random_uuid(),
    title           varchar(255) not null,
    description     varchar(1000),
    artist_id       uuid not null,
    museum_id       uuid,
    content_file_id uuid not null,
    primary key (id),
    constraint fk_artist_id foreign key (artist_id) references artist (id),
    constraint fk_museum_id foreign key (museum_id) references museum (id),
    constraint fk_painting_content_file foreign key (content_file_id) references files (id)
);

create index if not exists idx_painting_content_file_id on painting (content_file_id);

delete from users;
delete from painting;
delete from artist;
delete from museum;
delete from country;

insert into country (name) values ('Австралия');
insert into country (name) values ('Австрия');
insert into country (name) values ('Азербайджан');
insert into country (name) values ('Албания');
insert into country (name) values ('Алжир');
insert into country (name) values ('Ангола');
insert into country (name) values ('Андорра');
insert into country (name) values ('Антигуа и Барбуда');
insert into country (name) values ('Аргентина');
insert into country (name) values ('Армения');
insert into country (name) values ('Афганистан');
insert into country (name) values ('Багамские Острова');
insert into country (name) values ('Бангладеш');
insert into country (name) values ('Барбадос');
insert into country (name) values ('Бахрейн');
insert into country (name) values ('Белиз');
insert into country (name) values ('Белоруссия');
insert into country (name) values ('Бельгия');
insert into country (name) values ('Бенин');
insert into country (name) values ('Болгария');
insert into country (name) values ('Боливия');
insert into country (name) values ('Босния и Герцеговина');
insert into country (name) values ('Ботсвана');
insert into country (name) values ('Бразилия');
insert into country (name) values ('Бруней');
insert into country (name) values ('Буркина-Фасо');
insert into country (name) values ('Бурунди');
insert into country (name) values ('Бутан');
insert into country (name) values ('Вануату');
insert into country (name) values ('Великобритания');
insert into country (name) values ('Венгрия');
insert into country (name) values ('Венесуэла');
insert into country (name) values ('Восточный Тимор');
insert into country (name) values ('Вьетнам');
insert into country (name) values ('Габон');
insert into country (name) values ('Республика Гаити');
insert into country (name) values ('Гайана');
insert into country (name) values ('Гамбия');
insert into country (name) values ('Гана');
insert into country (name) values ('Гватемала');
insert into country (name) values ('Гвинея');
insert into country (name) values ('Гвинея-Бисау');
insert into country (name) values ('Германия');
insert into country (name) values ('Гондурас');
insert into country (name) values ('Гренада');
insert into country (name) values ('Греция');
insert into country (name) values ('Грузия');
insert into country (name) values ('Дания');
insert into country (name) values ('Джибути');
insert into country (name) values ('Доминика');
insert into country (name) values ('Доминиканская Республика');
insert into country (name) values ('Египет');
insert into country (name) values ('Замбия');
insert into country (name) values ('Зимбабве');
insert into country (name) values ('Израиль');
insert into country (name) values ('Индия');
insert into country (name) values ('Индонезия');
insert into country (name) values ('Иордания');
insert into country (name) values ('Ирак');
insert into country (name) values ('Иран');
insert into country (name) values ('Ирландия');
insert into country (name) values ('Исландия');
insert into country (name) values ('Испания');
insert into country (name) values ('Италия');
insert into country (name) values ('Йемен');
insert into country (name) values ('Кабо-Верде');
insert into country (name) values ('Казахстан');
insert into country (name) values ('Камбоджа');
insert into country (name) values ('Камерун');
insert into country (name) values ('Канада');
insert into country (name) values ('Катар');
insert into country (name) values ('Кения');
insert into country (name) values ('Республика Кипр');
insert into country (name) values ('Киргизия');
insert into country (name) values ('Кирибати');
insert into country (name) values ('Китай');
insert into country (name) values ('Колумбия');
insert into country (name) values ('Коморы');
insert into country (name) values ('Республика Конго');
insert into country (name) values ('Демократическая Республика Конго');
insert into country (name) values ('Корейская Народно-Демократическая Республика');
insert into country (name) values ('Республика Корея');
insert into country (name) values ('Коста-Рика');
insert into country (name) values ('Кот-д’Ивуар');
insert into country (name) values ('Куба');
insert into country (name) values ('Кувейт');
insert into country (name) values ('Лаос');
insert into country (name) values ('Латвия');
insert into country (name) values ('Лесото');
insert into country (name) values ('Либерия');
insert into country (name) values ('Ливан');
insert into country (name) values ('Ливия');
insert into country (name) values ('Литва');
insert into country (name) values ('Лихтенштейн');
insert into country (name) values ('Люксембург');
insert into country (name) values ('Маврикий');
insert into country (name) values ('Мавритания');
insert into country (name) values ('Мадагаскар');
insert into country (name) values ('Малави');
insert into country (name) values ('Малайзия');
insert into country (name) values ('Мали');
insert into country (name) values ('Мальдивы');
insert into country (name) values ('Мальта');
insert into country (name) values ('Марокко');
insert into country (name) values ('Маршалловы Острова');
insert into country (name) values ('Мексика');
insert into country (name) values ('Федеративные Штаты Микронезии');
insert into country (name) values ('Мозамбик');
insert into country (name) values ('Молдавия');
insert into country (name) values ('Монако');
insert into country (name) values ('Монголия');
insert into country (name) values ('Мьянма');
insert into country (name) values ('Намибия');
insert into country (name) values ('Науру');
insert into country (name) values ('Непал');
insert into country (name) values ('Нигер');
insert into country (name) values ('Нигерия');
insert into country (name) values ('Нидерланды');
insert into country (name) values ('Никарагуа');
insert into country (name) values ('Новая Зеландия');
insert into country (name) values ('Норвегия');
insert into country (name) values ('Объединённые Арабские Эмираты');
insert into country (name) values ('Оман');
insert into country (name) values ('Пакистан');
insert into country (name) values ('Палау');
insert into country (name) values ('Панама');
insert into country (name) values ('Папуа — Новая Гвинея');
insert into country (name) values ('Парагвай');
insert into country (name) values ('Перу');
insert into country (name) values ('Польша');
insert into country (name) values ('Португалия');
insert into country (name) values ('Россия');
insert into country (name) values ('Руанда');
insert into country (name) values ('Румыния');
insert into country (name) values ('Сальвадор');
insert into country (name) values ('Самоа');
insert into country (name) values ('Сан-Марино');
insert into country (name) values ('Сан-Томе и Принсипи');
insert into country (name) values ('Саудовская Аравия');
insert into country (name) values ('Флаг Северной Македонии');
insert into country (name) values ('Сейшельские Острова');
insert into country (name) values ('Сенегал');
insert into country (name) values ('Сент-Винсент и Гренадины');
insert into country (name) values ('Сент-Китс и Невис');
insert into country (name) values ('Сент-Люсия');
insert into country (name) values ('Сербия');
insert into country (name) values ('Сингапур');
insert into country (name) values ('Сирия');
insert into country (name) values ('Словакия');
insert into country (name) values ('Словения');
insert into country (name) values ('Соединённые Штаты Америки');
insert into country (name) values ('Соломоновы Острова');
insert into country (name) values ('Сомали');
insert into country (name) values ('Судан');
insert into country (name) values ('Суринам');
insert into country (name) values ('Сьерра-Леоне');
insert into country (name) values ('Таджикистан');
insert into country (name) values ('Таиланд');
insert into country (name) values ('Танзания');
insert into country (name) values ('Того');
insert into country (name) values ('Тонга');
insert into country (name) values ('Тринидад и Тобаго');
insert into country (name) values ('Тувалу');
insert into country (name) values ('Тунис');
insert into country (name) values ('Туркменистан');
insert into country (name) values ('Турция');
insert into country (name) values ('Уганда');
insert into country (name) values ('Узбекистан');
insert into country (name) values ('Украина');
insert into country (name) values ('Уругвай');
insert into country (name) values ('Фиджи');
insert into country (name) values ('Филиппины');
insert into country (name) values ('Финляндия');
insert into country (name) values ('Франция');
insert into country (name) values ('Хорватия');
insert into country (name) values ('Центральноафриканская Республика');
insert into country (name) values ('Чад');
insert into country (name) values ('Черногория');
insert into country (name) values ('Чехия');
insert into country (name) values ('Чили');
insert into country (name) values ('Швейцария');
insert into country (name) values ('Швеция');
insert into country (name) values ('Флаг Шри-Ланки');
insert into country (name) values ('Эквадор');
insert into country (name) values ('Экваториальная Гвинея');
insert into country (name) values ('Эритрея');
insert into country (name) values ('Эсватини');
insert into country (name) values ('Эстония');
insert into country (name) values ('Эфиопия');
insert into country (name) values ('Южно-Африканская Республика');
insert into country (name) values ('Южный Судан');
insert into country (name) values ('Ямайка');
insert into country (name) values ('Япония');
insert into country (name) values ('Ватикан');
insert into country (name) values ('Палестина');
