package de.thb.webbaki.service;

import de.thb.webbaki.entity.Sector;
import de.thb.webbaki.repository.SectorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SectorService {
    @Autowired
    private SectorRepository sectorRepository;

    public List<Sector> getAllSectors(){return sectorRepository.findAll();}
}
