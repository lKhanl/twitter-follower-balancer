package dev.oguzhanercelik.repository;

import dev.oguzhanercelik.model.TwitterStatus;
import dev.oguzhanercelik.model.entity.TwitterUser;
import dev.oguzhanercelik.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TwitterUserRepository extends JpaRepository<TwitterUser, Long> {

    Optional<TwitterUser> findByUsernameAndUser(String username, User user);

    List<TwitterUser> findByUserAndStatus(User user, TwitterStatus status);

    void deleteByUser(User u);

}
