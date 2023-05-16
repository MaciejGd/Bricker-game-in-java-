import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class Ball extends Rectangle{
	
	int xVelocity;
	int yVelocity;
	int initialSpeed = 5;
	
	Ball(int x, int y, int width,int height){
		super(x,y,width,height);
		Random random = new Random();
		int yCourse = random.nextInt(2);
		if(yCourse == 0) {
			yCourse--;
		}
		setYDirection(initialSpeed*yCourse);
		int xCourse = random.nextInt(2);
		if(xCourse == 0) {
			xCourse--;
		}
		setXDirection(initialSpeed*xCourse);
	}
	public void setYDirection(int yDirection) {
		yVelocity = yDirection; 
	}
	public void setXDirection(int xDirection) {
		xVelocity = xDirection;
	}
	public void move() {
		x += xVelocity;
		y += yVelocity;
	}
	public int centerX() {
		return x + width/2;
	}
	public int centerY() {
		return y + height/2;
	}
	public void draw(Graphics g) {
		g.setColor(Color.white);
		g.fillOval(x, y, width, height);
	}
}
