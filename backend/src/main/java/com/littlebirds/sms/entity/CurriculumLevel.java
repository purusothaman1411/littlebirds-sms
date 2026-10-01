package com.littlebirds.sms.entity;

public enum CurriculumLevel {
    STD_1_10,
    STD_11_12;

    public static CurriculumLevel forStandard(int standard) {
        return standard <= 10 ? STD_1_10 : STD_11_12;
    }
}
