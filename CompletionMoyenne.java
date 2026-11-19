import java.util.List;
import java.util.ArrayList;

/**
 * Strategie de completion utilisant la moyenne par espece.
 * 
 * Cette classe permet de completer les valeurs manquantes (taille, poids,
 * taux d'infestation) en les remplacant par la moyenne des valeurs connues
 * pour la meme espece.
 * @see CompletionModele
 */
public class CompletionMoyenne extends CompletionModele {
    
    /**
     * Calcule la moyenne des taux d'infestation dans une liste de poissons.
     * 
     * @param poissons la liste des poissons
     * @return la moyenne des infestations, ou null si aucune valeur valide
     */
    private static Double moyenneTaux(List<Poisson> poissons) {
        double somme = 0;
        int count = 0;
        for (Poisson p : poissons) {
            Double t = p.getInfestation();
            if (t != null && t >= 0) {
                somme += t;
                count++;
            }
        }
        return count > 0 ? somme / count : null;
    }

    /**
     * Calcule la moyenne des tailles dans une liste de poissons.
     * 
     * @param poissons la liste des poissons
     * @return la moyenne des tailles, ou null si aucune valeur valide
     */
    private static Double moyenneTaille(List<Poisson> poissons) {
        double somme = 0;
        int count = 0;
        for (Poisson p : poissons) {
            Double t = p.getTaille();
            if (t != null && t > 0) {
                somme += t;
                count++;
            }
        }
        return count > 0 ? somme / count : null;
    }

    /**
     * Calcule la moyenne des poids dans une liste de poissons.
     * 
     * @param poissons la liste des poissons
     * @return la moyenne des poids, ou null si aucune valeur valide
     */
    private static Double moyennePoids(List<Poisson> poissons) {
        double somme = 0;
        int count = 0;
        for (Poisson p : poissons) {
            Double t = p.getPoids();
            if (t != null && t > 0) {
                somme += t;
                count++;
            }
        }
        return count > 0 ? somme / count : null;
    }

    /**
     * Complete les valeurs manquantes d'une population par la moyenne.
     * 
     * Pour chaque attribut (taille, poids, infestation), calcule la moyenne
     * des valeurs connues et remplace les valeurs manquantes ou invalides
     * par cette moyenne.
     * 
     * @param population la population a traiter (modifiee directement)
     */
    @Override
    protected void completerPopulation(Population population) {
        List<Poisson> poissons = population.getPoissons();
        Double moyTaux = moyenneTaux(poissons);
        Double moyTaille = moyenneTaille(poissons);
        Double moyPoids = moyennePoids(poissons);
        String espece = population.getEspece();

        // Completion des tailles manquantes
        if (moyTaille != null) {
            for (Poisson p : poissons) {
                if (p.getTaille() == null || p.getTaille() <= 0) {
                    p.setTaille(moyTaille);
                }
            }
        } else {
            System.out.println("Population " + espece + " : Aucune taille connue, moyenne impossible.");
        }

        // Completion des infestations manquantes
        if (moyTaux != null) {
            for (Poisson p : poissons) {
                if (p.getInfestation() == null || p.getInfestation() < 0) {
                    p.setInfestation(moyTaux);
                }
            }
        } else {
            System.out.println("Population " + espece + " : Aucun taux connu, moyenne impossible.");
        }

        // Completion des poids manquants
        if (moyPoids != null) {
            for (Poisson p : poissons) {
                if (p.getPoids() == null || p.getPoids() <= 0) {
                    p.setPoids(moyPoids);
                }
            }
        } else {
            System.out.println("Population " + espece + " : Aucun poids connu, moyenne impossible.");
        }
    }
    
    /**
     * Test unitaire pour la classe CompletionMoyenne.
     * 
     * @param args arguments de la ligne de commande (non utilises)
     */
    public static void main(String[] args) {
        System.out.println("=== TEST COMPLETION MOYENNE ===\n");
        
        // Creation des poissons de test
        List<Poisson> poissons = new ArrayList<>();
        poissons.add(new Poisson(1, "Saumon", 2.5, 30.0, 5.0));
        poissons.add(new Poisson(2, "Saumon", 3.0, 35.0, null));
        poissons.add(new Poisson(3, "Saumon", 2.8, 32.0, 7.0));
        poissons.add(new Poisson(4, "Saumon", null, null, null));
        
        // Creation de la population
        Population population = new Population(poissons);
        List<Population> populations = new ArrayList<>();
        populations.add(population);
        
        // Affichage avant completion
        System.out.println("--- AVANT ---");
        for (Poisson p : poissons) {
            System.out.println("ID " + p.getId() + 
                " | taille=" + (p.getTaille() != null ? p.getTaille() : "null") +
                " | poids=" + (p.getPoids() != null ? p.getPoids() : "null") +
                " | infestation=" + (p.getInfestation() != null ? p.getInfestation() : "null"));
        }
        
        // Application de la completion
        System.out.println("\n--- APPLICATION ---");
        CompletionMoyenne completion = new CompletionMoyenne();
        completion.completer(populations);
        
        // Affichage apres completion
        System.out.println("\n--- APRES ---");
        for (Poisson p : poissons) {
            System.out.println("ID " + p.getId() + 
                " | taille=" + (p.getTaille() != null ? String.format("%.1f", p.getTaille()) : "null") +
                " | poids=" + (p.getPoids() != null ? String.format("%.1f", p.getPoids()) : "null") +
                " | infestation=" + (p.getInfestation() != null ? String.format("%.1f", p.getInfestation()) : "null"));
        }
    }
}