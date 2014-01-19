import java.util.Calendar;

/**
 * Contient les fonctions de tests
 * @author malik
 *
 */
public class Test {

	public static void main(String[] args) throws CloneNotSupportedException {
		Image image = new Image("carre_plein.png");
		Motif motif = new Motif("motif.png");

		int[] match = new int[2];
		long tic = Calendar.getInstance().getTimeInMillis();
		//match = image.minSSD(motif);
		long tac = Calendar.getInstance().getTimeInMillis();
		System.out.println("Question 2.1 - Best Match: i="+match[0]+";j="+match[1]);
		System.out.println("Computing time: "+(tac-tic)+"ms");

		tic = Calendar.getInstance().getTimeInMillis();
		match = new CalculInterCorrelation(image,motif).minSSD();
		tac = Calendar.getInstance().getTimeInMillis();
		System.out.println("Question 2.2 - Best Match: i="+match[0]+";j="+match[1]);
		System.out.println("Computing time: "+(tac-tic)+"ms");
		
		//Affiche la position du motif reconnu
		image.showCadre(match[0], match[1], motif);
		
		//Montre la rotation d'un motif
		motif.rotate(35).show();
	}

}
