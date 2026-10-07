import java.util.*;
import java.io.*;

public class HelpDeskApp {
    private static List<Ticket> tickets = new ArrayList<>();
    private static final String FILE_PATH = "data/tickets.csv";
    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        File dir = new File("data");
        if (!dir.exists()) dir.mkdir();
        charger();
        menuPrincipal();
    }

    private static void menuPrincipal() {
        while(true) {
            System.out.println("\n=== SYSTÈME HELP-DESK ===");
            System.out.println("1. Demandeur | 2. Technicien | 3. Responsable | 4. Quitter");
            String choix = sc.nextLine();
            if(choix.equals("4")) { sauvegarder(); break; }

            System.out.print("Entrez votre nom : ");
            String nom = sc.nextLine();
            if(choix.equals("1")) menuDemandeur(nom);
            else if(choix.equals("2")) menuSalarie(nom, "TECHNICIEN");
            else if(choix.equals("3")) menuSalarie(nom, "RESPONSABLE");
        }
    }

    private static void menuSalarie(String nom, String role) {
        while(true) {
            System.out.println("\n--- LISTE GLOBALE (" + role + ") ---");
            afficherLegende();
            tickets.forEach(t -> System.out.println(t.formatLigne()));
            System.out.println("\n1. Trier | 2. Gérer Ticket | 3. Retour");
            String c = sc.nextLine();
            if(c.equals("3")) break;
            
            if(c.equals("1")) {
                menuTri();
            } else if(c.equals("2")) {
                System.out.print("ID du ticket : ");
                try {
                    int id = Integer.parseInt(sc.nextLine());
                    tickets.stream().filter(t -> t.getId() == id).findFirst()
                           .ifPresentOrElse(tk -> menuAction(tk, nom, role), 
                                          () -> System.out.println("ID introuvable."));
                } catch (Exception e) { System.out.println("ID invalide."); }
            }
        }
    }

    private static void menuTri() {
        System.out.println("\n--- OPTIONS DE TRI ---");
        System.out.println("1. Date | 2. Catégorie | 3. Priorité | 4. Auteur | 5. Statut | 6. Technicien | 7. Annuler");
        String choix = sc.nextLine();
        switch(choix) {
            case "1": tickets.sort(Comparator.comparing(Ticket::getDateCreation).reversed()); break;
            case "2": tickets.sort(Comparator.comparing(Ticket::getCategorie)); break;
            case "3": tickets.sort(Comparator.comparing(Ticket::getPriorite).reversed()); break;
            case "4": tickets.sort(Comparator.comparing(Ticket::getNomCreateur, String.CASE_INSENSITIVE_ORDER)); break;
            case "5": tickets.sort(Comparator.comparing(Ticket::getStatut)); break;
            case "6": tickets.sort(Comparator.comparing(Ticket::getNomTech, String.CASE_INSENSITIVE_ORDER)); break;
            default: return;
        }
        System.out.println("✅ Liste triée.");
    }

    private static void menuAction(Ticket tk, String nom, String role) {
        while(true) {
            tk.afficherDetails();
            System.out.println("1.Assigner | 2.IA Solution | 3.Statut | 4.Commenter");
            System.out.println("5.Modifier Catégorie | 6.Modifier Priorité | 7.Retour");
            String a = sc.nextLine();
            if(a.equals("7")) break;
            
            try {
                if(a.equals("1")) {
                    if(role.equals("TECHNICIEN")) tk.setNomTech(nom);
                    else { System.out.print("Nom du technicien : "); tk.setNomTech(sc.nextLine()); }
                } else if(a.equals("2")) {
                    System.out.println("Appel de l'IA...");
                    tk.setSuggestionIA(IA.appeler("Donne une solution technique pour : " + tk.getDescription()));
                } else if(a.equals("3")) {
                    System.out.print("Nouveau statut (OUVERT, EN_COURS, RESOLU, CLOS) : ");
                    tk.setStatut(Statut.valueOf(sc.nextLine().toUpperCase()));
                } else if(a.equals("4")) {
                    System.out.print("Votre message : ");
                    tk.ajouterCommentaire(nom, sc.nextLine());
                } else if(a.equals("5")) {
                    System.out.println("Catégories : " + Arrays.toString(Categorie.values()));
                    System.out.print("Choix : ");
                    tk.setCategorie(Categorie.valueOf(sc.nextLine().toUpperCase()));
                } else if(a.equals("6")) {
                    System.out.println("Priorités : (BASSE, MOYENNE, HAUTE, CRITIQUE)");
                    System.out.print("Choix : ");
                    tk.setPriorite(Priorite.valueOf(sc.nextLine().toUpperCase()));
                }
                sauvegarder();
            } catch(Exception e) { System.out.println("⚠️ Erreur : valeur incorrecte."); }
        }
    }

    private static void afficherLegende() {
        System.out.println(String.format("\n%-4s | %-11s | %-15s | %-10s | %-8s | %-10s | %-10s | %s", 
            "ID", "DATE", "TITRE", "CATÉGORIE", "URGENCE", "AUTEUR", "STATUT", "TECH"));
        System.out.println("-".repeat(105));
    }

    private static void menuDemandeur(String nom) {
        while(true) {
            System.out.println("\n--- MES DEMANDES (" + nom + ") ---");
            afficherLegende();
            tickets.stream().filter(t -> t.getNomCreateur().equalsIgnoreCase(nom))
                   .forEach(t -> System.out.println(t.formatLigne()));
            System.out.println("\n1. Nouveau Ticket | 2. Voir Détails | 3. Retour");
            String c = sc.nextLine();
            if(c.equals("3")) break;
            if(c.equals("1")) { creerTicket(nom); sauvegarder(); }
            else if(c.equals("2")) {
                System.out.print("ID du ticket : ");
                try {
                    int id = Integer.parseInt(sc.nextLine());
                    tickets.stream().filter(t -> t.getId() == id && t.getNomCreateur().equalsIgnoreCase(nom))
                           .findFirst().ifPresentOrElse(Ticket::afficherDetails, () -> System.out.println("Non trouvé."));
                } catch(Exception e) {}
            }
        }
    }

    private static void creerTicket(String nom) {
        try {
            System.out.print("Titre : "); String t = sc.nextLine();
            System.out.print("Description : "); String d = sc.nextLine();
            System.out.println("Catégories : " + Arrays.toString(Categorie.values()));
            Categorie cat = Categorie.valueOf(sc.nextLine().toUpperCase());
            System.out.print("Priorité suggérée (BASSE, MOYENNE, HAUTE, CRITIQUE) : ");
            Priorite pInitial = Priorite.valueOf(sc.nextLine().toUpperCase());
            
            // Création initiale du ticket
            Ticket tk = new Ticket(t, d, cat, pInitial, nom);
            
            // Appel de l'IA pour arbitrage de l'urgence
            System.out.println("🤖 IA : Analyse de l'urgence en cours...");
            String prompt = "Analyse la priorité technique de ce problème : '" + d + "'. " +
                            "Réponds uniquement par un seul mot parmi : BASSE, MOYENNE, HAUTE, CRITIQUE.";
            String resIA = IA.appeler(prompt);
            
            try { 
                Priorite pIA = Priorite.valueOf(resIA.toUpperCase().trim());
                if(pIA != pInitial) {
                    System.out.println("⚠️ IA : Priorité ajustée de " + pInitial + " à " + pIA);
                    // Le setter ajoutera automatiquement l'entrée dans l'historique
                    tk.setPriorite(pIA);
                } else {
                    System.out.println("✅ IA : Priorité confirmée.");
                }
            } catch(Exception e) { 
                System.out.println("L'IA n'a pas pu arbitrer, priorité utilisateur conservée."); 
            }
            
            tickets.add(tk);
            System.out.println("✅ Ticket #" + tk.getId() + " enregistré.");
        } catch(Exception e) { 
            System.out.println("⚠️ Erreur de saisie : assurez-vous de respecter les noms des Enums."); 
        }
    }

    private static void sauvegarder() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_PATH))) {
            for(Ticket t : tickets) pw.println(t.toCSV());
        } catch(Exception e) { System.out.println("Erreur sauvegarde."); }
    }

    private static void charger() {
        File f = new File(FILE_PATH);
        if(!f.exists()) return;
        try (Scanner r = new Scanner(f)) {
            while(r.hasNextLine()) {
                String line = r.nextLine();
                if(line.isEmpty()) continue;
                String[] p = line.split("\\|\\|\\|");
                if(p.length >= 8) {
                    Ticket t = new Ticket(p[1], p[2], Categorie.valueOf(p[3]), Priorite.valueOf(p[4]), p[6]);
                    t.setStatut(Statut.valueOf(p[5]));
                    t.setNomTech(p[7]);
                    if(p.length > 8) t.setSuggestionIA(p[8].replace(" [NEWLINE] ", "\n"));
                    if(p.length > 9) t.chargerCommentaires(p[9]);
                    if(p.length > 10) t.setDateCreation(p[10]);
                    if(p.length > 11) t.chargerHistorique(p[11]);
                    Ticket.setCompteur(Integer.parseInt(p[0]));
                    tickets.add(t);
                }
            }
        } catch(Exception e) { System.out.println("Erreur chargement."); }
    }
}
