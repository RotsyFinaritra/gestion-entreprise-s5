package com.entreprise.service;

import com.entreprise.model.Entretien;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class ContratPdfService {

    @Autowired
    private NoteEntretienService noteEntretienService;

    public byte[] genererContratEssai(Entretien entretien) throws Exception {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, baos);
            
            document.open();
            
            // Titre
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
            Paragraph title = new Paragraph("CONTRAT DE TRAVAIL À L'ESSAI", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(30f);
            document.add(title);
            
            // Date et lieu
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK);
            Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.BLACK);
            
            Paragraph datePlace = new Paragraph();
            datePlace.add(new Chunk("Fait à ", normalFont));
            datePlace.add(new Chunk(entretien.getOffre().getLocal().getNom(), boldFont));
            datePlace.add(new Chunk(", le ", normalFont));
            datePlace.add(new Chunk(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), boldFont));
            datePlace.setAlignment(Element.ALIGN_RIGHT);
            datePlace.setSpacingAfter(20f);
            document.add(datePlace);
            
            // Parties contractantes
            document.add(new Paragraph("ENTRE LES SOUSSIGNÉS :", boldFont));
            document.add(new Paragraph(" ", normalFont)); // Espace
            
            // L'employeur
            Paragraph employeur = new Paragraph();
            employeur.add(new Chunk("L'EMPLOYEUR :\n", boldFont));
            employeur.add(new Chunk("Société : ", normalFont));
            employeur.add(new Chunk("GESTION ENTREPRISE S5\n", boldFont));
            employeur.add(new Chunk("Adresse : ", normalFont));
            employeur.add(new Chunk(entretien.getOffre().getLocal().getNom() + "\n", normalFont));
            employeur.add(new Chunk("Représentée par : ", normalFont));
            employeur.add(new Chunk("Monsieur le Directeur des Ressources Humaines\n", boldFont));
            employeur.setSpacingAfter(15f);
            document.add(employeur);
            
            // Le salarié
            Paragraph salarie = new Paragraph();
            salarie.add(new Chunk("LE SALARIÉ :\n", boldFont));
            salarie.add(new Chunk("Nom : ", normalFont));
            salarie.add(new Chunk(entretien.getCandidat().getNom().toUpperCase() + "\n", boldFont));
            salarie.add(new Chunk("Prénom : ", normalFont));
            salarie.add(new Chunk(entretien.getCandidat().getPrenom() + "\n", boldFont));
            salarie.add(new Chunk("Email : ", normalFont));
            salarie.add(new Chunk(entretien.getCandidat().getEmail() + "\n", normalFont));
            if (entretien.getCandidat().getTel() != null) {
                salarie.add(new Chunk("Téléphone : ", normalFont));
                salarie.add(new Chunk(entretien.getCandidat().getTel() + "\n", normalFont));
            }
            salarie.setSpacingAfter(20f);
            document.add(salarie);
            
            // Il a été convenu ce qui suit
            document.add(new Paragraph("IL A ÉTÉ CONVENU CE QUI SUIT :", boldFont));
            document.add(new Paragraph(" ", normalFont)); // Espace
            
            // Articles du contrat
            // Article 1 - Poste
            Paragraph article1 = new Paragraph();
            article1.add(new Chunk("ARTICLE 1 - POSTE ET FONCTIONS\n", boldFont));
            article1.add(new Chunk("Le salarié est engagé en qualité de ", normalFont));
            article1.add(new Chunk(entretien.getOffre().getPoste().getNom(), boldFont));
            article1.add(new Chunk(".\n", normalFont));
            article1.add(new Chunk("Mission : ", normalFont));
            article1.add(new Chunk(entretien.getOffre().getMission() != null ? entretien.getOffre().getMission() : "À définir selon les besoins de l'entreprise", normalFont));
            article1.setSpacingAfter(15f);
            document.add(article1);
            
            // Article 2 - Lieu de travail
            Paragraph article2 = new Paragraph();
            article2.add(new Chunk("ARTICLE 2 - LIEU DE TRAVAIL\n", boldFont));
            article2.add(new Chunk("Le salarié exercera ses fonctions au sein de nos locaux situés à ", normalFont));
            article2.add(new Chunk(entretien.getOffre().getLocal().getNom(), boldFont));
            article2.add(new Chunk(".", normalFont));
            article2.setSpacingAfter(15f);
            document.add(article2);
            
            // Article 3 - Période d'essai
            Paragraph article3 = new Paragraph();
            article3.add(new Chunk("ARTICLE 3 - PÉRIODE D'ESSAI\n", boldFont));
            article3.add(new Chunk("Le présent contrat est conclu pour une période d'essai de ", normalFont));
            article3.add(new Chunk("TROIS (3) MOIS", boldFont));
            article3.add(new Chunk(", renouvelable une fois pour une durée équivalente.\n", normalFont));
            article3.add(new Chunk("Cette période d'essai peut être interrompue à tout moment par l'une ou l'autre des parties sans préavis ni indemnité.", normalFont));
            article3.setSpacingAfter(15f);
            document.add(article3);
            
            // Article 4 - Date d'entrée en fonction
            Paragraph article4 = new Paragraph();
            article4.add(new Chunk("ARTICLE 4 - DATE D'ENTRÉE EN FONCTION\n", boldFont));
            article4.add(new Chunk("Le salarié prendra ses fonctions le ", normalFont));
            article4.add(new Chunk(LocalDateTime.now().plusDays(7).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), boldFont));
            article4.add(new Chunk(".", normalFont));
            article4.setSpacingAfter(15f);
            document.add(article4);
            
            // Article 5 - Rémunération
            Paragraph article5 = new Paragraph();
            article5.add(new Chunk("ARTICLE 5 - RÉMUNÉRATION\n", boldFont));
            article5.add(new Chunk("La rémunération sera fixée selon la grille salariale en vigueur dans l'entreprise et fera l'objet d'un avenant au présent contrat.\n", normalFont));
            article5.add(new Chunk("Le salaire sera versé mensuellement.", normalFont));
            article5.setSpacingAfter(15f);
            document.add(article5);
            
            // Signatures
            document.add(new Paragraph(" ", normalFont)); // Espace
            
            // Table pour les signatures
            PdfPTable signatureTable = new PdfPTable(2);
            signatureTable.setWidthPercentage(100);
            signatureTable.setSpacingBefore(30f);
            
            PdfPCell employeurCell = new PdfPCell();
            employeurCell.setBorder(Rectangle.NO_BORDER);
            employeurCell.addElement(new Paragraph("L'EMPLOYEUR", boldFont));
            employeurCell.addElement(new Paragraph(" ", normalFont));
            employeurCell.addElement(new Paragraph(" ", normalFont));
            employeurCell.addElement(new Paragraph(" ", normalFont));
            employeurCell.addElement(new Paragraph("Signature et cachet", normalFont));
            
            PdfPCell salarieCell = new PdfPCell();
            salarieCell.setBorder(Rectangle.NO_BORDER);
            salarieCell.addElement(new Paragraph("LE SALARIÉ", boldFont));
            salarieCell.addElement(new Paragraph(" ", normalFont));
            salarieCell.addElement(new Paragraph(" ", normalFont));
            salarieCell.addElement(new Paragraph(" ", normalFont));
            salarieCell.addElement(new Paragraph(entretien.getCandidat().getPrenom() + " " + entretien.getCandidat().getNom().toUpperCase(), normalFont));
            
            signatureTable.addCell(employeurCell);
            signatureTable.addCell(salarieCell);
            
            document.add(signatureTable);
            
            // Note en bas de page
            // Calculer la note moyenne de l'entretien
            Double noteMoyenne = noteEntretienService.calculateMoyenneByEntretien(entretien.getIdEntretien());
            String noteText = (noteMoyenne != null) ? String.format("%.1f/20", noteMoyenne) : "Non noté";
            
            Paragraph note = new Paragraph();
            note.add(new Chunk("Note : ", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.GRAY)));
            note.add(new Chunk("Ce contrat a été généré automatiquement le " + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm")) + 
                " suite à la sélection du candidat lors des entretiens (Note obtenue : " + 
                noteText + ").", 
                FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.GRAY)));
            note.setAlignment(Element.ALIGN_CENTER);
            note.setSpacingBefore(20f);
            document.add(note);
            
            document.close();
            return baos.toByteArray();
            
        } catch (Exception e) {
            throw new Exception("Erreur lors de la génération du PDF du contrat : " + e.getMessage(), e);
        }
    }
}
