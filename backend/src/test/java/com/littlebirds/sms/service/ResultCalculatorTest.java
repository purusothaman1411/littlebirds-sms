package com.littlebirds.sms.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.littlebirds.sms.dto.ResultSummary;

class ResultCalculatorTest {

    @Test
    void gradeBandsMatchConsoleApp() {
        assertEquals("A+", ResultCalculator.grade(90));
        assertEquals("A", ResultCalculator.grade(89.99));
        assertEquals("A", ResultCalculator.grade(80));
        assertEquals("B", ResultCalculator.grade(70));
        assertEquals("C", ResultCalculator.grade(60));
        assertEquals("D", ResultCalculator.grade(50));
        assertEquals("F", ResultCalculator.grade(49.99));
    }

    @Test
    void completeAndPassed() {
        ResultSummary s = ResultCalculator.summarize(List.of(80, 70, 90, 60, 100), 5);
        assertEquals(400, s.total());
        assertEquals(500, s.maxTotal());
        assertEquals(80.0, s.percentage());
        assertEquals("A", s.grade());
        assertEquals("PASS", s.result());
        assertTrue(s.complete());
    }

    @Test
    void oneSubjectBelow35FailsEvenWithHighPercentage() {
        ResultSummary s = ResultCalculator.summarize(List.of(100, 100, 100, 100, 34), 5);
        assertEquals("FAIL", s.result());
        assertEquals(35, ResultCalculator.PASS_MARK);
        assertEquals("PASS", ResultCalculator.summarize(List.of(35, 35, 35, 35, 35), 5).result());
    }

    @Test
    void missingSubjectsMakeResultIncomplete() {
        // the legacy app showed "PASS, 40%" after a single mark; now it is INCOMPLETE
        ResultSummary s = ResultCalculator.summarize(List.of(40), 5);
        assertFalse(s.complete());
        assertEquals("INCOMPLETE", s.result());
        assertNull(s.percentage());
        assertNull(s.grade());
        assertEquals(40, s.total());
    }

    @Test
    void humanitiesUsesFiveSubjects() {
        ResultSummary s = ResultCalculator.summarize(List.of(50, 50, 50, 50, 50), 5);
        assertEquals(50.0, s.percentage());
        assertEquals("D", s.grade());
    }
}
