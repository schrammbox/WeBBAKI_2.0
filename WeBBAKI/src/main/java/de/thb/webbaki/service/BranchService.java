package de.thb.webbaki.service;

import de.thb.webbaki.entity.Branch;
import de.thb.webbaki.repository.BranchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BranchService {
    @Autowired
    private BranchRepository branchRepository;

    public List<Branch> getAllBranches(){return branchRepository.findAll();}

    public Branch getBranchByName(String name){
        return branchRepository.getBranchByName(name);
    }
}
