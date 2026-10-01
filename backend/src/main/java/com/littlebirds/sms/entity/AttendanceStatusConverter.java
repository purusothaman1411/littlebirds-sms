package com.littlebirds.sms.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class AttendanceStatusConverter implements AttributeConverter<AttendanceStatus, String> {

    @Override
    public String convertToDatabaseColumn(AttendanceStatus status) {
        return status == null ? null : status.getCode();
    }

    @Override
    public AttendanceStatus convertToEntityAttribute(String dbValue) {
        return dbValue == null ? null : AttendanceStatus.fromCode(dbValue);
    }
}
