
public class BFSMain {
	public static void main(String[] args) {
		int[][] map= new int[][] {
				{0, 0, 0, 0, 0, 0, 0, 0},
				{0, 0, 1, 0, 0, 0, 0, 0},
				{0, 1, 1, 0, 0, 0, 0, 0},
				{0, 0, 0, 0, 0, 0, 0, 0},
				{0, 0, 0, 0, 0, 0, 0, 0},
				{0, 1, 1, 0, 0, 1, 1, 1},
				{0, 0, 1, 0, 1, 1, 0, 0},
				{0, 0, 1, 0, 0, 0, 0, 0}
		};
		BFS bfs = new BFS(map.length, map[0].length, vector -> map[vector.getX()][vector.getY()]==0);
		bfs.addSource(new Vector(1, 1), 0);
		while(bfs.getQueue().size()>0) {
			bfs.step();
		}
		printBFS(bfs);
	}
	public static void printBFS(BFS bfs) {
		System.out.println("BFS[SourceId]");
		for(int j=0;j<bfs.getHeight();++j) {
			System.out.print('|');
			for(int i=0;i<bfs.getWidth();++i) {
				System.out.print(pad("        ", Integer.toString(bfs.getSourceId(i, bfs.getHeight()-j-1)))+"|");
			}
			System.out.println();
		}
		System.out.println("BFS[DirectionToSource]");
		for(int j=0;j<bfs.getHeight();++j) {
			System.out.print('|');
			for(int i=0;i<bfs.getWidth();++i) {
				Direction direction = bfs.getDirectionToSource(i, bfs.getHeight()-j-1);
				System.out.print(pad("            ", direction == null ? "null" : direction.toString())+"|");
			}
			System.out.println();
		}
		System.out.println("BFS[Step]");
		for(int j=0;j<bfs.getHeight();++j) {
			System.out.print('|');
			for(int i=0;i<bfs.getWidth();++i) {
				System.out.print(pad("        ", Integer.toString(bfs.getStep(i, bfs.getHeight()-j-1)))+"|");
			}
			System.out.println();
		}
	}
	public static String pad(String pad, String string) {
		return (pad.concat(string)).substring(string.length());
	}
	public static String getDirectionShorthand(Direction direction) {
		switch(direction) {
			case NORTH:
				return " N";
			case NORTHEAST:
				return "NE";
			case EAST:
				return " E";
			case SOUTHEAST:
				return "SE";
			case SOUTH:
				return " S";
			case SOUTHWEST:
				return "SW";
			case WEST:
				return " W";
			case NORTHWEST:
				return "NW";
			default:
				return "  ";
		}
	}
}
