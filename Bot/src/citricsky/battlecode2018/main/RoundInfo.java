package citricsky.battlecode2018.main;

import citricsky.battlecode2018.library.GameController;
import citricsky.battlecode2018.library.Unit;

public class RoundInfo {
	private static int roundNumber;
	private static Unit[] enemiesOnMap;
	public static void update() {
		roundNumber = GameController.INSTANCE.getRoundNumber();
		enemiesOnMap = GameController.INSTANCE.getAllUnitsByFilter(
				unit -> unit.getTeam() == GameController.INSTANCE.getEnemyTeam() &&
						unit.getLocation().isOnMap()
		);
	}
	public static Unit[] getEnemiesOnMap() {
		return enemiesOnMap;
	}
	public static int getRoundNumber() {
		return roundNumber;
	}
}
