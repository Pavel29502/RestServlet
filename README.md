RestServlet

Описание:
REST API для управления пользователями, файлами и историей загрузок.  
Позволяет создавать, просматривать, изменять и удалять данные через HTTP запросы.

Цель проекта: 
Разработать RESTful CRUD-приложение, где можно работать с сущностями User, File, Event через Postman.

Как запустить?

1. Установить Java 17+
2. Установить MySQL (порт 3307)
3. Склонировать из репозитория:  
   `git clone https://github.com/Pavel29502/RestServlet`
4. Собрать проект:  
   `mvn clean package`
5. Скопировать `RestServle.war` в папку `webapps/` Tomcat
6. Запустить Tomcat (`startup.bat`)
7. Открыть Postman и отправлять запросы на:  
   http://localhost:8080/RestServle/api/...

 Использованные технологии:
- Java 17
- Maven
- Hibernate (аннотации)
- Flyway
- MySQL
- Servlets (Jakarta)
- Gson
- Tomcat 10
- Postman

Автор:
Хатеновский Павел

