package com.entreprise.service;

import com.entreprise.model.PosteCompetance;
import com.entreprise.repository.PosteCompetanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PosteCompetanceService {
    
    @Autowired
    private PosteCompetanceRepository posteCompetanceRepository;
    
    public List<PosteCompetance> findAll() {
        return posteCompetanceRepository.findAll();
    }
    
    public Optional<PosteCompetance> findById(Long id) {
        return posteCompetanceRepository.findById(id);
    }
    
    public PosteCompetance save(PosteCompetance posteCompetance) {
        return posteCompetanceRepository.save(posteCompetance);
    }
    
    public void deleteById(Long id) {
        posteCompetanceRepository.deleteById(id);
    }
    
    public List<PosteCompetance> findByPosteId(Long idPoste) {
        return posteCompetanceRepository.findByPosteIdPoste(idPoste);
    }
    
    public List<PosteCompetance> findByCompetanceId(Long idCompetance) {
        return posteCompetanceRepository.findByCompetanceIdCompetance(idCompetance);
    }
    
    public boolean existsByPosteAndCompetance(Long idPoste, Long idCompetance) {
        return posteCompetanceRepository.existsByPosteIdPosteAndCompetanceIdCompetance(idPoste, idCompetance);
    }
    
    @Transactional
    public void deleteByPosteAndCompetance(Long idPoste, Long idCompetance) {
        posteCompetanceRepository.deleteByPosteIdPosteAndCompetanceIdCompetance(idPoste, idCompetance);
    }
}
