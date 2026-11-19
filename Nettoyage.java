import java.util.List;

/**
 * Classe utilitaire pour le nettoyage des donnees des poissons.
 * Remplace par null les valeurs invalides (negatives ou nulles).
 */
public class Nettoyage {
    
    /**
     * Nettoie une liste de poissons en remplacant par null les valeurs invalides.
     * Regles de nettoyage :
     * - Taille inferieure ou egale a 0 devient null
     * - Poids inferieur ou egal a 0 devient null
     * - Infestation inferieure a 0 devient null
     *
     * @param poissons la liste des poissons a nettoyer (modifiee directement)
     */
    public static void nettoyage(List<Poisson> poissons) {
        for (Poisson p : poissons) {
            if (p.getTaille() != null && p.getTaille() <= 0) {
                p.setTaille(null);
            }
            if (p.getPoids() != null && p.getPoids() <= 0) {
                p.setPoids(null);
            }
            if (p.getInfestation() != null && p.getInfestation() < 0) {
                p.setInfestation(null);
            }
        }
    }
    
    /**
     * Test unitaire pour la classe Nettoyage.
     */
    public static void main(String[] args) {
        System.out.println("=== TEST NETTOYAGE ===\n");
        
        List<Poisson> poissons = List.of(
            new Poisson(1, "Saumon", 2.5, 30.0, 5.0),
            new Poisson(2, "Merlu", -1.5, 25.0, 3.0),
            new Poisson(3, "Thon", 3.0, -10.0, 2.0),
            new Poisson(4, "Sardine", 2.0, 15.0, -5.0),
            new Poisson(5, "Maquereau", 0.0, 20.0, 1.0),
            new Poisson(6, "Bar", 4.0, 0.0, 2.0),
            new Poisson(7, "Truite", null, null, null)
        );
        
        System.out.println("--- AVANT NETTOYAGE ---");
        for (Poisson p : poissons) {
            System.out.printf("ID %d : taille=%-6s poids=%-6s infestation=%s%n",
                p.getId(),
                p.getTaille() != null ? p.getTaille() : "null",
                p.getPoids() != null ? p.getPoids() : "null",
                p.getInfestation() != null ? p.getInfestation() : "null");
        }
        
        Nettoyage.nettoyage(poissons);
        
        System.out.println("\n--- APRES NETTOYAGE ---");
        for (Poisson p : poissons) {
            System.out.printf("ID %d : taille=%-6s poids=%-6s infestation=%s%n",
                p.getId(),
                p.getTaille() != null ? p.getTaille() : "null",
                p.getPoids() != null ? p.getPoids() : "null",
                p.getInfestation() != null ? p.getInfestation() : "null");
        }
        
        System.out.println("\n=== TEST REUSSI ===");
    }
}