package reconnaissance;

import java.awt.Color;

import ij.ImagePlus;
import ij.gui.NewImage;

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
	 * Question 2.2
	 * distX = norme de X, distP = norme de P; distPX = produit scalaire entre X et P
	 * @param x
	 * @param y
	 * @param motif
	 * @return
	 */
	public int SSD2(int x, int y, Motif motif){
		int res = 0;int distX=0; int distP=0, distPX=0;
		for(int i = 0; i<motif.getWidth() ; i++){
			for(int j = 0; j<motif.getHeight() ; j++){
				distX = distX+ (this.getPixel(i, j)*this.getPixel(x+i, y+j));
				distP= distP+ motif.getPixel(i, j)*motif.getPixel(x+i, y+j);
				distPX= distPX + motif.getPixel(i, j)*this.getPixel(x+i, y+j);		
			}
		}
		res = distX*distX + distP*distP - 2*distPX;
		return res;
	}

	public int[] minSSD2(Motif motif){
		int[] res = new int[2];
		res[0]=0; res[1]=0;
		int min= this.SSD2(0, 0, motif);
		int N = this.getHeight();
		int M = this.getWidth();
		int[][] Sn = new int[N][M];
		for (int i=0; i<=N;i++){
			for( int j=0; j<=M;j++){
				for (int k=0; k<=motif.getWidth();k++) {Sn[i][j]+= motif.getPixel(i, j)*motif.getPixel(i, j+k);}
			}
		}
		int[][] Smn = new int[N][M];
		for (int i=0; i<=N;i++){
			for( int j=0; j<=M;j++){
				for (int k=0;k<=motif.getHeight();k++){Smn[i][j]+=Sn[i+k][j];}
			}
		}
	}
	//reste � trouver le minimum de Smn[i][j] et �a donnera la position de la forme qu'on cherche ??
	//C'est �a ou pas ? Help !!!
}