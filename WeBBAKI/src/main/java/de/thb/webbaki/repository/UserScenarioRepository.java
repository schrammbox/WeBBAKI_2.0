package de.thb.webbaki.repository;

import de.thb.webbaki.entity.Questionnaire;
import de.thb.webbaki.entity.Scenario;
import de.thb.webbaki.entity.UserScenario;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.RepositoryDefinition;

import java.util.List;

@RepositoryDefinition(domainClass = UserScenario.class, idClass = Long.class)
public interface UserScenarioRepository extends CrudRepository<UserScenario, Long> {
    List<UserScenario> findAllByQuestionnaire(Questionnaire questionnaire);
    UserScenario findByScenario(Scenario scenario);
    void deleteAllByQuestionnaire_Id(long id);
    boolean existsByScenario_IdAndQuestionnaire_Id(long scenarioId, long questId);
}
