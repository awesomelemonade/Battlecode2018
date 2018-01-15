package citricsky.battlecode2018.library;

import static bc.Direction.*;

public enum Direction {
	NORTH     ( 0,  1, North),
	NORTHEAST ( 1,  1, Northeast),
	EAST      ( 1,  0, East),
	SOUTHEAST ( 1, -1, Southeast),
	SOUTH     ( 0, -1, South),
	SOUTHWEST (-1, -1, Southwest),
	WEST      (-1,  0, West),
	NORTHWEST (-1,  1, Northwest),
	CENTER    ( 0,  0, Center);


	public static final Direction[] CARDINAL_DIRECTIONS = new Direction[]
			{Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};
	public static final Direction[] DIAGONAL_CENTER = new Direction[]
			{Direction.NORTHEAST, Direction.SOUTHEAST, Direction.SOUTHWEST, Direction.NORTHWEST, Direction.CENTER};
	public static final Direction[] COMPASS = new Direction[] {
			Direction.NORTH, Direction.NORTHEAST, Direction.EAST, Direction.SOUTHEAST,
			Direction.SOUTH, Direction.SOUTHWEST, Direction.WEST, Direction.SOUTHWEST
	};
	private static final int LENGTH = 8; // While there are 9 values, this is still 8 to not include CENTER in opposite/rotations/random

	private Vector offset;
	private bc.Direction bcDirection;

	Direction(int x, int y, bc.Direction bcDirection) {
		offset = new Vector(x, y);
		this.bcDirection = bcDirection;
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

	protected bc.Direction getBcDirection(){
		return bcDirection;
	}

	protected static Direction valueOf(bc.Direction bcDirection) {
		switch(bcDirection) {
			case North:
				return Direction.NORTH;
			case Northeast:
				return Direction.NORTHEAST;
			case East:
				return Direction.EAST;
			case Southeast:
				return Direction.SOUTHEAST;
			case South:
				return Direction.SOUTH;
			case Southwest:
				return Direction.SOUTHWEST;
			case West:
				return Direction.WEST;
			case Northwest:
				return Direction.NORTHWEST;
			case Center:
				return Direction.CENTER;
			default:
				return null;
		}
	}

	public static Direction randomDirection() {
		return Direction.values()[(int) (Math.random() * LENGTH)];
	}
}
