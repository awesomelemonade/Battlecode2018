package citricsky.battlecode2018.library;

public class PlanetMap {
	private bc.PlanetMap bcPlanetMap;
	
	public PlanetMap(bc.PlanetMap bcPlanetMap) {
		this.bcPlanetMap = bcPlanetMap;
	}
	
	public int getWidth() {
		return (int)bcPlanetMap.getWidth();
	}
	
	public int getHeight() {
		return (int)bcPlanetMap.getHeight();
	}
	
	public Planet getPlanet() {
		return Planet.valueOf(bcPlanetMap.getPlanet());
	}
	
	public Unit[] getInitialUnits() {
		return LibraryUtil.toArray(bcPlanetMap.getInitial_units());
	}
	
	public int getInitialKarboniteAt(MapLocation location) {
		return (int)bcPlanetMap.initialKarboniteAt(location.getBcMapLocation());
	}
	
	public boolean isPassableTerrainAt(MapLocation location) {
		return bcPlanetMap.isPassableTerrainAt(location.getBcMapLocation())>0;
	}
	
	public boolean isOnMap(MapLocation location) {
		return bcPlanetMap.onMap(location.getBcMapLocation());
	}
}
