package de.thb.webbaki.service.snapshot;

import de.thb.webbaki.repository.snapshot.ReportRepository;
import de.thb.webbaki.repository.snapshot.ReportScenarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReportScenarioService {
    @Autowired
    ReportScenarioRepository reportScenarioRepository;
}
