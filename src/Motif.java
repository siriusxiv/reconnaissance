import ij.process.ImageProcessor;

/**
 * Un motif est en tout point similaire à une image
 * On lui ajoute cependant une norme, ainsi qu'un vecteur
 * normalisé pour ne les calculer qu'une seule fois
 * (à la création d'une instance de Motif).
 * @author malik
 *
 */
public class Motif extends Image {

	private double norme;
	private double[] normalizedVector;

	/**
	 * Initialise un motif entièrement noir
	 * La vecteur normalisé est fixé comme étant nul.
	 * @param w
	 * @param h
	 */
	public Motif(int w, int h) {
		super(w, h);
		norme=0;
		normalizedVector=new double[w*h];
		for(int i = 0; i<normalizedVector.length ; i++){
			normalizedVector[i]=0;
		}
	}

	/**
	 * On calcule la norme et on crée le vecteur normalisé
	 * pour l'utiliser plus tard dans les calculs.
	 * @param path
	 */
	public Motif(String path) {
		super(path);
		norme=0;
		for(int i = 0; i<this.getWidth() ; i++){
			for(int j = 0; j<this.getHeight() ; j++){
				norme+=this.getPixel(i, j)*this.getPixel(i, j);
			}
		}
		norme=Math.sqrt(norme);
		normalizedVector=new double[this.getWidth()*this.getHeight()];
		if(norme==0){
			for(int i = 0; i<normalizedVector.length ; i++){
				normalizedVector[i]=0;
			}
		}else{
			for(int i = 0; i<this.getWidth() ; i++){
				for(int j = 0; j<this.getHeight() ; j++){
					normalizedVector[i+this.getWidth()*j]=(double)this.getPixel(i, j)/norme;
				}
			}
		}
	}
	public Motif(ImageProcessor ip){
		super(ip);
		norme=0;
		for(int i = 0; i<this.getWidth() ; i++){
			for(int j = 0; j<this.getHeight() ; j++){
				norme+=this.getPixel(i, j)*this.getPixel(i, j);
			}
		}
		norme=Math.sqrt(norme);
		normalizedVector=new double[this.getWidth()*this.getHeight()];
		if(norme==0){
			for(int i = 0; i<normalizedVector.length ; i++){
				normalizedVector[i]=0;
			}
		}else{
			for(int i = 0; i<this.getWidth() ; i++){
				for(int j = 0; j<this.getHeight() ; j++){
					normalizedVector[i+this.getWidth()*j]=(double)this.getPixel(i, j)/norme;
				}
			}
		}
	}

	/**
	 * @return the norm
	 */
	public double getNorme() {
		return norme;
	}
	/**
	 * @return the norm
	 */
	public double[] getNormalizedVector() {
		return normalizedVector;
	}
	
	/**
	 * Tourne le motif de l'angle désiré (en degré)
	 * @param angle
	 * @return
	 */
	public Motif rotate(double angle){
		ImageProcessor ip = this.getChannelProcessor().duplicate();
		ip.rotate(angle);
		return new Motif(ip);
	}
	/**
	 * change les dimmensions de l'image en gardant le même ratio entre la largeur et la longueur
	 * @param newWidth
	 * @return Le motif réduit ou agrandi
	 */
	public Motif resizeRatio(int newWidth){
		return new Motif(this.getChannelProcessor().resize(newWidth));
	}
	/**
	 * change les dimensions de l'image
	 * @param newWidth
	 * @param newHeight
	 * @return Le motif réduit ou agrandi
	 */
	public Motif resize(int newWidth, int newHeight){
		return new Motif(this.getChannelProcessor().resize(newWidth,newHeight));
	}
	/**
	 * Utilise un filtre moyenneur avant de réduire l'image
	 * @param newWidth
	 * @param newHeight
	 * @param useAverage
	 * @return Le motif réduit ou agrandi
	 */
	public Motif resizeAverage(int newWidth, int newHeight, boolean useAverage){
		return new Motif(this.getChannelProcessor().resize(newWidth,newHeight,useAverage));
	}
}
