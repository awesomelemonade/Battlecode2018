package citricsky.battlecode2018.main;

import citricsky.battlecode2018.library.GameController;
import citricsky.battlecode2018.library.Planet;
import citricsky.battlecode2018.library.Vector;
import citricsky.battlecode2018.util.Util;

import java.util.*;

public class PlanetCommunication {
	private Planet planet;
	private Planet oppositePlanet;
	private int count;
	private Vector[] landingPositions;
	public PlanetCommunication() {
		this.planet = GameController.INSTANCE.getPlanet();
		this.oppositePlanet = planet == Planet.EARTH ? Planet.MARS : Planet.EARTH;
		this.landingPositions = new Vector[100];
		this.count = 0;
		if (planet == Planet.MARS) addLandingLocations();
	}
	public void update() {
		if (planet == Planet.EARTH) {
			int[] teamArray = oppositePlanet.getTeamArray();
			while(count < teamArray[0]) {
				landingPositions[count] = uncompressVector(teamArray[count + 1]);
				count++;
			}
		}
	}
	public Vector getLanding(int count) {
		return landingPositions[count];
	}
	public void addLanding(Vector vector) {
		if (count < 80) {
			count++;
			GameController.INSTANCE.writeTeamArray(0, count);
			GameController.INSTANCE.writeTeamArray(count, compressVector(vector));
		}
	}
	private static final int BITMASK = 0b1111111111111111;
	private static final int OFFSET_X = 16;
	private static final int OFFSET_Y = 0;
	public int compressVector(Vector vector) {
		return ((vector.getX() & BITMASK) << OFFSET_X) | ((vector.getY() & BITMASK) << OFFSET_Y);
	}
	public Vector uncompressVector(int data) {
		return new Vector((data >>> OFFSET_X) & BITMASK, (data >>> OFFSET_Y) & BITMASK);
	}

	private static final int[] DIFF = new int[] {-1, 0, 1};
	private static final int BOUND_START = 1000;

	private void addLandingLocations() {
		Planet mars = Planet.MARS;
		int width = mars.getWidth();
		int height = mars.getHeight();

		int[][] hitMap = new int[width][height];
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (!mars.getMapLocation(x, y).isPassableTerrain() || x == 0 || x == width - 1 || y == 0 || y == height - 1) {
					hitMap[x][y] = BOUND_START;
				}
			}
		}
		while (true) {
			int[][] oldHitMap = Arrays.stream(hitMap).map(int[]::clone).toArray(int[][]::new);
			boolean complete = true;

			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					if (oldHitMap[x][y] == 0) {
						complete = false;
					} else {
						for (int xDiff : DIFF) {
							for (int yDiff : DIFF) {
								if (!Util.outOfBounds(x + xDiff, y + yDiff, width, height)) {
									hitMap[x + xDiff][y + yDiff]++;
								}
							}
						}
					}
				}
			}
			if (complete) break;
		}

		SortedSet<Map.Entry<Vector, Integer>> locations = new TreeSet<>((Map.Entry<Vector, Integer> a, Map.Entry<Vector, Integer> b) -> {
			int compare = Integer.compare(a.getValue(), b.getValue());
			if (compare == 0) {
				compare = Integer.compare(a.getKey().getX(), b.getKey().getX());
				if (compare == 0) {
					return Integer.compare(a.getKey().getY(), b.getKey().getY());
				}
				return compare;
			}
			return compare;
		});


		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (hitMap[x][y] < BOUND_START) {
					locations.add(new AbstractMap.SimpleImmutableEntry<>(new Vector(x, y), hitMap[x][y]));
				}
			}
		}

		Set<Vector> ignoreLocations = new HashSet<>();
		for (Map.Entry<Vector, Integer> location : locations) {
			boolean shouldIgnore = false;
			for (Vector ignoreLoc : ignoreLocations) {
				if (location.getKey().getDistanceSquared(ignoreLoc) <= 4) {
					shouldIgnore = true;
					break;
				}
			}
			if (!shouldIgnore) {
				ignoreLocations.add(location.getKey());
				addLanding(location.getKey());
			}
		}
	}
}
