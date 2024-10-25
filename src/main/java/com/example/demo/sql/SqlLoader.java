package com.example.demo.sql;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SqlLoader {
    @Value("${app.schema.name}")
    private String schemaName;

    public String GET_LATEST_RATES() {
        return String.format("SELECT * FROM %s.rate WHERE currency_name = :currency AND delete_flag = false", schemaName);
    }
}
