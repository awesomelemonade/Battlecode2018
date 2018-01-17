package citricsky.battlecode2018.util;

import java.util.function.Predicate;

import citricsky.battlecode2018.library.Direction;
import citricsky.battlecode2018.library.GameController;
import citricsky.battlecode2018.library.MapLocation;

public class Util {
	public static final Predicate<MapLocation> PASSABLE_PREDICATE = location -> {
		if (!location.isOnMap()) {
			return false;
		}
		if (location.hasUnitAtLocation()) {
			if (location.getUnit().getTeam() == GameController.INSTANCE.getTeam()) {
				if (location.getUnit().isStructure()) {
					return false;
				}
			}
		}
		return location.isPassableTerrain();
	};
	private static int[] buildArray;
	public static void init() {
		buildArray = new int[256];
		for(int i=0;i<buildArray.length;++i) {
			buildArray[i] = calcBuildArray(i);
		}
	}
	public static int calcBuildArray(int neighbors) {
		int counter = 0;
		for(Direction direction: Direction.COMPASS) {
			if(((neighbors >>> direction.ordinal()) & 1) == 0){
				counter++;
			}
		}
		//flood fill
		for(Direction direction: Direction.COMPASS) {
			if(((neighbors >>> direction.ordinal()) & 1) == 0) {
				neighbors = floodFill(neighbors, direction.ordinal());
				break;
			}
		}
		//loop over
		for(Direction direction: Direction.COMPASS) {
			if(((neighbors >>> direction.ordinal()) & 1) == 0) {
				return -1;
			}
		}
		return counter;
	}
	public static int floodFill(int neighbors, int bit) {
		if(((neighbors >>> bit) & 1) == 1) {
			return neighbors; //already filled
		}else {
			neighbors = neighbors | (1 << bit);
			if(bit==Direction.NORTH.ordinal()) {
				neighbors = floodFill(neighbors, Direction.NORTHEAST.ordinal());
				neighbors = floodFill(neighbors, Direction.NORTHWEST.ordinal());
				neighbors = floodFill(neighbors, Direction.WEST.ordinal());
				neighbors = floodFill(neighbors, Direction.EAST.ordinal());
			}else if(bit==Direction.NORTHWEST.ordinal()) {
				neighbors = floodFill(neighbors, Direction.NORTH.ordinal());
				neighbors = floodFill(neighbors, Direction.WEST.ordinal());
			}else if(bit==Direction.WEST.ordinal()) {
				neighbors = floodFill(neighbors, Direction.NORTHWEST.ordinal());
				neighbors = floodFill(neighbors, Direction.SOUTHWEST.ordinal());
				neighbors = floodFill(neighbors, Direction.NORTH.ordinal());
				neighbors = floodFill(neighbors, Direction.SOUTH.ordinal());
			}else if(bit==Direction.SOUTHWEST.ordinal()) {
				neighbors = floodFill(neighbors, Direction.WEST.ordinal());
				neighbors = floodFill(neighbors, Direction.SOUTH.ordinal());
			}else if(bit==Direction.SOUTH.ordinal()) {
				neighbors = floodFill(neighbors, Direction.SOUTHWEST.ordinal());
				neighbors = floodFill(neighbors, Direction.SOUTHEAST.ordinal());
				neighbors = floodFill(neighbors, Direction.WEST.ordinal());
				neighbors = floodFill(neighbors, Direction.EAST.ordinal());
			}else if(bit==Direction.SOUTHEAST.ordinal()) {
				neighbors = floodFill(neighbors, Direction.SOUTH.ordinal());
				neighbors = floodFill(neighbors, Direction.EAST.ordinal());
			}else if(bit==Direction.EAST.ordinal()) {
				neighbors = floodFill(neighbors, Direction.SOUTHEAST.ordinal());
				neighbors = floodFill(neighbors, Direction.NORTHEAST.ordinal());
				neighbors = floodFill(neighbors, Direction.NORTH.ordinal());
				neighbors = floodFill(neighbors, Direction.SOUTH.ordinal());
			}else if(bit==Direction.NORTHEAST.ordinal()) {
				neighbors = floodFill(neighbors, Direction.EAST.ordinal());
				neighbors = floodFill(neighbors, Direction.NORTH.ordinal());
			}
			return neighbors;
		}
	}
	public static int getNeighbors(MapLocation location, Predicate<MapLocation> predicate) {
		int neighbors = 0;
		for(Direction direction: Direction.COMPASS) {
			if(predicate.test(location.getOffsetLocation(direction))) {
				neighbors = neighbors | (1 << direction.ordinal());
			}
		}
		return neighbors;
	}
	public static int getNeighbors(Direction... directions) {
		int neighbors = 0;
		for(Direction direction: directions) {
			neighbors = neighbors | (1 << direction.ordinal());
		}
		return neighbors;
	}
	public static int getBuildArray(int neighbors) {
		return buildArray[neighbors];
	}
}
