# My Cloud Storage (Web + Desktop)

## Версия 0.5 (Первый показ)

### Что это:
- Сетевое хранилище: Позволяет загружать, скачивать и хранить в облаке Ваши файлы.
- Посмотреть как работает: https://youtu.be/F1hE2-DFcek

### Технологии
- Java 11 
- Spring-boot 
- Lombok
- Flyway 
- PostgreSQL 
- Thymeleaf
- BootStrap
- HTML + CSS

### Как запустить
#### `Внимание! В системе создан User: Login - login, Password - pass.`
- Скачать https://github.com/CrazzzyE1/cloudstorageWEB/archive/refs/heads/master.zip
- Настроить в pom.xml
```
<url>jdbc:postgresql://localhost:5432/YOUR_DB_NAME?currentSchema=SCHEMA_NAME</url>
<user>YOUR_LOGIN_DB</user>
<password>YOUR_PASSWORD_DB</password>
<schemas>
  <schema>SCHEMA_NAME</schema>
</schemas>
```
- Настроить файл `application.properties`
- Запустить: `Maven -> Plugins -> Flyway -> Clean`, далее `Maven -> Plugins -> Flyway -> Migrate`
- RUN Application

### Что реализовано:
- Авторизация пользователя
- Регистрация пользователя
- Выход пользователя из системы
- Многопользовательский доступ
- Загрузка и скачивание файлов (Максимальный размер 1 Мб)
- Поиск файлов без учета регистра
- Создание, удаление директорий
- Копирование, перемещение, удаление файлов
- Статистика свободного / занятого места в хранилище
+ Корзина
  - Восстановление одного файла
  - Восстановление всех файлов сразу
  - Удаление одного файла
  - Удаление всех файлов сразу
- Проверка на дублирование имен файлов и папок
- Отображение файлов на странице
- Визуальные эффекты
+ Настройки
  - Окно смены пароля      (ТРЕБУЕТСЯ ИСПРАВЛЕНИЕ БАГОВ - ЭЛЕМЕНТ В РАЗРАБОТКЕ)
  - Окно удаления аккаунта (ТРЕБУЕТСЯ ИСПРАВЛЕНИЕ БАГОВ - ЭЛЕМЕНТ В РАЗРАБОТКЕ)
  
### Что планируется:
- Доработка функционала для окна Смены пароля
- Доработка функционала для окна Удаление аккаунта
- Валидация данных, вводимых во всех формах проекта
- Передача файлов большого размера
- Создание `DESKTOP версии приложения`

### Как организовано хранение файлов:
При загрузке файла с компьютера пользователя в сетевое хранилище, файл получает служебное имя и заносится в базу данных.
Все файлы расположены в одной папке `users_files`.
Дальнейшее обращение происходит исключительно к базе данных. 
Физический файл используется только для скачивания.
Так же при полном удалении данных о файле из БД - физический файл так же удаляется с диска сетевого хранилища.
Все папки - виртуальные. Существуют исключительно в БД.
