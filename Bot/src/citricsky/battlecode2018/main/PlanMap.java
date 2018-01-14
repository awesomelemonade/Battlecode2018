package citricsky.battlecode2018.main;

import java.util.function.IntPredicate;

import citricsky.battlecode2018.library.MapLocation;
import citricsky.battlecode2018.library.PlanetMap;
import citricsky.battlecode2018.library.Unit;
import citricsky.battlecode2018.library.UnitType;
import citricsky.battlecode2018.library.Vector;

public class PlanMap {
	private PlanetMap starterMap;
	private int[][][] data;
	public PlanMap(PlanetMap starterMap) {
		this.starterMap = starterMap;
		this.data = new int[starterMap.getWidth()][starterMap.getHeight()][DataType.values().length];
		init(starterMap);
	}
	private void init(PlanetMap map) {
		for(int i=0;i<data.length;++i) {
			for(int j=0;j<data[0].length;++j) {
				MapLocation location = map.getPlanet().getMapLocation(i, j);
				data[i][j][DataType.TERRAIN.getIndex()] = starterMap.isPassableTerrainAt(location)?0:1;
				data[i][j][DataType.KARBONITE.getIndex()] = starterMap.getInitialKarboniteAt(location);
				data[i][j][DataType.STRUCTURE.getIndex()] = -1;
				data[i][j][DataType.OWNER.getIndex()] = -1;
			}
		}
	}
	public int getWidth() {
		return data.length;
	}
	public int getHeight() {
		return data[0].length;
	}
	public boolean isOnMap(Vector position) {
		return position.getX() >= 0 && position.getX() < data.length &&
				position.getY() >= 0 && position.getY() < data[0].length;
	}
	public boolean isPassable(Vector position) {
		if(!isOnMap(position)) {
			return false;
		}
		for(DataType type: DataType.values()) {
			if(!type.isPassable(data[position.getX()][position.getY()][type.getIndex()])){
				return false;
			}
		}
		return true;
	}
	public void setStructure(Vector position, UnitType type) {
		data[position.getX()][position.getY()][DataType.STRUCTURE.getIndex()] = type.ordinal();
	}
	public void setOwner(Vector position, Unit unit) {
		data[position.getX()][position.getY()][DataType.OWNER.getIndex()] = unit.getId();
	}
	public int getKarbonite(Vector position) {
		return data[position.getX()][position.getY()][DataType.KARBONITE.getIndex()];
	}
	public enum DataType {
		TERRAIN(0, x->x==0), KARBONITE(1, x->true), STRUCTURE(2, x->x<0), OWNER(3, x->true);
		private int index;
		private IntPredicate passable;
		DataType(int index, IntPredicate passable){
			this.index = index;
			this.passable = passable;
		}
		public int getIndex() {
			return index;
		}
		public boolean isPassable(int data) {
			return passable.test(data);
		}
	}
}
