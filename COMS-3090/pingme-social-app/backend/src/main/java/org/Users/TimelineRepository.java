package org.Users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
public interface TimelineRepository extends JpaRepository<Timeline, Long> {

    Timeline findByUsername(String username);

    @Transactional
    void deleteByUsername(String username);
}
