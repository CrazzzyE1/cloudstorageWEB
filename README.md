# My Cloud Storage (Web + Desktop)

## Версия 1.0

### Что это:
- Сетевое хранилище: Позволяет загружать, скачивать и хранить в облаке Ваши файлы.
- Посмотреть как работает: https://www.youtube.com/watch?v=HuUEU6WP2Q0

### Технологии
- Java 11 
- Spring-boot 
- Lombok
- Flyway 
- PostgreSQL 
- Thymeleaf
- JavaScript
- BootStrap
- HTML + CSS

### WEB - Как запустить
#### `Внимание! В системе создан User: Login - login, Password - pass.`
#### `Внимание! В системе создан Admin: Login - admin, Password - pass.`
- Скачать https://github.com/CrazzzyE1/cloudstorageWEB/archive/refs/heads/master.zip
- Настроить файл `application.properties`
- RUN Application

### Desktop
#### `Внимание! В системе создан User: Login - login, Password - pass.`
#### `Внимание! В системе создан Admin: Login - admin, Password - pass.`
- Скачать https://github.com/CrazzzyE1/MyCloudCientWEB.git
- RUN

### Что реализовано:
- Авторизация пользователя
- Регистрация пользователя
- Уведомление об успешной регистрации.
- Выход пользователя из системы
- Многопользовательский доступ
- Загрузка и скачивание файлов
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
  - Окно смены пароля
  - Окно удаления аккаунта
- Валидация данных, вводимых во всех формах проекта
- `DESKTOP версии приложения`

### Как организовано хранение файлов:
При загрузке файла с компьютера пользователя в сетевое хранилище, файл получает служебное имя и заносится в базу данных.
Все файлы расположены в одной папке `users_files`.
Дальнейшее обращение происходит исключительно к базе данных. 
Физический файл используется только для скачивания.
Так же при полном удалении данных о файле из БД - физический файл так же удаляется с диска сетевого хранилища.
Все папки - виртуальные. Существуют исключительно в БД.
