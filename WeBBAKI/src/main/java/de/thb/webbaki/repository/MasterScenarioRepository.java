package de.thb.webbaki.repository;

import de.thb.webbaki.entity.MasterScenario;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.RepositoryDefinition;

import java.util.List;

@RepositoryDefinition(domainClass = MasterScenario.class, idClass = Long.class)
public interface MasterScenarioRepository extends CrudRepository<MasterScenario, Long> {

    List<MasterScenario> getByActive(boolean active);
    List<MasterScenario> getAllByActiveOrderByPositionInRow(boolean active);
    List<MasterScenario> findAllByOrderByPositionInRow();
}
