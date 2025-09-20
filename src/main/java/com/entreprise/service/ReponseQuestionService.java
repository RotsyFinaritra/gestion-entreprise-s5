package com.entreprise.service;

import com.entreprise.model.ReponseQuestion;
import com.entreprise.repository.ReponseQuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ReponseQuestionService {
    
    @Autowired
    private ReponseQuestionRepository reponseQuestionRepository;
    
    public List<ReponseQuestion> findAll() {
        return reponseQuestionRepository.findAll();
    }
    
    public Optional<ReponseQuestion> findById(Long id) {
        return reponseQuestionRepository.findById(id);
    }
    
    public ReponseQuestion save(ReponseQuestion reponseQuestion) {
        return reponseQuestionRepository.save(reponseQuestion);
    }
    
    public void deleteById(Long id) {
        reponseQuestionRepository.deleteById(id);
    }
    
    public List<ReponseQuestion> findByQuestionId(Long idQuestion) {
        return reponseQuestionRepository.findByQuestionIdQuestion(idQuestion);
    }
}
