package com.example.homeserviceprovider.service;

import com.example.homeserviceprovider.base.service.BaseEntityService;
import com.example.homeserviceprovider.domain.comment.Comment;


import java.util.List;
import java.util.Optional;

public interface CommentService extends BaseEntityService<Comment, Long> {

    @Override
    void save(Comment comment);

    @Override
    void delete(Comment comment);

    @Override
    Optional<Comment> findById(Long aLong);

    @Override
    List<Comment> findAll();
}
