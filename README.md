   # User-Management-Application
This is a user web application built with Java, Spring Boot, and JPA/Hibernate for managing user records in MYSQL database. The application provides CRUD (Create, Read, Update, Delete) operations for managing entities and implements event-driven messaging to track each significant operation by publishing messages to a message broker (Kafka). These messages are then consumed by a separate service that logs audit information into an audit table, providing a clear history of data changes.

    1. Spring-boot
    2. Global Exception Handling
    3. Swagger Explorer
    4. Apache Kafka
    5. Unit Testing

# There are the following steps for the setup of the project.
    1. JDK 17 
    2. Spring-boot 3.3.5
    3. Mysql server 8.3 or later
    4. Apache Kafka 2.12
    
# Create the database on Mysql server.
  create database user_management_app
  Set the database configuration in the properties file. Like Username, Password, URL, etc..
```sh
    spring.datasource.url=jdbc:mysql://localhost:3306/user_management_app
    spring.datasource.username=root
    spring.datasource.password=root
```

 # Start the Apache Kafka and zookeeper servers.
Reference for the setup of kafka and Zookeeper on your Ubuntu system : https://www.fosstechnix.com/install-apache-kafka-on-ubuntu-22-04-lts/

 ```sh
sudo systemctl start zookeeper  
sudo systemctl start kafka
```
### Configuration of kafka:
  spring.kafka.bootstrap-servers=localhost:9092

> Note: `The default port for Kafka is 9092. If you want to change the configuration for the kafka, you can change the property above.`

 # Start the user management application.
 ```sh
mvn spring-boot:run
```

# Use Swagger Explorer for the APIs listing
http://localhost:8080/swagger-ui/index.html