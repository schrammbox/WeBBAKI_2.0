package de.thb.webbaki.service.snapshot;

import de.thb.webbaki.repository.snapshot.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReportService {
    @Autowired
    ReportRepository reportRepository;
}
