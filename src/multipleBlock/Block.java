package multipleBlock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Block {
	private int block;
	private double front;
	private double back;
	private List<Pick> picks = new ArrayList<>();
	private HashMap<Integer, List<Pick>> block_aisle_picks = new HashMap<>();
	
	public int getBlock() {
		return block;
	}
	public void setBlock(int block) {
		this.block = block;
	}
	public double getFront() {
		return front;
	}
	public void setFront(double front) {
		this.front = front;
	}
	public double getBack() {
		return back;
	}
	public void setBack(double back) {
		this.back = back;
	}
	public List<Pick> getPicks() {
		return picks;
	}
	public void setPicks(List<Pick> picks) {
		this.picks = picks;
	}
	public HashMap<Integer, List<Pick>> getBlock_aisle_picks() {
		return block_aisle_picks;
	}
	public void setBlock_aisle_picks(HashMap<Integer, List<Pick>> block_aisle_picks) {
		this.block_aisle_picks = block_aisle_picks;
	}
}
