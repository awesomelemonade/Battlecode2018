package citricsky.battlecode2018.library;

public enum Planet {
	EARTH(bc.Planet.Earth),
	MARS(bc.Planet.Mars);

	private static final int BUFFER = 2;
	private bc.Planet bcPlanet;
	private PlanetMap startingMap;
	private MapLocation[][] mapLocations;

	Planet(bc.Planet bcPlanet) {
		this.bcPlanet = bcPlanet;
		this.startingMap = new PlanetMap(GameController.INSTANCE.getBcGameController().startingMap(bcPlanet));
		this.mapLocations = new MapLocation[startingMap.getWidth()+BUFFER*2][startingMap.getHeight()+BUFFER*2];
	}
	
	public void init() {
		this.startingMap.init();
	}
	
	public MapLocation getMapLocation(int x, int y) {
		if(mapLocations[x+BUFFER][y+BUFFER]==null) {
			mapLocations[x+BUFFER][y+BUFFER] = new MapLocation(new bc.MapLocation(bcPlanet, x, y));
		}
		return mapLocations[x+BUFFER][y+BUFFER];
	}
	
	public MapLocation getMapLocation(Vector position) {
		return getMapLocation(position.getX(), position.getY());
	}
	
	protected static MapLocation getMapLocation(bc.MapLocation location) {
		return Planet.valueOf(location.getPlanet()).getMapLocation(location.getX(), location.getY());
	}

	public int[] getTeamArray() {
		return LibraryUtil.toArray(GameController.INSTANCE.getBcGameController().getTeamArray(bcPlanet));
	}
	
	public PlanetMap getStartingMap() {
		return startingMap;
	}
	
	public int getWidth() {
		return startingMap.getWidth();
	}
	
	public int getHeight() {
		return startingMap.getHeight();
	}

	protected bc.Planet getBcPlanet(){
		return bcPlanet;
	}
	
	protected static Planet valueOf(bc.Planet bcPlanet) {
		switch(bcPlanet) {
			case Earth:
				return Planet.EARTH;
			case Mars:
				return Planet.MARS;
			default:
				return null;
		}
	}
}
