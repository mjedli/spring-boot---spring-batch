package com.example.batchprocessing;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.batch.item.xml.builder.StaxEventItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

@Configuration
public class BatchConfiguration {
	
	private static final Logger log = LoggerFactory.getLogger(BatchConfiguration.class);
	
	// tag::readerwriterprocessor[]
	@Bean
	public FlatFileItemReader<Person> reader() {
		return new FlatFileItemReaderBuilder<Person>()
			.name("personItemReader")
			.resource(new ClassPathResource("sample-data.csv"))
			.delimited()
			.names(new String[]{"firstName", "lastName"})
			.fieldSetMapper(new BeanWrapperFieldSetMapper<Person>() {{
				setTargetType(Person.class);
			}})
			.build();
	}

	@Bean
	public PersonItemProcessor processor() {
		return new PersonItemProcessor();
	}
	
	@Bean
	public PersonIXmltemProcessor processorXml() {
		return new PersonIXmltemProcessor();
	}

	@Bean
	public JdbcBatchItemWriter<Person> writer(DataSource dataSource) {
		return new JdbcBatchItemWriterBuilder<Person>()
			.itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
			.sql("INSERT INTO people (first_name, last_name) VALUES (:firstName, :lastName)")
			.dataSource(dataSource)
			.build();
	}
	
    @Bean
    public ItemWriter<PersonXml> writerXml() {
    	
    	String exportFile = "xmloutput/" + new ClassPathResource("sample-xml.xml").getFilename();
    	
    	log.info("File xml output : " +  exportFile);
    	
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setClassesToBeBound(PersonXml.class);
         
        return new StaxEventItemWriterBuilder<PersonXml>()
                .name("contactItemWriter")
                .version("1.0")
                .rootTagName("ContactList")
                .resource(new FileSystemResource(exportFile))
                .marshaller(marshaller)
                .build();
         
    } 
    
	// end::readerwriterprocessor[]

	// tag::jobstep[]
	@Bean
	public Job importUserJob(JobRepository jobRepository,
			JobCompletionNotificationListener listener, Step step1, Step step2) {
		return new JobBuilder("importUserJob", jobRepository)
			.incrementer(new RunIdIncrementer())
			.listener(listener)
			.start(step1)
			.next(step2)
			//.end()
			.build();
	}

	@Bean
	public Step step1(JobRepository jobRepository,
			PlatformTransactionManager transactionManager, JdbcBatchItemWriter<Person> writer) {
		return new StepBuilder("step1", jobRepository)
			.<Person, Person> chunk(10, transactionManager)
			.reader(reader())
			.processor(processor())
			.writer(writer)
			.build();
	}
	
    @Bean
    public Step step2(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step2", jobRepository).<Person, PersonXml>chunk(10, transactionManager).reader(reader())
                .processor(processorXml()).writer(writerXml()).build();
    }
	
	
	// end::jobstep[]
	
	
/*
    @Bean
    public Job contactExportJob(JobRepository jobRepository, Step step2) {
        return new JobBuilder("Contract-Extract-Job", jobRepository).flow(step2).end().build();
    }

*/



}
