import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

//bonuses to get from destroying bricks
public class Bonus extends Rectangle{
	
	int id;
	int speed = 2;
	boolean visible;
	
	Bonus(int x, int y, int width, int height, int id, boolean visible){
		super(x,y,width,height);
		this.id = id;
		this.visible = visible;
	}
	public void move() {
		y = y+speed;
	}
	public void draw(Graphics g) {
		if(visible==true) {
			switch(id) {
			case 1:
				g.setColor(Color.red);
				break;
			case 2:
				g.setColor(Color.green);
				break;
			case 3:
				g.setColor(Color.orange);
			}
			g.drawOval(x,y,width,height);
		}
		
	}
}
