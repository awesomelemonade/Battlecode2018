package citricsky.battlecode2018.library;

public class ResearchInfo {
	private int roundsLeft;
	private UnitType[] queue;
	private UnitType nextInQueue;

	protected ResearchInfo(bc.ResearchInfo bcResearchInfo) {
		roundsLeft = (int)bcResearchInfo.roundsLeft();
		queue = Util.toArray(bcResearchInfo.queue());
		nextInQueue = UnitType.valueOf(bcResearchInfo.nextInQueue());
	}

	public int getRoundsLeft() {
		return roundsLeft;
	}

	public UnitType[] getQueue() {
		return queue;
	}

	public UnitType getNextInQueue() {
		return nextInQueue;
	}
}
