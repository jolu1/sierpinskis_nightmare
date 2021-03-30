import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

abstract class Fractal{
	int x1, y1;
	abstract void move(int dx, int dy);
	abstract void draw(Graphics g);
	abstract int getX();
	abstract int getY();
	abstract void setColour(Color c);
	abstract int getSize();
	abstract int numSides();
	abstract boolean containsPoint(int x, int y);
	abstract void travel();
	abstract Color getColour();

	abstract Rectangle getAggro();
	abstract void aggro();
	abstract void unaggro();
	abstract boolean isAggroed();
	abstract void hitLeft();
	abstract void hitRight();
	abstract boolean getLeft();
	abstract boolean getRight();
}

class Circle extends Fractal{
	private int x1, y1;
	private int diameter;
	private Color c;
	private int midX, midY;
	private double dx, dy;
	private Rectangle aggrobox;
	private boolean aggroed = false, hitLeft = true, hitRight = false;

	public Circle(int x, int y, int diameter, Color c){
		x1=x;
		y1=y;
		this.diameter = diameter;
		this.c=c;
		double angle = Math.toRadians(360)  * Math.random(); 
		double speed = 2 + 5*Math.random();   
		dx = Math.cos(angle) * speed;
		dy = Math.sin(angle) * speed;
	}
	public void draw(Graphics g){
		g.setColor(c);	
		drawEye(x1,y1,diameter,g);
	}
	
	public void drawEye(int x1, int y1, int diameter, Graphics g){
		if(diameter<15)
			return;
		g.drawOval(x1,y1,diameter,diameter);
		int midpointX = x1+diameter/2;
		int midpointY = y1+diameter/2;
		g.drawOval(midpointX,midpointY,diameter*4/10,diameter*4/10); //lower right
		g.drawOval(midpointX-(diameter*4/10),midpointY,diameter*4/10,diameter*4/10); //lower left
		g.drawOval(midpointX,midpointY-(diameter*4/10),diameter*4/10,diameter*4/10); //top right
		g.drawOval(midpointX-(diameter*4/10),midpointY-(diameter*4/10),diameter*4/10,diameter*4/10); //top left
		
		drawEye(midpointX,midpointY,diameter*4/10,g);
		drawEye(midpointX-(diameter*4/10),midpointY,diameter*4/10,g);
		drawEye(midpointX,midpointY-(diameter*4/10),diameter*4/10,g);
		drawEye(midpointX-(diameter*4/10),midpointY-(diameter*4/10),diameter*4/10,g);   
	}
	public void move (int dx, int dy){
		x1 = x1+dx;
		y1 = y1+dy;
	}
	
	public void travel(){	  
		double xmin = 0, xmax = 1280;
		double ymin = 0, ymax = 768;
		double newx = x1 + dx;
		double newy = y1 + dy;
      
		if (newy < ymin + diameter/2) {
			newy = 2*(ymin+diameter/2) - newy;
			dy = Math.abs(dy); 
		} else if (newy > ymax - diameter/2) {
			newy = 2*(ymax-diameter/2) - newy;
			dy = -Math.abs(dy);
		}  if (newx < xmin + diameter/2) {
			newx = 2*(xmin+diameter/2) - newx;
			dx = Math.abs(dx);  
		} else if (newx > xmax - diameter/2) {
			newx = 2*(xmax-diameter/2) - newx;
			dx = -Math.abs(dx);
		}
      
		x1 = (int)newx;
		y1 = (int)newy;
    }
	
	public boolean containsPoint(int x, int y){
		int midpointY = y1+(diameter/2);
		int midpointX = x1+(diameter/2);
		
		double length = Math.sqrt((midpointX-x)*(midpointX-x)+(midpointY-y)*(midpointY-y));
		if (length < diameter/2)
			return true;
		return false;
	}
	
	public int getX(){
		return x1;
	}
	
	public int getY(){
		return y1;
	}
	
	public void setColour(Color c){
		this.c = c;
	}
	
	public int getSize(){
		return diameter;
	}
	
	public int numSides(){
		return 1;
	}
	
	public Color getColour(){
		return c;
	}
	public Rectangle getAggro(){
		return aggrobox;
	}
	public void aggro(){
		aggroed = true;
	}
	public void unaggro(){
		aggroed = false;
	}
	public boolean isAggroed(){
		return aggroed;
	}
	public void hitRight(){
		hitRight = true;
		hitLeft = false;
	}
	public void hitLeft(){
		hitRight = false;
		hitLeft = true;
	}
	public boolean getLeft(){
		return hitLeft;
	}
	public boolean getRight(){
		return hitRight;
	}
}

class Triangle extends Fractal{
	private int size;
	private Color c;
	private Rectangle aggrobox;
	private boolean aggroed = false, hitLeft = true, hitRight = false;
	private int speed;
	
	public Triangle(int x, int y, int size, Color c, int s){
		x1=x;
		y1=y;
		this.size=size;
		this.c=c;
		speed = s;
		aggrobox = new Rectangle(x1-300,y1-300,(size*2)+600,(size*2)+600);
	}
	public void draw(Graphics g){
		g.setColor(c);	
		drawTriangle(x1,y1,size,g);
	}
	
	public void drawTriangle(int x1, int y1, int size, Graphics g){
		if (size<10) 
			return;
			
		int x2 = x1 + (int)(size * Math.cos(Math.toRadians(60)));
		int y2 = y1 - (int)(size * Math.sin(Math.toRadians(60)));

		g.drawLine( x1, y1, x1+size, y1 );
		g.drawLine( x1, y1, x2, y2 );
		g.drawLine( x2, y2, x1+size, y1 );
		
		drawTriangle( x1, y1, size/2, g );
		drawTriangle( x2, y1, size/2, g );
		drawTriangle( x2-(size/4),y1-(int)((size/2)*Math.sin(Math.toRadians(60))),size/2,g );
	}
	public void move (int dx, int dy){
		x1 = x1+dx;
		y1 = y1+dy;
		aggrobox.setLocation(x1-300,y1-300);
	}
	public boolean containsPoint(int x, int y){
		int x2 = x1 + (int)(size * Math.cos(Math.toRadians(60))); //B
		int y2 = y1 - (int)(size * Math.sin(Math.toRadians(60))); //B
		int x3 = x1 + size; //C
		int y3 = y1; //C
		double lenAB = Math.sqrt(Math.pow((y2-y1),2)+Math.pow((x2-x1),2));
		double lenBC = Math.sqrt(Math.pow((y3-y2),2)+Math.pow((x3-x2),2));
		double lenCA = Math.sqrt(Math.pow((y3-y1),2)+Math.pow((x3-x1),2));
		double lenPA = Math.sqrt(Math.pow((x-x1),2)+Math.pow((y-y1),2));
		double lenPB = Math.sqrt(Math.pow((x-x2),2)+Math.pow((y-y2),2));
		double lenPC = Math.sqrt(Math.pow((x-x3),2)+Math.pow((y-y3),2));
		double sABC = (lenAB+lenBC+lenCA)/2;
		double sPAB = (lenPA+lenAB+lenPB)/2;
		double sPBC = (lenPB+lenBC+lenPC)/2;
		double sPCA = (lenPC+lenCA+lenPA)/2;
		
		double aABC = Math.sqrt(sABC*(sABC-lenAB)*(sABC-lenBC)*(sABC-lenCA));
		double aPAB = Math.sqrt(sPAB*(sPAB-lenPA)*(sPAB-lenAB)*(sPAB-lenPB));
		double aPBC = Math.sqrt(sPBC*(sPBC-lenPB)*(sPBC-lenBC)*(sPBC-lenPC));
		double aPCA = Math.sqrt(sPCA*(sPCA-lenPC)*(sPCA-lenCA)*(sPCA-lenPA));
		
		if((aPAB+aPBC+aPCA)-aABC<=1)
			return true;
		return false;
	}
	
	public int getX(){
		return x1;
	}
	public int getY(){
		return y1;
	}
	
	public int getSize(){
		return size;
	}
	public void setColour(Color c){
		this.c = c; 
	}
	public int numSides(){
		return 3;
	}
	public void travel(){}
	
	public Color getColour(){
		return c;
	}
	public Rectangle getAggro(){
		return aggrobox;
	}
	public void aggro(){
		aggroed = true;
	}
	public void unaggro(){
		aggroed = false;
	}
	public boolean isAggroed(){
		return aggroed;
	}
	public void hitRight(){
		hitRight = true;
		hitLeft = false;
	}
	public void hitLeft(){
		hitRight = false;
		hitLeft = true;
	}
	public boolean getLeft(){
		return hitLeft;
	}
	public boolean getRight(){
		return hitRight;
	}
}

class Square extends Fractal{
	private int x1;
	private int y1;
	private int size;
	private int speed;
	private Color c;
	private Rectangle aggrobox;
	private boolean aggroed = false, hitLeft = true, hitRight = false;
	
	public Square(int x, int y, int size, Color c, int s){
		x1=x;
		y1=y;
		this.size=size;
		this.c=c;
		speed = s;
		aggrobox = new Rectangle(x1-300,y1-300,(size*2)+600,(size*2)+600);
	}
	public void draw(Graphics g){
		g.setColor(c);	
		drawSquare(x1,y1,size,g);
		aggrobox.setLocation(x1-300,y1-300);
	}
	
	public void drawSquare(int x1, int y1, int size, Graphics g){
		if(size<=2) 
			return;
		g.drawLine(x1,y1,x1+size,y1);
		g.drawLine(x1+size, y1, x1+size, y1+size);
		g.drawLine(x1+size, y1+size, x1, y1+size);
		g.drawLine(x1, y1+size, x1, y1);
		  
		drawSquare( x1, y1, size/3, g);
		drawSquare( x1+size/3, y1, size/3, g);
		drawSquare( x1+2*size/3, y1, size/3, g);
		drawSquare( x1, y1+size*2/3, size/3, g);
		drawSquare( x1+size/3, y1+size*2/3, size/3, g);
		drawSquare( x1+2*size/3, y1+size*2/3, size/3, g);
		drawSquare( x1, y1+size/3, size/3, g);
		drawSquare( x1, y1+2*size/3,size/3, g);
		drawSquare( x1+2*size/3, y1+size/3, size/3, g);
	}      
	
	public void move (int dx, int dy){
		x1 = x1+dx;
		y1 = y1+dy;
	}
	public boolean containsPoint(int x, int y){
		if((x>=x1 && x<= x1+size) && (y>=y1 && y<=y1+size))
			return true;
		return false;
	}
	public int getX(){
		return x1;
	}
	public int getY(){
		return y1;
	}
	public int getSize(){
		return size;
	}
	public void setColour(Color c){
		this.c = c; 
	}
	public int numSides(){
		return 4;
	}
	public void travel(){}
	
	public Color getColour(){
		return c;
	}
	public Rectangle getAggro(){
		return aggrobox;
	}
	public void aggro(){
		aggroed = true;
	}
	public void unaggro(){
		aggroed = false;
	}
	public boolean isAggroed(){
		return aggroed;
	}
	public void hitRight(){
		hitRight = true;
		hitLeft = false;
	}
	public void hitLeft(){
		hitRight = false;
		hitLeft = true;
	}
	public boolean getLeft(){
		return hitLeft;
	}
	public boolean getRight(){
		return hitRight;
	}
}   