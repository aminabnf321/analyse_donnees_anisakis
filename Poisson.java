
/**
 * Représente un poisson avec ses caractéristiques biologiques
 * et son niveau d'infestation par le parasite Anisakis.
 */
public class Poisson {
    private int id;
    private String espece;
    private Double poids;
    private Double taille;
    private Double infestation;



/**
     * Construit un poisson avec ses caractéristiques.
     * @param id L'identifiant unique du poisson        
     * @param espece Le nom de l'espèce du poisson
     * @param poids Le poids du poisson en kilogrammes
     * @param taille La taille du poisson en centimètres (peut être null)
     * @param infestation Le taux d'infestation par le parasite Anisakis (peut être null)
     */


    public Poisson(int id,String espece, Double poids, Double taille, Double infestation){
        this.id = id;
        this.espece= espece;
        this.poids = poids;
        this.taille=taille;
        this.infestation=infestation;
        
        
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }   
    /**
     * Retourne l'espèce du poisson.
     * 
     * @return le nom de l'espèce
     */
    public String getEspece(){
        return this.espece;
    }


    /**
     * Modifie l'espèce du poisson.
     * 
     * @param espece le nouveau nom de l'espèce
     */
    public void setEspece(String espece){
        this.espece=espece;
    }

    /**
     * Retourne le poids du poisson.
     * 
     * @return le poids en kilogrammes
     */
    public Double getPoids(){
        return this.poids;
    }

    /**
     * Modifie le poids du poisson.
     * 
     * @param poids le nouveau poids en kilogrammes
     */
    public void setPoids(Double poids){
        this.poids=poids;
    }


    /**
     * Retourne la taille du poisson.
     * 
     * @return la taille en centimètres, ou null si non mesurée
     */
    public Double getTaille(){
        return this.taille;
    }


     /**
     * Modifie la taille du poisson.
     * 
     * @param taille la nouvelle taille en centimètres (peut être null)
     */
    public void setTaille(Double taille){
        this.taille=taille;
    }


    /**
     * Retourne le taux d'infestation du poisson.
     * 
     * @return le nombre de parasites Anisakis, ou null si non mesuré
     */
    public Double getInfestation(){
        return this.infestation;
    }


    /**
     * Modifie le taux d'infestation du poisson.
     * 
     * @param infestation le nouveau nombre de parasites Anisakis (peut être null)
     */

    public void setInfestation(Double infestation){
        this.infestation=infestation;
    }   


        /**
     * Retourne une représentation textuelle du poisson.
     * 
     * @return une chaîne contenant toutes les caractéristiques du poisson
     */

    @Override
    public String toString() {
        return "Poisson{" +
                "id=" + id +
                ", espece='" + espece + '\'' +
                ", poids=" + poids +
                ", taille=" + taille +
                ", infestation=" + infestation +
      
                '}';
    }
    public static void main(String[] args) {
    System.out.println("=== TEST CLASSE POISSON ===\n");
    
    // Test constructeur
    Poisson p1 = new Poisson(1, "Saumon", 2.5, 30.0, 5.0);
    Poisson p2 = new Poisson(2, "Merlu", 1.8, 25.0, null);
    Poisson p3 = new Poisson(3, "Thon", null, null, null);
    
    // Test getters
    System.out.println("--- AFFICHAGE DES POISSONS ---");
    System.out.println("p1: " + p1);
    System.out.println("p2: " + p2);
    System.out.println("p3: " + p3);
    
    // Test setters
    System.out.println("\n--- MODIFICATION DE p3 ---");
    p3.setEspece("Thon rouge");
    p3.setPoids(4.5);
    p3.setTaille(45.0);
    p3.setInfestation(12.0);
    System.out.println("p3 après modification: " + p3);
    
    // Test valeur null
    System.out.println("\n--- TEST VALEURS NULL ---");
    System.out.println("Infestation de p2: " + (p2.getInfestation() == null ? "null" : p2.getInfestation()));
}
}
