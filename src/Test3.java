import java.util.Calendar;

/**
 * 
 * @author malik
 *
 */
public class Test3 {
	
	public static void main(String[] args){
		Image image = new Image("visage_scalednoised.png");
		Motif motif = new Motif("eye.png");
		//Montre la rotation d'un motif
		long tic = Calendar.getInstance().getTimeInMillis();
		Calcul3 c = new Calcul3(image,motif,false);
		int[] match = c.calcul();
		long tac = Calendar.getInstance().getTimeInMillis();
		System.out.println("Question 2.3 - Best Match: i="+match[0]+";j="+match[1]);
		System.out.println("Total computing time: "+(tac-tic)+"ms");
		
		//Affiche la position du motif reconnu
		image.showCadre(match[0], match[1], c.getBestMotif());
		/*
		motif.resizeRatio(150).show();
		motif.resize(20, 100).show();
		motif.resizeAverage(20, 100, true);
		*/
	}
}
