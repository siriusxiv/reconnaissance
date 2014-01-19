import java.awt.Color;

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
	
	/**
	 * Obtient le processeur lié à la l'image
	 * @return
	 */
	public ImageProcessor getChannelProcessor() {
		return image.getProcessor();
	}
}