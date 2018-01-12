package citricsky.battlecode2018.main;

import citricsky.battlecode2018.library.Direction;
import citricsky.battlecode2018.library.GameController;
import citricsky.battlecode2018.library.Unit;

public class EarthPlayer {
	private static boolean[] buildArray;
	public static void init() {
		buildArray = new boolean[256];
		for(int i=0;i<buildArray.length;++i) {
			buildArray[i] = calcBuildArray(i);
		}
	}
	public static boolean calcBuildArray(int neighbors) {
		//flood fill
		for(Direction direction: Direction.values()) {
			if(((neighbors >>> direction.ordinal()) & 1) == 0) {
				neighbors = floodFill(neighbors, direction.ordinal());
				break;
			}
		}
		//loop over
		for(Direction direction: Direction.values()) {
			if(((neighbors >>> direction.ordinal()) & 1) == 0) {
				return false;
			}
		}
		return true;
	}
	public static int floodFill(int neighbors, int bit) {
		if(((neighbors | (1 << bit)) & 1) == 1) {
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
	public static void execute() {
		GameController gc = GameController.INSTANCE;

		while(true) {
			System.out.println("R: " + gc.getRoundNumber() + "; K: " + gc.getCurrentKarbonite());
			
			//TODO
			
			gc.yield();
		}
	}
	public static boolean canBuild(boolean[] neighboring) {
		int num = 0;
		for(boolean neighbor: neighboring) {
			num = num << num;
			if(neighbor) {
				num = num+1;
			}
		}
		return buildArray[num];
	}
}
