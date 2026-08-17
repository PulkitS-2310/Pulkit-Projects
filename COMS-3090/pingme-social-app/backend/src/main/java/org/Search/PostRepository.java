package org.Search;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByUsername(String username);
    
    List<Post> findByTitleContainingOrDescriptionContaining(String titleKeyword, String descriptionKeyword);

    //Post findById(Long id);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.hashtags WHERE p.id = :id")
    Optional<Post> findByIdWithHashtags(@Param("id") Long id);

    List<Post> findByTitleContaining(String title);

    @Transactional
    void deleteByUsername(String username);
    
    @Query("SELECT p FROM Post p JOIN p.hashtags h WHERE h.tag LIKE %:tag%")
    List<Post> findByHashtagsTagContaining(@Param("tag") String tag);
    
    List<Post> findAllByOrderByIdDesc();

    void deleteById(int id);
}
