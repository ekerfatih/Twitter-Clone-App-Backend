package com.workitech.s19.challenge.controller;

import com.workitech.s19.challenge.dto.comment.CommentPatchDTO;
import com.workitech.s19.challenge.dto.comment.CommentRequestDTO;
import com.workitech.s19.challenge.dto.comment.CommentResponseDTO;
import com.workitech.s19.challenge.service.CommentService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/comment")
@RequiredArgsConstructor
@RestController
public class CommentController {

    private final CommentService commentService;

    //delete update create getbyposid

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable @Positive Long id) {
        commentService.delete(id);
    }

    @GetMapping("/{id}")
    public List<CommentResponseDTO> getCommentsByTweetId(@PathVariable @Positive Long id) {
        return commentService.getCommentsWithPostId(id);
    }

    @PatchMapping("/{commentId}")
    public CommentResponseDTO updateComment(@PathVariable @Positive Long commentId, @RequestBody @Validated CommentPatchDTO commentPatchDTO) {
        return commentService.update(commentId, commentPatchDTO);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{tweetId}")
    public CommentResponseDTO createComment(@PathVariable @Positive Long tweetId, @RequestBody @Validated CommentRequestDTO commentRequestDTO) {
        return commentService.create(tweetId, commentRequestDTO);
    }
}
