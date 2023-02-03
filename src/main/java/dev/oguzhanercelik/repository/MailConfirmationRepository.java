package dev.oguzhanercelik.repository;

import dev.oguzhanercelik.model.entity.MailConfirmation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MailConfirmationRepository extends JpaRepository<MailConfirmation, Long> {

    MailConfirmation findByTokenAndMail(String token, String mail);

}
