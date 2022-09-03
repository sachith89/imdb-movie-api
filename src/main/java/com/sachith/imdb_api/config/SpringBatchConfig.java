package com.sachith.imdb_api.config;


import com.sachith.imdb_api.entity.NameBasics;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableBatchProcessing
@AllArgsConstructor
public class SpringBatchConfig {

    private JobBuilderFactory jobBuilderFactory;

    private StepBuilderFactory stepBuilderFactory;

    private DataSource dataSource;

//    @Bean
//    public ItemReadListener<NameBasics> readListener() {
//        return new ReadListener<NameBasics>();
//    }

    @Bean
    public StepExecutionListener stepListener() {
        return new StepListener();
    }

    @Bean
    public ItemWriteListener<NameBasics> writeListener() {
        return new WriteListener<NameBasics>();
    }

    @Bean
    public JobExecutionListener jobListener() {
        return new JobListener();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<NameBasics> nameBasicsItemReader() {
        FlatFileItemReader<NameBasics> itemReader = new FlatFileItemReader<>();
        itemReader.setResource(new FileSystemResource("C:\\Users\\N4427\\Desktop\\IMDB\\name.basics.tsv\\name.basics.tsv"));
        itemReader.setName("txtReader");
        itemReader.setLinesToSkip(1);
        itemReader.setLineMapper(nameBasicLineMapper());
        return itemReader;
    }

    private LineMapper<NameBasics> nameBasicLineMapper() {
        DefaultLineMapper<NameBasics> lineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter("\t");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames(
                "nconst",
                "primaryName",
                "birthYear",
                "deathYear",
                "primaryProfession",
                "knownForTitles"
        );

        BeanWrapperFieldSetMapper<NameBasics> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(NameBasics.class);

        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);

        return lineMapper;
    }

    @Bean
    public NameBasicsItemProcessor nameBasicsItemProcessor() {
        return new NameBasicsItemProcessor();
    }

    @Bean
    @StepScope
    public JdbcBatchItemWriter<NameBasics> nameBasicsItemWriter() {
        JdbcBatchItemWriter<NameBasics> writer = new JdbcBatchItemWriter<>();
        writer.setDataSource(dataSource);
        writer.setSql("INSERT INTO tbl_name_basics VALUES (:nconst, :birthYear, :deathYear, :knownForTitles, :primaryName, :primaryProfession)");
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        writer.afterPropertiesSet();

        return writer;
    }

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(15);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(30);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setThreadNamePrefix("Batch-");
        return executor;
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("csv-step").<NameBasics, NameBasics>chunk(1000)
                .listener(stepListener())
              //  .listener(readListener())
                .listener(writeListener())
                .reader(nameBasicsItemReader())
                .processor(nameBasicsItemProcessor())
                .writer(nameBasicsItemWriter())
                .faultTolerant()
                .skip(SQLException.class)
                .skipLimit(10)
                .taskExecutor(taskExecutor())
                .build();
    }

    @Bean
    public Job runJob() {
        return jobBuilderFactory.get("import-name-basics")
                .flow(step1()).end().build();
    }



}