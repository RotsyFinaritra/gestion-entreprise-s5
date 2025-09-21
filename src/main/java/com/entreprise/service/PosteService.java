package com.entreprise.service;

import com.entreprise.model.Poste;
import com.entreprise.model.User;
import com.entreprise.repository.PosteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class PosteService {
    
    @Autowired
    private PosteRepository posteRepository;
    
    public List<Poste> findAll() {
        return posteRepository.findAll();
    }
    
    public List<Poste> findByDepartement(User departement) {
        return posteRepository.findByDepartement(departement);
    }
    
    public List<Poste> findByDepartementId(Long departementId) {
        return posteRepository.findByDepartementIdUser(departementId);
    }
    
    public Optional<Poste> findById(Long id) {
        return posteRepository.findById(id);
    }
    
    public Poste save(Poste poste) {
        return posteRepository.save(poste);
    }
    
    public void deleteById(Long id) {
        posteRepository.deleteById(id);
    }
}
