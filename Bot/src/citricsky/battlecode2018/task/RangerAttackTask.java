package citricsky.battlecode2018.task;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import citricsky.battlecode2018.library.MapLocation;
import citricsky.battlecode2018.library.Planet;
import citricsky.battlecode2018.library.Unit;
import citricsky.battlecode2018.library.UnitType;
import citricsky.battlecode2018.main.RoundInfo;
import citricsky.battlecode2018.unithandler.PathfinderTask;

public class RangerAttackTask implements PathfinderTask {
	// 40 to 50 attack range for Ranger
	private static final int[] OFFSET_X = new int[] {7, 7, 6, 6, 5, 5, 4, 3, 2, 1, 0, -1, -2, -3, -4, -5, -5, -6, -6, -7,
													-7, -7, -6, -6, -5, -5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5, 5, 6, 6, 7};
	private static final int[] OFFSET_Y = new int[] {0, 1, 2, 3, 4, 5, 5, 6, 6, 7, 7, 7, 6, 6, 5, 5, 4, 3, 2, 1,
													0, -1, -2, -3, -4, -5, -5, -6, -6, -7, -7, -7, -6, -6, -5, -5, -4, -3, -2, -1};
	private Set<MapLocation> cache;
	private Predicate<MapLocation> stopCondition = location -> cache.contains(location);

	public RangerAttackTask() {
		cache = new HashSet<MapLocation>();
	}

	@Override
	public void update() {
		cache.clear();
		for(Unit enemy: RoundInfo.getEnemiesOnMap()) {
			for(int i = 0; i < OFFSET_X.length; ++i) {
				int offsetX = enemy.getLocation().getMapLocation().getPosition().getX() + OFFSET_X[i];
				int offsetY = enemy.getLocation().getMapLocation().getPosition().getY() + OFFSET_Y[i];
				Planet planet = enemy.getLocation().getMapLocation().getPlanet();
				if(isOnMap(offsetX, offsetY, planet)) {
					cache.add(planet.getMapLocation(offsetX, offsetY));
				}
			}
		}
	}
	
	private boolean isOnMap(int x, int y, Planet planet) {
		return x >= 0 && x < planet.getWidth() && y >= 0 && y < planet.getHeight();
	}

	@Override
	public void execute(Unit unit, MapLocation location) {
		int bestDistanceSquared = Integer.MAX_VALUE;
		Unit bestTarget = null;
		boolean onlySeenFactory = true;
		for (Unit enemyUnit : RoundInfo.getEnemiesOnMap()) {
			int distanceSquared = enemyUnit.getLocation().getMapLocation().getPosition().getDistanceSquared(location.getPosition());
			if(distanceSquared > unit.getRangerCannotAttackRange() && distanceSquared < unit.getAttackRange()) {
				if(onlySeenFactory && enemyUnit.getType() != UnitType.FACTORY) {
					bestDistanceSquared = distanceSquared;
					bestTarget = enemyUnit;
					onlySeenFactory = false;
				}else {
					if(distanceSquared < bestDistanceSquared) {
						if(onlySeenFactory || (!onlySeenFactory && enemyUnit.getType() != UnitType.FACTORY)) {
							bestDistanceSquared = distanceSquared;
							bestTarget = enemyUnit;
						}
					}
				}
			}
		}
		if (bestTarget != null) {
			if (unit.isAttackReady() && unit.canAttack(bestTarget)) {
				unit.attack(bestTarget);
			}
		}
	}

	@Override
	public Predicate<MapLocation> getStopCondition() {
		return stopCondition;
	}
}
