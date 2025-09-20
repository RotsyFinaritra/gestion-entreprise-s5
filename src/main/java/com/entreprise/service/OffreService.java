package com.entreprise.service;

import com.entreprise.model.Offre;
import com.entreprise.repository.OffreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class OffreService {
    
    @Autowired
    private OffreRepository offreRepository;
    
    public List<Offre> findAll() {
        return offreRepository.findAll();
    }
    
    public Optional<Offre> findById(Long id) {
        return offreRepository.findById(id);
    }
    
    public Offre save(Offre offre) {
        return offreRepository.save(offre);
    }
    
    public void deleteById(Long id) {
        offreRepository.deleteById(id);
    }
    
    public List<Offre> findByPosteId(Long idPoste) {
        return offreRepository.findByPosteIdPoste(idPoste);
    }
}
