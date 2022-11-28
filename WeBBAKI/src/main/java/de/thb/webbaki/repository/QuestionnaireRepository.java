package de.thb.webbaki.repository;

import de.thb.webbaki.entity.Questionnaire;
import de.thb.webbaki.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.RepositoryDefinition;

import java.time.LocalDateTime;
import java.util.List;

@RepositoryDefinition(domainClass = Questionnaire.class, idClass = Long.class)
public interface QuestionnaireRepository extends CrudRepository<Questionnaire, Long> {

    List<Questionnaire> findAllByUser(User user);
    Questionnaire findById(long id);
    boolean existsByUser_id(long id);
    boolean existsByIdAndUser_Id(long questId, long userId);
    Questionnaire findFirstByUser_IdOrderByIdDesc(long id);

    void deleteAllByUser(User user);

    void deleteQuestionnaireById(long id);
}
