package com.example.demoSpringBatch.mappers;


import com.example.demoSpringBatch.models.Order;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;


public class CustomerRowMapper implements RowMapper<Order> {
    private static final DateTimeFormatter DT_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");


    @Override
    public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
        //@// @formatter:off
        return Order.builder()
                .id(UUID.fromString(rs.getString("id")))
                .name(rs.getString("name"))
                .build();
        // @formatter:on
    }

}