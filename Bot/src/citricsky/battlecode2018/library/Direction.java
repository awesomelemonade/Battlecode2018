package citricsky.battlecode2018.library;

public enum Direction {
	NORTH(new Vector(0, -1), bc.Direction.North),
	NORTHEAST(new Vector(1, -1), bc.Direction.Northeast),
	EAST(new Vector(1, 0), bc.Direction.East),
	SOUTHEAST(new Vector(1, 1), bc.Direction.Southeast),
	SOUTH(new Vector(0, 1), bc.Direction.South),
	SOUTHWEST(new Vector(-1, 1), bc.Direction.Southwest),
	WEST(new Vector(-1, 0), bc.Direction.West),
	NORTHWEST(new Vector(-1, -1), bc.Direction.Northwest);
	private Vector offset;
	private bc.Direction bcDirection;
	private Direction(Vector offset, bc.Direction bcDirection) {
		this.offset = offset;
		this.bcDirection = bcDirection;
	}
	public Direction getOpposite() {
		return Direction.values()[(this.ordinal()+4)%8];
	}
	public Direction rotateClockwise() {
		return Direction.values()[(this.ordinal()+1)%8];
	}
	public Direction rotateCounterClockwise() {
		return Direction.values()[(this.ordinal()+7)%8];
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
		return Direction.values()[(int)(Math.random()*8)];
	}
}
