/* 
Mursheed Drahman and Joanne Lu
ICS4U1-01
Summative Project: Sierpinski's Nightmare
Ms. Strelkovska
December 24, 2011
*/

import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.*;
import java.util.ArrayList;
import java.awt.Rectangle;
import java.awt.GraphicsEnvironment;
import java.awt.GraphicsDevice;
import sun.audio.*;
import java.util.Scanner;

class MyPanel extends JPanel implements ActionListener,MouseListener,MouseMotionListener{
	///IO & Highscore Variables///
	private Scanner input;
	private PrintWriter output;
	private boolean displayHS = false;
	private ArrayList<String> userName = new ArrayList<String>();
	private ArrayList<Integer> userScore = new ArrayList<Integer>();
	private BufferedImage highscores;
	private String userInput;

	///Menu and JPanel Buttons///
	private JButton help, pause, toMenu;
	private JPanel btnPanel;
	private Rectangle startButton = new Rectangle(1072,31,184,72);
	private Rectangle highscoreButton = new Rectangle(1072,118,184,72);
	private Rectangle quitButton = new Rectangle(1072,228,184,72);
	
	///Platform Variables///
	private ArrayList<Rectangle> platforms = new ArrayList<Rectangle>();
	private ArrayList<Rectangle> proximities = new ArrayList<Rectangle>();
	
	///Game Timers///
	private Timer fps, shootclock, bgFPS, resetter, gameoverTimer, bossSpawn, recover, scoreTime;
	
	///Images & Backgrounds///
	private BufferedImage bg, bg1;
	private ArrayList<BufferedImage> hill = new ArrayList<BufferedImage>();
	private BufferedImage blank, aim, targetaim;
	private int bgFrame = 0;
	boolean gameover = false;
	boolean gameoverScreen = false;
	private int red = 110;
	private ImageIcon test1, test2, gameoverImage, head;
	private BufferedImage leg1,leg2,leg3,leg4;
	private boolean menu = true;
	
	///Player and User Interface///
	private Player player = new Player(512,578, this);
	private Rectangle player1 = new Rectangle(512,578,100,100);
	private int playerLives = 5;
	private Cursor blanked;
	private int jtime=0;
	private int stageN =1;
	private int playerHP = 100;
	private int tarX,tarY;
	private boolean hitBelow, hitLeft, hitRight;
	private ArrayList<Bullet> bullets = new ArrayList<Bullet>();
	private boolean knockedDown = false;
	private boolean left=false, right=false, jump=false, canJump = true, targeted = false, shooting = false, onPlatform = false;

	///Audio///
	private AudioPlayer bgP;
    private AudioStream bgM;
    private ContinuousAudioDataStream loop = null;
    private AudioData a_d = null;
	
	///Fractal Monsters///
	private ArrayList<Fractal> baddies = new ArrayList<Fractal>();
	
	///Boss - Speyeder///
	private int x1 = 1280;
	private int y1 = 250;
	private int diameter = 90;  
	private int leftMostX = 9999;
	private Color eyeColour = Color.WHITE;
	private int eyeHit = 0;
	private boolean still = true;
	private boolean mL = false;
	private boolean mR = true;
	private Timer spiderMovementUp;
	private boolean up = false;
	private int y = 200;

	///In Gameplay - Scores///
	private int digit;
	private int score_screen;
	private int numDigits = 0;	
	private int bonusPt = 200;
	private int damageDealt = 0;
	private ArrayList<BufferedImage> score = new ArrayList<BufferedImage>();
	private boolean win = false;
	
	public MyPanel(){
		setBackground(new Color(255,255,255));
		setLayout(new BorderLayout());
		initializeImages();
		blanked = Toolkit.getDefaultToolkit().createCustomCursor(blank, new Point(0,0), "neutral");
		setCursor(blanked); 
		fps = new Timer(13,this); 
		shootclock = new Timer(145,this);
		bgFPS = new Timer(700,this);
		resetter = new Timer(1000,this);
		scoreTime = new Timer(2000,this);
		gameoverTimer = new Timer(3000,this);
		spiderMovementUp = new Timer(3000,this);
		bossSpawn = new Timer (3000, this);
		recover = new Timer(300,this);
		
		//player proximities - hit boxes//
		proximities.add(new Rectangle(537,678,50,1)); //bottom
		proximities.add(new Rectangle(587,578,1,90)); //right
		proximities.add(new Rectangle(522,578,1,90)); //left
		proximities.add(new Rectangle(537,578,50,1)); //top
		
		help = new JButton("Help");
		pause = new JButton("Pause");
		toMenu = new JButton("To Menu");
		
		help.addActionListener(this);
		pause.addActionListener(this);
		toMenu.addActionListener(this);

		addMouseListener(this);
		addMouseMotionListener(this);
	    setFocusable(true);   
		addKeyListener(new KeyAdapter(){
			//player controls//
            public void keyPressed(KeyEvent e) {
                String pressedKey = e.getKeyText(e.getKeyCode());
				if(!knockedDown){
					if( pressedKey.equals("A") ||  pressedKey.equals("Left")){
						left = true;
						player.walk();
					} 
					if( pressedKey.equals("S")||  pressedKey.equals("Down")){
						player.winner();
					} 
					if( pressedKey.equals("D")||  pressedKey.equals("Right")){
						right = true;
						player.walk();
					} 
					if( pressedKey.equals("Space") || pressedKey.equals("W")||  pressedKey.equals("Up")){
						if(canJump && onPlatform){
							jump = true;
							canJump = false;
						}
					}
				}
			}
			public void keyReleased(KeyEvent e){
				String pressedKey = e.getKeyText(e.getKeyCode());
				if(!knockedDown){
					if( pressedKey.equals("A")||  pressedKey.equals("Left") ){
						left = false;
						player.stopwalk();
					} 
					if( pressedKey.equals("S")||  pressedKey.equals("Down")){
						player.unwin();
					} 
					if( pressedKey.equals("D")||  pressedKey.equals("Right")){
						right = false;
						player.stopwalk();
					} 
					if( pressedKey.equals("Space")|| pressedKey.equals("W") ||  pressedKey.equals("Up")){
					}
				}
			}
		});                
		btnPanel = new JPanel();
		btnPanel.setLayout(new GridLayout(1,4));
		btnPanel.add(help);
		btnPanel.add(pause);
		btnPanel.add(toMenu);
		btnPanel.setFocusable(false);
		this.add(btnPanel, BorderLayout.SOUTH); 
	
	}
	//initializes a stage
	private void stageStart(int stage){
		eyeHit = 0; 
		still = true; //spider boss' eyes are unbroken
		bullets.clear();
		baddies.clear();
		platforms.clear();
		if (playerHP <= 0){ // resets variables in case of gameover
			playerHP = 100;
			gameover = false;
			startBGM();
		}
		if (stage == 1){ 
			player.setPosition(0,0);
			
			baddies.add(new Triangle(512,384,50,Color.white,1));
			baddies.add(new Triangle(600,300,50, Color.white,1));
			
			platforms.add(new Rectangle(0,570,400,20));
			platforms.add(new Rectangle(500,480,300,20));
			platforms.add(new Rectangle(600,350,200,20));
			platforms.add(new Rectangle(300,250,100,12));
			platforms.add(new Rectangle(0,690,1000,200));
		}else if (stage == 2){
			player.setPosition(640,0);
			
			baddies.add(new Triangle(1280,300,100, Color.white, 1));
			baddies.add(new Triangle(-300,300, 100, Color.gray, 1));
			
			platforms.add(new Rectangle(200,450,150,20));
			platforms.add(new Rectangle(800,550,150,20));
			platforms.add(new Rectangle(50,550,150,15));
			platforms.add(new Rectangle(500,400,150,20));
			platforms.add(new Rectangle(0,690,500,200));
			platforms.add(new Rectangle(700,690,500,200));
		}else if (stage == 3){
			if (player.getY()>766) //if player falls off screen, reset position
				player.setPosition(1100,0);
				
			baddies.add(new Square(-300,300, 150, Color.white, 1));
			
			platforms.add(new Rectangle(200,450,150,20));
			platforms.add(new Rectangle(800,550,150,20));
			platforms.add(new Rectangle(50,550,150,15));
			platforms.add(new Rectangle(500,400,150,20));
			platforms.add(new Rectangle(0,690,500,200));
			platforms.add(new Rectangle(700,690,500,200));
		}else if(stage == 4){
			player.setPosition(0,0);
			
			baddies.add(new Square(-300,300, 40, Color.white, 1));
			baddies.add(new Square(-300,-50, 40, Color.white, 1));
			baddies.add(new Square(1280,300, 40, Color.white, 1));
			
			platforms.add(new Rectangle(200,450,150,20));
			platforms.add(new Rectangle(50,550,150,15));
			platforms.add(new Rectangle(500,400,150,20));
			platforms.add(new Rectangle(600,480,150,20));
			platforms.add(new Rectangle(850,350,150,15));
		}else if (stage ==5){
			if (player.getY()>766)
				player.setPosition(600,0);
			baddies.add(new Triangle(700,800,200,Color.white, 1));
			
			platforms.add(new Rectangle(200,450,150,20));
			platforms.add(new Rectangle(50,550,150,15));
			platforms.add(new Rectangle(500,400,150,20));
			platforms.add(new Rectangle(600,480,150,20));
			platforms.add(new Rectangle(850,350,150,15));
		}else if (stage == 6){
			player.setPosition(600,0);
			
			platforms.add(new Rectangle(0,680,1280,40));
			
			baddies.add(new Triangle(-100,400,50,Color.white, 1));
			baddies.add(new Square(-100,400,50,Color.white, 1));
			baddies.add(new Triangle(1280,500,50,Color.white, 1));
			baddies.add(new Square(1280,500,50,Color.white, 1));
		}else if (stage == 7){
			platforms.add(new Rectangle(0,680,1280,40));
			
			baddies.add(new Square(1300,300,250,Color.white, 1));
		}else if (stage == 8){
			platforms.add(new Rectangle(0,680,1280,40));
			baddies.add(new Triangle(-300,250,250,Color.white, 1));
		}
		else if (stage == 9){
			player.setPosition(90,0);
			
			baddies.add(new Triangle(600,20,60,Color.white, 1));
			baddies.add(new Triangle(700,15,60,Color.white, 1));
			baddies.add(new Triangle(800,10,60,Color.white, 1));
			baddies.add(new Triangle(900,10,60,Color.white, 1));
			baddies.add(new Triangle(1000,15,60,Color.white, 1));
			baddies.add(new Triangle(1100,20,60,Color.white, 1));
			
			platforms.add(new Rectangle(500,500,600,600));
			platforms.add(new Rectangle(100,400,600,600));
			platforms.add(new Rectangle(700,500,400,600));
		}else if (stage ==10){
			if (player.getY()>766)
				player.setPosition(90,0);
			baddies.add(new Square(600,700,60,Color.white, 1));
			baddies.add(new Square(700,700,60,Color.white, 1));
			baddies.add(new Square(800,700,60,Color.white, 1));
			baddies.add(new Square(900,700,60,Color.white, 1));
			baddies.add(new Square(1000,700,60,Color.white, 1));
			baddies.add(new Square(1100,700,60,Color.white, 1));
		
			platforms.add(new Rectangle(500,500,600,600));
			platforms.add(new Rectangle(100,400,600,600));
			platforms.add(new Rectangle(700,500,400,600));

		}else if (stage == 11){
			player.setPosition(600,0);
			
			baddies.add(new Square(600,700,60,Color.white, 1));
			baddies.add(new Triangle(0,0,100, Color.gray, 1));
			
			platforms.add(new Rectangle(300,600,700,600));
			platforms.add(new Rectangle(350,520,100,11));
			platforms.add(new Rectangle(450,520,100,11));
		}
		else if (stage == 12){
			if (player.getY()>766)
				player.setPosition(600,400);
				
			baddies.add(new Square(1200,0,140,Color.black, 1));
			
			platforms.add(new Rectangle(300,600,700,600));
			platforms.add(new Rectangle(350,520,100,11));
			platforms.add(new Rectangle(650,520,100,11));
		}
		else if (stage == 13){
			player.setPosition(0,500);
			baddies.add(new Square(1280,100,100,new Color(10,10,10), 1));
			baddies.add(new Square(1280,200,100,new Color(10,10,10), 1));
			baddies.add(new Square(1280,300,100,new Color(10,10,10), 1));
			baddies.add(new Square(1280,400,100,new Color(10,10,10), 1));
			baddies.add(new Square(1280,500,100,new Color(10,10,10), 1));
			baddies.add(new Square(1280,600,100,new Color(10,10,10), 1));
			
			platforms.add(new Rectangle(0,600,1280,300));
			platforms.add(new Rectangle(0,0,1280,300));
		}
		else if (stage == 14){
			baddies.add(new Square(-100,100,100,new Color(10,10,10), 1));
			baddies.add(new Square(-100,200,100,new Color(10,10,10), 1));
			baddies.add(new Square(-100,300,100,new Color(10,10,10), 1));
			baddies.add(new Square(-100,400,100,new Color(10,10,10), 1));
			baddies.add(new Square(-100,500,100,new Color(10,10,10), 1));
			baddies.add(new Square(-100,600,100,new Color(10,10,10), 1));
			platforms.add(new Rectangle(0,600,1280,300));
			platforms.add(new Rectangle(0,0,1280,300));
		}
		else if (stage == 15){
			player.setPosition(0,500);
			baddies.add(new Square(1000,500,200,Color.white,0));
			baddies.add(new Triangle(1000,500,200,Color.white,0));
			platforms.add(new Rectangle(0,680,1280,40));
		}
		else if (stage == 16){
			baddies.add(new Triangle(350,700,15,new Color(10,10,10),1));
			baddies.add(new Triangle(450,700,15,Color.white,1));
			baddies.add(new Triangle(650,700,15,new Color(10,10,10),1));
			baddies.add(new Triangle(950,700,15,Color.white,1));
			baddies.add(new Triangle(1050,700,15,new Color(10,10,10),1));
			baddies.add(new Triangle(150,700,15,Color.white,1));
			platforms.add(new Rectangle(0,680,1280,40));
		}
		else if (stage == 17){
			baddies.add(new Triangle(350,700,60,Color.white,5));
			baddies.add(new Triangle(450,700,60,Color.white,5));
			baddies.add(new Triangle(650,700,60,Color.white,5));
			baddies.add(new Triangle(950,700,60,Color.white,5));
			baddies.add(new Triangle(1050,700,60,Color.white,5));
			baddies.add(new Triangle(150,700,60,Color.white,5));
			platforms.add(new Rectangle(0,680,1280,40));
		}
		else if (stage == 18){
			player.setPosition(0,0);
			
			baddies.add(new Triangle (1380,0,300,Color.white, 2));
			
			platforms.add(new Rectangle(300,450,150,20));
			platforms.add(new Rectangle(800,500,150,20));
			platforms.add(new Rectangle(110,550,150,15));
			platforms.add(new Rectangle(500,350,150,20));
			platforms.add(new Rectangle(0,690,500,200));
			platforms.add(new Rectangle(0,690,500,200));
		}
		else if(stage ==19){
			if (player.getY()>738)
				player.setPosition(0,0);
				
			baddies.add(new Square (1380,0,400,Color.white, 1));
			
			platforms.add(new Rectangle(300,450,150,20));
			platforms.add(new Rectangle(800,500,150,20));
			platforms.add(new Rectangle(110,550,150,15));
			platforms.add(new Rectangle(500,350,150,20));
			platforms.add(new Rectangle(0,690,500,200));
			platforms.add(new Rectangle(0,690,500,200));
		}
		
		else if (stage == 20){
			if (player.getY()>738)
				player.setPosition(0,0);
			/////speyeder boss/////
			bossSpawn.start();
			baddies.add(new Circle(x1,y1,diameter/2,eyeColour));
			baddies.add(new Circle(x1+diameter/2,y1,diameter/2,eyeColour));
			int midX = x1+diameter/2;
			int midY = y1+diameter/2;
			baddies.add(new Circle(midX-diameter/2-diameter*7/10-diameter/2, midY-diameter/2-diameter/4, diameter/2, eyeColour));
			baddies.add(new Circle(midX+diameter/2, midY-diameter/2, diameter*7/10, eyeColour));
			baddies.add(new Circle(midX-diameter/2-diameter*7/10, midY-diameter/2, diameter*7/10, eyeColour));
				
			baddies.add(new Circle((midX+diameter/2+diameter*7/10), (midY-diameter/2-diameter/4), diameter/2, eyeColour));
		
			baddies.add(new Circle(midX-diameter/2, midY-diameter*7/10, diameter/4, eyeColour));
			baddies.add(new Circle(midX+diameter/2-diameter/4, midY-diameter*7/10, diameter/4, eyeColour));   
			
			platforms.add(new Rectangle(300,450,150,20));
			platforms.add(new Rectangle(800,500,150,20));
			platforms.add(new Rectangle(110,550,150,15));
			platforms.add(new Rectangle(500,350,150,20));
			platforms.add(new Rectangle(0,690,500,200));
			platforms.add(new Rectangle(0,690,500,200));
		}
		else if (stage == 21){
			player.winner();//end of game
			bossSpawn.stop();
			win = true;
		}
	}
	
	//stops all of the timers, used when gameover and pause occurs
	public void pause(){ 
		fps.stop();
		shootclock.stop();
		bgFPS.stop();
		resetter.stop();
		scoreTime.stop();
		spiderMovementUp.stop();
		gameoverTimer.stop();
		bossSpawn.stop();
	}
	//resumes all of the stopped timers
	public void resume(){
		fps.start();
		shootclock.start();
		bgFPS.start();
		resetter.start();
		scoreTime.start();
		spiderMovementUp.start();
		if(stageN==20)
			bossSpawn.start();
	}
	public void initializeImages(){ //importing images
		try{ 
			head = new ImageIcon("Images\\head.gif");
			bg = ImageIO.read(new File("Images\\TITLE.png"));
			aim = ImageIO.read(new File("Images\\aim.png"));
			targetaim = ImageIO.read(new File("Images\\aimtargeted.png"));
			blank = ImageIO.read(new File("Images\\blank.png"));
			test1 = new ImageIcon("Images\\P1.gif");
			test2 = new ImageIcon("Images\\P1_2.gif");
			gameoverImage = new ImageIcon("Images\\gameover.gif");
			leg1 = ImageIO.read(new File("Images\\leg1.png"));
			leg2 = ImageIO.read(new File("Images\\leg2.png"));
			leg3 = ImageIO.read(new File("Images\\leg3.png"));
			leg4 = ImageIO.read(new File("Images\\leg4.png"));
			highscores = ImageIO.read(new File("Images\\highscores.png"));
			for (int i = 0; i<29; i++){
				hill.add(ImageIO.read(new File("Images\\Background\\Hill\\"+i+".jpg")));
			}
			for(int i = 0; i<10; i++){
				score.add(ImageIO.read(new File("Images\\"+i+".png")));
			}
		}catch(IOException e){
			System.out.println(e);
		}
	}
	
	public void startBGM(){ //start the background music 
		bgP = AudioPlayer.player;
		try{
			bgM = new AudioStream(new FileInputStream("boss_1.wav")); 
			a_d= bgM.getData();
			loop = new ContinuousAudioDataStream(a_d); 
		} catch (Exception e){
			System.out.println(e.toString());
		}
		bgP.start(loop);
	}
	
	public void stopBGM(){ //stop the background music
		bgP.stop(loop);
	}
	
	public void playerHitCheck(int i){ //checks when a monster hits a player, player gets knocked back and damaged
		if(playerHP>0 && knockedDown == false){
			if(baddies.get(i).containsPoint(player.getX(),player.getY())){ //hit from left side
				playerHP -= baddies.get(i).getSize()/5; 
				if(damageDealt-50 > 0) //when monster hits player, score gets 50 subtracted
					damageDealt -= 50;
				else
					damageDealt = 0; 
				left = false;
				right = false;
				jump = false;
				knockedDown = true;
				hitRight = true;
				recover.start();
			}else if(baddies.get(i).containsPoint(player.getX()+player.getWidth()/2,player.getY()-1)){ //hit from above
				playerHP -= baddies.get(i).getSize()/5;
				if (damageDealt-50 > 0)
					damageDealt -= 50;
				else
					damageDealt = 0;
				left = false;
				right = false;
				jump = false;
				knockedDown = true;
				recover.start();
			}else if(baddies.get(i).containsPoint(player.getX()+player.getWidth(),player.getY())){ //hit from right
				playerHP -= baddies.get(i).getSize()/5;
				if (damageDealt-50 > 0)
					damageDealt -= 50;
				else
					damageDealt = 0;
				left = false;
				right = false;
				jump = false;
				hitLeft = true;
				knockedDown = true;
				recover.start();
			}else if(baddies.get(i).containsPoint(player.getX(),player.getY()+player.getHeight()/2)){ //hit from left
				playerHP -= baddies.get(i).getSize()/5;
				if (damageDealt-50 > 0)
					damageDealt -= 50;
				else
					damageDealt = 0;
				left = false;
				right = false;
				jump = false;
				hitRight= true;
				knockedDown = true;
				recover.start();
			}else if(baddies.get(i).containsPoint(player.getX()+player.getWidth(),player.getHeight()/2)){ //hit from right
				playerHP -= baddies.get(i).getSize()/5;
				if (damageDealt-50 > 0)
					damageDealt -= 50;
				else
					damageDealt = 0;
				left = false;
				right = false;
				jump = false;
				hitLeft = true;
				knockedDown = true;
				recover.start();
			}else if(baddies.get(i).containsPoint(player.getX(),player.getY()+player.getHeight())){ //hit from left
				playerHP -= baddies.get(i).getSize()/5;
				if (damageDealt-50 > 0)
					damageDealt -= 50;
				else
					damageDealt = 0;
				left = false;
				right = false;
				hitRight = true;
				jump = false;
				knockedDown = true;
				recover.start();
			}else if(baddies.get(i).containsPoint(player.getX()+player.getWidth()/2,player.getY()+player.getHeight()+1)){ //hit from below
				playerHP -= baddies.get(i).getSize()/5;
				if (damageDealt-50 > 0)
					damageDealt -= 50;
				else
					damageDealt = 0;
				left = false;
				right = false;
				jump = false;
				hitBelow = true;
				knockedDown = true;
				recover.start();
			}else if(baddies.get(i).containsPoint(player.getX()+player.getWidth(),player.getY()+player.getHeight())){ //hit from right
				playerHP -= baddies.get(i).getSize()/5;
				if (damageDealt-50 > 0)
					damageDealt -= 50;
				else
					damageDealt = 0;
				left = false;
				right = false;
				jump = false;
				hitLeft = true;
				knockedDown = true;
				recover.start();
			}
		}
	}
	
	//painting images
	public void paintComponent(Graphics g){
		super.paintComponent(g);
			if(menu){
				g.drawImage(bg,0,0,getWidth(),getHeight(),0,0,1280,768,null);
				g.drawImage(targetaim,tarX-25,tarY-25,tarX+25,tarY+25,0,0,50,50,null);
				if (displayHS){ //highscore display
					g.drawImage(highscores,0,0,1280,768,0,0,1280,768,null);
				}
			} else if (!gameoverScreen){
				if (stageN != 13 && stageN != 14)
					g.drawImage(hill.get(bgFrame),0,0,getWidth(),getHeight(),0,0,1280,768,null);
				else{
					g.setColor(Color.black);
					g.fillRect(0,0,1280,768);
				}
				
				
				//position of the spider legs under the eyes - legs always under leftmost eye
					if (still){
						leftMostX = 9999;
						for(int c=0; c<baddies.size(); c++){
							if(baddies.get(c).numSides() == 1){	
								if(baddies.get(c).getX()<leftMostX){
									leftMostX = baddies.get(c).getX();
								}
							}
						}
						g.drawImage(leg4,leftMostX,y,leftMostX+50,y+500,0,0,100,500,null);
						g.drawImage(leg2,leftMostX+100,y,leftMostX+150,y+500,0,0,100,500,null);
						g.drawImage(leg3,leftMostX+150,y,leftMostX+200,y+500,0,0,100,500,null);
						g.drawImage(leg1,leftMostX+250,y,leftMostX+300,y+500,0,0,100,500,null);
					}
					
				for(int i=0; i < baddies.size(); i++){
					if(baddies.get(i).numSides() == 1)
						baddies.get(i).draw(g);
				}
				for (int i = 0; i< platforms.size(); i++){
					g.setColor(Color.black);
					g.fillRect((int)platforms.get(i).getX(),(int)platforms.get(i).getY(),(int)platforms.get(i).getWidth(),(int)platforms.get(i).getHeight());
					g.setColor(Color.gray);
					g.drawRect((int)platforms.get(i).getX(),(int)platforms.get(i).getY(),(int)platforms.get(i).getWidth(),(int)platforms.get(i).getHeight());
				}
				for(int i=0; i < baddies.size(); i++){
					if(baddies.get(i).numSides() != 1)
						baddies.get(i).draw(g);
				}
				for(int i=0;i<bullets.size();i++){
					bullets.get(i).draw(g);
				}
				
				player.draw(g);
				
				//hp bar
				g.setColor(Color.white);
				g.drawRect(715, 35, 500, 6);
				g.fillRect(715, 35, playerHP*5, 6);
				for (int i = 0; i<playerLives; i++)
					head.paintIcon(this, g, 715+(i*43),55);
				//target cursor
				score_screen = damageDealt;
				String numD = score_screen+"";
				numDigits = numD.length();
				int scoreImgWidth = 50;
				int yCoor = 15;
				while(score_screen>0){
					digit = score_screen%10;
					g.drawImage(score.get(digit),numDigits*scoreImgWidth,yCoor,numDigits*scoreImgWidth+scoreImgWidth,yCoor+scoreImgWidth,0,0,scoreImgWidth,scoreImgWidth,null);
					numDigits -= 1;
					score_screen = score_screen/10;
				}
				if(!targeted)
					g.drawImage(aim,tarX-25,tarY-25,tarX+25,tarY+25,0,0,50,50,null);
				else
					g.drawImage(targetaim,tarX-25,tarY-25,tarX+25,tarY+25,0,0,50,50,null);
				
				if(gameover){
					if(red>=0){
						g.setColor(new Color(red,0,0));
						g.fillRect(0,0,1280,768);
					}
				}
			} else if (gameoverScreen){
				gameoverImage.paintIcon(this,g,0,0);
			} 
	}

	//MouseListener
	public void mouseClicked( MouseEvent e ){}
	public void mousePressed( MouseEvent e ){
		shooting = true;
		player.attack();
	}
	
	//MouseMotionListener
	public void mouseDragged( MouseEvent e ){ //retrieves x,y of mouse
		tarX = e.getX();
		tarY = e.getY();
		targeted = false;
		for (int i = 0; i < baddies.size(); i++){
			if (baddies.get(i).containsPoint(tarX,tarY) )
				targeted = true;
		}
		if(tarX>(player.getX()+(player.getWidth()/2))) //facing left or right
			player.faceR();
	    else if (tarX<(player.getX()+(player.getWidth()/2)))
			player.faceL();
	}
	
	public void mouseReleased( MouseEvent e ){
		shooting = false;
		player.stopattack();
		if(menu){ // menu buttons
			if(startButton.contains(tarX,tarY)){
				startBGM();
				playerHP = 100;
				bonusPt = 200;
				damageDealt = 0;
				playerLives = 5;
				menu = false;
				win = false;
				gameover = false;
				gameoverScreen = false;
				stageN = 1;
				stageStart(stageN);
				resume();
				} else if (highscoreButton.contains(tarX,tarY)){
					try{
						input = new Scanner(new File("highscores.dat"));
					} catch (Exception error) {
						System.out.println(error.toString());
					}
					String st;
					int current;
					int totalCnt = 0;
					int min = 0;
					int index = 0;
					String sorted="";
					while(input.hasNext()){
						st = input.nextLine();
						current = Integer.parseInt(st.substring(st.indexOf(" ")+1));
						userScore.add(current);
						userName.add(st.substring(0,(st.indexOf(" "))));
						if(current>min){
							min = current; 
							index = totalCnt;
						}
						totalCnt++;			
					}
					sorted = sorted + userName.get(index)+" "+userScore.get(index)+"\n"; //first score
					for (int x = 0; x<2; x++){
						userScore.remove(index);
						userName.remove(index);
						totalCnt = 0;
						min = 0;
						index = 0;
						for(int k=0; k<userScore.size(); k++){
							current = userScore.get(k);
							if(current>min){
								min = current; 
								index = totalCnt;
							}
							totalCnt++;			
						}
						sorted = sorted + userName.get(index)+" "+userScore.get(index)+"\n"; //second score
					}
						
					displayHS = true;
					JOptionPane.showMessageDialog(null, sorted, "Highscore", JOptionPane.PLAIN_MESSAGE);
					input.close();
					userScore.clear();
					userName.clear();
					displayHS = false;
				} else if (quitButton.contains(tarX,tarY)){
					System.exit(0);
			}
		}
	}
	
	public void mouseEntered( MouseEvent e ){}
    public void mouseExited( MouseEvent e ){}

    public void mouseMoved( MouseEvent e ){
		tarX = e.getX();
		tarY = e.getY();
		repaint();
		targeted = false;
		for (int i = 0; i < baddies.size(); i++){
			if (baddies.get(i).containsPoint(tarX,tarY) )
				targeted = true;
		}
		if(tarX>(player.getX()+(player.getWidth()/2))){ //facing left or right
			player.faceR();
	    }else if (tarX<(player.getX()+(player.getWidth()/2))){
			player.faceL();
		}
	}
	
	public void actionPerformed(ActionEvent evt){
		boolean remove = false; //for the bullet
		boolean canLeft = true, canRight = true;
		int a=0;
		if(evt.getSource()==bgFPS){//bg animations
			if (bgFrame<28)
				bgFrame++;
			else
				bgFrame = 0;
		}
		if(evt.getSource()==bossSpawn){ // boss mechanic
			if ((int)(Math.random()*2)+1==1){
				baddies.add(new Square((int)(Math.random()*1280)+1,(int)(Math.random()*768)+1, 20,Color.white, 2));
			}else
				baddies.add(new Triangle((int)(Math.random()*1280)+1,(int)(Math.random()*768)+1, 20,Color.white, 2));
		}
		if(evt.getSource()==spiderMovementUp){ // boss mechanic
			if (up)
				up = false;
			else
				up = true;
		}
		
		//timer to calculate points gained//
		if(evt.getSource()==scoreTime){
			bonusPt-= 1;
		}
		
		if(evt.getSource()==fps){ // main timer, deals with most animations and calculations
			if (win){
				pause();
				try{
					output = new PrintWriter(new BufferedWriter(new FileWriter("highscores.dat",true)));
				} catch (Exception e) {
					System.out.println(e.toString());
				}
				userInput = JOptionPane.showInputDialog("CONGRATULATIONS!\nEnter your name: \n(WARNING!:use only alphabets, no other characters or the score may not display properly!) ");
				menu = true;
				stopBGM();
				output.write(userInput + " " + damageDealt + "\n");
				player.unwin();
				output.close();
			}	
			else{
				if (baddies.size() == 0 && !gameover){
					stageN++;
					stageStart(stageN);
				}
				if (player.getY()>=768){
					playerHP = 0;
				}
				if (playerHP < 1 && !gameover){
					gameover = true;
					playerLives--;
				}
				if(gameover){
					stopBGM();
					red-=1;
				} 
				if (red<=0 && playerLives < 1){
					gameoverTimer.start();
					gameoverScreen = true;
				}
				if (red<=0 && !gameoverTimer.isRunning()){
					stageStart(stageN);
					red = 120;
					if (damageDealt - 500 > 0)
						damageDealt -= 500;
					else
						damageDealt = 0;
				}
				
				for (int i = 0; i<bullets.size(); i++){
					bullets.get(i).move();
					for (int j = 0; j<baddies.size(); j++){
						//checks whether bullet is within chasing distance of monster
						if (baddies.get(j).numSides()!=1 &&  baddies.get(j).getAggro().contains(bullets.get(i).getX(),bullets.get(i).getY()))
							baddies.get(j).aggro();
						if (bullets.get(i).collidesWith(baddies.get(j))){
							if(!still || baddies.get(j).numSides()!=1){ //gets points if bullet hits spider eyes, non moving, or non-spider monster 
								if(bonusPt>0)
									damageDealt = damageDealt + bonusPt*3/5;
								else
									damageDealt = damageDealt + 10;
							}
							remove = true;
							if(baddies.get(j).numSides() == 1){ //if monster hit is a circle
								if(baddies.get(j).getColour() == Color.white){
									baddies.get(j).setColour(Color.RED);
									if(bonusPt>0){
										damageDealt = damageDealt + bonusPt*3/5;
									} else {
										damageDealt = damageDealt + 10;
									}
									eyeHit++;
								}
								if (still == false && baddies.get(j).getSize()/4 > 5){ //if monster hit is a circle, moving
									baddies.add(new Circle(baddies.get(j).getX(),baddies.get(j).getY(),diameter/4,Color.red));
									baddies.add(new Circle(baddies.get(j).getX(),baddies.get(j).getY(),diameter/4,Color.red));
									baddies.add(new Circle(baddies.get(j).getX(),baddies.get(j).getY(),diameter/4,Color.red));
									baddies.add(new Circle(baddies.get(j).getX(),baddies.get(j).getY(),diameter/4,Color.red));
								}
								if(eyeHit == 8) //if you hit all eight eyes, speyeder starts moving
									still = false;
							} else if(baddies.get(j).numSides() == 3){ //triangle
								if( baddies.get(j).getSize()/3 > 10){
									int randomDirection = (int)(Math.random()*75)+10;
									baddies.add(new Triangle(baddies.get(j).getX()+baddies.get(j).getSize()/2+ randomDirection,baddies.get(j).getY()+baddies.get(j).getSize()/2+ randomDirection,baddies.get(j).getSize()/2,Color.white,1));
									baddies.add(new Triangle(baddies.get(j).getX()-baddies.get(j).getSize()/2+ randomDirection,baddies.get(j).getY()-baddies.get(j).getSize()/2+ randomDirection,baddies.get(j).getSize()/2,Color.white,1));
									baddies.add(new Triangle(baddies.get(j).getX()+baddies.get(j).getSize()/2+ randomDirection,baddies.get(j).getY()-baddies.get(j).getSize()/2+ randomDirection,baddies.get(j).getSize()/2,Color.white,1));
								}
							} else if(baddies.get(j).numSides() == 4){ //square
								if( baddies.get(j).getSize()/3 > 10){
									int randomDirection = (int)(Math.random()*75)+10;
									baddies.add(new Square(baddies.get(j).getX()+randomDirection,baddies.get(j).getY()+randomDirection,baddies.get(j).getSize()/3,Color.white, 1));
									baddies.add(new Square(baddies.get(j).getX()+randomDirection,baddies.get(j).getY()-randomDirection,baddies.get(j).getSize()/3,Color.white, 1));
									baddies.add(new Square(baddies.get(j).getX()-randomDirection,baddies.get(j).getY()+randomDirection,baddies.get(j).getSize()/3,Color.white, 1));
									baddies.add(new Square(baddies.get(j).getX()-randomDirection,baddies.get(j).getY()-randomDirection,baddies.get(j).getSize()/3,Color.white, 1));
									baddies.add(new Square(baddies.get(j).getX()+randomDirection,baddies.get(j).getY()+randomDirection,baddies.get(j).getSize()/3,Color.white, 1));
									baddies.add(new Square(baddies.get(j).getX()+randomDirection,baddies.get(j).getY()-randomDirection,baddies.get(j).getSize()/3,Color.white, 1));
									baddies.add(new Square(baddies.get(j).getX()-randomDirection,baddies.get(j).getY()+randomDirection,baddies.get(j).getSize()/3,Color.white, 1));
									baddies.add(new Square(baddies.get(j).getX()-randomDirection,baddies.get(j).getY()-randomDirection,baddies.get(j).getSize()/3,Color.white, 1));
								}
							}
							if(baddies.get(j).numSides() != 1 || still == false){
								baddies.remove(j);
							} 
						}
					}
					if (bullets.get(i).getTimer() > 100 && !remove) //bullets disappear after certain time
						remove = true;
					for(int x=0; x<platforms.size();x++){	
						if (platforms.get(x).contains(bullets.get(i).getX(),bullets.get(i).getY())){
							remove=true;
							break;
						}
					}
					if (remove){
						bullets.remove(i);
						remove = false;
					}
				}
				//movement for monsters
				for (int i = 0; i<baddies.size();i++){
					if(baddies.get(i).numSides() == 1 && still==true){ //circle eyes, unmoving - motion is left to right, vice versa
						if(up && baddies.get(i).getY()>=0)
							baddies.get(i).move(0,-1);
						else if (!up && baddies.get(i).getY()<=500)
							baddies.get(i).move(0,1);
						if(mL){
							baddies.get(i).move(-1,0);
						}else if(mR){
							baddies.get(i).move(1,0);
						}if(baddies.get(i).getX()==this.getWidth()-baddies.get(i).getSize()/2){
							mR = false;
							mL = true;
						} else if (baddies.get(i).getX()==0){
							mL=false;
							mR=true;
						}
						
						playerHitCheck(i);
					} else if(baddies.get(i).numSides()!=1){ //same movement for squares and triangles		
						if (baddies.get(i).getX()<0){
							baddies.get(i).hitLeft();
						}else if (baddies.get(i).getX()>1280){
							baddies.get(i).hitRight();
						}
						if (!jump && !knockedDown && (baddies.get(i).getAggro().intersects(player1) || baddies.get(i).isAggroed())){
							baddies.get(i).setColour(Color.red);
							if (baddies.get(i).getX()+(baddies.get(i).getSize()/2)<player.getX()+(player.getWidth()/2))
								baddies.get(i).move(2,0);
							else if(baddies.get(i).getX()+(baddies.get(i).getSize()/2)>player.getX()+(player.getWidth()/2))
								baddies.get(i).move(-2,0);
							if (baddies.get(i).getY()-(baddies.get(i).getSize()/2)<player.getY())
								baddies.get(i).move(0,2);
							else if (baddies.get(i).getY()-(baddies.get(i).getSize()/2)>player.getY())
								baddies.get(i).move(0,-2);				
						}else if (jump && (baddies.get(i).getAggro().intersects(player1) || baddies.get(i).isAggroed())){
						}
						else if(baddies.get(i).getLeft()){
							baddies.get(i).move(1,0);
							baddies.get(i).hitLeft();
						}else if (baddies.get(i).getRight()){
							baddies.get(i).move(-1,0);
							baddies.get(i).hitRight();
						}
						
						playerHitCheck(i);
					} else if(baddies.get(i).numSides()==1 && still == false){ //circle eyes, moving
						playerHitCheck(i);
						baddies.get(i).travel();
					}
					if(playerHP<0)
						playerHP = 0;
				}
				if (knockedDown){ //player gets knocked back if hit by monster
					if(hitLeft){
						left=true;
						player.move(7,0);
						hitLeft = false;
					}else if (hitRight){
						right=true;
						player.move(-7,0);
						hitRight = false;
					}else if(hitBelow){
						jump=true;
						hitBelow = false;
					}
				}
				if(jump){
					player.move(0,-6);
					player.jump();
					jtime++;
					if (jtime>30)
						jump = false;
				}if (!jump && onPlatform){
					jtime=0;
					player.land();
					canJump = true;
				}
				if(!jump && !onPlatform){
					player.move(0,6);
					player.fall();
				}
				for (int i = 0; i<platforms.size(); i++){ //up and down platform collision
					if (platforms.get(i).intersects(proximities.get(3))){
						jump = false;
						player.fall();
					}
					if (platforms.get(i).intersects(proximities.get(0))){
						onPlatform=true;
						break;
					}else{
						onPlatform=false;
					}
									
				}
				for (int i = 0; i<platforms.size(); i++){
					if (platforms.get(i).intersects(proximities.get(1))){ //left, right platform collision
						canLeft=false;
						break;
					}else{
						canLeft=true;			
					}if (platforms.get(i).intersects(proximities.get(2))){
						canRight=false;
						break;
					}else{
						canRight=true;
					}	
				}
				if (left && canLeft){
					if(tarX>(player.getX()+(player.getWidth()/2))){ //facing left or right
						player.faceR();
					}else if (tarX<(player.getX()+(player.getWidth()/2))){
						player.faceL();
					}	
					if (player.getX() > 0)
						player.move(-5,0);
						
				}
				if (right && canRight){
					if(tarX>(player.getX()+(player.getWidth()/2))){ //facing left or right
						player.faceR();
					}else if (tarX<(player.getX()+(player.getWidth()/2))){
						player.faceL();
					}	
					if (player.getX() < 1180)
						player.move(5,0);
				}
				player1.setLocation(player.getX(),player.getY());
				proximities.get(0).setLocation(player.getX()+25,player.getY()+player.getHeight());
				proximities.get(1).setLocation(player.getX()+25,player.getY());
				proximities.get(2).setLocation(player.getX()+75,player.getY());
				proximities.get(3).setLocation(player.getX()+25,player.getY());
			
				repaint();
			}
		}
		if(evt.getSource()==gameoverTimer){
			gameoverScreen = false;
			gameoverTimer.stop();
			pause();
			try{
				output = new PrintWriter(new BufferedWriter(new FileWriter("highscores.dat",true)));
			} catch (Exception e) {
				System.out.println(e.toString());
			}
			userInput = JOptionPane.showInputDialog("CONGRATULATIONS!\nEnter your name: \n(WARNING!:use only alphabets, no other characters or the score may not display properly!) ");
			menu = true;
			output.write(userInput + " " + damageDealt + "\n");
			output.close();
			menu = true;
		}
		//stops monsters from chasing after player
		if(evt.getSource()==resetter){
			for(int i = 0; i<baddies.size(); i++){
				baddies.get(i).unaggro();
			}
		}
		//come back from being knocked down
		if(evt.getSource()==recover){
			knockedDown = false;
			left = false;
			right = false;
			recover.stop();
		}
		//how fast bullets are shot 
		if(evt.getSource()==shootclock){
			if (shooting){
				bullets.add(new Bullet(player.getX()+50,player.getY()+50,tarX,tarY,3,Color.red,20));
			}
		}
		if(evt.getSource()==help){ 
			pause();
			JOptionPane.showMessageDialog(null,"PLAYER CONTROLS:\nA or LEFT ARROW KEY to go left.\nD or RIGHT ARROW KEY to go right.\nW or SPACE or UP ARROW KEY to jump.\nS or DOWN ARROW KEY to party!\nMove cursor to aim; cursor will turn red if monster is targeted.\nHold left click to shoot.\n------------------------\nSierpinski, the esteemed mathematician, is having a nightmare! His own fractal creations have turned against him - help him defeat all of the monsters as he traverses his dream world. Be warned though, you only have 5 lives.\nThe shorter time you take to destroy fractal monsters, the more points you will accumulate. You will lose 50 points if you get hit by a monster, and 500 points if you die.\nGetting hit by monsters will decrease your hp bar (more hp depending on how big monster is), and when it becomes empty, you will lose a life.\nIf GAME OVER occurs, then all your points will be reset and you will be taken to the menu.\nGood luck and have fun!"); 
			this.requestFocus(); //resets focus to the window
			resume();
		}else if (evt.getSource()==pause){
			pause();
			JOptionPane.showMessageDialog(null, "Game Paused.", "PAUSE", JOptionPane.PLAIN_MESSAGE);
			this.requestFocus(); //resets focus to the window
			resume();
		}else if (evt.getSource()==toMenu){
			stopBGM();
			pause();
			menu = true;
			this.requestFocus(); //resets focus to the window
		}
	}
}


public class SierpinskiNightmare{
	public static void main(String args[]){
		 MyPanel mainPanel = new MyPanel();
		 JFrame window = new JFrame("Sierpinski's Nightmare");
		 window.getContentPane().add(mainPanel, BorderLayout.CENTER);
		 window.setSize(1280,768);
		 window.setVisible( true ); 		
		 window.setResizable(false);
		 window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		 
	}
}