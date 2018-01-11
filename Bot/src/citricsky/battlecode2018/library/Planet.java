package citricsky.battlecode2018.library;

public enum Planet {
	EARTH(bc.Planet.Earth), MARS(bc.Planet.Mars);
	private bc.Planet bcPlanet;
	private Planet(bc.Planet bcPlanet) {
		this.bcPlanet = bcPlanet;
	}
	public int[] getTeamArray() {
		return Util.toArray(GameController.INSTANCE.getBcGameController().getTeamArray(bcPlanet));
	}
	//TODO
	public void getStartingMap() {
		//return GameController.INSTANCE.getBcGameController().startingMap(bcPlanet);
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
