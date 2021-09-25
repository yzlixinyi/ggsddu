package multipleBlock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Combined implements Algorithm{

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
		
		System.out.println("Finish STEP 1~3");
		
		// STEP 4
		// Set block i = i_max
		// STEP 5
		stepFive();
		
		if(!picks.isEmpty())
			System.out.println("ERROR: picks left!");

		return route;
	}

	private void stepFive() {
		
		System.out.print("Entering STEP 5 with ");
		System.out.println("Picks left: " + picks.size());
		
		// Determine whether or not block i contains items that have not been picked in step 3
		// STEP 5.1 If no items have to be picked in block i:
		if(blocks.get(current_b).getBlock_aisle_picks().isEmpty()) {
			// Traverse the nearest subaisle of block i to reach the next block.
			current_inter = current_aisle.getAisle_block_intersection().get(current_b - 1);
			route.add(current_inter);
			current_b -= 1;
			
			System.out.println("Entering STEP 7");
			
			// STEP 7
			stepSeven();
		}
		// STEP 5.2 If items have to be picked in block i:
		else {
			// Determine the leftmost subaisle and the rightmost subaisle that contains items
			a_keys.addAll(blocks.get(current_b).getBlock_aisle_picks().keySet());
			
			Collections.sort(a_keys);			
			int leftmost_a = a_keys.get(0);
			int rightmost_a = a_keys.get(a_keys.size() - 1);
			// Go from the current position to the nearest of these two.
			if(Math.abs(rightmost_a - current_a) > Math.abs(leftmost_a - current_a)) {
				current_a = leftmost_a;
			}else {
				current_a = rightmost_a;
				for(int i = 0; i < a_keys.size() - 1; i++) {
					int m = a_keys.remove(a_keys.size() - 1);
					a_keys.add(i, m);
				}
			}
			
			current_aisle = aisles.get(current_a);
			current_inter = current_aisle.getAisle_block_intersection().
					get(route.get(route.size() - 1).getBlock());
			if(!route.get(route.size() - 1).equals(current_inter))
				route.add(current_inter);
			
			System.out.println("Entering STEP 6");
			
			// STEP 6
			stepSix();
		}
	}

	private void stepSix() {
		// Apply the dynamic programming method
		List<Pick> dynamic_route = dynamicGenerateRoute();
		
		System.out.println("dynamic_route generated.");
		
		List<Integer> remove_a = new ArrayList<>();
		remove_a.addAll(blocks.get(current_b).getBlock_aisle_picks().keySet());
		for(int a: remove_a) {
			blocks.get(current_b).getBlock_aisle_picks().remove(a);
			aisles.get(a).getAisle_block_picks().remove(current_b);
		}
		picks.removeAll(dynamic_route);
		
		route.addAll(dynamic_route);
		
		current_a = route.get(route.size() - 1).getAisle();
		current_aisle = aisles.get(current_a);
		current_inter = current_aisle.getAisle_block_intersection().get(current_b - 1);
		route.add(current_inter);
		current_b -= 1;

		System.out.println("Entering STEP 7");
		
		// STEP 7
		stepSeven();
	}

	private void stepSeven() {
		// When the block closest to the depot has been evaluated, returns to the depot.
		// Otherwise return to step 5.
		if(current_b == 0) {
			current_inter = aisles.get(1).getAisle_block_intersection().get(0);
			route.add(current_inter);
		}
		else {
			System.out.println("Entering STEP 5");
			stepFive();
		}			
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

	private List<Pick> dynamicGenerateRoute() {
		List<Pick> leaveFromFront = new ArrayList<>();
		List<Pick> leaveFromBack = new ArrayList<>();
		double blockLength = blocks.get(current_b).getBack() - blocks.get(current_b).getFront();
		
		System.out.println("a_keys size: " + a_keys.size());
		
		if(current_inter.getBlock() == current_b) { // start from back
			// initialize: the first aisle
			int a1 = a_keys.remove(0);
			List<Pick> list1 = sortPicks(blocks.get(current_b).getBlock_aisle_picks().get(a1), false);
			leaveFromFront.addAll(list1);
			if(a_keys.isEmpty())
				return leaveFromFront;
			else {
				leaveFromFront.add(aisles.get(a1).getAisle_block_intersection().get(current_b - 1));
				double lengthToFront = blockLength;			
				leaveFromBack.addAll(list1);
				leaveFromBack.add(aisles.get(a1).getAisle_block_intersection().get(current_b));
				double lengthToBack = 2 * (blocks.get(current_b).getBack() - 
						list1.get(list1.size() - 1).getY());
				
				return dynamicLeaveFromFront(blockLength, leaveFromBack, leaveFromFront, 
						lengthToFront, lengthToBack);				
			}
		}else { // start from front
			// initialize: the first aisle
			int a1 = a_keys.remove(0);
			List<Pick> list1 = sortPicks(blocks.get(current_b).getBlock_aisle_picks().get(a1), true);			
			leaveFromFront.addAll(list1);			
			if(a_keys.isEmpty())
				return leaveFromFront;
			else {
				leaveFromFront.add(aisles.get(a1).getAisle_block_intersection().get(current_b - 1));
				double lengthToFront = 2 * (list1.get(list1.size() - 1).getY() - 
						blocks.get(current_b).getFront());				
				leaveFromBack.addAll(list1);
				leaveFromBack.add(aisles.get(a1).getAisle_block_intersection().get(current_b));
				double lengthToBack = blockLength;
				
				return dynamicLeaveFromFront(blockLength, leaveFromBack, leaveFromFront, 
						lengthToFront, lengthToBack);			
			}
		}
	}

	private List<Pick> dynamicLeaveFromFront(double blockLength, List<Pick> leaveFromBack, 
			List<Pick> leaveFromFront, double lengthToFront, double lengthToBack) {		
		while(a_keys.size() > 1) {
			int a = a_keys.remove(0);
			List<Pick> new_list = new ArrayList<>();
			new_list = blocks.get(current_b).getBlock_aisle_picks().get(a);
			List<Pick> listFromFront = sortPicks(new_list, true);
			List<Pick> listFromBack = sortPicks(new_list, false);
			double fToFront = 2 * (listFromBack.get(0).getY() - blocks.get(current_b).getFront());
			double bToBack = 2 * (blocks.get(current_b).getBack() - listFromFront.get(0).getY());
						
			List<Pick> temp_leaveFromBack = new ArrayList<>();
			List<Pick> temp_leaveFromFront = new ArrayList<>();
			double temp_lengthToBack = 0;
			double temp_lengthToFront = 0;
			
			// leaveFromBack
			if(lengthToFront + blockLength <= lengthToBack + bToBack) {
				// front to back
				temp_lengthToBack = lengthToFront + blockLength;
				temp_leaveFromBack.addAll(leaveFromFront);
				temp_leaveFromBack.add(aisles.get(a).getAisle_block_intersection().get(current_b - 1));				
				temp_leaveFromBack.addAll(listFromFront);
			}else {
				// back to back
				temp_lengthToBack = lengthToBack + bToBack;
				temp_leaveFromBack.addAll(leaveFromBack);
				temp_leaveFromBack.add(aisles.get(a).getAisle_block_intersection().get(current_b));
				temp_leaveFromBack.addAll(listFromBack);
			}
			
			// leaveFromFront
			if(lengthToBack + blockLength <= lengthToFront + fToFront) {
				// back to front
				temp_lengthToFront = lengthToBack + blockLength;
				temp_leaveFromFront.addAll(leaveFromBack);
				temp_leaveFromFront.add(aisles.get(a).getAisle_block_intersection().get(current_b));
				temp_leaveFromFront.addAll(listFromBack);
			}else {
				// front to front
				temp_lengthToFront = lengthToFront + fToFront;
				temp_leaveFromFront.addAll(leaveFromFront);
				temp_leaveFromFront.add(aisles.get(a).getAisle_block_intersection().get(current_b - 1));
				temp_leaveFromFront.addAll(listFromFront);
			}
			
			lengthToBack = temp_lengthToBack;
			lengthToFront = temp_lengthToFront;
			
			leaveFromBack.clear();
			leaveFromBack.addAll(temp_leaveFromBack);
			leaveFromBack.add(aisles.get(a).getAisle_block_intersection().get(current_b));
			
			leaveFromFront.clear();
			leaveFromFront.addAll(temp_leaveFromFront);
			leaveFromFront.add(aisles.get(a).getAisle_block_intersection().get(current_b - 1));
		}
		
		int a_r = a_keys.remove(0);
		// leave from front (always)
		List<Pick> fromBack = sortPicks(blocks.get(current_b).getBlock_aisle_picks().get(a_r), false);
		double frontToFront = 2 * (fromBack.get(0).getY() - blocks.get(current_b).getFront());
		
		if(lengthToBack + blockLength <= lengthToFront + frontToFront) {
			// back to front
			leaveFromBack.add(aisles.get(a_r).getAisle_block_intersection().get(current_b));
			leaveFromBack.addAll(fromBack);
			return leaveFromBack;
		}else {
			// front to front
			leaveFromFront.add(aisles.get(a_r).getAisle_block_intersection().get(current_b - 1));
			leaveFromFront.addAll(sortPicks(blocks.get(current_b).getBlock_aisle_picks().get(a_r), true));
//			while(!fromBack.isEmpty())  // This loop results in OutOfMemoryError!
//				leaveFromFront.add(fromBack.get(fromBack.size() - 1));
			return leaveFromFront;
		}
	}
	
}
