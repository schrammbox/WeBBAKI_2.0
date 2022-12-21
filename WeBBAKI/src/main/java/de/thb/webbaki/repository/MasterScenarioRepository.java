package de.thb.webbaki.repository;

import de.thb.webbaki.entity.MasterScenario;
import de.thb.webbaki.entity.Scenario;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.RepositoryDefinition;

import java.util.List;
import java.util.Optional;

@RepositoryDefinition(domainClass = MasterScenario.class, idClass = Long.class)
public interface MasterScenarioRepository extends CrudRepository<MasterScenario, Long> {

    List<MasterScenario> getByActive(boolean active);
    List<MasterScenario> getAllByActiveOrderByWeightDesc(boolean active);
    List<MasterScenario> findAllByOrderByWeightDesc();
}
