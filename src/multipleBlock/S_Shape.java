package multipleBlock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class S_Shape implements Algorithm {
	
	int current_a = 0;
	int current_b = 0;
	Aisle current_aisle = new Aisle();
	Block current_block = new Block();
	Intersection current_inter = new Intersection();
	
	HashMap<Integer, Aisle> aisles = new HashMap<>();
	HashMap<Integer, Block> blocks = new HashMap<>();
	List<Pick> picks = new ArrayList<>();
	
	List<Pick> route = new ArrayList<>();
	
	@Override
	public List<Pick> generateRoute(WarehouseInfo info) {
		
		
		aisles = info.getAisles();
		blocks = info.getBlocks();
		picks = info.getPicks();
		
		System.out.println(picks.size() + " Items");

		// STEP 1
		// left pick aisle
		for(int a = 1; a <= aisles.size(); a++) {
			if(!aisles.get(a).getAisle_block_picks().isEmpty()) {
				current_a = a;
				break;
			}
		}	
		current_aisle = aisles.get(current_a);
		// farthest block
		for(int b = blocks.size(); b > 0; b--) {
			if(!blocks.get(b).getBlock_aisle_picks().isEmpty()) {
				current_b = b;
				break;
			}
		}
		current_block = blocks.get(current_b);
		
		// STEP 2
		// Route starts by going from the depot to the front of the left pick aisle
		current_inter = current_aisle.getAisle_block_intersection().get(0);
		route.add(current_inter);
		
		// STEP 3
		// Traverse the left pick aisle up to the front cross aisle of the farthest block.
		for(int i = 1; i < current_b; i++) {
			if(aisles.get(current_a).getAisle_block_picks().containsKey(i)) {
				pick(aisles.get(current_a).getAisle_block_picks().get(i), true);
				picks.removeAll(blocks.get(i).getBlock_aisle_picks().get(current_a));
				blocks.get(i).getBlock_aisle_picks().remove(current_a);
				aisles.get(current_a).getAisle_block_picks().remove(i);
			}
		}
		current_inter = current_aisle.getAisle_block_intersection().get(current_b - 1);
		route.add(current_inter);
		
		// STEP 4
		stepFour();
		
		// STEP 5
		stepFive();
		
		if(!picks.isEmpty())
			System.out.println("ERROR: picks left!");
		
		return route;
	}

	private void pick(List<Pick> pickList, boolean frontToBack) {
		List<Pick> sortedPicks = new ArrayList<>();
		Pick item = pickList.remove(0);
		sortedPicks.add(item);
		if(frontToBack) {
			while(!pickList.isEmpty()) {
				Pick p = pickList.remove(0);
				int length = sortedPicks.size();
				for(int i = 0; i < length; i++) {
					Pick current_p = sortedPicks.get(i);
					if(p.getLocate() <= current_p.getLocate()) {
						sortedPicks.add(i, p);
						break;
					}
				}
				if(sortedPicks.size() == length)
					sortedPicks.add(p);
			}
		}else {
			while(!pickList.isEmpty()) {
				Pick p = pickList.remove(0);
				int length = sortedPicks.size();
				for(int i = 0; i < length; i++) {
					Pick current_p = sortedPicks.get(i);
					if(p.getLocate() >= current_p.getLocate()) {
						sortedPicks.add(i, p);
						break;
					}
				}
				if(sortedPicks.size() == length)
					sortedPicks.add(p);
			}
		}
		route.addAll(sortedPicks);
	}

	private void stepFour() {
		// Go right through front cross aisle of farthest block until a subaisle with a pick is reached
		for(int a = 1; a <= aisles.size(); a++) {
			if(!aisles.get(a).getAisle_block_picks().get(current_b).isEmpty()) {
				current_a = a;
				break;
			}
		}
		current_aisle = aisles.get(current_a);
		current_inter = current_aisle.getAisle_block_intersection().get(current_b - 1);
		route.add(current_inter);
		// If this is the only subaisle in this block with pick locations,
		// then pick all items and return to the front cross aisle of this block.
		if(current_block.getBlock_aisle_picks().size() == 1) {
			pick(current_aisle.getAisle_block_picks().get(current_b), true);
			route.add(current_inter);
			picks.removeAll(current_block.getBlock_aisle_picks().get(current_a));
			blocks.get(current_b).getBlock_aisle_picks().remove(current_a);
			aisles.get(current_a).getAisle_block_picks().remove(current_b);
			current_b -= 1;
			current_block = blocks.get(current_b);
		}
		// If there are two or more subaisles with picks in this block,
		// then traverse the subaisle entirely.
		else {
			pick(current_aisle.getAisle_block_picks().get(current_b), true);
			current_inter = current_aisle.getAisle_block_intersection().get(current_b);
			route.add(current_inter);
			picks.removeAll(current_block.getBlock_aisle_picks().get(current_a));
			blocks.get(current_b).getBlock_aisle_picks().remove(current_a);
			aisles.get(current_a).getAisle_block_picks().remove(current_b);
		}		
	}

	private void stepFive() {
		// STEP 5.1 There are picks remaining in the current block
		if(!current_block.getBlock_aisle_picks().isEmpty()) {
			// Determine the distance to the leftmost and the rightmost subaisle of this block with picks.
			List<Integer> a_keys = new ArrayList<>();
			a_keys.addAll(current_block.getBlock_aisle_picks().keySet());
			Collections.sort(a_keys);			
			int leftmost_a = a_keys.get(0);
			int rightmost_a = a_keys.get(a_keys.size() - 1);
			// Go to the closer of these two.
			current_a = Math.abs(rightmost_a - current_a) > Math.abs(leftmost_a - current_a) ? 
					leftmost_a : rightmost_a;
			current_aisle = aisles.get(current_a);
			current_inter = current_aisle.getAisle_block_intersection().get(current_b);
			route.add(current_inter);
			// Entirely traverse this subaisle and continue with step 6.
			pick(current_aisle.getAisle_block_picks().get(current_b), false);
			current_inter = current_aisle.getAisle_block_intersection().get(current_b - 1);
			route.add(current_inter);
			picks.removeAll(current_block.getBlock_aisle_picks().get(current_a));
			blocks.get(current_b).getBlock_aisle_picks().remove(current_a);
			aisles.get(current_a).getAisle_block_picks().remove(current_b);
			
			// STEP 6
			stepSix();	
			
			// STEP 8
			stepEight();
		}
		// STEP 5.2 There are no items left in the current block that have to be picked.
		else {
			// Continue in the same pick aisle to get to the next cross aisle and continue with step 8
			current_inter = current_aisle.getAisle_block_intersection().get(current_b - 1);
			route.add(current_inter);
			current_b -= 1;
			
			// STEP 8
			stepEight();
		}
	}

	private void stepSix() {
		// If there are items left in the current block that have to be picked,
		if(current_block.getBlock_aisle_picks().isEmpty())
			return;
		while(current_block.getBlock_aisle_picks().size() > 1) {
			// then traverse the cross aisle towards the next subaisle with a pick location.
			List<Integer> a_keys = new ArrayList<>();
			a_keys.addAll(current_block.getBlock_aisle_picks().keySet());
			Collections.sort(a_keys);
			int left_right_indicator = a_keys.get(0);
			current_a = left_right_indicator < current_a ? 
					a_keys.get(a_keys.size() - 1) : left_right_indicator;
			current_aisle = aisles.get(current_a);
			int up_down_indicator = current_inter.getBlock();			
			current_inter = current_aisle.getAisle_block_intersection().get(up_down_indicator);
			route.add(current_inter);
			// Traverse that subaisle entirely.
			if(up_down_indicator == current_b) {
				// Go from back to front of block
				pick(current_aisle.getAisle_block_picks().get(current_b), false);
				current_inter = current_aisle.getAisle_block_intersection().get(current_b -1);
			}else {
				// Go from front to back of block
				pick(current_aisle.getAisle_block_picks().get(current_b), true);
				current_inter = current_aisle.getAisle_block_intersection().get(current_b);
			}
			route.add(current_inter);
			picks.removeAll(current_block.getBlock_aisle_picks().get(current_a));
			blocks.get(current_b).getBlock_aisle_picks().remove(current_a);
			aisles.get(current_a).getAisle_block_picks().remove(current_b);
		}
		// Repeat this step until there is exactly one subaisle left with pick locations in the current block.
		
		// STEP 7
		stepSeven();
	}

	private void stepSeven() {
		// Go to the last subaisle with pick locations of the current block
		List<Integer> a_keys = new ArrayList<>();
		a_keys.addAll(current_block.getBlock_aisle_picks().keySet());
		current_a = a_keys.get(0);
		current_aisle = aisles.get(current_a);
		int traverse_indicator = current_inter.getBlock();	
		current_inter = current_aisle.getAisle_block_intersection().get(traverse_indicator);
		route.add(current_inter);
		// Retrieve the items and go to the front cross aisle of the current block:
		if(traverse_indicator == current_b) {
			// (i) traversing the subaisle entirely: back -> front
			pick(current_aisle.getAisle_block_picks().get(current_b), false);
			current_inter = current_aisle.getAisle_block_intersection().get(current_b -1);			
		}else {
			// (ii) entering and leaving the subaisle from the same side: front -> pick -> front
			pick(current_aisle.getAisle_block_picks().get(current_b), true);
		}
		route.add(current_inter);		
		picks.removeAll(current_block.getBlock_aisle_picks().get(current_a));
		blocks.get(current_b).getBlock_aisle_picks().remove(current_a);
		aisles.get(current_a).getAisle_block_picks().remove(current_b);
		current_b -= 1;	
	}

	private void stepEight() {		
		// If the block closest to the depot has not yet been examined, then return to step 5.
		if(current_b > 0) {
			current_block = blocks.get(current_b);
			stepFive();
		}			
		else {
			// STEP 9
			// Finally, return to the depot.
			current_inter = aisles.get(1).getAisle_block_intersection().get(0);
			route.add(current_inter);
		}		
	}

}
