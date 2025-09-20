package com.entreprise.service;

import com.entreprise.model.Profil;
import com.entreprise.repository.ProfilRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ProfilService {
    
    @Autowired
    private ProfilRepository profilRepository;
    
    public List<Profil> findAll() {
        return profilRepository.findAll();
    }
    
    public Optional<Profil> findById(Long id) {
        return profilRepository.findById(id);
    }
    
    public List<Profil> findByPosteId(Long posteId) {
        return profilRepository.findByPosteIdPoste(posteId);
    }
    
    public Profil save(Profil profil) {
        return profilRepository.save(profil);
    }
    
    public void deleteById(Long id) {
        profilRepository.deleteById(id);
    }
}
