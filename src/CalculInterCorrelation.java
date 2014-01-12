/**
 * Classe utilisée pour les calculs de la question 2.2
 * Chaque voisinage de l'image ainsi que le motif sont normalisés
 * dans les calculs.
 * @author malik
 *
 */
public class CalculInterCorrelation {
	private Motif motif;
	private Image image;
	/**
	 * sMN contient la norme de chaque voisinage de l'image
	 */
	private double[] sMN;
	
	public CalculInterCorrelation(Image image, Motif motif){
		this.motif=motif;
		this.image=image;
		double[] p2,sN;
		p2=new double[image.getWidth()*image.getHeight()];
		for(int i = 0 ; i<image.getWidth() ; i++){
			for(int j = 0 ; j < image.getHeight() ; j++){
				p2[i+image.getWidth()*j]=image.getPixel(i, j)*image.getPixel(i, j);
			}
		}
		int diffHeight = image.getHeight()-motif.getHeight();
		sN=new double[image.getWidth()	* diffHeight];
		for(int i = 0 ; i<image.getWidth() ; i++){
			for(int j = 0 ; j < diffHeight ; j++){
				sN[i+image.getWidth()*j]=0;
				for(int k = 0 ; k<motif.getHeight() ; k++){
					sN[i+image.getWidth()*j]+=p2[i+image.getWidth()*(j+k)];
				}
			}
		}
		int diffWidth = image.getWidth() - motif.getWidth();
		sMN=new double[diffWidth * diffHeight];
		for(int i = 0 ; i<diffWidth ; i++){
			for(int j = 0 ; j < diffHeight ; j++){
				sMN[i+diffWidth*j]=0;
				for(int k = 0 ; k<motif.getWidth() ; k++){
					sMN[i+diffWidth*j]+=sN[i+k+image.getWidth()*j];
				}
				sMN[i+diffWidth*j]=Math.sqrt(sMN[i+diffWidth*j]);
			}
		}
	}
	
	/**
	 * Calcule la distance au motif du voisinage en i et j de l'image
	 * @param i
	 * @param j
	 * @return
	 */
	public double SSD(int i, int j){
		return 2*(1-cos(i,j));
	}
	
	/**
	 * Calcule le cosinus entre le voisinage en i et j de l'image
	 * et le motif
	 * @param i
	 * @param j
	 * @return
	 */
	private double cos(int i, int j){
		double res = 0;
		for(int m = 0 ; m<motif.getWidth() ; m++){
			for(int n = 0 ; n<motif.getHeight() ; n++){
				res+=(double) image.getPixel(i+m,j+n)
						* motif.getNormalizedVector()[m+motif.getWidth()*n]
						/ sMN[i+(image.getWidth()-motif.getWidth())*j];
			}
		}
		return res;
	}
	
	/**
	 * Renvoie la position sur l'image où le motif matche le plus.
	 * @return Un couple d'int : l'abscisse et l'ordonnée x de l'image
	 */
	public int[] minSSD(){
		int[] res = new int[2];
		res[0]=0; res[1]=0;
		double min = this.SSD(0, 0);
		for(int i = 0; i<image.getWidth()-motif.getWidth() ; i++){
			for(int j = 0; j<image.getHeight()-motif.getHeight() ; j++){
				double ssd = this.SSD(i, j);
				if(min>ssd){
					res[0]=i; res[1]=j;
					min=ssd;
				}
			}
		}
		return res;
	}
}
