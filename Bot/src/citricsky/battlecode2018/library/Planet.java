package citricsky.battlecode2018.library;

public enum Planet {
	EARTH(bc.Planet.Earth),
	MARS(bc.Planet.Mars);

	private bc.Planet bcPlanet;
	private PlanetMap planetMap;

	Planet(bc.Planet bcPlanet) {
		this.bcPlanet = bcPlanet;
		this.planetMap = new PlanetMap(GameController.INSTANCE.getBcGameController().startingMap(bcPlanet));
	}

	public int[] getTeamArray() {
		return LibraryUtil.toArray(GameController.INSTANCE.getBcGameController().getTeamArray(bcPlanet));
	}
	
	public PlanetMap getStartingMap() {
		return planetMap;
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
