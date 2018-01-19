
public class BFSMain {
	public static void main(String[] args) {
		for(Direction direction: Direction.values()) {
			if(direction == Direction.CENTER) {
				continue;
			}
			int encoded = 1 << direction.ordinal();
			System.out.println(direction+"="+getFormatted(encoded));
		}
		int[][] map= new int[][] {
				{0, 0, 0, 0, 0, 0, 0, 0},
				{0, 0, 0, 0, 0, 0, 0, 0},
				{0, 0, 0, 0, 0, 0, 0, 0},
				{0, 0, 0, 0, 0, 0, 0, 0},
				{0, 0, 0, 0, 0, 0, 0, 0},
				{0, 0, 0, 0, 0, 0, 0, 0},
				{0, 0, 0, 0, 0, 0, 0, 0},
				{0, 0, 0, 0, 0, 0, 0, 0}
		};
		BFS bfs = new BFS(map.length, map[0].length, vector -> map[vector.getX()][vector.getY()]==0, new Vector(1, 1));
		while(bfs.getQueue().size()>0) {
			bfs.step();
		}
		printBFS(bfs);
	}
	public static void printBFS(BFS bfs) {
		System.out.println("BFS[DirectionFromSource]");
		for(int j=0;j<bfs.getHeight();++j) {
			for(int i=0;i<bfs.getWidth();++i) {
				System.out.print(getFormatted(bfs.getDirectionFromSource(i, bfs.getHeight()-j-1))+" ");
			}
			System.out.println();
		}
		System.out.println("BFS[DirectionToSource]");
		for(int j=0;j<bfs.getHeight();++j) {
			for(int i=0;i<bfs.getWidth();++i) {
				System.out.print(getFormatted(bfs.getDirectionToSource(i, bfs.getHeight()-j-1))+" ");
			}
			System.out.println();
		}
	}
	public static String getFormatted(int encoded) {
		String binaryString = Integer.toBinaryString(encoded);
		return ("00000000"+binaryString).substring(binaryString.length());
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
