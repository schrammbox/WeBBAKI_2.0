package de.thb.webbaki.service;

import de.thb.webbaki.entity.Questionnaire;
import de.thb.webbaki.entity.Scenario;
import de.thb.webbaki.entity.User;
import de.thb.webbaki.entity.UserScenario;
import de.thb.webbaki.repository.UserScenarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserScenarioService {
    @Autowired
    private UserScenarioRepository userScenarioRepository;

    public List<UserScenario> getAllByQuestionnaire(Questionnaire questionnaire){return userScenarioRepository.findAllByQuestionnaire(questionnaire);}

    public UserScenario getUserScenarioFromScenario(Scenario scenario){return userScenarioRepository.findByScenario(scenario);}

    public void saveUserScenario(UserScenario userScenario){
        userScenarioRepository.save(userScenario);
    }
}
