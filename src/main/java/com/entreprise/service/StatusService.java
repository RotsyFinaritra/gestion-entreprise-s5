package com.entreprise.service;

import com.entreprise.model.Status;
import com.entreprise.repository.StatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class StatusService {
    
    @Autowired
    private StatusRepository statusRepository;
    
    public List<Status> findAll() {
        return statusRepository.findAll();
    }
    
    public Optional<Status> findById(Long id) {
        return statusRepository.findById(id);
    }
    
    public Optional<Status> findByNom(String nom) {
        try {
            return statusRepository.findByNom(nom);
        } catch (org.springframework.dao.IncorrectResultSizeDataAccessException e) {
            // En cas de doublons, retourner le premier trouvé
            List<Status> statusList = statusRepository.findAllByNom(nom);
            return statusList.isEmpty() ? Optional.empty() : Optional.of(statusList.get(0));
        }
    }
    
    public List<Status> findAllByNom(String nom) {
        return statusRepository.findAllByNom(nom);
    }
    
    public Status save(Status status) {
        return statusRepository.save(status);
    }
    
    public void deleteById(Long id) {
        statusRepository.deleteById(id);
    }
}
