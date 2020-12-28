package com.stylefeng.guns.rest.common.convert;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class DateMapper {
    public String asString(LocalDateTime dateTime) {
        return dateTime != null ? DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(dateTime) : null;
    }
}
