package de.thb.webbaki.service;

import de.thb.webbaki.controller.form.ResetPasswordForm;
import de.thb.webbaki.entity.PasswordResetToken;
import de.thb.webbaki.entity.User;
import de.thb.webbaki.mail.EmailSender;
import de.thb.webbaki.mail.Templates.UserNotifications.ResetPasswordNotification;
import de.thb.webbaki.repository.PasswordResetTokenRepository;
import de.thb.webbaki.service.Exceptions.EmailNotMatchingException;
import de.thb.webbaki.service.Exceptions.PasswordResetTokenExpired;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Stream;

@Service
public class PasswordResetTokenService {

    @Autowired
    private UserService userService;
    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;
    @Autowired
    private EmailSender emailSender;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public PasswordResetToken getByToken(String token) {
        return passwordResetTokenRepository.findByToken(token);
    }

    public PasswordResetToken getByUser(User user) {
        return passwordResetTokenRepository.findByUser(user);
    }

    public void deleteByExpiryDate(Date now) {
        passwordResetTokenRepository.deleteByExpiryDateLessThan(now);
    }

    public void deleteAllExpired(Date now) {
        passwordResetTokenRepository.deleteAllExpiredSince(now);
    }

    /**
     * Takes all Password reset token by expiry Date
     *
     * @param now as given Date
     * @return Stream PasswordResetToken
     */
    public Stream<PasswordResetToken> getAllByExpiryDate(Date now) {
        return passwordResetTokenRepository.findAllByExpiryDateLessThan(now);
    }

    /**
     * Creates new random Password Reset Token if user forgot his password
     *
     * @param user to link the right user
     * @throws EmailNotMatchingException
     */
    public void createPasswordResetToken(User user) throws EmailNotMatchingException {

        String token = UUID.randomUUID().toString();

        PasswordResetToken myToken = new PasswordResetToken(user, token);
        passwordResetTokenRepository.save(myToken);

        emailSender.send(user.getEmail(), ResetPasswordNotification.resetPasswordMail(user.getFirstName(), user.getLastName(), token));
    }

    /**
     * activating password reset process
     *
     * @param token to get fitting connection user - token
     * @param form  to enter new password
     * @return true if process successful or false if process did not succeed
     * @throws PasswordResetTokenExpired
     */
    public boolean resetUserPassword(String token, ResetPasswordForm form) throws PasswordResetTokenExpired {
        PasswordResetToken resetToken = getByToken(token);
        User user = userService.getUserByUsername(resetToken.getUser().getUsername());
        Date now = Date.from(Instant.now());

        if (form.getNewPassword().equals(form.getConfirmPassword()) && !resetToken.isConfirmed()) {
            if (now.before(resetToken.getExpiryDate())) {
                user.setPassword(passwordEncoder.encode(form.getNewPassword()));
                resetToken.setConfirmed(true);

                passwordResetTokenRepository.save(resetToken);
                userService.saveUser(user);

                return true;
            } else throw new PasswordResetTokenExpired("Token has been expired.");
        }
        return false;

    }
}
