package com.amarisTest.funds.helpers;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import org.joda.time.DateTime;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class DynamoDBDateConverter implements DynamoDBTypeConverter<String, DateTime> {

    private static final DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public String convert(DateTime object) {
        return object.toString(formatter);
    }

    @Override
    public DateTime unconvert(String object) {
        try {
            return formatter.parseDateTime(object);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid date format for DateTime: " + object, e);
        }
    }
}
