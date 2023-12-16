package com.training.resource.repository;

/**
 * ResourceLesson缓存
 * by organwalk 2023-11-02
 */
public interface ResourceLessonCache {
    void saveResourceLessonTypeAndPath(Integer resourceId, String fileExtension, String filePath);
    String getResourceLessonTypeAndPath(Integer resourceId);
    void deleteResourceLessonTypeAndPath(Integer resourceId);
}
