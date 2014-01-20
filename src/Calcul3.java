import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Permet de répondre à la Question 2.3
 * @author malik
 *
 */
public class Calcul3 {
	private Image image;
	private Motif originalMotif;
	private List<Motif> motifs;
	private Motif bestMotif=null;
	
	/**
	 * Initialise le calcul.
	 * Si on cherche des motifs de différentes tailles, rotation doit valoir FAUX,
	 * si on cherche des motifs issues de la rotation du premier, rotation
	 * doit valoir VRAI.
	 * @param motif
	 * @param rotation
	 */
	public Calcul3(Image image, Motif motif, boolean rotation){
		this.image=image;
		this.originalMotif = motif;
		motifs = new ArrayList<Motif>();
		if(rotation)
			this.addMotifsSemblablesRotation(2, 5);
		else
			this.addMotifsSemblablesEtirement(5, 10);
		System.out.println(motifs.size()+" patterns were made");
	}
	/**
	 * Renvoie une liste de 2*n+1 motifs qu'on a fait tournée d'un angle 
	 * de (pasDegres * i) où i est dans [-nombreDeMotifs;nombreDeMotifs]
	 * @param pasDegres
	 * @param nombreDeMotifs
	 * @return
	 */
	private List<Motif> addMotifsSemblablesRotation(double pasDegres, int nombreDeMotifs){
		for(int i = -nombreDeMotifs ; i<nombreDeMotifs ; i++){
			motifs.add(originalMotif.rotate(pasDegres*i));
		}
		return motifs;
	}
	/**
	 * Renvoie une liste de 2*n+1 motifs qu'on a étiré
	 * de (pas * i)% où i est dans [-nombreDeMotifs;nombreDeMotifs]
	 * @param pas
	 * @param nombreDeMotifs
	 * @return
	 */
	private List<Motif> addMotifsSemblablesEtirement(int pas, int nombreDeMotifs){
		int w = originalMotif.getWidth();
		for(int i = -nombreDeMotifs ; i<nombreDeMotifs ; i++){
			motifs.add(originalMotif.resizeRatio(
					(100+pas*i)*w/100
					));
		}
		return motifs;
	}

	/**
	 * Affiche tous les motifs stockés dans cette instance de Calcul3
	 */
	public void show(){
		for(Motif m : motifs){
			m.show();
		}
	}

	/**
	 * Renvoie la position que matche le plus
	 * @return
	 */
	public int[] calcul(){
		int[] res = new int[2];
		long tic = Calendar.getInstance().getTimeInMillis();
		Calcul3SubResult resTemp = new CalculInterCorrelation(image,motifs.get(0)).minSSDforCalcul3();
		double min = resTemp.min;
		this.bestMotif=motifs.get(0);
		for(int i = 1 ; i<motifs.size() ; i++){
			long tac = Calendar.getInstance().getTimeInMillis();
			System.out.println("motif #"+(i-1)+" Computing Time:"+(Calendar.getInstance().getTimeInMillis()-tic)+" Norm:"+resTemp.min);
			tic = tac;
			resTemp = new CalculInterCorrelation(image,motifs.get(i)).minSSDforCalcul3();
			if(resTemp.min<min){
				res[0]=resTemp.res[0];
				res[1]=resTemp.res[1];
				min = resTemp.min;
				this.bestMotif=motifs.get(i);
			}
		}
		return res;
	}
	
	/**
	 * Getter du meilleur motif trouvé.
	 * Renvoie null si le calcul n'a pas encore été fait.
	 * @return
	 */
	public Motif getBestMotif(){
		return bestMotif;
	}
}
