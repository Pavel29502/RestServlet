RestServlet

REST API для управления пользователями, файлами и историей загрузок.  
Позволяет создавать, просматривать, изменять и удалять данные через HTTP запросы.

Цель: разработать CRUD-приложение для работы с сущностями User, File, Event через Postman.

---

## Запуск

Установить Java 17+ и MySQL (порт 3307).
Создать базу данных:
MySQL
CREATE DATABASE servlet
Flyway создаст таблицы автоматически.

Склонировать репозиторий:

bash
git clone https://github.com/Pavel29502/RestServlet
Собрать проект:

bash
mvn clean package
Скопировать RestServle.war в webapps/ Tomcat.

Запустить Tomcat (startup.bat).

Отправлять запросы через Postman на http://localhost:8080/RestServle/api/....

Параметры БД: localhost:3307, база servlet, логин root, пароль root.

Примеры запросов
User
Метод	URL	Тело	Ответ
POST	/api/users	{"name": "Иван"}	201
GET	/api/users	—	200
GET	/api/users/1	—	200
PUT	/api/users/1	{"name": "Новое имя"}	200
DELETE	/api/users/1	—	200
Ошибки: 400 — пустое имя, 404 — не найден, 409 — дубликат.

File
Метод	URL	Тело	Ответ
POST	/api/files	form-data: file (File), userId (Text)	201
GET	/api/files	—	200
GET	/api/files/1	—	200
GET	/api/files/download/1	—	200
PUT	/api/files/1	{"name": "new.pdf", "filePath": "D:/uploads/new.pdf"}	200
DELETE	/api/files/1	—	200

Event
Метод	URL	Тело	Ответ
POST	/api/events	{"user": {"id": 1}, "file": {"id": 1}}	201
GET	/api/events	—	200
GET	/api/events/1	—	200
GET	/api/events/user/1	—	200
GET	/api/events/file/1	—	200
PUT	/api/events/1	{"user": {"id": 2}}	200
DELETE	/api/events/1	—	200
Ошибки: 400 — пустое тело / нет user / нет file / нет id, 404 — не найдено.

Swagger UI
http://localhost:8080/RestServle/openapi.json — OpenAPI JSON

http://localhost:8080/RestServle/swagger/ — Swagger UI

Технологии
Java 17, Maven, Hibernate, Flyway, MySQL, Servlets (Jakarta), Gson, Tomcat 10, Postman, Swagger.

Автор
Хатеновский Павел
