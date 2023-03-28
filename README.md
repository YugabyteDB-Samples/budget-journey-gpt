# BudgetJourney - Discover Your Dream City Within Your Budget

Imagine you want to visit a city and have a specific budget in mind. BudgetJourney is an app designed to suggest multiple points of interest within the city, tailored to fit your budget constraints.

This Java-based microservice application leverages the OpenAI GPT API to generate recommendations for points of interest. To optimize costs and reduce the volume of requests to the GPT API, all previous suggestions are stored in a YugabyteDB database. Alternatively, PostgreSQL can be used if required.

## Prerequisite

* Java 19: https://sdkman.io
* OpenAI API key: https://platform.openai.com/overview
* YugabyteDB Managed cluster (or another type of YugabyteDB/PostgreSQL deployment): https://cloud.yugabyte.com

## Configuration

Open the `application.properties` file and provide the following configuration settings:

1. OpenAI API key:
    ```shell
    openai.key={YOUR_API_KEY}
    ```
2. Database connectivity settings:
    ```shell
    spring.datasource.url = {DATABASE_JDBC_URL}
    spring.datasource.username = {DATABASE_USERNAME}
    spring.datasource.password = {DATABASE_PASSWORD}
    ```

## Usage

Start the app from a terminal:
```shell
mvn spring-boot:run
```

The application should automatically open a browser window on the following address: http://localhost:8080


To experience what BudgetJourney has to offer, simply provide a city name and budget limit. Please note that it may take 30 seconds or more for the OpenAI GPT to generate suggestions. However, to enhance efficiency, all previous suggestions are stored in the YugabyteDB database and will be served from there whenever you inquire about the same city and budget again.