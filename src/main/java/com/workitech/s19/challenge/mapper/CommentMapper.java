package com.workitech.s19.challenge.mapper;

import com.workitech.s19.challenge.dto.comment.CommentPatchDTO;
import com.workitech.s19.challenge.dto.comment.CommentRequestDTO;
import com.workitech.s19.challenge.dto.comment.CommentResponseDTO;
import com.workitech.s19.challenge.entity.Comment;
import com.workitech.s19.challenge.entity.Tweet;
import com.workitech.s19.challenge.entity.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CommentMapper {

    public Comment toEntity(CommentRequestDTO commentRequestDTO, User user, Tweet tweet) {
        Comment comment = new Comment();
        comment.setCommentText(commentRequestDTO.commentText());
        comment.setTweet(tweet);
        comment.setUser(user);
        comment.setTime(LocalDateTime.now());
        return comment;
    }

    public CommentResponseDTO toCommentResponse(Comment comment) {
        return new CommentResponseDTO(
                comment.getId(),
                comment.getCommentText(),
                comment.getUser().getUsername(),
                comment.getTime()
        );
    }

    public Comment updateComment(Comment commentToUpdate, CommentPatchDTO commentPatchDTO) {
        if (commentToUpdate.getCommentText() != null) {
            commentToUpdate.setCommentText(commentPatchDTO.commentText());
        }
        return commentToUpdate;
    }

}
