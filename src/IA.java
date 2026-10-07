import java.net.URI;
import java.net.http.*;
import java.time.Duration;

public class IA {
    public static String appeler(String prompt) {
        try {
            // Préparation du JSON pour Ollama
            String json = "{\"model\": \"mistral\", \"prompt\": \"" + prompt.replace("\"", "\\\"") + "\", \"stream\": false}";
            
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(15))
                    .build();

            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:11434/api/generate"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = client.send(req, HttpResponse.BodyHandlers.ofString());
            String body = response.body();

            // Extraction précise du champ "response"
            // On cherche le début de la valeur après "response":"
            int start = body.indexOf("\"response\":\"") + 12;
            // On cherche la fin de la chaîne de caractères (le guillemet de fermeture)
            // en faisant attention aux guillemets échappés dans le texte
            int end = body.indexOf("\",\"done\":"); 

            if (start > 11 && end > start) {
                String resultat = body.substring(start, end);
                // On remplace les caractères d'échappement JSON pour un affichage propre
                return resultat.replace("\\n", "\n").replace("\\\"", "\"").trim();
            }
            
            return "Désolé, je n'ai pas pu formater la réponse de l'IA.";

        } catch (java.net.ConnectException e) {
            return "Erreur : Ollama n'est pas lancé. (Tapez 'ollama run mistral' dans un terminal)";
        } catch (Exception e) {
            return "Erreur lors de la communication avec l'IA.";
        }
    }
}
