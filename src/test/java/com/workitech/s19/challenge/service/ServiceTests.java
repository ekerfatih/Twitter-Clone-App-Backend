package com.workitech.s19.challenge.service;

import com.workitech.s19.challenge.dto.comment.CommentPatchDTO;
import com.workitech.s19.challenge.dto.comment.CommentRequestDTO;
import com.workitech.s19.challenge.dto.comment.CommentResponseDTO;
import com.workitech.s19.challenge.dto.register.RegisterUser;
import com.workitech.s19.challenge.dto.register.ResponseUser;
import com.workitech.s19.challenge.entity.Comment;
import com.workitech.s19.challenge.entity.Role;
import com.workitech.s19.challenge.entity.Tweet;
import com.workitech.s19.challenge.entity.User;
import com.workitech.s19.challenge.exceptions.TwitterException;
import com.workitech.s19.challenge.exceptions.UniqueKeyTwitterException;
import com.workitech.s19.challenge.mapper.CommentMapper;
import com.workitech.s19.challenge.mapper.UserMapper;
import com.workitech.s19.challenge.repository.CommentRepository;
import com.workitech.s19.challenge.repository.RoleRepository;
import com.workitech.s19.challenge.repository.TweetRepository;
import com.workitech.s19.challenge.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceTests {

    @Mock
    CommentRepository commentRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    TweetRepository tweetRepository;
    @Mock
    RoleRepository roleRepository;
    @Mock
    CommentMapper commentMapper;
    @Mock
    UserMapper userMapper;
    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    CommentServiceImpl commentService;
    @InjectMocks
    AuthenticationServiceImpl authenticationService;

    User user = new User(1L, "feker", "f@f.com", "x", "Fatih", "Eker", null, new ArrayList<>(), new ArrayList<>());
    Tweet tweet = new Tweet(10L, "hello", user, LocalDateTime.now(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

    void withAuth(String username) {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(username, "x");
        SecurityContext sc = SecurityContextHolder.createEmptyContext();
        sc.setAuthentication(auth);
        SecurityContextHolder.setContext(sc);
    }

    void clearAuth() {
        SecurityContextHolder.clearContext();
    }

    @DisplayName("Kayıt olma")
    @Test
    void register_success() {
        RegisterUser dto = new RegisterUser("feker", "f@f.com", "1234", "Fatih", "Eker");
        Role role = new Role(1L, "USER");

        when(roleRepository.findByAuthority("USER")).thenReturn(Optional.of(role));
        when(userRepository.existsByUsername("feker")).thenReturn(false);
        when(userRepository.existsByEmail("f@f.com")).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(i -> {
            User u = i.getArgument(0);
            return new User(99L, u.getUsername(), u.getEmail(), u.getPassword(), u.getFirstName(), u.getLastName(), role, new ArrayList<>(), new ArrayList<>());
        });
        when(userMapper.toResponse(any(User.class), eq("Registered Successfully")))
                .thenReturn(new ResponseUser("feker", "f@f.com", "Registered Successfully"));

        ResponseUser result = authenticationService.register(dto);

        assertEquals("feker", result.username());
        verify(userRepository).save(any(User.class));
    }

    @DisplayName("Kayıt: username varsa hata")
    @Test
    void register_shouldThrow_whenUsernameExists() {
        RegisterUser dto = new RegisterUser("feker", "f@f", "1234", "Fatih", "Eker");
        when(userRepository.existsByUsername("feker")).thenReturn(true);
        assertThrows(UniqueKeyTwitterException.class, () -> authenticationService.register(dto));
        verify(userRepository, never()).save(any());
    }

    @DisplayName("Kayıt: email varsa hata")
    @Test
    void register_shouldThrow_whenEmailExists() {
        RegisterUser dto = new RegisterUser("feker", "f@f.com", "1234", "Fatih", "Eker");
        when(userRepository.existsByUsername("feker")).thenReturn(false);
        when(userRepository.existsByEmail("f@f.com")).thenReturn(true);
        assertThrows(UniqueKeyTwitterException.class, () -> authenticationService.register(dto));
        verify(userRepository, never()).save(any());
    }

    @DisplayName("create: başarılı (login şart)")
    @Test
    void create_success() {
        withAuth("feker");
        when(userRepository.findUserByUsername("feker")).thenReturn(Optional.of(user));
        when(tweetRepository.findById(10L)).thenReturn(Optional.of(tweet));
        CommentRequestDTO req = new CommentRequestDTO("Mock Comment", LocalDateTime.now());
        Comment toSave = new Comment(null, req.commentText(), tweet, req.time(), user);
        when(commentMapper.toEntity(eq(req), eq(user), eq(tweet))).thenReturn(toSave);
        Comment saved = new Comment(100L, "Mock Comment", tweet, req.time(), user);
        when(commentRepository.save(toSave)).thenReturn(saved);
        when(commentMapper.toCommentResponse(saved))
                .thenReturn(new CommentResponseDTO(100L, "Mock Comment", "feker", saved.getTime()));

        CommentResponseDTO out = commentService.create(10L, req);

        assertEquals(100L, out.id());
        assertEquals("Mock Comment", out.commentText());
        verify(commentRepository).save(toSave);
        clearAuth();
    }

    @DisplayName("create: login yoksa AccessDeniedException")
    @Test
    void create_requiresLogin() {
        clearAuth();
        when(tweetRepository.findById(10L)).thenReturn(Optional.of(tweet));
        assertThrows(AccessDeniedException.class,
                () -> commentService.create(10L, new CommentRequestDTO("x", LocalDateTime.now())));
        verify(commentRepository, never()).save(any());
    }

    @DisplayName("create: tweet yoksa TwitterException")
    @Test
    void create_tweetNotFound() {
        when(tweetRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(TwitterException.class,
                () -> commentService.create(999L, new CommentRequestDTO("x", LocalDateTime.now())));
        verify(commentRepository, never()).save(any());
    }

    @DisplayName("update: sahibi güncelleyebilir (login şart)")
    @Test
    void update_success_owner() {
        withAuth("feker");
        when(userRepository.findUserByUsername("feker")).thenReturn(Optional.of(user));
        Comment existing = new Comment(100L, "old", tweet, LocalDateTime.now(), user);
        when(commentRepository.findById(100L)).thenReturn(Optional.of(existing));
        CommentPatchDTO patch = new CommentPatchDTO("new");
        Comment updated = new Comment(100L, "new", tweet, existing.getTime(), user);
        when(commentMapper.updateComment(existing, patch)).thenReturn(updated);
        when(commentMapper.toCommentResponse(updated))
                .thenReturn(new CommentResponseDTO(100L, "new", "feker", updated.getTime()));

        CommentResponseDTO out = commentService.update(100L, patch);

        assertEquals("new", out.commentText());
        clearAuth();
    }

    @DisplayName("update: login yoksa AccessDeniedException")
    @Test
    void update_requiresLogin() {
        clearAuth();
        assertThrows(AccessDeniedException.class,
                () -> commentService.update(100L, new CommentPatchDTO("x")));
        verify(commentRepository, never()).findById(anyLong());
    }

    @DisplayName("update: sahibi değilse AccessDeniedException")
    @Test
    void update_forbidden_notOwner() {
        withAuth("feker");
        when(userRepository.findUserByUsername("feker")).thenReturn(Optional.of(user));
        User other = new User(2L, "x", "x@x", "p", "a", "b", null, new ArrayList<>(), new ArrayList<>());
        Comment existing = new Comment(100L, "old", tweet, LocalDateTime.now(), other);
        when(commentRepository.findById(100L)).thenReturn(Optional.of(existing));
        assertThrows(AccessDeniedException.class, () -> commentService.update(100L, new CommentPatchDTO("new")));
        verify(commentMapper, never()).updateComment(any(), any());
        clearAuth();
    }

    @DisplayName("delete: yorum sahibi silebilir (login şart)")
    @Test
    void delete_byCommentOwner() {
        withAuth("feker");
        when(userRepository.findUserByUsername("feker")).thenReturn(Optional.of(user));
        Comment c = new Comment(100L, "y", tweet, LocalDateTime.now(), user);
        when(commentRepository.findById(100L)).thenReturn(Optional.of(c));
        commentService.delete(100L);
        verify(commentRepository).hardDeleteById(100L);
        clearAuth();
    }

    @DisplayName("delete: tweet sahibi silebilir (login şart)")
    @Test
    void delete_byTweetOwner() {
        withAuth("feker");
        when(userRepository.findUserByUsername("feker")).thenReturn(Optional.of(user));
        User commenter = new User(2L, "a", "a@a", "p", "n", "s", null, new ArrayList<>(), new ArrayList<>());
        Tweet tw = new Tweet(11L, "t", user, LocalDateTime.now(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        Comment c = new Comment(101L, "y", tw, LocalDateTime.now(), commenter);
        when(commentRepository.findById(101L)).thenReturn(Optional.of(c));
        commentService.delete(101L);
        verify(commentRepository).hardDeleteById(101L);
        clearAuth();
    }

    @DisplayName("delete: login yoksa AccessDeniedException")
    @Test
    void delete_requiresLogin() {
        clearAuth();
        assertThrows(AccessDeniedException.class, () -> commentService.delete(1L));
        verify(commentRepository, never()).hardDeleteById(anyLong());
    }

    @DisplayName("delete: ne yorum ne tweet sahibi ise AccessDeniedException")
    @Test
    void delete_forbidden() {
        withAuth("feker");
        when(userRepository.findUserByUsername("feker")).thenReturn(Optional.of(user));
        Tweet tw = new Tweet(12L, "t",
                new User(3L, "b", "b@b", "p", "n", "s", null, new ArrayList<>(), new ArrayList<>()),
                LocalDateTime.now(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        Comment c = new Comment(102L, "y", tw, LocalDateTime.now(),
                new User(2L, "a", "a@a", "p", "n", "s", null, new ArrayList<>(), new ArrayList<>()));
        when(commentRepository.findById(102L)).thenReturn(Optional.of(c));
        assertThrows(AccessDeniedException.class, () -> commentService.delete(102L));
        verify(commentRepository, never()).hardDeleteById(anyLong());
        clearAuth();
    }

    @DisplayName("getCommentsWithPostId")
    @Test
    void getCommentsWithPostId_mapsAll() {
        Comment c1 = new Comment(1L, "a", tweet, LocalDateTime.now(), user);
        Comment c2 = new Comment(2L, "b", tweet, LocalDateTime.now(), user);
        when(commentRepository.getCommentsWithPostId(10L)).thenReturn(List.of(c1, c2));
        when(commentMapper.toCommentResponse(c1)).thenReturn(new CommentResponseDTO(1L, "a", "feker", c1.getTime()));
        when(commentMapper.toCommentResponse(c2)).thenReturn(new CommentResponseDTO(2L, "b", "feker", c2.getTime()));

        List<CommentResponseDTO> out = commentService.getCommentsWithPostId(10L);

        assertThat(out).hasSize(2);
        assertThat(out.get(0).commentText()).isEqualTo("a");
    }
}
