package com.example.homeserviceprovider.mapper;

import com.example.homeserviceprovider.domain.comment.Comment;
import com.example.homeserviceprovider.dto.request.CommentRequestDTO;
import com.example.homeserviceprovider.dto.response.CommentResponseDTO;

import org.springframework.stereotype.Component;

@Component
public class CommentMapper {

    public CommentResponseDTO convertToDTO(Comment comment) {
        CommentResponseDTO commentResponseDTO = new CommentResponseDTO();
        commentResponseDTO.setComment(comment.getTextComment());
        commentResponseDTO.setScore(comment.getScore());
        return commentResponseDTO;
    }

    public Comment convertToComment(CommentRequestDTO dto) {
        return new Comment(
                dto.getScore(),
                dto.getComment()
        );
    }

}
