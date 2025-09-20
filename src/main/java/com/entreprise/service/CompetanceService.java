package com.entreprise.service;

import com.entreprise.model.Competance;
import com.entreprise.repository.CompetanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CompetanceService {
    
    @Autowired
    private CompetanceRepository competanceRepository;
    
    public List<Competance> findAll() {
        return competanceRepository.findAll();
    }
    
    public Optional<Competance> findById(Long id) {
        return competanceRepository.findById(id);
    }
    
    public Competance save(Competance competance) {
        return competanceRepository.save(competance);
    }
    
    public void deleteById(Long id) {
        competanceRepository.deleteById(id);
    }
}
