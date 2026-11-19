import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GenerateSVGGraphique {
  
    /**
         * @author Benkhelif Amina, Abbad Leaticia, Deepseek  
         * partie 4. Générer le SVG
    **/ 
    /**
     * 
     * Génère un graphique SVG pour une population donnée
     * @param population La population à visualiser
     * @param nomFichier Nom du fichier SVG à générer
    
     */
    public static void genererGraphiqueSVG(Population population, String nomFichier) {
        List<Poisson> poissons = population.getPoissons();
        String espece = population.getEspece();
        
        // 1. Récupérer les points valides (taille et infestation connues)
        List<Point> pointsValides = new ArrayList<>();
        for (Poisson p : poissons) {
            if (p.getTaille() != null && p.getInfestation() != null) {
                pointsValides.add(new Point(p.getTaille(), p.getInfestation()));
            }
        }
        
        if (pointsValides.size() < 2) {
            System.out.println("Pas assez de points pour générer le graphique de " + espece);
            return;
        }
        
        // 2. Calculer la droite de régression
        double a = 0, b = 0;
        int n = pointsValides.size();
        double sommeX = 0, sommeY = 0;
        
        for (Point p : pointsValides) {
            sommeX += p.x;
            sommeY += p.y;
        }
        
        double moyenneX = sommeX / n;
        double moyenneY = sommeY / n;
        
        double numerateur = 0, denominateur = 0;
        for (Point p : pointsValides) {
            numerateur += (p.x - moyenneX) * (p.y - moyenneY);
            denominateur += (p.x - moyenneX) * (p.x - moyenneX);
        }
        
        a = (denominateur != 0) ? numerateur / denominateur : 0;
        b = moyenneY - a * moyenneX;
        
        // 3. Déterminer les échelles pour le SVG
        double minX = Double.MAX_VALUE, maxX = Double.MIN_VALUE;
        double minY = Double.MAX_VALUE, maxY = Double.MIN_VALUE;
        
        for (Point p : pointsValides) {
            minX = Math.min(minX, p.x);
            maxX = Math.max(maxX, p.x);
            minY = Math.min(minY, p.y);
            maxY = Math.max(maxY, p.y);
        }
        
        // Ajouter une marge de 10%
        double margeX = (maxX - minX) * 0.1;
        double margeY = (maxY - minY) * 0.1;
        minX = Math.max(0, minX - margeX);
        maxX = maxX + margeX;
        minY = Math.max(0, minY - margeY);
        maxY = maxY + margeY;
        
        // Dimensions du graphique
        int width = 800;
        int height = 600;
        int paddingTop = 50;
        int paddingBottom = 80;
        int paddingLeft = 80;
        int paddingRight = 50;
        
        int graphWidth = width - paddingLeft - paddingRight;
        int graphHeight = height - paddingTop - paddingBottom;
        
        // 4. Générer le SVG
        StringBuilder svg = new StringBuilder();
        
        svg.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        svg.append("<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"").append(width).append("\" height=\"").append(height).append("\" viewBox=\"0 0 ").append(width).append(" ").append(height).append("\">\n");
        
        // Fond blanc
        svg.append("  <rect width=\"").append(width).append("\" height=\"").append(height).append("\" fill=\"white\"/>\n");
        
        // Titre
        svg.append("  <text x=\"").append(width/2).append("\" y=\"30\" text-anchor=\"middle\" font-size=\"20\" font-weight=\"bold\" fill=\"#333\">");
        svg.append("Population: ").append(espece).append(" - Régression Linéaire</text>\n");
        
        // Axes
        svg.append("  <!-- Axe X -->\n");
        svg.append("  <line x1=\"").append(paddingLeft).append("\" y1=\"").append(height - paddingBottom);
        svg.append("\" x2=\"").append(width - paddingRight).append("\" y2=\"").append(height - paddingBottom);
        svg.append("\" stroke=\"black\" stroke-width=\"2\"/>\n");
        
        svg.append("  <!-- Axe Y -->\n");
        svg.append("  <line x1=\"").append(paddingLeft).append("\" y1=\"").append(paddingTop);
        svg.append("\" x2=\"").append(paddingLeft).append("\" y2=\"").append(height - paddingBottom);
        svg.append("\" stroke=\"black\" stroke-width=\"2\"/>\n");
        
        // Flèches des axes
        svg.append("  <!-- Flèche X -->\n");
        svg.append("  <polygon points=\"").append(width - paddingRight - 5).append(",").append(height - paddingBottom);
        svg.append(" ").append(width - paddingRight).append(",").append(height - paddingBottom);
        svg.append(" ").append(width - paddingRight - 10).append(",").append(height - paddingBottom - 5);
        svg.append(" ").append(width - paddingRight - 10).append(",").append(height - paddingBottom + 5);
        svg.append("\" fill=\"black\"/>\n");
        
        svg.append("  <!-- Flèche Y -->\n");
        svg.append("  <polygon points=\"").append(paddingLeft).append(",").append(paddingTop + 5);
        svg.append(" ").append(paddingLeft).append(",").append(paddingTop);
        svg.append(" ").append(paddingLeft - 5).append(",").append(paddingTop + 10);
        svg.append(" ").append(paddingLeft + 5).append(",").append(paddingTop + 10);
        svg.append("\" fill=\"black\"/>\n");
        
        // Labels des axes
        svg.append("  <text x=\"").append(width/2).append("\" y=\"").append(height - 30).append("\" text-anchor=\"middle\" font-size=\"14\" fill=\"#555\">");
        svg.append("Taille (cm)</text>\n");
        
        svg.append("  <text x=\"25\" y=\"").append(height/2).append("\" text-anchor=\"middle\" font-size=\"14\" fill=\"#555\" transform=\"rotate(-90, 25, ").append(height/2).append(")\">");
        svg.append("Taux d'infestation (%)</text>\n");
        
        // Graduations axe X
        int nbGraduationsX = 6;
        for (int i = 0; i <= nbGraduationsX; i++) {
            double xValue = minX + (maxX - minX) * i / nbGraduationsX;
            int xPixel = paddingLeft + (int)(graphWidth * (xValue - minX) / (maxX - minX));
            
            svg.append("  <line x1=\"").append(xPixel).append("\" y1=\"").append(height - paddingBottom);
            svg.append("\" x2=\"").append(xPixel).append("\" y2=\"").append(height - paddingBottom + 5);
            svg.append("\" stroke=\"black\" stroke-width=\"1\"/>\n");
            
            svg.append("  <text x=\"").append(xPixel).append("\" y=\"").append(height - paddingBottom + 20);
            svg.append("\" text-anchor=\"middle\" font-size=\"10\" fill=\"#555\">");
            svg.append(String.format("%.0f", xValue)).append("</text>\n");
        }
        
        // Graduations axe Y
        int nbGraduationsY = 6;
        for (int i = 0; i <= nbGraduationsY; i++) {
            double yValue = minY + (maxY - minY) * (nbGraduationsY - i) / nbGraduationsY;
            int yPixel = paddingTop + (int)(graphHeight * (maxY - yValue) / (maxY - minY));
            
            svg.append("  <line x1=\"").append(paddingLeft - 5).append("\" y1=\"").append(yPixel);
            svg.append("\" x2=\"").append(paddingLeft).append("\" y2=\"").append(yPixel);
            svg.append("\" stroke=\"black\" stroke-width=\"1\"/>\n");
            
            svg.append("  <text x=\"").append(paddingLeft - 10).append("\" y=\"").append(yPixel + 3);
            svg.append("\" text-anchor=\"end\" font-size=\"10\" fill=\"#555\">");
            svg.append(String.format("%.0f", yValue)).append("</text>\n");
        }
        
        // 5. Points bleus (données réelles)
        for (Point p : pointsValides) {
            int xPixel = paddingLeft + (int)(graphWidth * (p.x - minX) / (maxX - minX));
            int yPixel = paddingTop + (int)(graphHeight * (maxY - p.y) / (maxY - minY));
            
            svg.append("  <circle cx=\"").append(xPixel).append("\" cy=\"").append(yPixel);
            svg.append("\" r=\"6\" fill=\"#3498db\" stroke=\"#2980b9\" stroke-width=\"2\"/>\n");
        }
        
        // 6. Droite rouge (régression)
        double x1 = minX;
        double y1 = a * x1 + b;
        double x2 = maxX;
        double y2 = a * x2 + b;
        
        // S'assurer que les points sont dans le graphique
        y1 = Math.max(minY, Math.min(maxY, y1));
        y2 = Math.max(minY, Math.min(maxY, y2));
        
        int x1Pixel = paddingLeft + (int)(graphWidth * (x1 - minX) / (maxX - minX));
        int y1Pixel = paddingTop + (int)(graphHeight * (maxY - y1) / (maxY - minY));
        int x2Pixel = paddingLeft + (int)(graphWidth * (x2 - minX) / (maxX - minX));
        int y2Pixel = paddingTop + (int)(graphHeight * (maxY - y2) / (maxY - minY));
        
        svg.append("  <line x1=\"").append(x1Pixel).append("\" y1=\"").append(y1Pixel);
        svg.append("\" x2=\"").append(x2Pixel).append("\" y2=\"").append(y2Pixel);
        svg.append("\" stroke=\"#e74c3c\" stroke-width=\"3\" stroke-dasharray=\"8,4\"/>\n");
        
        // 7. Légende
        svg.append("  <rect x=\"").append(width - 180).append("\" y=\"60\" width=\"160\" height=\"70\" rx=\"5\" fill=\"white\" stroke=\"#ccc\" stroke-width=\"1\"/>\n");
        
        svg.append("  <circle cx=\"").append(width - 165).append("\" cy=\"80\" r=\"5\" fill=\"#3498db\"/>\n");
        svg.append("  <text x=\"").append(width - 155).append("\" y=\"84\" font-size=\"12\" fill=\"#555\">Données réelles</text>\n");
        
        svg.append("  <line x1=\"").append(width - 170).append("\" y1=\"105\" x2=\"").append(width - 155).append("\" y2=\"105\" stroke=\"#e74c3c\" stroke-width=\"2\" stroke-dasharray=\"8,4\"/>\n");
        svg.append("  <text x=\"").append(width - 150).append("\" y=\"109\" font-size=\"12\" fill=\"#555\">Régression</text>\n");
        
        // 8. Équation de la droite
        svg.append("  <text x=\"").append(width - 170).append("\" y=\"140\" font-size=\"11\" fill=\"#e74c3c\">");
        svg.append(String.format("y = %.3fx + %.1f", a, b)).append("</text>\n");
        
        svg.append("</svg>");
        
        // Écrire le fichier
        try (FileWriter writer = new FileWriter(nomFichier)) {
            writer.write(svg.toString());
            System.out.println("Graphique SVG généré : " + nomFichier);
        } catch (IOException e) {
            System.err.println(" Erreur : " + e.getMessage());
        }
    }
    
    // Classe interne pour un point
    private static class Point {
        double x, y;
        Point(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }
}

