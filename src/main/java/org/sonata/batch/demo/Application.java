package org.sonata.batch.demo;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.sonata.batch.demo.reader.DirHandler;
import org.sonata.batch.demo.reader.FileHandler;
import org.sonata.batch.demo.writer.ViewDataWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowJobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@SpringBootApplication
@EnableBatchProcessing
@EnableAutoConfiguration
@EnableTransactionManagement
public class Application implements CommandLineRunner {

	@Autowired
	private DataSource dataSource;

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	private List<String> dirs = new ArrayList<>();
	private List<String> files = new ArrayList<>();
	private String dirPath = "D:/abhijit/hybris/hybris/bin";

	public static void main(String[] args) throws Exception {
		SpringApplication.run(Application.class, args);
	}

	@Override
	@JobScope
	public void run(String... args) throws Exception {
		JobParameters params = new JobParameters();
		FlowJobBuilder builder = jobBuilderFactory.get("aaa").start(step1()).on("OK").to(step2()).on("OK").to(step3())
				.on("OK").end("Success").build().repository(jobRepository());
		JobExecution jobExecution = jobRepository().createJobExecution("aaa", params);
		Job job = builder.build();
		job.execute(jobExecution);

		/*
		 * JobLauncher launcher = new SimpleJobLauncher(); launcher.run(job, params);
		 */
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean() {
		return new LocalContainerEntityManagerFactoryBean();
	}

	@Bean
	public PlatformTransactionManager transactionManager() {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(entityManagerFactoryBean().getObject());
		return transactionManager;
	}

	@Bean
	public JobRepository jobRepository() throws Exception {
		JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
		factory.setDataSource(dataSource);
		factory.setTransactionManager(transactionManager());
		factory.afterPropertiesSet();
		return factory.getObject();
	}

	@Bean
	@JobScope
	@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
	public TaskletStep step1() throws Exception {
		DirHandler handler = new DirHandler();
		handler.setDirectories(dirs);
		handler.setDirPath(dirPath);
		TaskletStep step = stepBuilderFactory.get("xxx").tasklet(handler).startLimit(100).build();
		step.setName("xxx");
		return step;
	}

	@Bean
	@JobScope
	@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
	public TaskletStep step2() throws Exception {
		FileHandler handler = new FileHandler();
		handler.setDirectories(dirs);
		handler.setFiles(files);
		TaskletStep step = stepBuilderFactory.get("yyy").tasklet(handler).startLimit(100).build();
		step.setName("yyy");
		return step;
	}

	@Bean
	@JobScope
	@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
	public TaskletStep step3() throws Exception {
		ViewDataWriter handler = new ViewDataWriter();
		handler.setFiles(files);
		TaskletStep step = stepBuilderFactory.get("zzz").tasklet(handler).build();
		step.setName("zzz");
		return step;
	}
}
