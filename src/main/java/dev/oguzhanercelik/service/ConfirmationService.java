package dev.oguzhanercelik.service;

import dev.oguzhanercelik.model.entity.MailConfirmation;
import dev.oguzhanercelik.repository.MailConfirmationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConfirmationService {

    private final MailConfirmationRepository mailConfirmationRepository;

    public MailConfirmation save(MailConfirmation mailConfirmation) {
        return mailConfirmationRepository.save(mailConfirmation);
    }

    public MailConfirmation findByTokenAndMail(String token, String mail) {
        return mailConfirmationRepository.findByTokenAndMail(token, mail);
    }

}
