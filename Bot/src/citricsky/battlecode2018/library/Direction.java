package citricsky.battlecode2018.library;

import static bc.Direction.*;

public enum Direction {
	NORTH     ( 0, -1, North),
	NORTHEAST ( 1, -1, Northeast),
	EAST      ( 1,  0, East),
	SOUTHEAST ( 1,  1, Southeast),
	SOUTH     ( 0,  1, South),
	SOUTHWEST (-1,  1, Southwest),
	WEST      (-1,  0, West),
	NORTHWEST (-1, -1, Northwest);

	private Vector offset;

	private bc.Direction bcDirection;

	Direction(int x, int y, bc.Direction bcDirection) {
		offset = new Vector(x, y);
		this.bcDirection = bcDirection;
	}

	public Direction getOpposite() {
		return Direction.values()[(this.ordinal() + 4) % 8];
	}

	public Direction rotateClockwise() {
		return Direction.values()[(this.ordinal() + 1) % 8];
	}

	public Direction rotateCounterClockwise() {
		return Direction.values()[(this.ordinal() + 7) % 8];
	}

	public Vector getVectorOffset() {
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
			default:
				return null;
		}
	}

	public static Direction randomDirection() {
		return Direction.values()[(int) (Math.random() * 8)];
	}
}
