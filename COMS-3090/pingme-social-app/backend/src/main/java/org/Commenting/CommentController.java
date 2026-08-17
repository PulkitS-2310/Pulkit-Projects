package org.Commenting;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.images.TimeFormatter;

@RestController
@RequestMapping("/comment")
@CrossOrigin(origins = "*")
public class CommentController {
    
    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TimeFormatter timeFormatter;
    
    // Create a new comment
    @PostMapping
    public ResponseEntity<Comment> createComment(@RequestBody Comment comment) {
        // Set timestamp if not provided
        if (comment.getTimestamp() == null) {
            comment.setTimestamp(timeFormatter.getFormatData());
        }
        Comment savedComment = commentRepository.save(comment);
        return ResponseEntity.ok(savedComment);
    }
    
    // Get all comments
    @GetMapping("/all")
    public ResponseEntity<List<Comment>> getAllComments() {
        List<Comment> comments = commentRepository.findAll();
        return ResponseEntity.ok(comments);
    }
    
    // Get comment by ID
    @GetMapping("/{id}")
    public ResponseEntity<Comment> getCommentById(@PathVariable int id) {
        Optional<Comment> comment = commentRepository.findById(id);
        return comment.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    // Get comments by username
    @GetMapping("/user/{username}")
    public ResponseEntity<List<Comment>> getCommentsByUsername(@PathVariable String username) {
        List<Comment> comments = commentRepository.findByUsername(username);
        return ResponseEntity.ok(comments);
    }
    
    // Search comments by content
    @GetMapping("/search")
    public ResponseEntity<List<Comment>> searchComments(@RequestParam String keyword) {
        List<Comment> comments = commentRepository.findByContentContaining(keyword);
        return ResponseEntity.ok(comments);
    }
    
    // Update a comment
    @PutMapping("/{id}")
    public ResponseEntity<Comment> updateComment(@PathVariable int id, @RequestBody Comment updatedComment) {
        return commentRepository.findById(id)
                .map(existingComment -> {
                    existingComment.setContent(updatedComment.getContent());
                    existingComment.setTimestamp(timeFormatter.getFormatData());
                    Comment savedComment = commentRepository.save(existingComment);
                    return ResponseEntity.ok(savedComment);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    // Delete a comment
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable int id) {
        return commentRepository.findById(id)
                .map(comment -> {
                    commentRepository.deleteById(id);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    // Delete all comments by username
    @DeleteMapping("/user/{username}")
    public ResponseEntity<Void> deleteCommentsByUsername(@PathVariable String username) {
        commentRepository.deleteByUsername(username);
        return ResponseEntity.ok().build();
    }
}
