package citricsky.battlecode2018.library;

public class Robot extends Unit {
	public Robot(bc.Unit bcUnit) {
		super(bcUnit);
	}

	public void attack() {
		
	}

	public long getAbilityCooldown() {
		return bcUnit.abilityCooldown();
	}
	
}
