package multipleBlock;

import java.util.HashMap;
import java.util.List;

public class Aisle {
	private int aisle;
	private double x;
	private HashMap<Integer, List<Pick>> aisle_block_picks = new HashMap<>();
	/**
	 * key: block_id - 1,
	 * that means the intersection is at the back of the block with same id
	 */
	private HashMap<Integer, Intersection> aisle_block_intersection = new HashMap<>();
		
	public int getAisle() {
		return aisle;
	}
	public void setAisle(int aisle) {
		this.aisle = aisle;
	}
	public double getX() {
		return x;
	}
	public void setX(double x) {
		this.x = x;
	}
	public HashMap<Integer, List<Pick>> getAisle_block_picks() {
		return aisle_block_picks;
	}
	public void setAisle_block_picks(HashMap<Integer, List<Pick>> aisle_block_pick) {
		this.aisle_block_picks = aisle_block_pick;
	}
	public HashMap<Integer, Intersection> getAisle_block_intersection() {
		return aisle_block_intersection;
	}
	public void setAisle_block_intersection(HashMap<Integer, Intersection> aisle_block_intersection) {
		this.aisle_block_intersection = aisle_block_intersection;
	}
}
