package multipleBlock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WarehouseInfo {

	private int Pick_aisle_width;
	private int Cross_aisle_width;
	private int Rack_deep;
	private int Rack_length;
	
	private int Aisle_num;
	private int[] Location_num;
	
	private HashMap<Integer, Block> blocks = new HashMap<>();
	private HashMap<Integer, Aisle> aisles = new HashMap<>();
	private List<Pick> picks = new ArrayList<>();
	
	public int getPick_aisle_width() {
		return Pick_aisle_width;
	}
	public void setPick_aisle_width(int pick_aisle_width) {
		Pick_aisle_width = pick_aisle_width;
	}
	public int getCross_aisle_width() {
		return Cross_aisle_width;
	}
	public void setCross_aisle_width(int cross_aisle_width) {
		Cross_aisle_width = cross_aisle_width;
	}
	public int getRack_deep() {
		return Rack_deep;
	}
	public void setRack_deep(int rack_deep) {
		Rack_deep = rack_deep;
	}
	public int getRack_length() {
		return Rack_length;
	}
	public void setRack_length(int rack_length) {
		Rack_length = rack_length;
	}
	public int getAisle_num() {
		return Aisle_num;
	}
	public void setAisle_num(int aisle_num) {
		Aisle_num = aisle_num;
	}
	public int[] getLocation_num() {
		return Location_num;
	}
	public void setLocation_num(int[] location_num) {
		Location_num = location_num;
	}
	public HashMap<Integer, Block> getBlocks() {
		return blocks;
	}
	public void setBlocks(HashMap<Integer, Block> blocks) {
		this.blocks = blocks;
	}
	public HashMap<Integer, Aisle> getAisles() {
		return aisles;
	}
	public void setAisles(HashMap<Integer, Aisle> aisles) {
		this.aisles = aisles;
	}
	public List<Pick> getPicks() {
		return picks;
	}
	public void setPicks(List<Pick> picks) {
		this.picks = picks;
	}
	
}
