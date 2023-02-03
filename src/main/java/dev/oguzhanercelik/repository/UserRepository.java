package dev.oguzhanercelik.repository;

import dev.oguzhanercelik.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByEmail(String email);

	@Modifying
	@Transactional
	@Query(value = "delete from users where id = ?1", nativeQuery = true)
	void deleteByEmail(Long id);
	
}
