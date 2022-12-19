package de.thb.webbaki.service;

import de.thb.webbaki.entity.MasterScenario;
import de.thb.webbaki.repository.MasterScenarioRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Builder
@AllArgsConstructor
public class MasterScenarioService {
    private final MasterScenarioRepository masterScenarioRepository;

    public List<MasterScenario> getAllMasterScenarios(){return (List<MasterScenario>) masterScenarioRepository.findAll();}
    public void saveALlMasterScenarios(List<MasterScenario> masterScenarios){masterScenarioRepository.saveAll(masterScenarios);}
    public void deleteAllMasterScenarios(List<MasterScenario> masterScenarios){masterScenarioRepository.deleteAll(masterScenarios);}
    public List<MasterScenario> getAllMasterScenariosByActiveTrue(){return masterScenarioRepository.getByActive(true);}

}
