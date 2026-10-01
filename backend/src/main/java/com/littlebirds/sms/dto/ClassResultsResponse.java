package com.littlebirds.sms.dto;

import java.util.List;

public record ClassResultsResponse(Integer standard, List<StudentResultRow> students) {
}
