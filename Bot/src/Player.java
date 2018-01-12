import citricsky.battlecode2018.library.GameController;
import citricsky.battlecode2018.library.Planet;
import citricsky.battlecode2018.main.EarthPlayer;
import citricsky.battlecode2018.main.MarsPlayer;
import citricsky.battlecode2018.util.Util;

public class Player {
	public static void main(String[] args) {
		Util.init();
		if (GameController.INSTANCE.getPlanet() == Planet.EARTH) {
			EarthPlayer.execute();
		} else {
			MarsPlayer.execute();
		}
	}
}
