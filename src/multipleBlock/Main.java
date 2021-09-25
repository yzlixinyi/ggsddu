/**
 * Routing methods for warehouses with multiple cross aisles
 */
package multipleBlock;

import java.io.IOException;
import java.util.List;

/**
 * @author Li Xinyi
 *
 */
public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		WarehouseInfo design = new WarehouseInfo();

		// The example in Roodbergen's essay
//		design.setPick_aisle_width(2);
//		design.setCross_aisle_width(1);
//		design.setRack_deep(1);
//		design.setRack_length(1);
//		design.setAisle_num(6);
//		design.setLocation_num(new int[]{7, 7, 7});
//		String pick_order_file = "src/data/Roodbergen.txt";
		
		// The numerical test of homework
		design.setPick_aisle_width(1);
		design.setCross_aisle_width(3);
		design.setRack_deep(1);
		design.setRack_length(2);
		design.setAisle_num(18);
		design.setLocation_num(new int[]{10, 10, 12, 12});
		String pick_order_file = "src/data/order.txt";
		
		Initialization initial = new Initialization();		
		try {
			initial.initializeMap(pick_order_file, design);	
			Algorithm s_shape = new S_Shape();
			System.out.print("Using S_Shape Heuristic to Pick ");
			List<Pick> route_s = s_shape.generateRoute(design);
			Result result_s = new Result();
			result_s.printOutResult(route_s);
			new DrawMap(design, route_s, "S_Shape: " + result_s.getRoute_length());
						
			initial.initializeMap(pick_order_file, design);			
			Algorithm largest_gap = new Largest_Gap();
			System.out.print("Using Largest_Gap Heuristic to Pick ");
			List<Pick> route_l = largest_gap.generateRoute(design);
			Result result_l = new Result();			
			result_l.printOutResult(route_l);
			new DrawMap(design, route_l, "Largest_Gap: " + result_l.getRoute_length());

			initial.initializeMap(pick_order_file, design);	
			Algorithm combined = new Combined();
			System.out.print("Using Combined Heuristic to Pick ");
			List<Pick> route_c = combined.generateRoute(design);
			Result result_c = new Result();
			result_c.printOutResult(route_c);
			new DrawMap(design, route_c, "Combined: " + result_c.getRoute_length());
			
			System.out.println("------------------------SUMMARY------------------------");
			System.out.println("S_Shape Heuristic: " + result_s.getRoute_length());
			System.out.println("Largest_Gap Heuristic: " + result_l.getRoute_length());
			System.out.println("Combined Heuristic: " + result_c.getRoute_length());
						
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
