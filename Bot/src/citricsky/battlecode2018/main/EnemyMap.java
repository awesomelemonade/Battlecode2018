package citricsky.battlecode2018.main;

import citricsky.battlecode2018.library.*;
import citricsky.battlecode2018.util.Constants;

import java.util.Arrays;
import java.util.LinkedList;


public class EnemyMap {
	private static int[] scoreCache;
	private static int[] updateTime;
	private static int[][] heatMap;
	private static int[][] fuzzyMap;
	private static int[][] blobMap;

	static {
		scoreCache = new int[Constants.MAX_UNIT_ID];
		updateTime = new int[Constants.MAX_UNIT_ID];
	}

	public static int getScore(Unit enemyUnit) {
		if (!enemyUnit.getLocation().isOnMap()) {
			return Integer.MIN_VALUE;
		}
		if (updateTime[enemyUnit.getId()] == RoundInfo.getRoundNumber()) {
			return scoreCache[enemyUnit.getId()];
		}
		scoreCache[enemyUnit.getId()] = calcScore(enemyUnit.getLocation().getMapLocation());
		updateTime[enemyUnit.getId()] = RoundInfo.getRoundNumber();
		return scoreCache[enemyUnit.getId()];
	}
	
	public static int calcScore(MapLocation location) {
		Unit[] nearby = location.senseNearbyUnitsByFilter(2, unit -> unit.getLocation().isOnMap());
		int numEnemies = (int) Arrays.stream(nearby).filter(unit -> unit.getTeam() == GameController.INSTANCE.getEnemyTeam()).count();
		int numFriendlies = nearby.length - numEnemies;
		return numEnemies - (2 * numFriendlies);
	}

	public static void updateHeatMap() {
		Planet planet = GameController.INSTANCE.getPlanet();
		
		int w = planet.getWidth();
		int h = planet.getHeight();
		int halfW = (w + 1) / 2;
		int halfH = (h + 1) / 2;
		
		heatMap = new int[planet.getWidth()][planet.getHeight()];
		fuzzyMap = new int[halfW][halfH];
		blobMap = new int[w][h];
		for (Unit unit : GameController.INSTANCE.getAllUnits()) {
			if (!unit.getLocation().isOnMap() || unit.getType() == UnitType.FACTORY || unit.getType() == UnitType.ROCKET) continue;

			int mult = unit.getTeam() == GameController.INSTANCE.getTeam() ? 1 : -1;

			MapLocation loc = unit.getLocation().getMapLocation();
			Vector pos = loc.getPosition();
			int cX = pos.getX();
			int cY = pos.getY();

			heatMap[cX][cY] += 3*mult;
			fuzzyMap[cX / 2][cY / 2] += 3*mult;


			for (MapLocation targetLoc : loc.getAllMapLocationsWithin(unit.getVisionRange())) {
				Vector targetPos = targetLoc.getPosition();
				heatMap[targetPos.getX()][targetPos.getY()] += 1*mult;
				fuzzyMap[targetPos.getX() / 2][targetPos.getY() / 2] += 1*mult;
			}
			if (unit.getType() == UnitType.RANGER) {
				for (MapLocation targetLoc : loc.getAllMapLocationsWithin(unit.getRangerCannotAttackRange())) {
					Vector targetPos = targetLoc.getPosition();
					heatMap[targetPos.getX()][targetPos.getY()] -= 1*mult;
					fuzzyMap[targetPos.getX() / 2][targetPos.getY() / 2] -= 1*mult;
				}
			}
		}
		boolean[] visited = new boolean[w * h];
		LinkedList<Integer> queue = new LinkedList<Integer>();
		LinkedList<Integer> blobs = new LinkedList<Integer>();
		blobs.add(0);
		
		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				int blobSum = 0;
				int initialVal = heatMap[x][y];
				if (initialVal == 0) continue;
				boolean isFriendly = initialVal > 0;
				visited[y*h + x] = true;
				queue.add(y*h + x);
				while (queue.size() > 0) {
					int s = queue.poll();
					int currX = s % h;
					int currY = s / h;
					int val = heatMap[currX][currY];
					if (val != 0 && (val > 0) == isFriendly) {
						blobSum += val;
						blobMap[currX][currY] = blobs.size();
						if (currX > 0) {
							int num = currY*h + currX - 1;
							if (!visited[num]) {
								visited[num] = true;
								queue.add(num);
							}
						}
						if (currX < w-1) {
							int num = currY*h + currX + 1;
							if (!visited[num]) {
								visited[num] = true;
								queue.add(num);
							}
						}
						if (currY > 0) {
							int num = (currY-1)*h + currX;
							if (!visited[num]) {
								visited[num] = true;
								queue.add(num);
							}
						}
						if (currY < h-1) {
							int num = (currY+1)*h + currX;
							if (!visited[num]) {
								visited[num] = true;
								queue.add(num);
							}
						}
					}
				}
				if (blobSum != 0) {
					blobs.add(blobSum);
				}
			}
		}
		
		System.out.println("MAP");
		for (int y = h - 1; y >= 0; y--) {
			for (int x = 0; x < w; x++) {
				String print = String.valueOf(blobs.get(blobMap[x][y]));
				while (print.length() < 2) {
					print += " ";
				}
				System.out.print(print + " ");
			}
			System.out.println();
		}
	}
}
