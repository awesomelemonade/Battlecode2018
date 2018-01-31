package citricsky.battlecode2018.library;


public enum Team {
	RED(bc.Team.Red),
	BLUE(bc.Team.Blue);

	private bc.Team bcTeam;

	Team(bc.Team bcTeam) {
		this.bcTeam = bcTeam;
	}

	public Team getOpposite() {
		return Team.getOpposite(this);
	}
	
	protected bc.Team getBcTeam(){
		return bcTeam;
	}

	protected static Team valueOf(bc.Team team) {
		switch(team) {
		case Red:
			return Team.RED;
		case Blue:
			return Team.BLUE;
		default:
			return null;
		}
	}
	
	public static Team getOpposite(Team team) {
		switch(team) {
		case RED:
			return Team.BLUE;
		case BLUE:
			return Team.RED;
		default:
			return null;
		}
	}
}
