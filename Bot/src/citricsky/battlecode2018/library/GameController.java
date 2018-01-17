package citricsky.battlecode2018.library;

import java.util.function.Predicate;

public enum GameController {
	INSTANCE;

	private bc.GameController bcGameController;
	private Planet planet;
	private Team team;
	private Team enemyTeam;

	public void init() {
		this.bcGameController = new bc.GameController();
		this.planet = Planet.valueOf(bcGameController.planet());
		this.team = Team.valueOf(bcGameController.team());
		this.enemyTeam = this.team.getOpposite();
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
	 *
	 * @return
	 */
	public int getCurrentFlightDuration() {
		return (int) bcGameController.currentDurationOfFlight();
	}

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

	public Unit[] getMyUnits() {
		return LibraryUtil.toArray(bcGameController.myUnits());
	}

	public Unit[] getMyUnitsByFilter(Predicate<? super Unit> predicate) {
		return LibraryUtil.toFilteredArray(bcGameController.myUnits(), predicate);
	}

	public Unit[] getAllUnits() {
		return LibraryUtil.toArray(bcGameController.units());
	}

	public Unit[] getAllUnitsByFilter(Predicate<? super Unit> predicate) {
		return LibraryUtil.toFilteredArray(bcGameController.units(), predicate);
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

	public Team getEnemyTeam() {
		return enemyTeam;
	}

	public int getRoundNumber() {
		return (int) bcGameController.round();
	}

	public int getTimeLeft() {
		return bcGameController.getTimeLeftMs();
	}

	public void yield() {
		for (Planet planet : Planet.values()) {
			planet.clearMapLocations();
		}
		bcGameController.nextTurn();
	}

	protected bc.GameController getBcGameController() {
		return bcGameController;
	}
}
