package com.littlebirds.sms.dto;

import java.util.List;

public record StudentMarksResponse(
        String studentId,
        String studentName,
        Integer standard,
        String group,
        List<SubjectMarkDto> marks,
        List<String> missingSubjects,
        ResultSummary summary) {
}
