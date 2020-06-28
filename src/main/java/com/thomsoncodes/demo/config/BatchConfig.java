package com.thomsoncodes.demo.config;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.thomsoncodes.demo.model.User;
import com.thomsoncodes.demo.processor.UserItemProcessor;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

@Configuration
@EnableBatchProcessing
public class BatchConfig {
	
	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	@Autowired
	private DataSource dataSource;
		
	@Bean
	public JdbcCursorItemReader<User> reader(){
		JdbcCursorItemReader<User> cursorItemReader = new JdbcCursorItemReader<>();
		cursorItemReader.setDataSource(dataSource);
		cursorItemReader.setSql("SELECT user_id,first_name,last_name,email FROM demodb.user");
		cursorItemReader.setRowMapper(new UserRowMapper());
		return cursorItemReader;
	}
	
	@Bean
	public UserItemProcessor processor(){
		return new UserItemProcessor();
	}
	
	@Bean
	public FlatFileItemWriter<User> writer(){
		FlatFileItemWriter<User> writer = new FlatFileItemWriter<User>();
		writer.setResource(new ClassPathResource("users.csv"));
//		writer.setResource(new FileSystemResource("users.csv"));
		
		DelimitedLineAggregator<User> lineAggregator = new DelimitedLineAggregator<User>();
		lineAggregator.setDelimiter(",");
		
		BeanWrapperFieldExtractor<User>  fieldExtractor = new BeanWrapperFieldExtractor<User>();
		fieldExtractor.setNames(new String[]{"userId","firstName","lastName", "email"});
		lineAggregator.setFieldExtractor(fieldExtractor);
		
		writer.setLineAggregator(lineAggregator);
		writer.setAppendAllowed(true);
		
		return writer;
	}
	
	 
	@Bean
	public Step step1(){
		return stepBuilderFactory
				.get("step1")
				.<User,User>chunk(3)
				.reader(reader())
				.processor(processor())
				.writer(writer())
				.build();
	}

	@Bean
	public Job exportUserJob(){
		return jobBuilderFactory
				.get("exportUserJob")
				.incrementer(new RunIdIncrementer())
				.flow(step1())
				.end()
				.build();
	}
}