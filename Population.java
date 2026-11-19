import java.util.*;

/**
 * Représente une population de poissons d'une même espèce.
 * Cette classe est immuable : une fois créée, ses caractéristiques
 * (espèce, effectif, longueur moyenne, poids moyen, taux d'infestation moyen)
 * ne peuvent plus être modifiées.
 */
public class Population {
    private final String espece;
    private final int effectif;
    private final List<Poisson> poissons;
    private final double TailleMoyenne;
    private final double poidsMoyen;
    private final double tauxMoyen;

    /**
     * Construit une population à partir d'une liste de poissons.
     * Calcule automatiquement l'espèce (prise du premier poisson),
     * l'effectif, la longueur moyenne, le poids moyen et le taux d'infestation moyen.
     * Les valeurs nulles ou égales à zéro sont ignorées dans les calculs des moyennes.
     * 
     * @param poissons liste de poissons (tous de la même espèce)
     * @throws IllegalArgumentException si la liste est null ou vide
     */
    public Population(List<Poisson> poissons) {
        if (poissons == null || poissons.isEmpty()) {
            throw new IllegalArgumentException("Liste vide");
        }

        this.poissons = new ArrayList<>(poissons);
        this.espece = poissons.get(0).getEspece();
        this.effectif = poissons.size();

        double sommeTaille = 0, sommePoids = 0, sommeTaux = 0;
        int countTaille = 0, countPoids = 0, countTaux = 0;

        for (Poisson p : poissons) {
           if (p.getTaille() != null && p.getTaille() > 0) {
    sommeTaille += p.getTaille();
    countTaille++;
}

          if (p.getPoids() != null && p.getPoids() > 0) {
    sommePoids += p.getPoids();
    countPoids++;
}

            if (p.getInfestation() != null && p.getInfestation() >= 0) {
                sommeTaux += p.getInfestation();
                countTaux++;
            }
        }

        this.TailleMoyenne = countTaille > 0 ? sommeTaille / countTaille : 0;
        this.poidsMoyen = countPoids > 0 ? sommePoids / countPoids : 0;
        this.tauxMoyen = countTaux > 0 ? sommeTaux / countTaux : 0;
    }

    /**
     * Retourne l'espèce de la population.
     * 
     * @return le nom de l'espèce
     */
    public String getEspece() { 
        return espece; 
    }

    /**
     * Retourne l'effectif de la population.
     * 
     * @return le nombre de poissons dans la population
     */
    public int getEffectif() { 
        return effectif; 
    }

    /**
     * Retourne la longueur moyenne des poissons de la population.
     * 
     * @return la longueur moyenne en centimètres, ou 0 si aucune longueur n'est disponible
     */
    public double getLongueurMoyenne() { 
        return TailleMoyenne; 
    }

    /**
     * Retourne le poids moyen des poissons de la population.
     * 
     * @return le poids moyen en kilogrammes, ou 0 si aucun poids n'est disponible
     */
    public double getPoidsMoyen() { 
        return poidsMoyen; 
    }

    /**
     * Retourne le taux d'infestation moyen des poissons de la population.
     * 
     * @return le nombre moyen de parasites Anisakis par poisson, ou 0 si aucun taux n'est disponible
     */
    public double getTauxMoyen() { 
        return tauxMoyen; 
    }

    /**
     * Retourne la liste des poissons de la population.
     * 
     * @return une copie de la liste des poissons
     */
    public List<Poisson> getPoissons() { 
        return new ArrayList<>(poissons); 
    }

    /**
     * Retourne une représentation textuelle de la population.
     * 
     * @return une chaîne contenant l'espèce, l'effectif, la longueur moyenne,
     *         le poids moyen et le taux d'infestation moyen
     */
    @Override
    public String toString() {
        return String.format("Population{espece='%s', effectif=%d, longueurMoy=%.1f, poidsMoy=%.1f, tauxMoy=%.1f}",
                espece, effectif, TailleMoyenne, poidsMoyen, tauxMoyen);
    }

    public static void main(String[] args) {
        System.out.println("=== TEST CLASSE POPULATION ===\n");
        
        // Création de poissons
        List<Poisson> poissons = new ArrayList<>();
        poissons.add(new Poisson(1, "Saumon", 2.5, 30.0, 5.0));
        poissons.add(new Poisson(2, "Saumon", 3.0, 35.0, null));
        poissons.add(new Poisson(3, "Saumon", 2.8, 32.0, 7.0));
        poissons.add(new Poisson(4, "Saumon", null, null, null));
        
        // Création de la population
        Population population = new Population(poissons);
        
        // Test getters
        System.out.println("--- INFORMATIONS POPULATION ---");
        System.out.println("Espèce: " + population.getEspece());
        System.out.println("Effectif: " + population.getEffectif());
        System.out.println("Longueur moyenne: " + String.format("%.2f", population.getLongueurMoyenne()));
        System.out.println("Poids moyen: " + String.format("%.2f", population.getPoidsMoyen()));
        System.out.println("Taux d'infestation moyen: " + String.format("%.2f", population.getTauxMoyen()));
        
        // Test toString
        System.out.println("\n--- TOSTRING ---");
        System.out.println(population);
        
        // Test liste des poissons
        System.out.println("\n--- LISTE DES POISSONS ---");
        for (Poisson p : population.getPoissons()) {
            System.out.println("  " + p);
        }
        
        // Test tentative de modification (immuable)
        System.out.println("\n--- VERIFICATION IMMUTABILITE ---");
        List<Poisson> copie = population.getPoissons();
        copie.clear(); // On vide la copie
        System.out.println("Population originale a encore " + population.getEffectif() + " poissons (copie modifiée sans impact)");
    }

}