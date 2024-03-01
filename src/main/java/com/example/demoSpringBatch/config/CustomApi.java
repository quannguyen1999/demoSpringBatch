//package com.example.demoSpringBatch.config;
//
//import com.example.demoSpringBatch.mappers.CustomerRowMapper;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import org.springframework.batch.core.repository.JobRepository;
//import org.springframework.batch.core.scope.context.StepContext;
//import org.springframework.batch.core.scope.context.StepSynchronizationManager;
//import org.springframework.batch.item.ItemReader;
//import org.springframework.batch.item.NonTransientResourceException;
//import org.springframework.batch.item.ParseException;
//import org.springframework.batch.item.UnexpectedInputException;
//import org.springframework.batch.item.database.JdbcPagingItemReader;
//import org.springframework.batch.item.database.Order;
//import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.jdbc.datasource.DataSourceTransactionManager;
//
//import javax.sql.DataSource;
//import java.sql.ResultSet;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.UUID;
//
//public class CustomApi implements ItemReader<Object[]> {
//    private final JdbcTemplate jdbcTemplate;
//    private final String sqlQuery;
//    private ResultSet resultSet;
//
//    public CustomApi(DataSource dataSource) {
//        this.jdbcTemplate = new JdbcTemplate(dataSource);
//        // Adjust the SQL query to fetch only records meeting specific criteria
//        this.sqlQuery = "SELECT * FROM your_table WHERE some_column = ?";
//    }
//
//    @Override
//    public Object[] read() throws Exception {
//        if (resultSet == null || !resultSet.next()) {
//            // Initialize or advance the ResultSet if it's null or there are no more rows
//            resultSet = jdbcTemplate.queryForRowSet(sqlQuery);
//            if (!resultSet.next()) {
//                return null; // No more data to read
//            }
//        }
//
//        // Process the current row from the ResultSet and return as an Object[]
//        Object[] rowData = new Object[resultSet.getMetaData().getColumnCount()];
//        for (int i = 0; i < rowData.length; i++) {
//            rowData[i] = resultSet.getObject(i + 1);
//        }
//        return rowData;
//    }
//}
