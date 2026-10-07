public abstract class Utilisateur {
    protected String nom;
    public Utilisateur(String n) { this.nom = n; }
    public String getNom() { return nom; }
}
class Demandeur extends Utilisateur { public Demandeur(String n) { super(n); } }
class Technicien extends Utilisateur { public Technicien(String n) { super(n); } }
class Responsable extends Technicien { public Responsable(String n) { super(n); } }

