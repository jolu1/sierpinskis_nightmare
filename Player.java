/* 
Mursheed Drahman and Joanne Lu
ICS4U1-01
Summative Project: Unnamed
Ms. Strelkovska
December 24, 2011
*/

import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.*;

class Player{
	private ImageIcon sprite=null;
	private int imgLength = 0, x1,y1;
	private int imgHeight = 0;
	boolean left,right,jump,fall,land,walk,attacking,winner;
	private JPanel comp;
	
	public Player (int x, int y, JPanel comp){
		x1 = x;
		y1 = y;
		this.comp = comp;
		try{
			sprite = new ImageIcon("Images\\P1.gif");
		}catch(Exception e){
			System.out.println(e.toString());
		}
		imgLength = sprite.getIconWidth();
		imgHeight = sprite.getIconHeight();
	}
	public void move (int dx, int dy){
		x1 = x1+dx;
		y1 = y1+dy;
	}
	public void draw(Graphics g){
		if(left){
			try{
				if (winner){
					sprite = new ImageIcon("Images\\P1_L.gif");
				}else{
					if (walk && !jump && !fall){
						if(attacking)
							sprite = new ImageIcon("Images\\P1_R_A.gif");
						else
							sprite = new ImageIcon("Images\\P1_R.gif");
					}else if(jump)
						if(attacking)
							sprite = new ImageIcon("Images\\P1_J_A.gif");
						else
							sprite = new ImageIcon("Images\\P1_J.gif");
					else if (fall)
						if(attacking)
							sprite = new ImageIcon("Images\\P1_F_A.gif");
						else
							sprite = new ImageIcon("Images\\P1_F.gif");
					else{
						if(attacking)
							sprite = new ImageIcon("Images\\P1_A.gif");
						else
							sprite = new ImageIcon("Images\\P1.gif");
					}
				}
			}catch(Exception e){
				System.out.println(e.toString());
			}
		}else if(right){
			try{
				if (winner){
					sprite = new ImageIcon("Images\\P1_2_L.gif");
				}else{
					if (walk && !jump && !fall){
						if(attacking)
							sprite = new ImageIcon("Images\\P1_2_R_A.gif");
						else
							sprite = new ImageIcon("Images\\P1_2_R.gif");
					}else if(jump)
						if(attacking)
							sprite = new ImageIcon("Images\\P1_2_J_A.gif");
						else
							sprite = new ImageIcon("Images\\P1_2_J.gif");
					else if (fall)
						if(attacking)
							sprite = new ImageIcon("Images\\P1_2_F_A.gif");
						else
							sprite = new ImageIcon("Images\\P1_2_F.gif");
					else{
						if(attacking)
							sprite = new ImageIcon("Images\\P1_2_A.gif");
						else
							sprite = new ImageIcon("Images\\P1_2.gif");
						}	
					}
			}catch(Exception e){
				System.out.println(e.toString());
			}
		}
		sprite.paintIcon(comp, g, x1, y1);
	}
	public void faceL(){
		left = true;
		right = false;
	}
	public void faceR(){
		right = true;
		left = false;
	}
	public void jump(){
		jump = true;
	}
	public void fall(){
		fall = true;
		jump = false;
	}
	public void land(){
		fall = false;
	}
	public void walk(){
		walk = true;
	}
	public void stopwalk(){
		walk = false;
	}
	public void attack(){
		attacking = true;
	}
	public void stopattack(){
		attacking = false;
	}
	public void winner(){
		winner = true;
	}
	public void unwin(){
		winner = false;
	}
	public int getX(){
		return x1;
	}
	public int getY(){
		return y1;
	}
	public int getWidth(){
		return imgLength;
	}
	public int getHeight(){
		return imgHeight;
	}
	public boolean containsPoint(int x, int y){
		return false;
	}
	public void setPosition(int x, int y){
		x1 = x;
		y1 = y;
	}
}