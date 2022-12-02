package de.thb.webbaki.repository.snapshot;

import de.thb.webbaki.entity.snapshot.ReportScenario;
import de.thb.webbaki.entity.snapshot.Snapshot;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.RepositoryDefinition;

@RepositoryDefinition(domainClass = ReportScenario.class, idClass = Long.class)
public interface ReportScenarioRepository extends CrudRepository<ReportScenario, Long> {
}
