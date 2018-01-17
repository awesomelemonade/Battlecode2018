package citricsky.battlecode2018.task;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import citricsky.battlecode2018.library.MapLocation;
import citricsky.battlecode2018.library.Unit;
import citricsky.battlecode2018.library.UnitType;
import citricsky.battlecode2018.main.RoundInfo;
import citricsky.battlecode2018.unithandler.PathfinderTask;

public class RangerAttackTask implements PathfinderTask {
	private Set<MapLocation> invalid;
	private Set<MapLocation> valid;
	private Predicate<MapLocation> stopCondition = location -> {
		if (invalid.contains(location)) {
			return false;
		}
		if (valid.contains(location)) {
			return true;
		}
		Unit[] enemyUnits = RoundInfo.getEnemiesOnMap().clone();
		Arrays.sort(enemyUnits, (unit1, unit2) -> {
			int distanceSquared1 = unit1.getLocation().getMapLocation().getPosition().getDistanceSquared(location.getPosition());
			int distanceSquared2 = unit2.getLocation().getMapLocation().getPosition().getDistanceSquared(location.getPosition());
			return Integer.compare(distanceSquared1, distanceSquared2);
		});
		
		for (Unit enemyUnit : enemyUnits) {
			int distanceSquared = enemyUnit.getLocation().getMapLocation().getPosition().getDistanceSquared(location.getPosition());
			if (distanceSquared < 40) {
				invalid.add(location);
				return false;
			}
			if (distanceSquared <= 50) {
				valid.add(location);
				return true;
			}
		}
		invalid.add(location);
		return false;
	};

	public RangerAttackTask() {
		invalid = new HashSet<MapLocation>();
		valid = new HashSet<MapLocation>();
	}

	@Override
	public void update() {
		invalid.clear();
		valid.clear();
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
