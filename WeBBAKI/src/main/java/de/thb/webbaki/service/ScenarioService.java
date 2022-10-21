package de.thb.webbaki.service;


import de.thb.webbaki.entity.Scenario;
import de.thb.webbaki.repository.ScenarioRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Builder
@AllArgsConstructor
public class ScenarioService {
    private final ScenarioRepository scenarioRepository;

    public List<Scenario> getAllScenarios(){return (List<Scenario>) scenarioRepository.findAll();}

    public void addScenario(Scenario s){scenarioRepository.save(s); }

    public long getNumberOfScenarios(){return scenarioRepository.count();}
}
