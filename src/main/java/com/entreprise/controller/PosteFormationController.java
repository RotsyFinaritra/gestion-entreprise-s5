package com.entreprise.controller;

import com.entreprise.model.PosteFormation;
import com.entreprise.service.PosteFormationService;
import com.entreprise.service.PosteService;
import com.entreprise.service.FormationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/admin/poste_formation")
public class PosteFormationController {
    
    @Autowired
    private PosteFormationService posteFormationService;
    
    @Autowired
    private PosteService posteService;
    
    @Autowired
    private FormationService formationService;
    
    @GetMapping("/list")
    public String listPosteFormations(Model model) {
        model.addAttribute("posteFormations", posteFormationService.findAll());
        model.addAttribute("postes", posteService.findAll());
        model.addAttribute("formations", formationService.findAll());
        model.addAttribute("activeSection", "poste-formations");
        return "poste_formation/list";
    }
    
    @GetMapping("/form")
    public String newPosteFormation(Model model) {
        model.addAttribute("posteFormation", new PosteFormation());
        model.addAttribute("postes", posteService.findAll());
        model.addAttribute("formations", formationService.findAll());
        model.addAttribute("activeSection", "poste-formations");
        return "poste_formation/form";
    }
    
    @PostMapping("/save")
    public String savePosteFormation(@ModelAttribute PosteFormation posteFormation, 
                                      RedirectAttributes redirectAttributes) {
        try {
            // Vérifier si la relation existe déjà (uniquement pour les nouvelles créations)
            if (posteFormation.getIdPosteFormation() == null && 
                posteFormationService.existsByPosteAndFormation(
                    posteFormation.getPoste().getIdPoste(), 
                    posteFormation.getFormation().getIdFormation())) {
                redirectAttributes.addFlashAttribute("error", 
                    "Cette relation poste-formation existe déjà.");
                return "redirect:/admin/poste_formation/form";
            }
            
            PosteFormation saved = posteFormationService.save(posteFormation);
            
            if (posteFormation.getIdPosteFormation() == null) {
                redirectAttributes.addFlashAttribute("message", 
                    "Association créée avec succès : " + saved.getPoste().getNom() + 
                    " - " + saved.getFormation().getNom());
            } else {
                redirectAttributes.addFlashAttribute("message", 
                    "Association modifiée avec succès.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Erreur lors de la sauvegarde: " + e.getMessage());
        }
        return "redirect:/admin/poste_formation/list";
    }
    
    @GetMapping("/edit/{id}")
    public String editPosteFormation(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<PosteFormation> optionalPosteFormation = posteFormationService.findById(id);
        if (optionalPosteFormation.isPresent()) {
            model.addAttribute("posteFormation", optionalPosteFormation.get());
            model.addAttribute("postes", posteService.findAll());
            model.addAttribute("formations", formationService.findAll());
            model.addAttribute("activeSection", "poste-formations");
            return "poste_formation/form";
        } else {
            redirectAttributes.addFlashAttribute("error", "Association non trouvée");
            return "redirect:/admin/poste_formation/list";
        }
    }
    
    @PostMapping("/update/{id}")
    public String updatePosteFormation(@PathVariable Long id, 
                                        @ModelAttribute PosteFormation posteFormation, 
                                        RedirectAttributes redirectAttributes) {
        try {
            posteFormation.setIdPosteFormation(id);
            posteFormationService.save(posteFormation);
            redirectAttributes.addFlashAttribute("success", 
                "Relation poste-formation modifiée avec succès.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Erreur lors de la modification: " + e.getMessage());
        }
        return "redirect:/poste-formations";
    }
    
    @PostMapping("/delete/{id}")
    @ResponseBody
    public String deletePosteFormation(@PathVariable Long id) {
        try {
            posteFormationService.deleteById(id);
            return "success";
        } catch (Exception e) {
            return "error";
        }
    }
    
    @GetMapping("/by-poste/{posteId}")
    public String getFormationsByPoste(@PathVariable Long posteId, Model model) {
        model.addAttribute("posteFormations", posteFormationService.findByPosteId(posteId));
        model.addAttribute("poste", posteService.findById(posteId).orElse(null));
        model.addAttribute("activeSection", "poste-formations");
        return "poste_formation/by_poste";
    }
    
    @GetMapping("/by-formation/{formationId}")
    public String getPostesByFormation(@PathVariable Long formationId, Model model) {
        model.addAttribute("posteFormations", posteFormationService.findByFormationId(formationId));
        model.addAttribute("formation", formationService.findById(formationId).orElse(null));
        model.addAttribute("activeSection", "poste-formations");
        return "poste_formation/by_formation";
    }
}
