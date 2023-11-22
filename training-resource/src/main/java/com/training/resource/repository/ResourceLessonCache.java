package com.training.resource.repository;

public interface ResourceLessonCache {
    void saveResourceLessonTypeAndPath(Integer resourceId, String fileExtension, String filePath);
    String getResourceLessonTypeAndPath(Integer resourceId);
    void deleteResourceLessonTypeAndPath(Integer resourceId);
}
