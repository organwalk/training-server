package com.training.learn.reposoty;

import org.springframework.stereotype.Repository;

/**
 * by zhaozhifeng 2023-11-10
 */
@Repository
public interface LikeCache {
    void saveCommentLike(String key,String field,Integer value);

    void saveReplyLike(String key,String field,Integer value);

    Object getCommentLike(String key,String field);

    Object getReplyLike(String key,String field);

    void deleteCommentLike(String key,String field);

    void deleteReplyLike(String key,String field);


}
