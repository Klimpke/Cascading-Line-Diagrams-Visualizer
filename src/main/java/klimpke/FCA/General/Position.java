package klimpke.FCA.General;

/**
 * 
 * @author Klimpke
 *
 */
public class Position{
	private int x;
	private int y;
	
	public Position(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public void setY(int newY) {
		this.y = newY;
	}

	public Position minus(Position pos) {
		return new Position(this.x-pos.getX(), this.y-pos.getY());
	}

	public void add(Position vector) {
		this.x += vector.getX();
		this.y += vector.getY();
	}

	public void setX(int x) {
		this.x = x;
	}

	public double getDistanceTo(Position pos) {
		return Math.sqrt(Math.pow(Math.abs(pos.getX()-this.getX()),2)+Math.pow(Math.abs(pos.getY()-this.getY()),2));
	}
}
