package de.thb.webbaki.repository;

import de.thb.webbaki.entity.PasswordResetToken;
import de.thb.webbaki.entity.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.RepositoryDefinition;

import java.util.Date;
import java.util.stream.Stream;

@RepositoryDefinition(domainClass = PasswordResetTokenRepository.class, idClass = Long.class)
public interface PasswordResetTokenRepository extends CrudRepository<PasswordResetToken, Long> {

    PasswordResetToken findByToken(String token);

    PasswordResetToken findByUser(User user);

    Stream<PasswordResetToken> findAllByExpiryDateLessThan(Date now);

    void deleteByExpiryDateLessThan(Date now);

    @Modifying
    @Query("delete from PasswordResetToken t where t.expiryDate<= ?1")
    void deleteAllExpiredSince(Date now);
}
