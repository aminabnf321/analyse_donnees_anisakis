import java.util.ArrayList;
import java.util.List;

/**
 * Strategie de completion utilisant la regression lineaire.
 * 
 * Cette classe permet de completer les valeurs manquantes du taux d'infestation
 * en utilisant la regression lineaire entre la taille du poisson (x) et son
 * taux d'infestation (y). L'equation obtenue est y = a*x + b.
 * 
 * La regression n'est appliquee que si la population contient au moins 2 poissons
 * ayant a la fois une taille et un taux d'infestation connus.
 * @see CompletionModele
 */
public class CompletionRegression extends CompletionModele {
    
    /**
     * Formule de la pente : a = Σ(x - x̄)(y - ȳ) / Σ(x - x̄)²
     * Formule de l'ordonnee : b = ȳ - a * x̄
     */
    
    /**
     * Complete les valeurs manquantes d'infestation dans une population.
     * 
     * Pour chaque poisson sans infestation mais avec une taille connue,
     * on predit l'infestation via l'equation y = a*x + b.
     * 
     * @param population la population a traiter (modifiee directement)
     */
    @Override
    protected void completerPopulation(Population population) {
        List<Poisson> poissons = population.getPoissons();

        // 1. Recuperer les poissons avec taille ET infestation connues
        List<Poisson> valides = new ArrayList<>();
        for (Poisson p : poissons) {
            if (p.getTaille() != null && p.getInfestation() != null) {
                valides.add(p);
            }
        }

        // 2. Verifier qu'on a assez de points
        if (valides.size() < 2) {
            System.out.println("Pas assez de donnees pour la regression sur " + population.getEspece());
            return;
        }

        int n = valides.size();
        double sommeTailles = 0;
        double sommeInfestations = 0;

        // 3. Calculer les sommes
        for (Poisson p : valides) {
            sommeTailles += p.getTaille();
            sommeInfestations += p.getInfestation();
        }

        // 4. Calculer les moyennes
        double moyenneTailles = sommeTailles / n;
        double moyenneInfestations = sommeInfestations / n;

        // 5. Calculer le numerateur et le denominateur
        double numerateur = 0;
        double denominateur = 0;

        for (Poisson p : valides) {
            double taille = p.getTaille();
            double infestation = p.getInfestation();
            numerateur += (taille - moyenneTailles) * (infestation - moyenneInfestations);
            denominateur += (taille - moyenneTailles) * (taille - moyenneTailles);
        }

        // 6. Calculer la pente a et l'ordonnee b
        double a = (denominateur != 0) ? numerateur / denominateur : 0;
        double b = moyenneInfestations - a * moyenneTailles;

        // 7. Predire et completer les valeurs manquantes
        int compteur = 0;
        for (Poisson p : poissons) {
            if (p.getInfestation() == null && p.getTaille() != null) {
                double infestationPredite = a * p.getTaille() + b;
                p.setInfestation(infestationPredite);
                compteur++;
            }
        }
        System.out.println(compteur + " valeurs d'infestation completees par regression pour " + population.getEspece());
    }
    
    /**
     * Test unitaire pour la classe CompletionRegression.
     * 
     * @param args arguments de la ligne de commande (non utilises)
     */
    public static void main(String[] args) {
        System.out.println("=== TEST COMPLETION REGRESSION ===\n");
        
        // Creation des poissons de test
        List<Poisson> poissons = new ArrayList<>();
        poissons.add(new Poisson(1, "Saumon", 2.5, 30.0, 5.0));
        poissons.add(new Poisson(2, "Saumon", 2.8, 32.0, 6.5));
        poissons.add(new Poisson(3, "Saumon", 3.0, 35.0, 8.0));
        poissons.add(new Poisson(4, "Saumon", 2.6, 33.0, null));
        poissons.add(new Poisson(5, "Saumon", 2.9, 34.0, null));
        
        // Creation de la population
        Population population = new Population(poissons);
        List<Population> populations = new ArrayList<>();
        populations.add(population);
        
        // Affichage avant regression
        System.out.println("--- AVANT REGRESSION ---");
        for (Poisson p : poissons) {
            System.out.println("ID " + p.getId() + 
                " | taille=" + (p.getTaille() != null ? p.getTaille() : "null") +
                " | infestation=" + (p.getInfestation() != null ? p.getInfestation() : "null"));
        }
        
        // Application de la regression
        System.out.println("\n--- CALCUL DE LA REGRESSION ---");
        CompletionRegression completion = new CompletionRegression();
        completion.completer(populations);
        
        // Affichage apres regression
        System.out.println("\n--- APRES REGRESSION ---");
        for (Poisson p : poissons) {
            System.out.println("ID " + p.getId() + 
                " | taille=" + p.getTaille() +
                " | infestation=" + (p.getInfestation() != null ? String.format("%.2f", p.getInfestation()) : "null"));
        }
    }
}