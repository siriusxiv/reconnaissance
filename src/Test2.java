import java.util.Calendar;
import java.util.LinkedList;

/**
 * 
 * @author malik
 *
 */
public class Test2 {

    public static void main(String[] args) throws CloneNotSupportedException {
        Image image = new Image("visage.jpg");
        Motif[] motifs = new Motif[3];
        motifs[0] = new Motif("motif_visage_2.jpg");
        motifs[1] = new Motif("motif.png");
        motifs[2] = new Motif("motif3.png");
        LinkedList<MatchMotif> matches = new LinkedList<MatchMotif>();

		long tic = Calendar.getInstance().getTimeInMillis();
        matches = image.detectObject(motifs);
		long tac = Calendar.getInstance().getTimeInMillis();
		System.out.println("Computing time: "+(tac-tic)+"ms");
        image.showCadre(matches);
    }

}
