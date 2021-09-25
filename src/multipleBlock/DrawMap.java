package multipleBlock;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.List;

import javax.swing.JFrame;

public class DrawMap extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private Graphics bg;

	public int expand = 18;
	public int xExpand;
	public int yExpand;
	public int header = 30;
	public int window_width;
	public int window_height;
	public final int bias_x;
	public final int bias_y;
		
	public DrawMap(WarehouseInfo info, List<Pick> route, String string){
        
		int pick_w = info.getPick_aisle_width();
		int cross_w = info.getCross_aisle_width();
		int rack_d = info.getRack_deep();
		int rack_l = info.getRack_length();
		int aisle_n = info.getAisle_num();
		int[] location_n = info.getLocation_num();
		
		window_width = aisle_n * pick_w + (aisle_n + 1) * 2 * rack_d;
		window_height = (location_n.length + 1) * cross_w;
		for(int i = 0; i < location_n.length; i++) {
			window_height += location_n[i] * rack_l;
		}
		int ratio_h = (1080 - header - 60) / window_height;
		int ratio_w = 1920 / window_width;
		int ratio = Math.min(ratio_h, ratio_w);
		expand = Math.min(expand, ratio);
		if(rack_l < rack_d) {
			xExpand = expand;
			yExpand = xExpand * rack_d / rack_l;
		}else {
			yExpand = expand;
			xExpand = yExpand * rack_l / rack_d;
		}			
		window_height *= yExpand;
		window_height += header;
		window_width *= xExpand;

		bias_x = xExpand * 2 * rack_d + xExpand * pick_w / 2;
		bias_y = yExpand * cross_w / 2;		
		
		Container p = getContentPane();//获取窗口的内容面板
        setBounds(0, 0, window_width, window_height);//调用父类setBounds方法，移动窗口到(100,100)位置，并设置窗口大小为宽500，高500
        setTitle(string);
        setVisible(true);//调用父类setVisible方法
        p.setBackground(Color.white);//调用对象p的setBackground方法，设置窗口背景颜色
        setLayout(null);   
        setResizable(false);//调用父类setResizable方法，false表示不允许窗口最大化
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//调用父类setDefaultCloseOperation方法，指定窗口关闭时退出程序。
        
        try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        bg =  this.getGraphics();
        
        paintLayout(bg, info, route);
        
    }

	private void paintLayout(Graphics bg, WarehouseInfo info, List<Pick> route) {
		int pick_w = xExpand * info.getPick_aisle_width();
		int rack_d = xExpand * info.getRack_deep();
		int rack_l = yExpand * info.getRack_length();
		int aisle_n = info.getAisle_num();
		int[] location_n = info.getLocation_num();
		HashMap<Integer, Block> blocks = info.getBlocks();
		HashMap<Integer, Aisle> aisles = info.getAisles();
		
		bg.setColor(Color.black);
		for(int b = 1; b <= location_n.length; b++) {
			for(int a = 1; a <= aisle_n; a++) {
				int x = (int) (bias_x + xExpand * aisles.get(a).getX());
				int y = (int) (window_height - yExpand * blocks.get(b).getBack());
				int x_l = x - pick_w / 2 - rack_d;
				int x_r = x + pick_w / 2;
				bg.drawRect(x_l, y, rack_d, location_n[b - 1] * rack_l);
				bg.drawRect(x_r, y, rack_d, location_n[b - 1] * rack_l);
				for(int c = 1; c < location_n[b - 1]; c++) {
					y += rack_l;
					bg.drawLine(x_l, y, x_l + rack_d, y);
					bg.drawLine(x_r, y, x_r + rack_d, y);
				}
			}
		}
			
		int p_x = 0;
		int p_y = 0;
		for(Pick p: route) {
			if(p.getClass().getSimpleName().startsWith("P")) {
				p_x = (int) (bias_x + xExpand * p.getX());
				p_y = (int) (window_height - bias_y - yExpand * p.getY());
				if(p.getSide() == 1)
					bg.fillRect(p_x - rack_d - pick_w / 2, p_y - rack_l / 2, rack_d, rack_l);			
				else
					bg.fillRect(p_x + pick_w / 2, p_y - rack_l / 2, rack_d, rack_l);								
			}
		}

		int pre_x = bias_x;
		int pre_y = window_height - bias_y;
		int count = 0;
				
		bg.fillOval(pre_x - 3, pre_y - 3, 6, 6);
		bg.setFont(new Font("Courier", Font.BOLD, 13));
		bg.drawString("Depot", pre_x - 42, pre_y + 4);
		
		bg.setFont(new Font("Arial", Font.BOLD, 16));
		bg.setColor(Color.blue);
		
		Graphics2D bg2 = (Graphics2D) bg;
		bg2.setStroke(new BasicStroke(3.0f));

		for(Pick p: route) {
			p_x = (int) (bias_x + xExpand * p.getX());
			p_y = (int) (window_height - bias_y - yExpand * p.getY());

			bg2.drawLine(pre_x, pre_y, p_x, p_y);
			pre_x = p_x;
			pre_y = p_y;
			
			if(p.getClass().getSimpleName().startsWith("P")) {
				count ++;
				if(p.getSide() == 1) {
					bg.clearRect(p_x - rack_d - pick_w / 2, p_y - rack_l / 2, rack_d, rack_l);			
					bg.drawString("" + count, p_x - rack_d - pick_w / 2, p_y + rack_l / 2);
				}else {
					bg.clearRect(p_x + pick_w / 2, p_y - rack_l / 2, rack_d, rack_l);
					bg.drawString("" + count, p_x + pick_w / 2, p_y + rack_l / 2);
				}
				
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
			}

		}
		
	}
}

