package citricsky.battlecode2018.library;


public enum Team {
	RED(bc.Team.Red), BLUE(bc.Team.Blue);
	private bc.Team bcTeam;
	private Team(bc.Team bcTeam) {
		this.bcTeam = bcTeam;
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
}
