package com.entreprise.service;

import com.entreprise.model.Poste;
import com.entreprise.model.PosteCompetance;
import com.entreprise.model.PosteFormation;
import com.entreprise.model.Competance;
import com.entreprise.model.Formation;
import com.entreprise.model.User;
import com.entreprise.repository.PosteRepository;
import com.entreprise.repository.PosteCompetanceRepository;
import com.entreprise.repository.PosteFormationRepository;
import com.entreprise.repository.CompetanceRepository;
import com.entreprise.repository.FormationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class PosteService {
    
    @Autowired
    private PosteRepository posteRepository;
    
    @Autowired
    private PosteCompetanceRepository posteCompetanceRepository;
    
    @Autowired
    private PosteFormationRepository posteFormationRepository;
    
    @Autowired
    private CompetanceRepository competanceRepository;
    
    @Autowired
    private FormationRepository formationRepository;
    
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
    
    @Transactional
    public void associateCompetences(Long posteId, List<Long> competencesIds) {
        // Supprimer les associations existantes
        List<PosteCompetance> existingAssociations = posteCompetanceRepository.findByPosteIdPoste(posteId);
        for (PosteCompetance association : existingAssociations) {
            posteCompetanceRepository.delete(association);
        }
        
        // Créer les nouvelles associations
        Optional<Poste> posteOpt = posteRepository.findById(posteId);
        if (posteOpt.isPresent()) {
            Poste poste = posteOpt.get();
            for (Long competanceId : competencesIds) {
                Optional<Competance> competanceOpt = competanceRepository.findById(competanceId);
                if (competanceOpt.isPresent()) {
                    PosteCompetance association = new PosteCompetance();
                    association.setPoste(poste);
                    association.setCompetance(competanceOpt.get());
                    posteCompetanceRepository.save(association);
                }
            }
        }
    }
    
    @Transactional
    public void associateFormations(Long posteId, List<Long> formationsIds) {
        // Supprimer les associations existantes
        List<PosteFormation> existingAssociations = posteFormationRepository.findByPosteIdPoste(posteId);
        for (PosteFormation association : existingAssociations) {
            posteFormationRepository.delete(association);
        }
        
        // Créer les nouvelles associations
        Optional<Poste> posteOpt = posteRepository.findById(posteId);
        if (posteOpt.isPresent()) {
            Poste poste = posteOpt.get();
            for (Long formationId : formationsIds) {
                Optional<Formation> formationOpt = formationRepository.findById(formationId);
                if (formationOpt.isPresent()) {
                    PosteFormation association = new PosteFormation();
                    association.setPoste(poste);
                    association.setFormation(formationOpt.get());
                    posteFormationRepository.save(association);
                }
            }
        }
    }
}
