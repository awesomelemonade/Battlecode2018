package citricsky.battlecode2018.library;

public class MapLocation {
	private bc.MapLocation bcMapLocation;
	private Planet planet;
	private Vector position;

	public MapLocation(Planet planet, Vector position) {
		this.planet = planet;
		this.position = position;
	}

	public Planet getPlanet() {
		return planet;
	}

	public Vector getPosition() {
		return position;
	}

	protected bc.MapLocation getBcMapLocation(){
		if (bcMapLocation==null) {
			bcMapLocation = new bc.MapLocation(planet.getBcPlanet(), position.getX(), position.getY());
		}

		return bcMapLocation;
	}
}
