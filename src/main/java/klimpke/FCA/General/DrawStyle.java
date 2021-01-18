package klimpke.FCA.General;

public enum DrawStyle {
	UNSCALED (0), SCALED (1), LOGARITHMIC (2), ADDITIVE(3), CASCADING(4);
	
	private int id;
	
	DrawStyle(int id){
		this.id = id;
	}
	
	public int id() {
		return this.id;
	}
}
