package org.swu.repository;

import jakarta.annotation.Nullable;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.swu.object.User;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUserid(String userid);

    @Transactional
    @Modifying
    @Query("DELETE FROM User u WHERE u.userid = :userid")
    void deleteByUserid(@Param("userid") String userId);

    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.userid = :newUserId WHERE u.userid = :oldUserId")
    void updateUserId(@Param("newUserId") String newUserId, @Param("oldUserId") String oldUserId);
}
