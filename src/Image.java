import java.awt.Color;
import java.util.LinkedList;

import ij.ImagePlus;
import ij.gui.NewImage;
import ij.io.FileSaver;
import ij.process.ImageProcessor;

/**
 * Wrapper pour le type ImagePlus de imageJ.
 * @author malik
 *
 */
public class Image {

	private ImagePlus image;

	/**
	 * Création d'une image vierge à partir de ses dimensions
	 * @param w largeur
	 * @param h hauteur
	 */
	public Image(int w,int h) {
		image = NewImage.createByteImage("test",w,h,1,NewImage.FILL_BLACK);
		image.getProcessor().setColor(Color.WHITE);
	}
	/**
	 * Création d'une image à partir d'une image sur le disque en donnant son chemin d'accès.
	 */
	public Image(String path){
		ImagePlus imageplus = new ImagePlus(path);
		Image img = new Image(imageplus.getWidth(),imageplus.getHeight());
		for(int i = 0; i<imageplus.getWidth() ; i++){
			for(int j = 0; j<imageplus.getHeight() ; j++){
				int[] rgb = imageplus.getPixel(i, j);
				img.setPixel(i, j, toGrey(rgb));
			}
		}
		image=img.image;
	}
	/**
	 * Constructeur qui copie une image
	 * @param imageACopier
	 */
	public Image(Image imageACopier){
		this(imageACopier.getWidth(),imageACopier.getHeight());
		for(int i = 0; i<imageACopier.getWidth() ; i++){
			for(int j = 0; j<imageACopier.getHeight() ; j++){
				this.setPixel(i, j, imageACopier.getPixel(i, j));
			}
		}
	}
	/**
	 * Crée une image à partir d'un ImageProcessor
	 * @param ip
	 */
	public Image(ImageProcessor ip){
		image = new ImagePlus("test", ip);
	}
	/**
	 * Met en gris une image en couleur
	 * @param rgb
	 * @return
	 */
	private static int toGrey(int[] rgb){
		return (int) (rgb[0]*0.299+rgb[1]*0.587+rgb[2]*0.114);
	}

	/**
	 * Accesseur
	 * @return la largeur de l'image
	 */
	public int getWidth() {
		return image.getWidth();
	}
	/**
	 * Accesseur
	 * @return la hauteur de l'image
	 */
	public int getHeight() {
		return image.getHeight();
	}

	/**
	 * "Allume" le pixel de coordonnees (x,y) avec la valeur value.
	 * de points traces
	 * @param x
	 * @param y
	 */
	public void setPixel(int x,int y, int value) {
		byte[] pixels = (byte[]) image.getProcessor().getPixels();
		pixels[y*image.getWidth()+x] =  (byte)value;
	}
	/**
	 * Permet d'obtenir la valeur d'un pixel (entre 0 et 255)
	 * @param x
	 * @param y
	 * @return
	 */
	public int getPixel(int x, int y){
		return image.getPixel(x, y)[0];
	}

	/**
	 * Calcule la norme entre le motif et l'image au point x et y.
	 * @param x
	 * @param y
	 * @param motif
	 * @return
	 */
	public int SSD(int x, int y, Motif motif){
		int res = 0;
		for(int i = 0; i<motif.getWidth() ; i++){
			for(int j = 0; j<motif.getHeight() ; j++){
				int dist = motif.getPixel(i, j)-this.getPixel(x+i, y+j);
				res+=dist*dist;
			}
		}
		return res;
	}


	/**
	 * Renvoie la position sur l'image où le motif matche le plus.
	 * @param motif
	 * @return Un couple d'int : l'abscisse et l'ordonnée x de l'image
	 */
	public int[] minSSD(Motif motif){
		int[] res = new int[2];
		res[0]=0; res[1]=0;
		int min = this.SSD(0, 0, motif);
		for(int i = 0; i<this.getWidth()-motif.getWidth() ; i++){
			for(int j = 0; j<this.getHeight()-motif.getHeight() ; j++){
				int ssd = this.SSD(i, j, motif);
				if(min>ssd){
					res[0]=i; res[1]=j;
					min=ssd;
				}
			}
		}
		return res;
	}

    /**
     * Renvoie la position sur une région de l'image où le motif matche le plus.
     *
     * @param  motif
     * @param  region les 4 coordonées de la région.
     * @return Un triplet d'entiers : x, y et la distance
     */
    public int[] minSSD(Motif motif, int[] region){
		int[] res = new int[3];
		res[0]=0; res[1]=0;
		int min = this.SSD(region[0], region[1], motif);
        
		for(int i = region[0]; i<region[2]; i++)
        {
			for(int j = region[1]; j<region[3]; j++)
            {
                if(i+motif.getWidth()<this.getWidth() && j+motif.getHeight()<this.getHeight())
                {
				    int ssd = this.SSD(i, j, motif);
				    if(min>ssd)
                    {
				    	res[0]=i; res[1]=j;
				    	min=ssd;
				    }
                }
			}
		}
        res[2] = min;
		return res;
	}

    /**
     * Détermine la meilleure position pour chacun des motifs.
     *
     * @param motifs les différents motifs.
     */
    public LinkedList<MatchMotif> detectObject(Motif[] motifs)
    {
        LinkedList<MatchMotif> matches = new LinkedList<MatchMotif>();
        LinkedList<int[]> regions = this.getRois();

        for(Motif motif : motifs)
        {
		    int min = this.SSD(0, 0, motif); 
            int res[] = new int[3];
            for(int[] region : regions)
            {
                res = this.minSSD(motif, region);
                if(min > res[2])
                    min = res[2];
            }
            matches.add(new MatchMotif(motif,res[0],res[1]));
        }
        return matches;
    }
    
    /**
     * Récupère des régions d'intérêt.
     * Pour ce faire, on récupère les bords de l'image, forme un rectange le plus petit possible
     * les contenants, on découpe ensuite la région selon un paramètre. 
     * @return une liste de tableau de 4 entiers contenant les coordonées des coins supérieur gauche et inférieur droit du rectangle
     */
    public LinkedList<int[]> getRois()
    {
        int i,j;
        int rectSize = 200; // Taille des rectangles (au moins égale à la plus grande dimensions des motifs.
        int[] grdRect = new int[4]; 
        grdRect[0]=this.getWidth();grdRect[1]=this.getHeight();grdRect[2]=0;grdRect[3]=0;
        LinkedList<int[]> regions = new LinkedList<int[]>();
        Image copie = new Image(this);
        
        // On trouve les bords et transforme l'image en image binaire.
        copie.image.getProcessor().findEdges();
        copie.image.getProcessor().autoThreshold();
        
        // On récupère le plus petit rectangle englobant tout les points allumés.
        for(i=0;i<this.getWidth();i++)
        {
            for(j=0;j<this.getWidth();j++)
            {
                int pixel = copie.getPixel(i,j);
                if(pixel==255)
                {
                    if(i<grdRect[0])
                        grdRect[0] = i;
                    
                    if(j<grdRect[1])
                        grdRect[1] = j;

                    if(i>grdRect[2])
                        grdRect[2] = i;

                    if(j>grdRect[3])
                        grdRect[3] = j;
                }
            }
        }
        
        /* Le but de cette partie aurait été de découper le rectangle principale en rectangle de taille inférieure.
		   En supposant que les motifs contiennent nécessairement un bord, on aurait ensuite pu éliminer les zones contenant une seule
		   couleur (donc pas de bord). Toutefois, cette méthode pose des problèmes dans le cas où le motif intersecte plusieurs
		   régions ou si le motif n'est pas rectangle.
		 
        // On découpe le grand rectangle en petits
        for(i=grdRect[0];i<grdRect[2];i+=rectSize)
        {
            for(j=grdRect[1];j<grdRect[3];j+=rectSize)
            {
                int[] region = new int[4];
                region[0] = i;
                region[1] = j;
                // Il est possible que les petits rectangles contiennent des zones qui sortent du grand. On suppose que les quelques
                // pixels ainsi rajoutés ne sont pas trop pénalisant pour les performances.
                region[2] = (i+rectSize<this.getWidth()) ? i+rectSize  : this.getWidth();
                region[3] = (j+rectSize<this.getHeight()) ? j+rectSize : this.getHeight();
                regions.add(region);
            }
        }
        */
        regions.add(grdRect);

        return regions;
    }

	/**
	 * Enregistre l'image construite au format PNG
	 * @param fileName le nom du fichier
	 */
	public void saveAsPng(String fileName) {
		FileSaver fs = new FileSaver(image);
		fs.saveAsPng(fileName+".png");
	}
	/** 
	 * Affiche l'image
	 * 
	 */	
	public void show(){
		image.show();
	}
	/**
	 * Affiche l'image de telle sorte qu'un cadre soit collé dessus.
	 * Le cadre fait la taille du motif et son coin supérieur gauche
	 * a pour coordonnées (x,y)
	 * @param x
	 * @param y
	 * @param motif
	 */
	public void showCadre(int x, int y, Motif motif){
		this.encadre(x, y, motif).show();
	}

    /**
     * Affiche une image avec tout les motifs encadrés
     * @param motifs
     */
    public void showCadre(LinkedList<MatchMotif> motifs)
    {
        Image image = new Image(this);

        for(MatchMotif motif : motifs)
        {
            image = image.encadre(motif.getX(),motif.getY(),motif.getMotif());
        }

        image.show();
    }
    
	/**
	 * Renvoie une image avec un cadre de la taille du motif dont
	 * le coin supérieur gauche est aux coordonnée (x,y)
	 * @param x
	 * @param y
	 * @param motif
	 * @return
	 */
	private Image encadre(int x, int y, Motif motif){
		Image imageEncadree = new Image(this);
		for(int i = x; i < x+motif.getWidth() ; i++){
			if(i%2==0)
				imageEncadree.setPixel(i, y, 50);
			else	
				imageEncadree.setPixel(i, y, 200);
		}
		for(int i = x; i < x+motif.getWidth() ; i++){
			if(i%2==0)
				imageEncadree.setPixel(i, y+motif.getHeight(), 50);
			else	
				imageEncadree.setPixel(i, y+motif.getHeight(), 200);
		}
		for(int j = y; j < y+motif.getHeight() ; j++){
			if(j%2==0)
				imageEncadree.setPixel(x, j, 50);
			else	
				imageEncadree.setPixel(x, j, 200);
		}
		for(int j = y; j < y+motif.getHeight() ; j++){
			if(j%2==0)
				imageEncadree.setPixel(x+motif.getWidth(), j, 50);
			else	
				imageEncadree.setPixel(x+motif.getWidth(), j, 200);
		}
		return imageEncadree;
	}

    public void showRegion(LinkedList<int[]> rects)
    {
        Image image = new Image(this);

        for(int[] rect : rects)
        {
            image = image.encadre(rect[0],rect[1],rect[2]-rect[0],rect[3]-rect[1]);
        }
        image.show();
    }
	
    private Image encadre(int x, int y, int w, int h){
		Image imageEncadree = new Image(this);
		for(int i = x; i < x+w; i++){
			if(i%2==0)
				imageEncadree.setPixel(i, y, 50);
			else	
				imageEncadree.setPixel(i, y, 200);
		}
		for(int i = x; i < x+w; i++){
			if(i%2==0)
				imageEncadree.setPixel(i, y+h, 50);
			else	
				imageEncadree.setPixel(i, y+h, 200);
		}
		for(int j = y; j < y+h; j++){
			if(j%2==0)
				imageEncadree.setPixel(x, j, 50);
			else	
				imageEncadree.setPixel(x, j, 200);
		}
		for(int j = y; j < y+h; j++){
			if(j%2==0)
				imageEncadree.setPixel(x+w, j, 50);
			else	
				imageEncadree.setPixel(x+w, j, 200);
		}
		return imageEncadree;
    }	
	/**
	 * Obtient le processeur lié à la l'image
	 * @return
	 */
	public ImageProcessor getChannelProcessor() {
		return image.getProcessor();
	}
}
