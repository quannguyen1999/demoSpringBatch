package com.example.demoSpringBatch.config;

import com.example.demoSpringBatch.mappers.CustomerRowMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.core.scope.context.StepSynchronizationManager;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Configuration
public class BatchConfiguration {
    protected Log logger = LogFactory.getLog(this.getClass());
    @Autowired
    private DataSource dataSource;


    @Bean
    public JdbcPagingItemReader<Object[]> pagingItemReader(JobRepository jobRepository, DataSourceTransactionManager transactionManager) throws JsonProcessingException {
        //TODO error reader only run one time
        // In your reader, you can access the JobExecution to retrieve JobParameters
        StepContext executionContext = StepSynchronizationManager.getContext();

        JdbcPagingItemReader<Object[]> reader = new JdbcPagingItemReader<>();
        reader.setDataSource(dataSource);
        reader.setFetchSize(10);
        reader.setRowMapper(new CustomerRowMapper());

        Map<String, org.springframework.batch.item.database.Order> sortKeys = new HashMap<>();
        sortKeys.put("id", org.springframework.batch.item.database.Order.ASCENDING);

        MySqlPagingQueryProvider queryProvider = new MySqlPagingQueryProvider();
        queryProvider.setSelectClause("select id, name");
        queryProvider.setFromClause("from " + UUID.randomUUID());
        queryProvider.setSortKeys(sortKeys);

        reader.setQueryProvider(queryProvider);

        return reader;
    }

    @Bean
    public Job importUserJob(JobRepository jobRepository,Step step1) {
        return new JobBuilder("MyJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(step1).build();
    }

    @Bean
    public Step step1(JobRepository jobRepository, DataSourceTransactionManager transactionManager) throws JsonProcessingException {
        return new StepBuilder("step1", jobRepository)
                .<Object[], Object[]>chunk(5, transactionManager)
                .reader(pagingItemReader(jobRepository, transactionManager))
                .writer(new StockDataExcelWriter())
                .faultTolerant()
                .build();
    }

    @Bean(name = "dataSourceTransactionManager")
    public DataSourceTransactionManager dataSourceTransactionManager(
            DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }


}



