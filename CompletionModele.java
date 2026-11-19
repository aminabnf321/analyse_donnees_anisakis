import java.util.List;

/**
 * Classe abstraite parente pour les stratégies de complétion.
 * Contient la logique commune de parcours des populations.
 */
public abstract class CompletionModele implements Completion {
    
    @Override
    public void completer(List<Population> populations) {
        for (Population population : populations) {
            completerPopulation(population);
        }
    }
    
    /**
     * Méthode abstraite à implémenter par les classes filles.
     * Chaque stratégie définit sa propre façon de compléter une population.
     */
    protected abstract void completerPopulation(Population population);
}