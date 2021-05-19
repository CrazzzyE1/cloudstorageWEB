# My Cloud Storage (Web + Desktop)

## Версия 0.5 (Первый показ)

### Технологии
- Java 11 
- Spring-boot 
- Lombok
- Flyway 
- PostgreSQL 

### Как запустить
- Скачать
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
- 
### Что реализовано
### Что не работает или работает не так как нужно
### Планы
