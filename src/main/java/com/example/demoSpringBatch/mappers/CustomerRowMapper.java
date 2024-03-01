package com.example.demoSpringBatch.mappers;


import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;


public class CustomerRowMapper implements RowMapper<Object[]> {
    private static final DateTimeFormatter DT_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");


    @Override
    public Object[] mapRow(ResultSet rs, int rowNum) throws SQLException {
        // Determine the number of columns in the ResultSet
        int columnCount = rs.getMetaData().getColumnCount();

        // Create an array to hold the values of the ResultSet
        Object[] rowValues = new Object[columnCount];

        // Retrieve each column value from the ResultSet and store it in the array
        for (int i = 0; i < columnCount; i++) {
            rowValues[i] = rs.getObject(i + 1); // Columns are 1-indexed in ResultSet
        }

        return rowValues;
    }

}