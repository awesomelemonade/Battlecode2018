package citricsky.battlecode2018.library;

public class PlanetMap {
	private bc.PlanetMap bcPlanetMap;
	private Planet planet;
	private boolean[][] passableTerrain;
	private int width;
	private int height;
	
	public PlanetMap(bc.PlanetMap bcPlanetMap) {
		this.bcPlanetMap = bcPlanetMap;
		this.planet = Planet.valueOf(bcPlanetMap.getPlanet());
		this.width = (int)bcPlanetMap.getWidth();
		this.height = (int)bcPlanetMap.getHeight();
		this.passableTerrain = new boolean[width][height];
		for (int i = 0; i < width; ++i) {
			for (int j = 0; j < height; ++j) {
				this.passableTerrain[i][j] = bcPlanetMap.isPassableTerrainAt(
						planet.getMapLocation(i, j).getBcMapLocation()) > 0;
			}
		}
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public Planet getPlanet() {
		return planet;
	}
	
	public Unit[] getInitialUnits() {
		return LibraryUtil.toArray(bcPlanetMap.getInitial_units());
	}
	
	public int getInitialKarboniteAt(MapLocation location) {
		return (int)bcPlanetMap.initialKarboniteAt(location.getBcMapLocation());
	}
	
	public boolean isPassableTerrainAt(int x, int y) {
		if (x < 0 || x >= width || y < 0 || y >= height) {
			return false;
		}
		return passableTerrain[x][y];
	}
	
	public boolean isOnMap(MapLocation location) {
		return bcPlanetMap.onMap(location.getBcMapLocation());
	}
}
