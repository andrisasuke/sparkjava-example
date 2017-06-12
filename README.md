Sparkjava Playground - sample CRUD Rest API application

## Requirement :

1. Java JDK 8
2. Gradle (optional)

## Content :

1. Guice dependency injection
2. Connect to MySQL or Postgres database
3. Hibernate persistence (with connection pool)
4. Properties configuration file using `https://github.com/typesafehub/config`
5. Elasticsearch
6. Java Bean Validation (javax.validation), validate NotNull property
7. to be continue..

## How to run :

1. `./gradlew build`
2. `./gradlew startServer`
3. curl to `http://localhost:4567/` for testing
4. `./gradlew fatJar` to create standalone jar application (jar file will be in `build/libs/*-all-[version].jar`)
5. `java -jar filename.jar` to run jar application