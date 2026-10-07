import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
public class Commentaire {
    private String auteur, texte, date;
    public Commentaire(String a, String t) {
        this.auteur = a; this.texte = t;
        this.date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM HH:mm"));
    }
    public String toString() { return "[" + date + "] " + auteur + " : " + texte; }
    public String toData() { return auteur + ">" + texte + ">" + date; }
}

