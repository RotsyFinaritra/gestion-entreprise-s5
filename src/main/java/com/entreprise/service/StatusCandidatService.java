package com.entreprise.service;

import com.entreprise.model.StatusCandidat;
import com.entreprise.repository.StatusCandidatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class StatusCandidatService {
    
    @Autowired
    private StatusCandidatRepository statusCandidatRepository;
    
    public List<StatusCandidat> findAll() {
        return statusCandidatRepository.findAll();
    }
    
    public Optional<StatusCandidat> findById(Long id) {
        return statusCandidatRepository.findById(id);
    }
    
    public List<StatusCandidat> findByCandidatId(Long candidatId) {
        return statusCandidatRepository.findByCandidatIdCandidat(candidatId);
    }
    
    public StatusCandidat save(StatusCandidat statusCandidat) {
        return statusCandidatRepository.save(statusCandidat);
    }
    
    public void deleteById(Long id) {
        statusCandidatRepository.deleteById(id);
    }
}
