package multipleBlock;

import java.util.List;

public class Result {

	private double route_length = 0;
	
	public void printOutResult(List<Pick> route) {
		double preX = 0;
		double preY = 0;
		System.out.println("------------------------------------");
		System.out.println("Picking Route:");
		for(Pick n: route) {		
			route_length += Math.abs(preX - n.getX()) + Math.abs(preY - n.getY());
			preX = n.getX();
			preY = n.getY();
			if(n.getClass().getSimpleName().startsWith("P"))
				System.out.println(n.getClass().getSimpleName() 
					+ "\t" + n.getBlock() + "-" + n.getAisle() + "-" + n.getLocate() + "-" 
					+ (n.getSide() > 1 ? "R" : "L"));
			else {
				if(n.getBlock() == 0)
					System.out.println("Intersection in front of Block " + (n.getBlock() + 1) 
							+ " in Aisle " + n.getAisle());
				else
					System.out.println("Intersection at the back of Block " + n.getBlock() 
							+ " in Aisle " + n.getAisle());
			}
		}
		System.out.println("------------------------------------");
		System.out.println("Total travelling distance: " + getRoute_length());
		System.out.println("------------------------------------------------------");
	}
	
	public double getRoute_length() {
		return route_length;
	}

}
