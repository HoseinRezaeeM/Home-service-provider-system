package com.example.homeserviceprovider.repository;


import com.example.homeserviceprovider.base.repository.BaseEntityRepository;
import com.example.homeserviceprovider.domain.comment.Comment;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends BaseEntityRepository<Comment,Long> {

}
