package com.entreprise.service;

import com.entreprise.model.PosteFormation;
import com.entreprise.repository.PosteFormationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PosteFormationService {
    
    @Autowired
    private PosteFormationRepository posteFormationRepository;
    
    public List<PosteFormation> findAll() {
        return posteFormationRepository.findAll();
    }
    
    public Optional<PosteFormation> findById(Long id) {
        return posteFormationRepository.findById(id);
    }
    
    public PosteFormation save(PosteFormation posteFormation) {
        return posteFormationRepository.save(posteFormation);
    }
    
    public void deleteById(Long id) {
        posteFormationRepository.deleteById(id);
    }
    
    public List<PosteFormation> findByPosteId(Long idPoste) {
        return posteFormationRepository.findByPosteIdPoste(idPoste);
    }
    
    public List<PosteFormation> findByFormationId(Long idFormation) {
        return posteFormationRepository.findByFormationIdFormation(idFormation);
    }
    
    public boolean existsByPosteAndFormation(Long idPoste, Long idFormation) {
        return posteFormationRepository.existsByPosteIdPosteAndFormationIdFormation(idPoste, idFormation);
    }
    
    @Transactional
    public void deleteByPosteAndFormation(Long idPoste, Long idFormation) {
        posteFormationRepository.deleteByPosteIdPosteAndFormationIdFormation(idPoste, idFormation);
    }
}
