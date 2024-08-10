# Spring Boot  And JobRunr with MongoDB

Requirements -

1. MongoDB cluster
2. Java 21 and maven
3. Docker Desktop

Maven is not a hard environment you can use maven wrapper present in the project.

## MongoDB

Lets start with creating a mongo db cluster using MongoDB Atlas.

- Select M0 free tier template.
- Give name to the cluster.
- Check Automate security setup
- Select **AWS as provider**
- Now go on and click create cluster.
- Create a user with read and write permission
- Add your ip address to the allowed ip

This will take some time, lets head over to our spring boot app.

## Spring Boot

Lets visit Josh Long 2nd favourite place on the internet -

[https://start.spring.io/](https://start.spring.io/)

Generate the project, extract zip and open project in ide or editor of your choice.
For folks going via editor use this command to start the application - 

```bash
## Linus / MacOs
./mvnw spring-boot:run
```
```bash
# Windows
/mvnw.cmd spring-boot:run
```

If you dont have maven installed use ./mvnw for

Add dependencies - **Spring Web and Spring Data MongoDB**

Add JobRunr spring boot starter dependency to your pom.xml ( don’t forget to reload maven)

```properties
<dependency> 
    <groupId>org.jobrunr</groupId> 
    <artifactId>jobrunr-spring-boot-3-starter</artifactId>
    <version>7.0.0</version> 
</dependency>
```

Please head over to https://www.jobrunr.io/en/documentation/configuration/spring/ for further details.

Jobrunr have multiple ways to create a job, we will use the annotation way.

Create a job in service application class.

```java
 	@Job(name = "this is my first jobrunr job")
	@Recurring(id = "my-first-job", cron = "* * * * *")
	public void myFirstJob() {
		System.out.println("My first job");
	}
```

Configurations for jobrunr and mongodb in **application.properties** -

```
spring.data.mongodb.uri=#get from mongodb atlast from connect option
spring.data.mongodb.database=jobrunr
org.jobrunr.background-job-server.enabled=true
org.jobrunr.database.database-name=jobrunr
```

All JobRunr configuration can be done via properties, check jobrunr spring boot starter documentation for other properties.

Now start your application, you should see the logs in every minute.

Note -

If you are using Amazon Document DB which is a mongo db api supported offering. You need to create a amazon document storage provider bean. For our case its not required.

```java
  @Bean
  public StorageProvider storageProvider(MongoClient mongoClient, JobMapper jobMapper) {
    AmazonDocumentDBStorageProvider documentDBStorageProvider =
        new AmazonDocumentDBStorageProvider(mongoClient, "jobRunr"); // jobRunr db name
    documentDBStorageProvider.setJobMapper(jobMapper);
    return documentDBStorageProvider;
  }
```

## JobRunr Dashboard

Jobrunr ship with its own dashboard, we can delete and trigger jobs from it as well. To enable dashboard add these property.

```
org.jobrunr.dashboard.enabled=true
org.jobrunr.dashboard.port=8000 #the port on which to start the dashboard
```

As jobrunr requires a different port from application (JobRunr Pro allows to run on same port), in case you have only single port exposed in your kubernetes cluster or your cloud environment, you can create a 2nd application with dashboad only with no background job servers and have the jobrunr port same as your exposed port.

Note -

Jobrunr uses “/api” for its endpoints, please beware of that.

## Metrics

Jobrunr publishes its own metrics to enable them add this property -

```
org.jobrunr.background-job-server.metrics.enabled=true
org.jobrunr.jobs.metrics.enabled=true
```

Integration with prometheus

Add actuator -

``` properties
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
```

In prometheus to scrape data use this -

scrape_configs: prometheus.yml

```yaml
- job_name: 'spring-boot-application'
  metrics_path: '/actuator/prometheus'
  scrape_interval: 15s # This can be adjusted based on our needs
  static_configs:
    - targets: ['**host.docker.internal:8080**']
```


To allow prometheus metrics to be published from spring boot - allow actuator endpoints visible

```
management.endpoints.web.exposure.include=*
management.endpoint.health.show-details=always
```

To start a prometheus container with prometheus.yml ( you will need docker desktop)

```bash
docker run --name prometheus -d -P -p 9090:9090 -v ./prometheus.yml:/etc/prometheus/prometheus.yml prom/prometheus
```

-P to allow endpoints to be visible in docker in our local machine

This will spin up the docker container on port 9090. To open prometheus - [localhost:9090](http://localhost:9090)

[http://localhost:9090/targets?search=](http://localhost:9090/targets?search=) this should show the app in up state.

Jobrunr metrics starts with jobrun_

For example search for - [jobrunr_jobs_all_time_succeeded](http://localhost:9090/graph?g0.expr=jobrunr_jobs_all_time_succeeded&g0.tab=0&g0.display_mode=lines&g0.show_exemplars=0&g0.range_input=1h)

## Patience is the key

Please be patient if you get stuck anywhere, 2 places where you can have difficulties are in establishing the connection with mongodb cluster or pushing metrics to prometheus.
If you get any error please take time to google them. Checks thread on jobrunr github issues section(the creator is a very cool guy, you can also raise issue in this repo as well)