package reconnaissance;

/**
 * Un motif est en tout point similaire à une image
 * @author malik
 *
 */
public class Motif extends Image {

	public Motif(int w, int h) {
		super(w, h);
	}
	public Motif(String path) {
		super(path);
	}
}
