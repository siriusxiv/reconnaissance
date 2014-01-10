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
	 * "allume" le pixel de coordonnees (x,y) avec la valeur value.
	 * de points traces
	 * @param x
	 * @param y
	 */
	public void setPixel(int x,int y, int value) {
		byte[] pixels = (byte[]) image.getProcessor().getPixels();
		pixels[y*image.getWidth()+x] =  (byte)value;
	}
}
