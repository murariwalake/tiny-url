echo "----------Building the project----------"
./mvnw clean install -DskipTests

# stop an remove the containers if they are already running
echo "----------Stopping and removing the containers zookeeper, redis and mysql if they are already running-----------"
docker stop zookeeper-container
docker rm zookeeper-container
docker stop redis-container
docker rm redis-container
docker stop mysql-container
docker rm mysql-container

echo "----------Running the docker containers zookeeper, redis and mysql -----------"
docker run -d --name zookeeper-container -p 2181:2181 zookeeper:latest
docker run -d --name redis-container -p 6379:6379 redis:latest
docker run -d --name mysql-container -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=tinyurl -p 3307:3306 mysql:latest

#wait until all the containers are up and running
echo "----------Waiting for the containers to be up and running-----------"
# Zookeeper takes some time to become up and running
sleep 30

#build the docker image
#echo "----------Building the docker image for the spring boot application----------"
#docker build -t tinyurl-spring-boot .
#
##stop and remove the container if it is already running
#echo "----------Stopping and removing the container tinyurl-spring-boot if it is already running-----------"
#docker stop tinyurl-spring-boot-container
#docker rm tinyurl-spring-boot-container
#
##run the docker container
#echo "----------Running the docker container tinyurl-spring-boot-----------"
#docker run -d --name tinyurl-spring-boot-container -p 8080:8080 --link mysql-container:mysql --link redis-container:redis tinyurl-spring-boot

#echo "----------Running the spring boot application----------"
./mvnw spring-boot:run