package com.littlebirds.sms.service;

import java.util.Collection;

import com.littlebirds.sms.dto.ResultSummary;

/**
 * The single home of the console app's result rules (previously copy-pasted in three classes):
 * percentage = total / (subjects x 100), grade bands, and pass = every subject at least 35.
 */
public final class ResultCalculator {

    public static final int PASS_MARK = 35;
    public static final int MAX_MARK = 100;

    public static final String PASS = "PASS";
    public static final String FAIL = "FAIL";
    public static final String INCOMPLETE = "INCOMPLETE";

    private ResultCalculator() {
    }

    public static String grade(double percentage) {
        if (percentage >= 90) return "A+";
        if (percentage >= 80) return "A";
        if (percentage >= 70) return "B";
        if (percentage >= 60) return "C";
        if (percentage >= 50) return "D";
        return "F";
    }

    /**
     * @param enteredMarks    marks entered so far for subjects in the student's curriculum
     * @param requiredSubjects number of subjects in the student's curriculum
     */
    public static ResultSummary summarize(Collection<Integer> enteredMarks, int requiredSubjects) {
        int total = 0;
        boolean allPassed = true;
        for (int mark : enteredMarks) {
            total += mark;
            if (mark < PASS_MARK) {
                allPassed = false;
            }
        }

        int maxTotal = requiredSubjects * MAX_MARK;
        boolean complete = requiredSubjects > 0 && enteredMarks.size() == requiredSubjects;

        if (!complete) {
            return new ResultSummary(total, maxTotal, null, null, INCOMPLETE, false);
        }

        double percentage = total * 100.0 / maxTotal;
        return new ResultSummary(total, maxTotal, round2(percentage), grade(percentage),
                allPassed ? PASS : FAIL, true);
    }

    public static double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
