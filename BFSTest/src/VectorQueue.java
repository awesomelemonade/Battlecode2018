
public class VectorQueue {
	private Vector[] array;
	private int index;
	private int size;
	public VectorQueue(int size) {
		this.array = new Vector[size];
	}
	
	public boolean add(Vector vector) {
		if (size + 1 > array.length) {
			System.out.println("Full");
			return false;
		}
		array[(index + size) % array.length] = vector;
		size += 1;
		return true;
	}
	
	public Vector poll() {
		if (size > 0) {
			Vector ret = array[index];
			index = (index + 1) % array.length;
			size -= 1;
			return ret;
		} else {
			return null;
		}
	}
	
	public void clear() {
		size = 0;
	}
	
	public int getSize() {
		return size;
	}
}
