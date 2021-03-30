import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Bullet{
	private int radius, timer;
	private double dx, dy, x, y;
	private Color color;
	
	public Bullet (int x,int y, int a, int b, int radius, Color c, int s){
		this.x = x;
		this.y = y;
		timer = 0;
		this.radius = radius;
		color = c;
		double vx = a - x;
		double vy = b - y;
		double dist = Math.sqrt(vx*vx + vy*vy);
		if (dist > 0) {
			double speed = s;
			dx = (vx / dist * speed);
			dy = (vy / dist * speed);
		}
	}
	public void draw(Graphics g){
		g.setColor(color);
		g.fillOval((int)x-radius,(int)y-radius,2*radius,2*radius);
	}
	public void move() {
        timer++;
		double newx = x + dx;
		double newy = y + dy;
          
		x = newx;
		y = newy;
    } 

	public int getX(){
		return (int)x;
	}
	public int getY(){
		return (int)y;
	}
	public int getTimer(){
		return timer;
	}
	public boolean collidesWith(Fractal f){
		if (f.containsPoint((int)(x+radius),(int)y))
			return true;
		else if (f.containsPoint((int)x,(int)(y+radius)))
			return true;
		else if (f.containsPoint((int)x,(int)(y-radius)))
			return true;
		else if (f.containsPoint((int)(x-radius),(int)y))
			return true;
		return false;
	} 
}
