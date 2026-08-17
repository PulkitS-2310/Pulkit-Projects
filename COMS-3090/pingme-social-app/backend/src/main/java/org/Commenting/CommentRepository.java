package org.Commenting;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {
    
    // Find comments by username
    List<Comment> findByUsername(String username);
    
    // Find comments by content containing a keyword
    List<Comment> findByContentContaining(String keyword);
    
    // Delete comments by username
    @Transactional
    void deleteByUsername(String username);
    
    // Find comments by timestamp
    List<Comment> findByTimestamp(String timestamp);
}
