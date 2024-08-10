package dev.rengoku.jobrunr_demo;

import com.mongodb.client.MongoClient;
import org.jobrunr.jobs.annotations.Job;
import org.jobrunr.jobs.annotations.Recurring;
import org.jobrunr.jobs.mappers.JobMapper;
import org.jobrunr.storage.StorageProvider;
import org.jobrunr.storage.nosql.documentdb.AmazonDocumentDBStorageProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class JobrunrDemoApplication {

  public static void main(String[] args) {
    SpringApplication.run(JobrunrDemoApplication.class, args);
  }

  @Job(name = "this is my first jobrunr job")
  @Recurring(id = "my-first-job", cron = "* * * * *")
  public void myFirstJob() {
    System.out.println("My first job");
  }

}
