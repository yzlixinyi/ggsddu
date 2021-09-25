package multipleBlock;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Li Xinyi
 *
 */
public class Initialization {
		
	public void initializeMap(String pick_order_file, WarehouseInfo info) throws IOException{
		
		final int Pick_aisle_width = info.getPick_aisle_width();
		final int Cross_aisle_width = info.getCross_aisle_width();
		final int Rack_deep = info.getRack_deep();
		final int Rack_length = info.getRack_length();
		final int Aisle_num = info.getAisle_num();
		final int[] Location_num = info.getLocation_num();
	
		final int Block_num = Location_num.length;
		
		HashMap<Integer, Block> blocks = info.getBlocks();
		HashMap<Integer, Aisle> aisles = info.getAisles();
		List<Pick> picks = info.getPicks();
		
		// initialize blocks
		int d = 0;
		for(int b = 1; b <= Block_num; b++) {			
			Block block = new Block();		
			block.setBlock(b);
			block.setFront(d);
			d += Rack_length * Location_num[b-1] + Cross_aisle_width;
			block.setBack(d);
			blocks.put(b, block);
		}
		
		// initialize aisles
		int dX = 2 * Rack_deep + Pick_aisle_width;
		for(int a = 1; a <= Aisle_num; a++) {
			Aisle aisle = new Aisle();
			aisle.setAisle(a);
			aisle.setX((a - 1) * dX); 
		
			Intersection cross0 = new Intersection();
			cross0.setAisle(a);
			cross0.setX(aisle.getX());
			cross0.setY(0);
			cross0.setBlock(0);
			aisle.getAisle_block_intersection().put(0, cross0);
			for(int i = 1; i <= Block_num; i++) {
				Intersection cross = new Intersection();
				cross.setAisle(a);
				cross.setX(aisle.getX());
				cross.setY(blocks.get(i).getBack());
				cross.setBlock(i);
				aisle.getAisle_block_intersection().put(i, cross);
			}
			aisles.put(a, aisle);
		}
		
		// initialize picks
		List<List<Integer>> pick_info = loadData(pick_order_file);
		for(List<Integer> line: pick_info) {
			Pick item = new Pick();
			item.setBlock(line.get(0));
			item.setAisle(line.get(1));
			item.setLocate(line.get(2));
			item.setSide(line.get(3));
			item.setX(aisles.get(item.getAisle()).getX());
			item.setY(blocks.get(item.getBlock()).getFront() 
					+ 0.5 * Cross_aisle_width 
					+ Rack_length * (item.getLocate() - 0.5));
			picks.add(item);
			blocks.get(item.getBlock()).getPicks().add(item);
			if(blocks.get(item.getBlock()).getBlock_aisle_picks().containsKey(item.getAisle()))
				blocks.get(item.getBlock()).getBlock_aisle_picks().get(item.getAisle()).add(item);
			else {
				List<Pick> b_a_p = new ArrayList<>();
				b_a_p.add(item);
				blocks.get(item.getBlock()).getBlock_aisle_picks().put(item.getAisle(), b_a_p);
			}
			if(aisles.get(item.getAisle()).getAisle_block_picks().containsKey(item.getBlock()))
				aisles.get(item.getAisle()).getAisle_block_picks().get(item.getBlock()).add(item);
			else {
				List<Pick> a_b_p = new ArrayList<>();
				a_b_p.add(item);
				aisles.get(item.getAisle()).getAisle_block_picks().put(item.getBlock(), a_b_p);
			}
		}
		
//		printPicks(picks);
//		printAisles(aisles);
	}
	
	private void printAisles(HashMap<Integer, Aisle> aisles) {
		System.out.println("------------------------------------------------------");
		System.out.println("Picks between Intersections Aisle by Aisle:");
		int picks_num = 0;
		for(int a: aisles.keySet()) {
			Aisle aisle = aisles.get(a);
			System.out.println("------------------------------");
			System.out.println("Aisle: " + a);
			for(int b: aisle.getAisle_block_picks().keySet()) {
				for(Pick p: aisle.getAisle_block_picks().get(b)) {
					picks_num ++;
					System.out.println("Pick: " + p.getBlock() + "-" + p.getAisle() + "-" + p.getLocate() + 
							"-" + (p.getSide() > 1 ? "R" : "L") + "\tx=" + p.getX() + "\ty=" + p.getY());
				}
				System.out.println("Intersection\tx=" + aisle.getAisle_block_intersection().get(b).getX()
						+ "\ty=" + aisle.getAisle_block_intersection().get(b).getY());
			}
		}
		System.out.println("------------------------------");
		System.out.println("Pick number: " + picks_num);
	}

	private void printPicks(List<Pick> picks) {
		System.out.println("------------------------------------------------------");
		System.out.println("Pick orders:");
		for(Pick pick: picks) {
			System.out.println(pick.getBlock() + "-" + pick.getAisle() + "-" + pick.getLocate() + 
					"-" + (pick.getSide() > 1 ? "R" : "L") + "\t\tx=" + pick.getX() + "\ty=" + pick.getY());
		}
		
	}

	/**
	 * Read in b-x-y-L/R lines and parse strings to form integer lists
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	private List<List<Integer>> loadData(String filePath) throws IOException {
		BufferedReader br = null;
		List<List<Integer>> list = new ArrayList<>();

		try {
			br = new BufferedReader(new FileReader(filePath));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		String line = "";
		while ((line = br.readLine()) != null) {
			List<Integer> location = new ArrayList<>();
			String[] seq = line.substring(0).split("-");
			for(int i = 0; i < 3; i++) {
				location.add(Integer.parseInt(seq[i]));
			}
			location.add(seq[3].equals("L") ? 1 : 2);
			list.add(location);
		}
		br.close();
		return list;
	}

}
