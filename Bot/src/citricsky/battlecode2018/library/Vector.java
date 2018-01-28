package citricsky.battlecode2018.library;

import java.util.Objects;

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

	public Vector getUnit() {
		double length = Math.sqrt(getDistanceSquared(0, 0));
		return new Vector((int) Math.round(x / length), (int) Math.round(y / length));
	}

	public Direction getDirectionTowards(Vector vector) {
		return getDirectionTowards(vector.getX(), vector.getY());
	}

	public Direction getDirectionTowards(int x, int y) {
		return Direction.valueOf(add(-x, -y).invert());
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof Vector) {
			Vector vector = (Vector)o;
			return x == vector.getX() && y == vector.getY();
		}
		return false;
	}
	
	@Override
	public String toString() {
		return String.format("Vector[%d, %d]", x, y);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(x, y);
	}
}
