import java.util.List;

/**
 * Interface pour les strategies de completion des donnees manquantes.
 * Permet d'implementer differentes methodes pour completer les valeurs null
 * dans les populations de poissons.
 * 
 * Deux implementations disponibles :
 * - CompletionMoyenne : complete par la moyenne de l'espece
 * - CompletionRegression : complete par regression lineaire taille/infestation
 */
public interface Completion {
    
    /**
     * Complete les valeurs manquantes dans une liste de populations.
     * 
     * @param populations la liste des populations a traiter (modifiee directement)
     */
    void completer(List<Population> populations);
}