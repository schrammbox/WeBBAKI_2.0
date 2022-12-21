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

    public List<MasterScenario> getAll(){return (List<MasterScenario>) masterScenarioRepository.findAll();}
    public List<MasterScenario> getAllOrderByWeightDesc(){return (List<MasterScenario>) masterScenarioRepository.findAllByOrderByWeightDesc();}
    public void saveAll(List<MasterScenario> masterScenarios){masterScenarioRepository.saveAll(masterScenarios);}
    public void deleteAll(List<MasterScenario> masterScenarios){masterScenarioRepository.deleteAll(masterScenarios);}
    public List<MasterScenario> getAllByActiveTrue(){return masterScenarioRepository.getByActive(true);}
    public List<MasterScenario> getAllByActiveTrueOrderByWeightDesc(){return masterScenarioRepository.getAllByActiveOrderByWeightDesc(true);}
    public void saveMasterScenario(MasterScenario masterScenario){masterScenarioRepository.save(masterScenario);}

}
