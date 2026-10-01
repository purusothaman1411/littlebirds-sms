package com.littlebirds.sms.dto;

/** One student's overall result, used by rankings, pass lists and class results. */
public record StudentResultRow(
        String studentId,
        String studentName,
        Integer standard,
        String group,
        int total,
        Double percentage,
        String grade,
        String result,
        boolean complete) {
}
