// CommentServiceImpl
package com.workitech.s19.challenge.service;

import com.workitech.s19.challenge.dto.comment.CommentPatchDTO;
import com.workitech.s19.challenge.dto.comment.CommentRequestDTO;
import com.workitech.s19.challenge.dto.comment.CommentResponseDTO;
import com.workitech.s19.challenge.entity.Comment;
import com.workitech.s19.challenge.entity.Tweet;
import com.workitech.s19.challenge.entity.User;
import com.workitech.s19.challenge.exceptions.TwitterException;
import com.workitech.s19.challenge.exceptions.TwitterNotFoundException;
import com.workitech.s19.challenge.mapper.CommentMapper;
import com.workitech.s19.challenge.repository.CommentRepository;
import com.workitech.s19.challenge.repository.TweetRepository;
import com.workitech.s19.challenge.repository.UserRepository;
import com.workitech.s19.challenge.util.UserUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final TweetRepository tweetRepository;
    private final CommentMapper commentMapper;

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<CommentResponseDTO> getCommentsWithPostId(Long id) {
        return commentRepository.getCommentsWithPostId(id)
                .stream()
                .map(commentMapper::toCommentResponse)
                .toList();
    }

    @Override
    @Transactional
    public CommentResponseDTO create(Long tweetId, CommentRequestDTO commentRequestDTO) {
        Tweet currentTweet = tweetRepository.findById(tweetId)
                .orElseThrow(() -> new TwitterException("TwitNotFound", HttpStatus.NOT_FOUND));
        User currentUser = UserUtil.getUser(userRepository);
        Comment comment = commentRepository.save(commentMapper.toEntity(commentRequestDTO, currentUser, currentTweet));
        return commentMapper.toCommentResponse(comment);
    }

    @Override
    @Transactional
    public CommentResponseDTO update(Long commentId, CommentPatchDTO commentPatchDTO) {
        User currentUser = UserUtil.getUser(userRepository);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new TwitterNotFoundException("Comment bulunamadı id: " + commentId));
        if (!comment.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Only owner can update this comment");
        }
        comment = commentMapper.updateComment(comment, commentPatchDTO);
        comment = commentRepository.save(comment);
        return commentMapper.toCommentResponse(comment);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        User current = UserUtil.getUser(userRepository);
        Comment c = commentRepository.findById(id)
                .orElseThrow(() -> new TwitterNotFoundException("Comment bulunamadı id: " + id));
        boolean isCommentOwner = c.getUser().getId().equals(current.getId());
        boolean isTweetOwner = c.getTweet().getUser().getId().equals(current.getId());
        if (!isCommentOwner && !isTweetOwner) {
            throw new AccessDeniedException("Only comment or tweet owner can delete");
        }
        commentRepository.hardDeleteById(id);
    }
}
