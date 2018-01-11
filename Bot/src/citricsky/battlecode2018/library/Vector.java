package citricsky.battlecode2018.library;

public class Vector {
	private int x;
	private int y;
	public Vector(int x, int y) {
		this.x = x;
		this.y = y;
	}
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public Vector add(Vector vector) {
		return add(vector.getX(), vector.getY());
	}
	public Vector add(int x, int y) {
		return new Vector(this.x+x, this.y+y);
	}
	public int getDistanceSquared(Vector vector) {
		int dx = x-vector.getX();
		int dy = y-vector.getY();
		return dx*dx+dy*dy;
	}
	public Vector invert() {
		return new Vector(-x, -y);
	}
}
