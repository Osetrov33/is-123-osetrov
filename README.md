# PulseBeat

Настольный музыкальный плеер на JavaFX с поддержкой локальной медиатеки, авторизации пользователей, отзывов, избранного, чтения метаданных MP3-файлов и гибкой архитектурой на основе паттернов проектирования.

## Оглавление

* Возможности
* Архитектура
* Структура проекта
* Технологии
* Запуск
* База данных
* Управление
* Используемые паттерны
* Скриншоты

## Возможности

### Работа с пользователями

* Регистрация новых пользователей
* Авторизация через логин и пароль
* Безопасное хранение паролей с использованием BCrypt
* Разделение данных между пользователями

### Музыкальная библиотека

* Импорт отдельных MP3-файлов
* Импорт целых музыкальных каталогов
* Автоматическое сканирование папок
* Защита от добавления дубликатов
* Поиск по названию трека
* Поиск по исполнителю
* Поиск по альбому

### Метаданные

* Чтение ID3-тегов через JAudioTagger
* Определение названия трека
* Определение исполнителя
* Определение альбома
* Резервное получение данных из имени файла
* Редактирование метаданных из интерфейса

### Воспроизведение

* Запуск и остановка треков
* Пауза и продолжение воспроизведения
* Переключение между треками
* Регулировка громкости
* Перемотка трека
* Автоматический переход к следующему треку
* Режим Repeat
* Режим Shuffle
* Управление очередью воспроизведения

### Избранное и отзывы

* Добавление треков в избранное
* Просмотр списка избранных композиций
* Оценка треков
* Добавление отзывов
* Расчет среднего рейтинга
* Просмотр истории отзывов

### Интерфейс

* Современный интерфейс на JavaFX
* Drag and Drop импорт файлов
* Динамическое обновление библиотеки
* Отдельная страница трека
* Панель очереди воспроизведения
* Панель статистики медиатеки
* CSS-оформление интерфейса

## Архитектура

Приложение построено по многослойной архитектуре:

MainApp
↓
Controllers
↓
Repository Layer
↓
DAO Layer
↓
Database

Дополнительно используются отдельные подсистемы:

* Metadata Readers
* Playback Strategies
* Factory
* Utility Classes

## Структура проекта

```text
com
├── controller
│   ├── LoginController
│   └── MainController
│
├── dao
│   ├── UserDAO
│   ├── TrackDAO
│   ├── FavoriteDAO
│   └── ReviewDAO
│
├── factory
│   └── ApplicationFactory
│
├── metadata
│   ├── TrackMetadataReader
│   ├── TagTrackMetadataReader
│   ├── FileNameTrackMetadataReader
│   └── FallbackTrackMetadataReader
│
├── model
│   ├── User
│   ├── Track
│   ├── Favorite
│   └── Review
│
├── playback
│   ├── PlaybackOrderStrategy
│   ├── SequentialPlaybackOrderStrategy
│   ├── ShufflePlaybackOrderStrategy
│   └── RepeatPlaybackOrderStrategy
│
├── repository
│   ├── TrackRepository
│   ├── SqlTrackRepository
│   ├── CachedTrackRepository
│   └── ReadOnlyTrackRepository
│
├── util
│   ├── AppConfig
│   ├── DatabaseUtil
│   └── MusicScanner
│
└── MainApp
```

## Технологии

| Технология              | Назначение                 |
| ----------------------- | -------------------------- |
| Java 17                 | Основной язык разработки   |
| JavaFX 21               | Пользовательский интерфейс |
| Maven                   | Сборка проекта             |
| Firebird / Red Database | Хранение данных            |
| Jaybird JDBC            | Подключение к БД           |
| BCrypt                  | Хеширование паролей        |
| JAudioTagger            | Чтение ID3-тегов           |
| CSS                     | Оформление интерфейса      |

## База данных

Основные таблицы системы:

### Users

* id
* login
* password_hash

### Tracks

* id
* title
* artist
* album
* file_path

### Favorites

* user_id
* track_id

### Reviews

* id
* user_id
* track_id
* rating
* review_text
* review_date

## Управление

### Основные действия

| Действие    | Описание                      |
| ----------- | ----------------------------- |
| Вход        | Авторизация пользователя      |
| Регистрация | Создание нового аккаунта      |
| Импорт      | Добавление музыки в медиатеку |
| Поиск       | Фильтрация списка треков      |
| Play        | Воспроизведение трека         |
| Pause       | Пауза                         |
| Next        | Следующий трек                |
| Previous    | Предыдущий трек               |
| Shuffle     | Случайный порядок             |
| Repeat      | Повтор текущего трека         |
| Favorites   | Просмотр избранного           |
| Reviews     | Просмотр и добавление отзывов |

## Используемые паттерны

### Repository

Абстрагирует работу с музыкальной библиотекой.

### DAO

Инкапсулирует SQL-запросы и операции с базой данных.

### Strategy

Используется для выбора порядка воспроизведения:

* SequentialPlaybackOrderStrategy
* ShufflePlaybackOrderStrategy
* RepeatPlaybackOrderStrategy

### Factory

ApplicationFactory отвечает за создание зависимостей приложения.

### Decorator

CachedTrackRepository добавляет кэширование поверх репозитория.

### Chain of Responsibility

Используется при чтении метаданных треков.

## Запуск

### Требования

* Java 17+
* Maven 3.8+
* Firebird или Red Database

### Сборка проекта

```bash
git clone https://github.com/USERNAME/PulseBeat.git

cd PulseBeat

mvn clean package
```

### Запуск

```bash
mvn javafx:run
```

или запуск класса:

```text
MainApp.java
```



