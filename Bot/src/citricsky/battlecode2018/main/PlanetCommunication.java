package citricsky.battlecode2018.main;

import citricsky.battlecode2018.library.GameController;
import citricsky.battlecode2018.library.Planet;
import citricsky.battlecode2018.library.Vector;

public class PlanetCommunication {
	private Planet planet;
	private Planet oppositePlanet;
	private int count;
	private Vector[] landingPositions;
	public PlanetCommunication() {
		this.planet = GameController.INSTANCE.getPlanet();
		this.oppositePlanet = planet == Planet.EARTH ? Planet.MARS : Planet.EARTH;
		this.landingPositions = new Vector[100];
		this.count = 0;
	}
	public void update() {
		if (planet == Planet.EARTH) {
			int[] teamArray = oppositePlanet.getTeamArray();
			while(count < teamArray[0]) {
				landingPositions[count] = uncompressVector(teamArray[count + 1]);
				count++;
			}
		}
	}
	public Vector getLanding(int count) {
		return landingPositions[count];
	}
	public void addLanding(Vector vector) {
		if (count < 80) {
			count++;
			GameController.INSTANCE.writeTeamArray(0, count);
			GameController.INSTANCE.writeTeamArray(count, compressVector(vector));
		}
	}
	private static final int BITMASK = 0b1111111111111111;
	private static final int OFFSET_X = 16;
	private static final int OFFSET_Y = 0;
	public int compressVector(Vector vector) {
		return ((vector.getX() & BITMASK) << OFFSET_X) | ((vector.getY() & BITMASK) << OFFSET_Y);
	}
	public Vector uncompressVector(int data) {
		return new Vector((data >>> OFFSET_X) & BITMASK, (data >>> OFFSET_Y) & BITMASK);
	}
}
