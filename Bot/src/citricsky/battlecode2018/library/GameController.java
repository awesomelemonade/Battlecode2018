package citricsky.battlecode2018.library;

public enum GameController {
	INSTANCE;

	private bc.GameController bcGameController;
	private Planet planet;
	private Team team;

	GameController() {
		this.bcGameController = new bc.GameController();
		this.planet = Planet.valueOf(bcGameController.planet());
		this.team = Team.valueOf(bcGameController.team());
	}

	public boolean canSenseLocation(MapLocation location) {
		return bcGameController.canSenseLocation(location.getBcMapLocation());
	}

	public boolean canSenseUnit(Unit unit) {
		return bcGameController.canSenseUnit(unit.getId());
	}

	/**
	 * The current duration of flight if a rocket were to be launched this round.
	 * Does not take into account any research done on rockets.
	 * @return
	 */
	public int getCurrentFlightDuration() {
		return (int) bcGameController.currentDurationOfFlight();
	}
	
	//MapLocation related
	public boolean isOccupiable(MapLocation location) { //TODO
		return bcGameController.isOccupiable(location.getBcMapLocation())==1;
	}

	public boolean hasUnitAtLocation(MapLocation location) { //TODO
		return GameController.INSTANCE.getBcGameController().hasUnitAtLocation(location.getBcMapLocation());
	}

	public int getKarboniteCount(MapLocation location) { //TODO
		return (int) bcGameController.karboniteAt(location.getBcMapLocation());
	}

	//sensing? TODO
	//VecMapLocation allLocationsWithin(MapLocation location, long radiusSquared)
	//VecUnit senseNearbyUnits(MapLocation location, long radius)
	//VecUnit senseNearbyUnitsByTeam(MapLocation location, long radius, Team team)
	//VecUnit senseNearbyUnitsByType(MapLocation location, long radius, UnitType type)
	//Unit senseUnitAtLocation(MapLocation location)
	//Patterns

	public void getAsteroidPattern() { //TODO
		GameController.INSTANCE.getBcGameController().asteroidPattern();
	}

	public void getOrbitPattern() { //TODO
		GameController.INSTANCE.getBcGameController().orbitPattern();
	}

	public void getRocketLandings() { //TODO
		//return bcGameController.rocketLandings();
	}

	//Research
	public void queueResearch(UnitType type) { //TODO
		//Why does it return short?
		bcGameController.queueResearch(type.getBcUnitType());
	}

	public ResearchInfo getResearchInfo() { //TODO
		return new ResearchInfo(bcGameController.researchInfo());
	}

	public void resetResearch() {
		//Why does it return short?
		bcGameController.resetResearch();
	}

	public void writeTeamArray(int index, int value) {
		bcGameController.writeTeamArray(index, value);
	}

	public int getCurrentKarbonite() {
		return (int) bcGameController.karbonite();
	}

	public boolean isOver() {
		return bcGameController.isOver();
	}

	public Unit[] getMyUnits(){
		return LibraryUtil.toArray(bcGameController.myUnits());
	}

	public Unit[] getAllUnits() {
		return LibraryUtil.toArray(bcGameController.units());
	}

	public Unit[] getUnitsInSpace() {
		return LibraryUtil.toArray(bcGameController.unitsInSpace());
	}

	public Planet getPlanet() {
		return planet;
	}

	public Team getTeam() {
		return team;
	}

	public long getRoundNumber() {
		return bcGameController.round();
	}

	public void yield() {
		bcGameController.nextTurn();
	}

	protected bc.GameController getBcGameController(){
		return bcGameController;
	}
}
