import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Lit les fichiers CSV et cree des objets Poisson.
 * Gere les separateurs ; et , ainsi que les formats vertical et horizontal.
 */
public class LectureCSV {


    /**
     * Decoupe une ligne en utilisant ; ou , comme separateur.
     */
    private static String[] decouperLigne(String ligne) {
        if (ligne.contains(";")) {
            return ligne.split(";", -1);
        }
        return ligne.split(",", -1);
    }

    /**
     * Extrait le nom de l'espece par defaut a partir du nom du fichier.
     */
    private static String extraireEspeceDuNomFichier(String nomFichier) {
        String nom = new java.io.File(nomFichier).getName();
        if (nom.toLowerCase().endsWith(".csv")) {
            nom = nom.substring(0, nom.length() - 4);
        }
        if (nom.contains(".")) {
            nom = nom.substring(0, nom.indexOf("."));
        }
        return nom;
    }

    /**
     * Verifie si un texte correspond a un nom d'attribut connu.
     */
    private static boolean estAttributSimple(String texte) {
        if (texte == null) return false;
        String nom = texte.trim().toLowerCase();
        return nom.equals("truehost") || nom.equals("host") || nom.equals("espece") ||
               nom.equals("species") || nom.equals("taille") || nom.equals("length") ||
               nom.equals("poids") || nom.equals("weight") || nom.equals("infestation") ||
               nom.equals("standard_abundance") || nom.equals("standard_lengthcalc") ||
               nom.equals("nparasitestotal") || nom.equals("total");
    }

    /**
     * Analyse l'en-tete et retourne la position de chaque attribut.
     */
    private static Map<String, Integer> trouverPositionsColonnes(String[] colonnes) {
        Map<String, Integer> positions = new HashMap<>();
        positions.put("espece", -1);
        positions.put("poids", -1);
        positions.put("taille", -1);
        positions.put("infestation", -1);

        Map<String, String[]> attributs = new HashMap<>();
        attributs.put("espece", new String[]{"TrueHost", "Host", "nom.merlu", "species", "espece", "Sample_code", "Espèce", "DataSpecies" , "Especes"});
        attributs.put("taille", new String[]{"StandardLength", "m.size (mm)", "m.size", "Standard_LengthCalc", "length", "longueur", "LT", "LT mm", "Length_cm", "Length_TL_cm", "Length_SL_cm", "Length_FL_cm_mean"});
        attributs.put("poids", new String[]{"Weight_kg", "poids", "weight", "Masse", "Masse g"});
        attributs.put("infestation", new String[]{"NParasitesTotal", "number of Anisakis sp L3", "Standard_Abundance", "infestation", "Total", "Prevalence", "Abundance_mean_per_fish", "Total_Number_of_Parasites"});

        for (int i = 0; i < colonnes.length; i++) {
            String nomColonne = colonnes[i].toLowerCase();
            for (Map.Entry<String, String[]> entry : attributs.entrySet()) {
                String attribut = entry.getKey();
                String[] synonymes = entry.getValue();
                for (String synonyme : synonymes) {
                    if (nomColonne.equals(synonyme.toLowerCase())) {
                        positions.put(attribut, i);
                        break;
                    }
                }
            }
        }

        return positions;
    }

    /**
     * Lit une valeur Double depuis une cellule.
     */
    private static Double lireDouble(String[] valeurs, int position) {
        if (position < 0 || position >= valeurs.length) {
            return null;
        }
        String texte = valeurs[position];
        if (texte == null || texte.isEmpty() || texte.equals("null") || texte.equals("NA")) {
            return null;
        }
        try {
            return Double.parseDouble(texte.replace(",", "."));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Lit une valeur texte depuis une cellule.
     */
    private static String lireTexte(String[] valeurs, int position, String valeurParDefaut) {
        if (position < 0 || position >= valeurs.length) {
            return valeurParDefaut;
        }
        String texte = valeurs[position];
        if (texte == null || texte.isEmpty()) {
            return valeurParDefaut;
        }
        return texte;
    }

    /**
     * Ajuste la mesure (mm → cm si necessaire).
     */
    private static Double ajusterMesure(Double valeur, String nomColonne) {
        if (valeur == null) return null;
        String nom = nomColonne.toLowerCase();
        if (nom.contains("mm") || nom.equals("lt") || nom.equals("m.size")) {
            return valeur / 10.0;
        }
        return valeur;
    }

    /**
     * Lit une cellule brute.
     */
    private static String lireCellule(String[] ligne, int col) {
        if (col >= ligne.length) return null;
        String valeur = ligne[col];
        if (valeur == null || valeur.isEmpty() || valeur.equals("null")) return null;
        return valeur.trim();
    }

    // ==================== FORMAT VERTICAL ====================

    /**
     * Lit un fichier au format vertical (chaque ligne = un poisson).
     */
    private static List<Poisson> lireFormatVertical(BufferedReader br, String entete, String especeParDefaut, String premiereLigne) throws IOException {
    List<Poisson> poissons = new ArrayList<>();
    String[] colonnes = decouperLigne(entete);
    Map<String, Integer> positions = trouverPositionsColonnes(colonnes);

    int posEspece = positions.get("espece");
    int posTaille = positions.get("taille");
    int posPoids = positions.get("poids");
    int posInfestation = positions.get("infestation");

    int id = 1;
    

    if (premiereLigne != null && !premiereLigne.isEmpty()) {
        String[] valeurs = decouperLigne(premiereLigne);
        ajouterPoisson(poissons, valeurs, posEspece, posTaille, posPoids, posInfestation, especeParDefaut, colonnes, id);
        id++;
    }
    

    String ligne;
    while ((ligne = br.readLine()) != null) {
        String[] valeurs = decouperLigne(ligne);
        if (ajouterPoisson(poissons, valeurs, posEspece, posTaille, posPoids, posInfestation, especeParDefaut, colonnes, id)) {
            id++;
        }
    }

    return poissons;
}

// Méthode helper pour ajouter un poisson
private static boolean ajouterPoisson(List<Poisson> poissons, String[] valeurs, 
                                       int posEspece, int posTaille, int posPoids, int posInfestation,
                                       String especeParDefaut, String[] colonnes, int id) {
    String espece = lireTexte(valeurs, posEspece, especeParDefaut);
    Double taille = lireDouble(valeurs, posTaille);
    Double poids = lireDouble(valeurs, posPoids);
    Double infestation = lireDouble(valeurs, posInfestation);

    if (posTaille >= 0 && posTaille < colonnes.length) {
        taille = ajusterMesure(taille, colonnes[posTaille]);
    }

    if (taille == null && poids == null && infestation == null) {
        return false;
    }

    poissons.add(new Poisson(id, espece, poids, taille, infestation));
    return true;
}

    // ==================== FORMAT HORIZONTAL ====================

    /**
     * Lit un fichier au format horizontal (premieres lignes = attributs).
     */
    private static List<Poisson> lireFormatHorizontal(BufferedReader br, String ligne1, String ligne2, String especeParDefaut) throws IOException {
        List<Poisson> poissons = new ArrayList<>();
        List<String[]> toutesLesLignes = new ArrayList<>();

        toutesLesLignes.add(decouperLigne(ligne1));
        toutesLesLignes.add(decouperLigne(ligne2));

        String ligne;
        while ((ligne = br.readLine()) != null) {
            toutesLesLignes.add(decouperLigne(ligne));
        }

        int ligneEspece = -1, ligneTaille = -1, lignePoids = -1, ligneInfestation = -1;

        for (int i = 0; i < toutesLesLignes.size(); i++) {
            String[] ligneData = toutesLesLignes.get(i);
            if (ligneData.length == 0) continue;

            String premierChamp = ligneData[0].trim().toLowerCase();

            if (premierChamp.equals("espece") || premierChamp.equals("species") || 
                premierChamp.equals("truehost") || premierChamp.equals("host")) {
                ligneEspece = i;
            } else if (premierChamp.equals("taille") || premierChamp.equals("length") || 
                       premierChamp.equals("standardlength") || premierChamp.equals("longueur") ||
                       premierChamp.equals("standard_lengthcalc")) {
                ligneTaille = i;
            } else if (premierChamp.equals("poids") || premierChamp.equals("weight")) {
                lignePoids = i;
            } else if (premierChamp.equals("infestation") || premierChamp.equals("tauxinfestation") ||
                       premierChamp.equals("nparasitestotal") || premierChamp.equals("standard_abundance") ||
                       premierChamp.equals("total")) {
                ligneInfestation = i;
            }
        }

        if (toutesLesLignes.isEmpty()) return poissons;
        
        int nbPoissons = toutesLesLignes.get(0).length - 1;

        for (int col = 1; col <= nbPoissons; col++) {
            String espece = especeParDefaut;
            if (ligneEspece != -1 && ligneEspece < toutesLesLignes.size()) {
                String especeLue = lireCellule(toutesLesLignes.get(ligneEspece), col);
                if (especeLue != null && !especeLue.isEmpty()) {
                    espece = especeLue;
                }
            }

            Double taille = (ligneTaille != -1 && ligneTaille < toutesLesLignes.size()) ? 
                lireDouble(toutesLesLignes.get(ligneTaille), col) : null;
            Double poids = (lignePoids != -1 && lignePoids < toutesLesLignes.size()) ? 
                lireDouble(toutesLesLignes.get(lignePoids), col) : null;
            Double infestation = (ligneInfestation != -1 && ligneInfestation < toutesLesLignes.size()) ? 
                lireDouble(toutesLesLignes.get(ligneInfestation), col) : null;

            if (taille == null && poids == null && infestation == null) {
                continue;
            }

            int id = col;
            poissons.add(new Poisson(id, espece, poids, taille, infestation));
        }

        System.out.println("Format horizontal : " + poissons.size() + " poissons lus");
        return poissons;
    }


    /**
     * Lit un fichier CSV et retourne la liste de tous les poissons.
     *
     * @param fichier chemin du fichier CSV
     * @return liste des poissons lus
     */
    public static List<Poisson> lirePoissons(String fichier) {
        List<Poisson> poissons = new ArrayList<>();
        String especeParDefaut = extraireEspeceDuNomFichier(fichier);

        try (BufferedReader br = new BufferedReader(new FileReader(fichier))) {
            String ligne1 = br.readLine();
            if (ligne1 == null) return poissons;
            
            // Enlever le BOM si present
            ligne1 = ligne1.replace("\uFEFF", "");
            
            String ligne2 = br.readLine();
            String[] ligne1Colonnes = decouperLigne(ligne1);
            String[] ligne2Colonnes = (ligne2 != null) ? decouperLigne(ligne2) : new String[0];
            
            // Detection du format horizontal :
            // - premiere ligne a une premiere cellule vide
            // - deuxieme ligne existe et sa premiere cellule est un attribut (pas un nombre)
            boolean formatHorizontal = ligne1Colonnes.length > 0 
                    && ligne1Colonnes[0].trim().isEmpty()
                    && ligne2 != null
                    && ligne2Colonnes.length > 0
                    && estAttributSimple(ligne2Colonnes[0]);

            if (formatHorizontal) {
                poissons = lireFormatHorizontal(br, ligne1, ligne2, especeParDefaut);
            } else {
                poissons = lireFormatVertical(br, ligne1, especeParDefaut, ligne2);
            }

        } catch (IOException e) {
            System.err.println("Erreur lecture fichier : " + fichier);
            e.printStackTrace();
        }

        return poissons;
    }

    /**
     * Lit un fichier et retourne une population unique (si une seule espece).
     *
     * @param fichier chemin du fichier CSV
     * @return population lue, ou null si aucun poisson
     */
    public static Population lirePopulation(String fichier) {
        List<Poisson> poissons = lirePoissons(fichier);
        if (poissons.isEmpty()) return null;
        return new Population(poissons);
    }

    /**
     * Lit un fichier et retourne une liste de populations groupees par espece.
     *
     * @param fichier chemin du fichier CSV
     * @return liste des populations
     */
    public static List<Population> lireToutesPopulations(String fichier) {
        List<Poisson> poissons = lirePoissons(fichier);
        Map<String, List<Poisson>> groupes = new HashMap<>();

        for (Poisson p : poissons) {
            groupes.computeIfAbsent(p.getEspece(), k -> new ArrayList<>()).add(p);
        }

        List<Population> populations = new ArrayList<>();
        for (List<Poisson> groupe : groupes.values()) {
            populations.add(new Population(groupe));
        }

        return populations;
    }

    // ==================== TEST UNITAIRE ====================

    /**
     * Test unitaire pour la classe LectureCSV.
     */
    public static void main(String[] args) {
        System.out.println("=== TEST LECTURE CSV ===\n");
        
        String fichierTest = "test3.csv";
        
        System.out.println("--- TEST LECTURE SIMPLE ---");
        List<Poisson> poissons = lirePoissons(fichierTest);
        
        if (poissons.isEmpty()) {
            System.out.println("Aucun poisson lu depuis " + fichierTest);
        } else {
            System.out.println("Nombre de poissons lus : " + poissons.size());
            System.out.println("\nPremier poisson : " + poissons.get(0));
            System.out.println("Dernier poisson : " + poissons.get(poissons.size() - 1));
        }
        
        System.out.println("\n=== TEST TERMINE ===");
    }
}