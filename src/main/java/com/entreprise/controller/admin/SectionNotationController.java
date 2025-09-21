package com.entreprise.controller.admin;

import com.entreprise.model.SectionNoteEntretien;
import com.entreprise.model.Poste;
import com.entreprise.service.SectionNoteEntretienService;
import com.entreprise.service.PosteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/sections-notation")
public class SectionNotationController {

    @Autowired
    private SectionNoteEntretienService sectionService;

    @Autowired
    private PosteService posteService;

    @GetMapping("/list")
    public String listSections(Model model) {
        List<SectionNoteEntretien> sections = sectionService.findAll();
        
        // Grouper les sections par poste
        Map<Poste, List<SectionNoteEntretien>> sectionsParPoste = sections.stream()
                .collect(Collectors.groupingBy(SectionNoteEntretien::getPoste));
        
        model.addAttribute("sections", sections);
        model.addAttribute("sectionsParPoste", sectionsParPoste);
        model.addAttribute("pageTitle", "Sections de Notation");
        model.addAttribute("pageDescription", "Gestion des sections d'évaluation pour les entretiens");
        model.addAttribute("activeSection", "sections-notation");
        return "admin/section_notation/list";
    }

    @GetMapping("/new")
    public String newSection(Model model) {
        SectionNoteEntretien section = new SectionNoteEntretien();
        List<Poste> postes = posteService.findAll();
        
        model.addAttribute("section", section);
        model.addAttribute("postes", postes);
        model.addAttribute("pageTitle", "Nouvelle Section");
        model.addAttribute("pageDescription", "Créer une nouvelle section d'évaluation");
        model.addAttribute("activeSection", "sections-notation");
        return "admin/section_notation/form";
    }

    @PostMapping("/save")
    public String saveSection(@ModelAttribute SectionNoteEntretien section, 
                             BindingResult result, 
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la validation des données");
            return "redirect:/admin/sections-notation/new";
        }

        try {
            sectionService.save(section);
            redirectAttributes.addFlashAttribute("success", "Section sauvegardée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la sauvegarde : " + e.getMessage());
        }

        return "redirect:/admin/sections-notation/list";
    }

    @GetMapping("/edit/{id}")
    public String editSection(@PathVariable Long id, Model model) {
        Optional<SectionNoteEntretien> sectionOpt = sectionService.findById(id);
        if (sectionOpt.isEmpty()) {
            return "redirect:/admin/sections-notation/list";
        }

        List<Poste> postes = posteService.findAll();
        
        model.addAttribute("section", sectionOpt.get());
        model.addAttribute("postes", postes);
        model.addAttribute("pageTitle", "Modifier Section");
        model.addAttribute("pageDescription", "Modifier une section d'évaluation");
        model.addAttribute("activeSection", "sections-notation");
        return "admin/section_notation/form";
    }

    @PostMapping("/update/{id}")
    public String updateSection(@PathVariable Long id,
                               @ModelAttribute SectionNoteEntretien section, 
                               BindingResult result, 
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la validation des données");
            return "redirect:/admin/sections-notation/edit/" + id;
        }

        try {
            section.setIdSection(id);
            sectionService.save(section);
            redirectAttributes.addFlashAttribute("success", "Section modifiée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la modification : " + e.getMessage());
        }

        return "redirect:/admin/sections-notation/list";
    }

    @PostMapping("/delete/{id}")
    public String deleteSection(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            if (sectionService.existsById(id)) {
                sectionService.deleteById(id);
                redirectAttributes.addFlashAttribute("success", "Section supprimée avec succès");
            } else {
                redirectAttributes.addFlashAttribute("error", "Section non trouvée");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la suppression : " + e.getMessage());
        }

        return "redirect:/admin/sections-notation/list";
    }

    @GetMapping("/by-poste/{posteId}")
    public String sectionsByPoste(@PathVariable Long posteId, Model model) {
        Optional<Poste> posteOpt = posteService.findById(posteId);
        if (posteOpt.isEmpty()) {
            return "redirect:/admin/sections-notation/list";
        }

        List<SectionNoteEntretien> sections = sectionService.findByPosteId(posteId);
        
        model.addAttribute("sections", sections);
        model.addAttribute("poste", posteOpt.get());
        model.addAttribute("pageTitle", "Sections - " + posteOpt.get().getNom());
        model.addAttribute("pageDescription", "Sections d'évaluation pour le poste " + posteOpt.get().getNom());
        model.addAttribute("activeSection", "sections-notation");
        return "admin/section_notation/by_poste";
    }
}
