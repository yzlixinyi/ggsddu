package multipleBlock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Largest_Gap implements Algorithm {

	int current_a = 0;
	int current_b = 0;
	int last_a = 0;
	List<Integer> a_keys = new ArrayList<>();
	Aisle current_aisle = new Aisle();
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
		
		// STEP 2
		// Route starts by going from the depot to the front of the left pick aisle
		current_inter = current_aisle.getAisle_block_intersection().get(0);
		route.add(current_inter);
		
		// STEP 3
		// Traverse the left pick aisle up to the front cross aisle of the farthest block.
		for(int i = 1; i < current_b; i++) {
			if(aisles.get(current_a).getAisle_block_picks().containsKey(i)) {
				route.addAll(sortPicks(aisles.get(current_a).getAisle_block_picks().get(i), true));
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
		if(blocks.get(current_b).getBlock_aisle_picks().size() == 1) {
			route.addAll(sortPicks(current_aisle.getAisle_block_picks().get(current_b), true));
			route.add(current_inter);
			picks.removeAll(blocks.get(current_b).getBlock_aisle_picks().get(current_a));
			blocks.get(current_b).getBlock_aisle_picks().remove(current_a);
			aisles.get(current_a).getAisle_block_picks().remove(current_b);
			current_b -= 1;
		}
		// If there are two or more subaisles with picks in this block,
		// then traverse the subaisle entirely.
		else {
			route.addAll(sortPicks(current_aisle.getAisle_block_picks().get(current_b), true));
			current_inter = current_aisle.getAisle_block_intersection().get(current_b);
			route.add(current_inter);
			picks.removeAll(blocks.get(current_b).getBlock_aisle_picks().get(current_a));
			blocks.get(current_b).getBlock_aisle_picks().remove(current_a);
			aisles.get(current_a).getAisle_block_picks().remove(current_b);
		}		
	}
	
	private void stepFive() {
		// STEP 5.1 There are picks remaining in the current block
		if(!blocks.get(current_b).getBlock_aisle_picks().isEmpty()) {
			// Determine the subaisle with pick locations that is farthest from the current position.
			a_keys.addAll(blocks.get(current_b).getBlock_aisle_picks().keySet());
			if(a_keys.size() == 1)
				last_a = a_keys.remove(0);
			else {
				Collections.sort(a_keys);			
				int leftmost_a = a_keys.get(0);
				int rightmost_a = a_keys.get(a_keys.size() - 1);
				last_a = Math.abs(leftmost_a - current_a) > Math.abs(rightmost_a - current_a) ? 
						leftmost_a : rightmost_a;
				a_keys.remove((Integer)last_a);
			}
			
			// STEP 6
			stepSix();
		}
		// STEP 5.2 There are no items left in the current block that have to be picked.
		else {
			// Continue in the same pick aisle to get to the next cross aisle and continue with step 9
			current_inter = current_aisle.getAisle_block_intersection().get(current_b - 1);
			route.add(current_inter);
			current_b -= 1;
			
			// STEP 9
			stepNine();
		}
	}

	private void stepSix() {		
		// Follow the shortest path through the back cross aisle starting at the current position
		// visiting all subaisles that have to be entered from the back
		if(a_keys.get(0) > last_a) { // from right to left
			while(!a_keys.isEmpty()) {
				current_a = a_keys.remove(a_keys.size() - 1);
				current_aisle = aisles.get(current_a);
				current_inter = current_aisle.getAisle_block_intersection().get(current_b);
				route.add(current_inter);
				// Each subaisle that is passed has to be entered up to the largest gap.
				pickFromBack(current_aisle.getAisle_block_picks().get(current_b));
			}
		}else { // from left to right
			while(!a_keys.isEmpty()) {
				current_a = a_keys.remove(0);
				current_aisle = aisles.get(current_a);
				current_inter = current_aisle.getAisle_block_intersection().get(current_b);
				route.add(current_inter);
				pickFromBack(current_aisle.getAisle_block_picks().get(current_b));
			}
		}
		// ending at the last subaisle of the current block.
		current_a = last_a;
		current_aisle = aisles.get(current_a);
		current_inter = current_aisle.getAisle_block_intersection().get(current_b);
		route.add(current_inter);
		
		// STEP 7
		stepSeven();
	}

	private void stepSeven() {
		// Traverse the last subaisle of the current block entirely to get to the front cross aisle
		route.addAll(sortPicks(current_aisle.getAisle_block_picks().get(current_b), false));
		current_inter = current_aisle.getAisle_block_intersection().get(current_b - 1);
		route.add(current_inter);
		picks.removeAll(blocks.get(current_b).getBlock_aisle_picks().get(current_a));
		blocks.get(current_b).getBlock_aisle_picks().remove(current_a);
		aisles.get(current_a).getAisle_block_picks().remove(current_b);
		
		// STEP 8
		stepEight();
	}

	private void stepEight() {
		// Move past all subaisles of the current block that have picks left.
		if(!blocks.get(current_b).getBlock_aisle_picks().isEmpty()) {
			a_keys.addAll(blocks.get(current_b).getBlock_aisle_picks().keySet());			
			Collections.sort(a_keys);
			if(a_keys.get(0) < last_a) { // from right to left
				while(!a_keys.isEmpty()) {
					current_a = a_keys.remove(a_keys.size() - 1);
					current_aisle = aisles.get(current_a);
					current_inter = current_aisle.getAisle_block_intersection().get(current_b - 1);
					route.add(current_inter);
					// Enter these subaisles up to the largest gap to pick the items.
					route.addAll(sortPicks(current_aisle.getAisle_block_picks().get(current_b), true));		
					picks.removeAll(blocks.get(current_b).getBlock_aisle_picks().get(current_a));
					blocks.get(current_b).getBlock_aisle_picks().remove(current_a);
					aisles.get(current_a).getAisle_block_picks().remove(current_b);
					route.add(current_inter);
				}
			}else { // from left to right
				while(!a_keys.isEmpty()) {
					current_a = a_keys.remove(0);
					current_aisle = aisles.get(current_a);
					current_inter = current_aisle.getAisle_block_intersection().get(current_b - 1);
					route.add(current_inter);					
					route.addAll(sortPicks(current_aisle.getAisle_block_picks().get(current_b), true));		
					picks.removeAll(blocks.get(current_b).getBlock_aisle_picks().get(current_a));
					blocks.get(current_b).getBlock_aisle_picks().remove(current_a);
					aisles.get(current_a).getAisle_block_picks().remove(current_b);
					route.add(current_inter);
				}
			}
		}		
		current_b -= 1;
		
		// STEP 9
		stepNine();
	}

	private void stepNine() {
		// If the block closest to the depot has not yet been examined, then return to step 5.
		if(current_b > 0)
			stepFive();
		else {
			// STEP 10
			// Finally, return to the depot.
			current_inter = aisles.get(1).getAisle_block_intersection().get(0);
			route.add(current_inter);
		}
	}

	private void pickFromBack(List<Pick> list) {
		double gaps[] = new double[list.size() + 1];
		List<Pick> sortedList = sortPicks(list, false);
		double y = blocks.get(current_b).getBack();
		for(int i = 0; i < sortedList.size(); i++) {
			gaps[i] = y - sortedList.get(i).getY();
			y = sortedList.get(i).getY();
		}
		gaps[gaps.length - 1] = y - blocks.get(current_b).getFront();
		
		int divide = 0;
		double largest_gap = gaps[0];
		for(int i = 1; i < gaps.length; i++) {
			if(gaps[i] > largest_gap) {
				divide = i;
				largest_gap = gaps[i];
			}
		}
		if(divide == 0)
			return;
		if(divide == sortedList.size()) {
			route.addAll(sortedList);
			picks.removeAll(sortedList);
			blocks.get(current_b).getBlock_aisle_picks().remove(current_a);
			aisles.get(current_a).getAisle_block_picks().remove(current_b);
		}
		else {
			for(int j = 0; j < divide; j++) {
				Pick p = sortedList.get(j);
				route.add(p);
				picks.remove(p);
				blocks.get(current_b).getBlock_aisle_picks().get(current_a).remove(p);
				aisles.get(current_a).getAisle_block_picks().get(current_b).remove(p);
			}
		}
		route.add(current_inter);
	}

	private List<Pick> sortPicks(List<Pick> list, boolean frontToBack) {
		List<Pick> sortedPicks = new ArrayList<>();
		List<Pick> pickList = new ArrayList<>();
		pickList.addAll(list);
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
		return sortedPicks;
	}

}
