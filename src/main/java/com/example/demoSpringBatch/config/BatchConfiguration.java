package com.example.demoSpringBatch.config;

import com.example.demoSpringBatch.mappers.CustomerRowMapper;
import com.example.demoSpringBatch.models.Order;
import jakarta.persistence.EntityManagerFactory;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.*;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.apache.commons.logging.Log;
import javax.sql.DataSource;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class BatchConfiguration {
    protected Log logger = LogFactory.getLog(this.getClass());
    @Autowired
    private DataSource dataSource;

    @Bean
    public JdbcCursorItemReader<Order> cursorItemReader(){
        JdbcCursorItemReader<Order> reader = new JdbcCursorItemReader<>();
        reader.setSql("SELECT id, name FROM OrderTest");
        reader.setDataSource(dataSource);
        reader.setFetchSize(2);
        reader.setRowMapper(new CustomerRowMapper());
        return reader;
    }

    @Bean
    public JdbcPagingItemReader<Order> pagingItemReader(){
        JdbcPagingItemReader<Order> reader = new JdbcPagingItemReader<>();
        reader.setDataSource(dataSource);
        reader.setFetchSize(10);
        reader.setRowMapper(new CustomerRowMapper());

        Map<String, org.springframework.batch.item.database.Order> sortKeys = new HashMap<>();
        sortKeys.put("id", org.springframework.batch.item.database.Order.ASCENDING);

        MySqlPagingQueryProvider queryProvider = new MySqlPagingQueryProvider();
        queryProvider.setSelectClause("select id, name");
        queryProvider.setFromClause("from OrderTest");
        queryProvider.setSortKeys(sortKeys);

        reader.setQueryProvider(queryProvider);

        return reader;
    }

//    @Bean
//    public ItemWriter<Order> customerItemWriter(){
//        return items -> {
//            System.out.println("total:"+ items.size());
//
////            throw new Exception();
//            for(Order c : items) {
//                System.out.println("debug? " + logger.isDebugEnabled());
//                System.out.println(c.toString());
//            }
//        };
//    }

    @Bean
    public ItemWriter<Order> writerExcel() {
        return items -> {
            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Orders");
                int rowNum = 0;
                for (Order order : items) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(order.getId().toString());
                    row.createCell(1).setCellValue(order.getName());
                }
                try (FileOutputStream fileOut = new FileOutputStream("output.xlsx")) {
                    workbook.write(fileOut);
                }
            } catch (IOException e) {
                throw new RuntimeException("Error writing to Excel file", e);
            }
        };
    }

    @Bean
    public Job importUserJob(JobRepository jobRepository,Step step1) {
        return new JobBuilder("MyJob", jobRepository)
                .incrementer(new RunIdIncrementer()).start(step1).build();
    }

    @Bean
    public Step step1(JobRepository jobRepository, DataSourceTransactionManager transactionManager) {
        return new StepBuilder("step1", jobRepository)
                .<Order, Order>chunk(5 , transactionManager)
                .reader(pagingItemReader())
                .writer(writerExcel())
                .build();
    }

    @Bean(name = "dataSourceTransactionManager")
    public DataSourceTransactionManager dataSourceTransactionManager(
            DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }


}



