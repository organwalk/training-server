package com.training.learn.reposoty;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestCache {
    void cacheTestDateTime(Integer testId, String start, String end, Integer lessonId);
    String getTestTimeCache(Integer testId);
    void cacheTestStudent(Integer testId, List<Integer> studentIdList);
    List<Integer>getTestStudentCache(Integer testId);
}
