package com.entreprise.service;

import com.entreprise.model.Formation;
import com.entreprise.repository.FormationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class FormationService {
    
    @Autowired
    private FormationRepository formationRepository;
    
    public List<Formation> findAll() {
        return formationRepository.findAll();
    }
    
    public Optional<Formation> findById(Long id) {
        return formationRepository.findById(id);
    }
    
    public Formation save(Formation formation) {
        return formationRepository.save(formation);
    }
    
    public void deleteById(Long id) {
        formationRepository.deleteById(id);
    }
}
