Introduction
------------
This is a simple URL shortening service. It generates a short URL for a given long URL. The short URL is generated using the base62 encoding of the counter value stored in zookeeper. The counter value is incremented by 1 for each new URL. The short URL is stored in redis with the key as the short URL and value as the long URL. The long URL is stored in the database with the short URL as the primary key.

Local setup
-----------
#### Pre-requisites:
- Java 17
- zookeeper running on localhost:2181 ```docker run -d --name zookeeper-container -p 2181:2181 zookeeper:latest```
- redis running on localhost:6379
    ```docker run -d --name redis-container -p 6379:6379 redis:latest```
- mysql running on localhost:3307
  ```docker run -d --name mysql-container -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=tinyurl -p 3307:3306 mysql:latest```

#### Clone the project:
```git clone https://github.com/murariwalake/tiny-url.git```
```cd tiny-url```

#### Build the project:
```./mvnw clean install -DskipTests```

#### Run the project:
```./mvnw spring-boot:run```

Note: start.sh script can also be used to build and run the project. The script will build the project, create a docker image, and run the docker container.

```./start.sh```

The application will be accessible at ```http://localhost:8080/ping```

APIs
----
- POST /api/v1/tinyurl
  - Request:
    ```json
    {
      "longUrl": "https://github.com/murariwalake/tiny-url"
    }
    ```
  - Response: 201 created
    ```json
    {
      "tinyUrl": "http://localhost:8080/Aa5"
    }
    ```
- GET /{shortUrl}
  - Redirects to the original URL
  
- DELETE /api/v1/tinyurl/{shortUrl}
  - Response: 204 no content

Postman collection
------------------
- Refer [Postman collection](./TinyUrlApi.postman_collection.json) to check all available APIs.

What all happens during application boot-up?
-------------------------------------------
- During application boot-up it executes ./src/main/resources/schema.sql to create the required tables in the database.
- Created zknode(only if not exists) in zookeeper at the path ```"/counter"``` with value '100000'
- Fetch the range start value from zookeeper and set it in the counter service.
- Initialize the counter value in the counter service.

Refer the article [here](https://medium.com/@murariwalake/tiny-url-system-design-implementation-using-spring-boot-8cddc2a66350) for more details.

