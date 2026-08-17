package org.Search;

import org.springframework.data.jpa.repository.JpaRepository;

public interface HashtagRepository extends JpaRepository<Hashtag, Long> {
    Hashtag findByTag(String tag);
}
