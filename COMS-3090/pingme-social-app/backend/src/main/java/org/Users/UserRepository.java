package org.Users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
public interface UserRepository extends JpaRepository<User, String> {

    User findByUsername(String username);

    User findByEmail(String email);

    @Transactional
    void deleteByUsername(String username);
}
