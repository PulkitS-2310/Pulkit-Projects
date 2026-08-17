package org.Chats;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface MessageRepository extends JpaRepository<Message, Long>{

    public Message getById(int id);

    @Transactional
    public void deleteById(int id);
}
