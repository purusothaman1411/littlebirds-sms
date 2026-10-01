package com.littlebirds.sms.dto;

/**
 * Result over ALL curriculum subjects. While marks are missing, complete is false, result is INCOMPLETE
 * and percentage and grade are null (they are omitted from the JSON).
 */
public record ResultSummary(
        int total,
        int maxTotal,
        Double percentage,
        String grade,
        String result,
        boolean complete) {
}
