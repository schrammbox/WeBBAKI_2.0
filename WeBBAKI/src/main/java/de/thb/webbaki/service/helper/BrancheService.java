package de.thb.webbaki.service.helper;

import de.thb.webbaki.entity.Branche;
import de.thb.webbaki.repository.BrancheRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BrancheService {
    @Autowired
    private BrancheRepository brancheRepository;

    public List<Branche> getAllBranches(){return brancheRepository.findAll();}
}
