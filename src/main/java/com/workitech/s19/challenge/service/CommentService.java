package com.workitech.s19.challenge.service;

import com.workitech.s19.challenge.dto.comment.CommentPatchDTO;
import com.workitech.s19.challenge.dto.comment.CommentRequestDTO;
import com.workitech.s19.challenge.dto.comment.CommentResponseDTO;

import java.util.List;

public interface CommentService {

    List<CommentResponseDTO> getCommentsWithPostId(Long id);

    CommentResponseDTO create(Long postId, CommentRequestDTO commentRequestDTO);

    CommentResponseDTO update(Long commentId, CommentPatchDTO commentPatchDTO);

    void delete(Long id);
}
