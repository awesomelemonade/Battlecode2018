
public enum Direction {
	NORTH     ( 0,  1),
	NORTHEAST ( 1,  1),
	EAST      ( 1,  0),
	SOUTHEAST ( 1, -1),
	SOUTH     ( 0, -1),
	SOUTHWEST (-1, -1),
	WEST      (-1,  0),
	NORTHWEST (-1,  1),
	CENTER    ( 0,  0);


	public static final Direction[] CARDINAL_DIRECTIONS = new Direction[]
			{Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};
	public static final Direction[] DIAGONAL_CENTER = new Direction[]
			{Direction.NORTHEAST, Direction.SOUTHEAST, Direction.SOUTHWEST, Direction.NORTHWEST, Direction.CENTER};
	public static final Direction[] COMPASS = new Direction[] {
			Direction.NORTH, Direction.NORTHEAST, Direction.EAST, Direction.SOUTHEAST,
			Direction.SOUTH, Direction.NORTHWEST, Direction.WEST, Direction.SOUTHWEST
	};
	private static final int LENGTH = 8; // While there are 9 values, this is still 8 to not include CENTER in opposite/rotations/random

	private Vector offset;

	Direction(int x, int y) {
		offset = new Vector(x, y);
	}

	public Direction getOpposite() {
		if (this == CENTER) return this;
		return Direction.values()[(this.ordinal() + 4) % LENGTH];
	}

	public Direction rotateClockwise() {
		if (this == CENTER) return this;
		return Direction.values()[(this.ordinal() + 1) % LENGTH];
	}

	public Direction rotateCounterClockwise() {
		if (this == CENTER) return this;
		return Direction.values()[(this.ordinal() + 7) % LENGTH];
	}

	public Vector getOffsetVector() {
		return offset;
	}

	public static Direction valueOf(Vector vector) {
		for (Direction d : Direction.values()) {
			if (vector.getUnit() == d.getOffsetVector()) return d;
		}
		return CENTER;
	}

	public static Direction randomDirection() {
		return Direction.values()[(int) (Math.random() * LENGTH)];
	}
}
