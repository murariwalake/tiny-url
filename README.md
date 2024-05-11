Local setup
-----------
Pre-requisites:
- Java 17
- zookeeper running on localhost:2181
    ```docker run -d --name zookeeper-container -p 2181:2181 zookeeper:latest```
- redis running on localhost:6379
    ```docker run -d --name redis-container -p 6379:6379 redis:latest```
- mysql running on localhost:3307
  ```docker run -d --name mysql-container -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=tinyurl -p 3307:3306 mysql:latest```

Build the project:
```./mvnw clean install -DskipTests```
Run the project:
```./mvnw spring-boot:run```

The application will be accessible at http://localhost:8080/ping

APIs
----
- POST /api/v1/shorten
  - Request:
    ```json
    {
      "url": "https://www.google.com"
    }
    ```
  - Response:
    ```json
    {
      "shortUrl": "http://localhost:8080/abc"
    }
    ```
- GET /{shortUrl}
  - Redirects to the original URL 

What all happens during application boot-up?
-------------------------------------------
- During application boot-up it executes ./src/main/resources/schema.sql to create the required tables in the database.
- Created zknode in zookeeper with the name 'counter' and value '100001'

