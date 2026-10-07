import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Ticket {
    private static int compteur = 1;
    private int id;
    private String titre, description, suggestionIA = "Aucune";
    private Categorie categorie;
    private Priorite priorite;
    private Statut statut = Statut.OUVERT;
    private String nomCreateur, nomTech = "Libre";
    private String dateCreation;
    private List<String> commentaires = new ArrayList<>();
    private List<String> historique = new ArrayList<>();

    public Ticket(String t, String d, Categorie c, Priorite p, String crea) {
        this.id = compteur++;
        this.titre = t; 
        this.description = d;
        this.categorie = c; 
        this.priorite = p;
        this.nomCreateur = crea;
        this.dateCreation = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM HH:mm"));
        ajouterEvenement("Ticket créé par " + crea);
    }

    public void ajouterEvenement(String action) {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM HH:mm"));
        this.historique.add("[" + date + "] " + action);
    }

    // Getters
    public int getId() { return id; }
    public String getTitre() { return titre; }
    public String getDescription() { return description; }
    public String getNomCreateur() { return nomCreateur; }
    public Categorie getCategorie() { return categorie; }
    public Priorite getPriorite() { return priorite; }
    public Statut getStatut() { return statut; }
    public String getNomTech() { return nomTech; }
    public String getDateCreation() { return dateCreation; }
    
    // Setters avec traçabilité historique
    public void setPriorite(Priorite p) { 
        ajouterEvenement("Priorité : " + this.priorite + " -> " + p);
        this.priorite = p; 
    }
    public void setStatut(Statut s) { 
        ajouterEvenement("Statut : " + this.statut + " -> " + s);
        this.statut = s; 
    }
    public void setNomTech(String n) { 
        ajouterEvenement("Technicien : " + this.nomTech + " -> " + n);
        this.nomTech = n; 
    }
    public void setCategorie(Categorie c) { 
        ajouterEvenement("Catégorie : " + this.categorie + " -> " + c);
        this.categorie = c; 
    }
    public void setSuggestionIA(String s) { this.suggestionIA = s; }
    public void setDateCreation(String d) { this.dateCreation = d; }

    public void ajouterCommentaire(String auteur, String texte) {
        this.commentaires.add("[" + auteur + "]: " + texte);
    }

    public String formatLigne() {
        return String.format("#%-3d | %-11s | %-15s | %-10s | %-8s | %-10s | %-10s | %s", 
                id, dateCreation, 
                (titre.length() > 15 ? titre.substring(0, 12) + "..." : titre),
                categorie, priorite, nomCreateur, statut, nomTech);
    }

    public void afficherDetails() {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("TICKET #" + id + " - " + titre);
        System.out.println("Créé le : " + dateCreation);
        System.out.println("=".repeat(70));
        System.out.println("Demandeur: " + nomCreateur + " | Catégorie: " + categorie);
        System.out.println("Statut: " + statut + " | Tech: " + nomTech + " | Urgence: " + priorite);
        System.out.println("-".repeat(30));
        System.out.println("DESCRIPTION: " + description);
        System.out.println("-".repeat(30));
        System.out.println("COMMENTAIRES:");
        if(commentaires.isEmpty()) System.out.println("Aucun commentaire.");
        else commentaires.forEach(System.out::println);
        System.out.println("-".repeat(30));
        System.out.println("HISTORIQUE DES ACTIONS:");
        if(historique.isEmpty()) System.out.println("Aucun historique.");
        else historique.forEach(System.out::println);
        System.out.println("-".repeat(30));
        System.out.println("SUGGESTION IA:\n" + suggestionIA);
        System.out.println("=".repeat(70));
    }

    public String toCSV() {
        String comms = String.join("##", commentaires);
        String hist = String.join("##", historique);
        return id + "|||" + titre + "|||" + description + "|||" + categorie + "|||" + priorite + "|||" + statut + "|||" + nomCreateur + "|||" + nomTech + "|||" + suggestionIA.replace("\n", " [NEWLINE] ") + "|||" + comms + "|||" + dateCreation + "|||" + hist;
    }

    public void chargerCommentaires(String brute) {
        if(!brute.isEmpty()) this.commentaires = new ArrayList<>(Arrays.asList(brute.split("##")));
    }

    public void chargerHistorique(String brute) {
        if(!brute.isEmpty()) this.historique = new ArrayList<>(Arrays.asList(brute.split("##")));
    }

    public static void setCompteur(int val) { if(val >= compteur) compteur = val + 1; }
}
