package de.thb.webbaki.repository;

import de.thb.webbaki.entity.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.RepositoryDefinition;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RepositoryDefinition(domainClass = User.class, idClass = Long.class)
public interface UserRepository extends CrudRepository<User, Long> {
    User findByEmail(String email);
    Optional<User> findById(long id);
    User findByUsername(String username);
    
    List<User> findAllByBranch_Name(String branch);

    List<User> findAllByCompany(String company);

    List<User> findAllByBranch_Sector_Name(String sector);
    List<User> findByRoles_Name(String rolename);
    boolean existsByIdAndRoles_Name(long id, String rolename);

    @Transactional
    @Modifying
    @Query("UPDATE User a " +
            "SET a.enabled = TRUE WHERE a.username = ?1")
    int enableUser(String username);

    @Transactional
    @Modifying
    @Query("Update User b " + "SET b.enabled = FALSE WHERE b.username = ?1")
    int deactivateUser(String username);
}
