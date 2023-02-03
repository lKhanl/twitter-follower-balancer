package dev.oguzhanercelik.service;

import dev.oguzhanercelik.model.entity.User;
import dev.oguzhanercelik.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
        try {
            return findByEmail(username);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public User findByEmail(String email) throws Exception {
        return userRepository.findByEmail(email).orElseThrow(() -> new Exception("User not found"));
    }

    public User save(User user) {
        return userRepository.save(user);
    }
}
