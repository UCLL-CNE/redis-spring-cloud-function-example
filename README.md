# Spring Cloud function example project with added Redis caching
Spring Cloud Function example repository with added Redis caching

## Prerequisites
The prerequisites for this example project are the same as for the [spring-cloud-function-example](https://github.com/UCLL-CNE/spring-cloud-function-example/tree/main) project.
An Azure Managed Redis instance also needs to be set up to connect to.

## The example project

The example project represents a classical "Hello, World" function, that runs on Azure Functions, and which is configured with Spring Cloud Function.
It receives a simple `User` JSON object, which contains a user name, and send back a `Greeting` object, which contains the welcome message to that user.

The user is cached in an Azure Managed Redis instance. The user is cached forever, so you will always receive the same username as the initial request.
This is of course quite inconvenient, can you modify it so that the user is cached for a limited amount of time?

To flush the cache, execute the following command in Azure cli:
```bash
az redisenterprise database flush --cluster-name <redis-database-name> --resource-group <resource-group>
```
## Maven Redis dependencies

To add Redis to your own Spring Boot project, you need to add the following dependencies to your Maven configuration:

```xml
<dependencies>
  ...
  <dependency>
    <groupId>com.azure.spring</groupId>
    <artifactId>spring-cloud-azure-starter-data-redis-lettuce</artifactId>
  </dependency>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
  </dependency>
  ...
</dependencies>


<dependencyManagement>
        <dependencies>
            ...
            <dependency>
                <groupId>com.azure.spring</groupId>
                <artifactId>spring-cloud-azure-dependencies</artifactId>
                <version>7.1.0</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
            ...
        </dependencies>
</dependencyManagement>
```

## Maven project configuration

Just like in the base Spring Cloud Function example project, some properties in your Maven project need to be configured to be able to deploy to Azure Function app according to your settings.
Modify these in your pom.xml file:

```xml
<properties>
    ...
    <!-- customize those five properties. The functionAppName should be unique across Azure -->
    <functionResourceGroup>resource-group-name-here</functionResourceGroup>
    <functionAppServicePlanName>service-plan-name-here</functionAppServicePlanName>
    <functionAppName>function-app-name-here</functionAppName>
    <functionAppRegion>westeurope</functionAppRegion>
    <functionPricingTier>Y1</functionPricingTier>
    ...
</properties>
```

Note: Azure Functions only supports up to Java 21, so make sure this is correctly configured in your Maven project.

## Configuring the Redis connection
To let your local Spring Boot application communicate with your Azure Managed Redis instance, configure the following values in your resources/application.properties file:
```xml
  spring.data.redis.host=<Managed Redis name>.<Location name>.redis.azure.net
  spring.data.redis.port=10000
  spring.data.redis.password=<Managed Redis password key>
  spring.data.redis.ssl.enabled=true
```
Remember, your resources/application.json file only applies to your local Java Spring Boot application. It does not apply to a deployed Azure Function app instance.

## Building and running the application locally

Just as with the base Spring Cloud Function example project, you can build and run the application locally by executing these commands:

```bash
mvn clean package
```

```bash
mvn azure-functions:run
```

## Deploying the Azure function with Redis

We can deploy the function again with the following command:

```bash
mvn azure-functions:deploy
```

As the Redis connection is configured in the resources/application.json file which normally gets loaded in in the Java Spring Boot application, we notice we don't have access to these in the deployed Azure Function app, resulting in a 500 internal server error. 
Can you resolve this issue?
There are multiple ways to resolve this, some require manual configuration and some can be done automatically.
Perhaps CI/CD can come to the rescue for this?
