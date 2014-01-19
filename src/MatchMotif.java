/**
 * Un wrapper pour la classe Motif qui ajoute la position.
 * Le but est de simplifier le type de retour de la méthode Image::detectObjet
 * tout en gardant la correspondance Motif/position.
 * @author quentin
 *
 */
public class MatchMotif {

	private Motif motif;
	private int x,y;

	public MatchMotif(Motif motif, int x, int y) {
        this.motif = motif;
        this.x     = x;
        this.y     = y;
	}

    public MatchMotif(Motif motif, int[] coord) {
        this.motif = motif;
        this.x     = coord[0];
        this.y     = coord[1];
	}
    
    public Motif getMotif()
    {
        return this.motif;
    }

    public int getX()
    {
        return this.x;
    }

    public int getY()
    {
        return this.y;
    }
}
