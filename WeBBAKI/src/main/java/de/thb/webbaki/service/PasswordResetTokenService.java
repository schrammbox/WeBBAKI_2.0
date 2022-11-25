package de.thb.webbaki.service;

import de.thb.webbaki.entity.PasswordResetToken;
import de.thb.webbaki.entity.User;
import de.thb.webbaki.repository.PasswordResetTokenRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;
import java.util.stream.Stream;

@Service
public class PasswordResetTokenService {

    private PasswordResetTokenRepository passwordResetTokenRepository;

    public PasswordResetToken getByToken(String token) {
        return passwordResetTokenRepository.findByToken(token);
    }

    public PasswordResetToken getByUser(User user) {
        return passwordResetTokenRepository.findByUser(user);
    }

    public Stream<PasswordResetToken> getAllByExpiryDate(Date now) {
        return passwordResetTokenRepository.findAllByExpiryDateLessThan(now);
    }

    public void deleteByExpiryDate(Date now) {
        passwordResetTokenRepository.deleteByExpiryDateLessThan(now);
    }

    public void deleteAllExpired(Date now) {
        passwordResetTokenRepository.deleteAllExpiredSince(now);
    }

    public void createPasswordResetToken(User user, String token){

        PasswordResetToken myToken = new PasswordResetToken(user, token);
        passwordResetTokenRepository.save(myToken);
    }


}
