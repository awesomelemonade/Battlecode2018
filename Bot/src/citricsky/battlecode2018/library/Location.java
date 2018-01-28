package citricsky.battlecode2018.library;

public class Location {
	private bc.Location bcLocation;
	private MapLocation mapLocation;
	
	public Location(bc.Location bcLocation) {
		this.bcLocation = bcLocation;
		if(bcLocation.isOnMap()) {
			this.mapLocation = Planet.getMapLocation(bcLocation.mapLocation());
		}else {
			this.mapLocation = null;
		}
	}
	public boolean isInSpace() {
		return bcLocation.isInSpace();
	}
	public boolean isInGarrison() {
		return bcLocation.isInGarrison();
	}
	public boolean isOnMap() {
		return bcLocation.isOnMap();
	}
	protected void setMapLocation(MapLocation mapLocation) {
		this.mapLocation = mapLocation;
	}
	public MapLocation getMapLocation() {
		return mapLocation;
	}
	public int getGarrisonStructureId() {
		return bcLocation.structure();
	}
}
