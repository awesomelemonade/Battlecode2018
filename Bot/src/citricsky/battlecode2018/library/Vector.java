package citricsky.battlecode2018.library;

public class Vector {
	private int x, y;

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
		return new Vector(this.x + x, this.y + y);
	}

	public int getDistanceSquared(Vector vector) {
		return getDistanceSquared(vector.getX(), vector.getY());
	}

	public int getDistanceSquared(int x, int y) {
		int dx = this.x - x;
		int dy = this.y - y;

		return dx * dx + dy * dy;
	}

	public Vector invert() {
		return new Vector(-x, -y);
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof Vector) {
			Vector vector = (Vector)o;
			return x == vector.getX() && y == vector.getY();
		}
		return false;
	}
}
