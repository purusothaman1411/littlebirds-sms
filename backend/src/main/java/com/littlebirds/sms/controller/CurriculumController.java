package com.littlebirds.sms.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.littlebirds.sms.service.CurriculumService;

/** Lookups the frontend needs for forms: the 11-12 groups and the subjects of a class. */
@RestController
@RequestMapping("/api")
public class CurriculumController {

    private final CurriculumService curriculumService;

    public CurriculumController(CurriculumService curriculumService) {
        this.curriculumService = curriculumService;
    }

    @GetMapping("/groups")
    public List<String> groups() {
        return CurriculumService.GROUPS;
    }

    /** Subjects for a standard (and group for 11-12), e.g. /api/subjects?standard=11&group=Commerce */
    @GetMapping("/subjects")
    public List<String> subjects(@RequestParam(name = "standard") Integer standard,
                                 @RequestParam(name = "group", required = false) String group) {
        String normalizedGroup = curriculumService.normalizeGroup(standard, group);
        return curriculumService.subjectNamesFor(standard, normalizedGroup);
    }
}
