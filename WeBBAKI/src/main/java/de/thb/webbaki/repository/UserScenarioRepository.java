package de.thb.webbaki.repository;

import de.thb.webbaki.entity.User;
import de.thb.webbaki.entity.UserScenario;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.RepositoryDefinition;

@RepositoryDefinition(domainClass = UserScenario.class, idClass = Long.class)
public interface UserScenarioRepository extends CrudRepository<UserScenario, Long> {
}
