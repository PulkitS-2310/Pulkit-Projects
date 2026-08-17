package org.Chats;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long>
{

    Group findGroupById(int id);

    Group findGroupByGroupNameAndCreator(String groupName, String creator);

    @Transactional
    void deleteGroupById(int id);


}