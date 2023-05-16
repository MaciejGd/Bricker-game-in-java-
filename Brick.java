import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class Brick extends Rectangle{
	
	boolean destroyed;
	
	Brick(int x, int y, int width,int height, boolean destroyed){
		super(x,y,width,height);
		this.destroyed = destroyed;
	}
	public void draw(Graphics g) {
		if(destroyed == false) {
			g.setColor(Color.white);
			g.drawRect(x, y ,width, height);
		}	
			
	}	
}

