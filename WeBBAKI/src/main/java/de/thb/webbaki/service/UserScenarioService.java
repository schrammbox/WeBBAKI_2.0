package de.thb.webbaki.service;

import de.thb.webbaki.entity.Questionnaire;
import de.thb.webbaki.entity.Scenario;
import de.thb.webbaki.entity.UserScenario;
import de.thb.webbaki.repository.UserScenarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserScenarioService {
    @Autowired
    private UserScenarioRepository userScenarioRepository;
    @Autowired
    private ScenarioService scenarioService;

    public List<UserScenario> getAllByQuestionnaire(Questionnaire questionnaire){return userScenarioRepository.findAllByQuestionnaire(questionnaire);}

    public UserScenario getUserScenarioFromScenario(Scenario scenario){return userScenarioRepository.findByScenario(scenario);}

    public void deleteAllUserScenariosByQuestionnaireId(long id){userScenarioRepository.deleteAllByQuestionnaire_Id(id);}

    public void saveUserScenario(UserScenario userScenario){
        userScenarioRepository.save(userScenario);
    }

    public void saveAllUserScenario(List<UserScenario> userScenarios){userScenarioRepository.saveAll(userScenarios);}

    public boolean existsUerScenarioByScenarioIdAndQuestionnaireId(long scenarioId, long questId){return userScenarioRepository.existsByScenario_IdAndQuestionnaire_Id(scenarioId, questId);}

    public UserScenario calculateUserScenarioAverage(List<UserScenario> userScenarios){
        float sumValue = 0;
        int dataAmountCounter = 0;
        for(UserScenario userScenario : userScenarios){
            //Dont use Unkown values for the average and the dataAmount
            if(userScenario.getThreatSituation() >= 0) {
                dataAmountCounter++;
                sumValue += userScenario.getThreatSituation();
            }
        }

        //If the length is equal to 0 all threatSituations were unknown(so the new one should be too --> -1)
        float averageValue = -1;
        if(dataAmountCounter != 0){
            averageValue = sumValue / dataAmountCounter;
        }

        return UserScenario.builder().threatSituation(averageValue).dataAmount(dataAmountCounter).build();
    }

    /**
     * Returns a map of UserScenarios with the ScenarioId as Key
     * @param userScenarios
     * @return
     */
    public Map<Long, UserScenario> getUserScenarioMapFromList(List<UserScenario> userScenarios){
        return userScenarios.stream().collect(Collectors.toMap((UserScenario userScenario) -> userScenario.getScenario().getId(), (UserScenario userScenario) -> userScenario));
    }

    public Map<Long, UserScenario> calculateUserScenarioAverageMap(List<Map<Long, UserScenario>> listOfUserScenarioMaps){
        Map<Long, UserScenario> averageUserScenarioMap = new HashMap();
        List<Scenario> scenarios = scenarioService.getAllScenarios();
        for(Scenario scenario : scenarios){
            List<UserScenario> userScenarios = new ArrayList<>();
            for(Map<Long, UserScenario> userScenarioMap : listOfUserScenarioMaps){
                if(userScenarioMap.containsKey(scenario.getId())) {
                    userScenarios.add(userScenarioMap.get(scenario.getId()));
                }else{
                    //Add empty UserScenario, if there is no one for this Scenario
                    userScenarios.add(UserScenario.builder().impact(-1).probability(-1).threatSituation(-1).scenario(scenario).build());
                }
            }
            UserScenario newUserScenario = calculateUserScenarioAverage(userScenarios);
            newUserScenario.setScenario(scenario);
            averageUserScenarioMap.put(scenario.getId(), newUserScenario);
        }
        return averageUserScenarioMap;
    }
}
