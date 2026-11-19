import java.util.ArrayList;
import java.util.List;

/**
 * Classe principale du projet EcoParasite.
 * Orchestre l'ensemble du processus : lecture, nettoyage, completion et visualisation des donnees.
 * 
 * @author VotreNom
 * @version 1.0
 */
public class Main {

    /** Chemin par defaut du fichier CSV a lire */
    private static final String FICHIER_PAR_DEFAUT = "data/test3.csv";
    
    /** Methode de completion par defaut ("moyenne" ou "regression") */
    private static final String METHODE_PAR_DEFAUT = "moyenne";

    /**
     * Formate une valeur Double pour l'affichage dans les tableaux.
     * 
     * @param valeur la valeur a formater (peut etre null)
     * @param unite l'unite a ajouter apres la valeur
     * @return la valeur formatee sous forme de chaine, ou "null" si la valeur est nulle
     */
    private static String formaterDouble(Double valeur, String unite) {
        if (valeur == null) {
            return "null";
        }
        return String.format("%.1f%s", valeur, unite);
    }

    /**
     * Convertit une liste de populations en une liste plate de tous les poissons.
     * 
     * @param populations la liste des populations
     * @return une liste contenant tous les poissons de toutes les populations
     */
    private static List<Poisson> poissonsDepuisPopulations(List<Population> populations) {
        List<Poisson> poissons = new ArrayList<>();

        for (Population pop : populations) {
            poissons.addAll(pop.getPoissons());
        }

        return poissons;
    }

    /**
     * Choisit la strategie de completion en fonction du parametre.
     * 
     * @param methode "moyenne" pour CompletionMoyenne, sinon CompletionRegression
     * @return l'instance de Completion correspondante
     */
    private static Completion choisirCompletion(String methode) {
        if (methode.equalsIgnoreCase("moyenne")) {
            return new CompletionMoyenne();
        }
        return new CompletionRegression();
    }

    /**
     * Affiche le nombre de poissons ayant des valeurs manquantes ou invalides.
     * 
     * @param poissons la liste des poissons a analyser
     */
    private static void afficherValeursManquantes(List<Poisson> poissons) {
        int manquantsTaille = 0;
        int manquantsPoids = 0;
        int manquantsInfestation = 0;

        for (Poisson p : poissons) {
            if (p.getTaille() == null || p.getTaille() <= 0) manquantsTaille++;
            if (p.getPoids() == null || p.getPoids() <= 0) manquantsPoids++;
            if (p.getInfestation() == null || p.getInfestation() < 0) manquantsInfestation++;
        }

        System.out.println("Poissons sans taille valide : " + manquantsTaille);
        System.out.println("Poissons sans poids valide : " + manquantsPoids);
        System.out.println("Poissons sans infestation valide : " + manquantsInfestation);
    }

    /**
     * Affiche un tableau formaté de tous les poissons.
     * 
     * @param poissons la liste des poissons a afficher
     */
    private static void afficherTableauPoissons(List<Poisson> poissons) {
        String separateur = "+----+------------------------+---------------+---------------+----------------+----------------+";
        
        System.out.println(separateur);
        System.out.printf("| %-2s | %-22s | %-13s | %-13s | %-14s | %n",
                "ID", "Espece", "Taille", "Poids", "Infestation");
        System.out.println(separateur);
        
        for (Poisson p : poissons) {
            System.out.printf("| %-2d | %-22s | %-13s | %-14s | %-14s | %n",
                    p.getId(),
                    p.getEspece(),
                    formaterDouble(p.getTaille(), ""),
                    formaterDouble(p.getPoids(), ""),
                    formaterDouble(p.getInfestation(), ""));
        }
        
        System.out.println(separateur);
    }
    
    /**
     * Affiche un tableau de toutes les populations avec leurs poissons.
     * 
     * @param populations la liste des populations a afficher
     */
    private static void afficherTableauPopulations(List<Population> populations) {
        for (Population pop : populations) {
            System.out.println("\n--- Population: " + pop.getEspece() + " (effectif: " + pop.getEffectif() + ") ---");
            afficherTableauPoissons(pop.getPoissons());
        }
    }

    /**
     * Point d'entree principal de l'application.
     * 
     * @param args arguments de la ligne de commande :
     *             args[0] = chemin du fichier CSV (optionnel)
     *             args[1] = methode de completion (optionnel : "moyenne" ou "regression")
     * 
     * Deroulement :
     * 1. Lecture du fichier CSV et creation des populations
     * 2. Nettoyage des donnees invalides
     * 3. Completion des valeurs manquantes selon la methode choisie
     * 4. Generation des graphiques SVG
     */
    public static void main(String[] args) {
        // 1. Lecture du fichier CSV
        String fichier = args.length > 0 ? args[0] : FICHIER_PAR_DEFAUT;
        String methode = args.length > 1 ? args[1] : METHODE_PAR_DEFAUT;

        List<Population> populations = LectureCSV.lireToutesPopulations(fichier);
        List<Poisson> tousLesPoissons = poissonsDepuisPopulations(populations);
        
        System.out.println("=== DONNEES INITIALES ===");
        System.out.println("Fichier utilise : " + fichier);
        System.out.println("Methode de completion : " + methode);
        System.out.println("Nombre de poissons : " + tousLesPoissons.size());
        
        afficherValeursManquantes(tousLesPoissons);
        
        System.out.println("\n=== APRES LA LECTURE (avec valeurs nulles) ===");
        afficherTableauPoissons(tousLesPoissons);
        
        // 2. Nettoyage des donnees invalides
        System.out.println("\n=== NETTOYAGE DES DONNEES ===");
        for (Population pop : populations) {
            Nettoyage.nettoyage(pop.getPoissons());
        }
        
        System.out.println("\n=== APRES NETTOYAGE ===");
        afficherValeursManquantes(tousLesPoissons);
        afficherTableauPopulations(populations);
        
        // 3. Completion avec la methode choisie
        System.out.println("\n=== COMPLETION DES DONNEES ===");
        Completion completion = choisirCompletion(methode);
        completion.completer(populations);
        
        System.out.println("\n=== APRES COMPLETION ===");
        afficherTableauPopulations(populations);
        
        // 4. Resume final
        System.out.println("\n=== RESUME FINAL ===");
        System.out.println("Valeurs encore manquantes apres completion :");
        afficherValeursManquantes(tousLesPoissons);

        // 5. Generation des graphiques SVG
        for (Population pop : populations) {
            String nomFichier = "graphique_" + pop.getEspece().replace(" ", "_") + ".svg";
            GenerateSVGGraphique.genererGraphiqueSVG(pop, nomFichier);
        }

        System.out.println("Graphiques generes ! Ouvrez les fichiers .svg dans votre navigateur.");
    }
}