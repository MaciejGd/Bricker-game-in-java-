import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class GamePanel extends JPanel implements Runnable{
	
	//path to the music in a game
	static final String musicPath = "C:\\Users\\macie\\eclipse-workspace\\Bricker\\src\\Eric Skiff - Underclocked NO COPYRIGHT 8-bit Music Background (online-audio-converter.com).wav";
	//path to the sound of destroying bricks
	static final String MUSIC_BRICK = "C:\\Users\\macie\\eclipse-workspace\\Bricker\\src\\mixkit-retro-game-notification-212.wav";
	static final int GAME_HEIGHT = 1000;
	static final int GAME_WIDTH = 1000;
	static final int BALL_DIAMETER = 15;
	static final int PADDLE_WIDTH = 250;
	static final int PADDLE_HEIGHT = 20;
	static final int BRICK_EDGE = 20;
	static final int BONUS_DIAMETER = 40;
	static final Dimension SCREEN_SIZE= new Dimension(GAME_WIDTH,GAME_HEIGHT);
	Brick bricks[] = new Brick[20*(GAME_WIDTH/BRICK_EDGE)];
	Paddle paddle;
	Ball ball;
	Thread gameThread;
	Image image;
	Graphics graphics;
	boolean startGame = false;
	PlayMusic brickCrushingEffect;
	Bonus bonus;
	int destroyedBricksCounter = 0;
	//bonuses variables
	ArrayList<Bonus> listOfBonuses = new ArrayList();
	ArrayList<Ball> listOfBalls = new ArrayList();
	GamePanel(){
		PlayMusic backgroundMusic = new PlayMusic();
		backgroundMusic.playMusic(musicPath);
		newBricks();
		newPaddle();
		newBall(GAME_WIDTH/2,GAME_HEIGHT/2);
		this.setFocusable(true);
		this.addKeyListener(new AL());
		this.setPreferredSize(SCREEN_SIZE);
		gameThread = new Thread(this);
		gameThread.start();
	}
	public void newBricks() {
		int k = 0;
		int u = 0;
		//Brick bricks[] = new Brick[16];
		for(int i = 0; i<bricks.length;i++) {
			if(k>GAME_WIDTH/BRICK_EDGE) {
				k = 0;
				u++;
			}
			if(k%7==1 || k%7==5 || k%7==6) {
				bricks[i] = new Brick(BRICK_EDGE*k,BRICK_EDGE*u,BRICK_EDGE,BRICK_EDGE,true);
			}
			else {
				bricks[i] = new Brick(BRICK_EDGE*k,BRICK_EDGE*u,BRICK_EDGE,BRICK_EDGE,false);
			}
			k++;
		}
	}
	public void paint(Graphics g) {
		image = createImage(getWidth(),getHeight());
		graphics = image.getGraphics();
		draw(graphics);
		g.drawImage(image,0,0,this);
	}
	public void newPaddle() {
		paddle = new Paddle(GAME_WIDTH/2-PADDLE_WIDTH/2,GAME_HEIGHT-PADDLE_HEIGHT,PADDLE_WIDTH,PADDLE_HEIGHT);
	}
	public void newBall(int x, int y) {
		listOfBalls.add(new Ball(x,y,BALL_DIAMETER,BALL_DIAMETER));
	}
	public void addBonus(int x, int y) {
		Random random = new Random();
		int id = random.nextInt(3);
		listOfBonuses.add(new Bonus(x,y,BONUS_DIAMETER,BONUS_DIAMETER,id,true));
	}
	public void bonusSpawn(Ball ball) {
		if(destroyedBricksCounter%11==0) {
			addBonus(ball.x,ball.y);
		}
	}
	public void draw(Graphics g) {
		if(gameEvaluation()!=0 && isWon()!=0) {
			paddle.draw(g);
			for(int i = 0; i<listOfBalls.size();i++) {
				listOfBalls.get(i).draw(g);
			}
			for(int i = 0; i<bricks.length; i++) {
				bricks[i].draw(g);
			}
			for(int i = 0;i<listOfBonuses.size();i++) {
				if(listOfBonuses.get(i).visible==true) {
					listOfBonuses.get(i).draw(g);
				}
			}
		}
		else if(gameEvaluation()==0){
			gameOver(g);
		}
		else if(isWon()==0) {
			startGame = false;
			youWon(g);
		}
			
	}
	public void move() {
		if(startGame==true) {
			paddle.move();
			for(int i = 0; i<listOfBalls.size();i++) {
				listOfBalls.get(i).move();
			}
			for(int i = 0; i<listOfBonuses.size();i++) {
				if(listOfBonuses.get(i).visible==true) {
					listOfBonuses.get(i).move();
				}
			}
		}
	}
	public void checkCollision() {
		//paddle cannot go out of the screen
		if(paddle.x>=GAME_WIDTH-PADDLE_WIDTH) {
			paddle.x = GAME_WIDTH-PADDLE_WIDTH;
		}
		if(paddle.x<=0) {
			paddle.x = 0;
		}
		//ball cannot go out off the screen
		for(int i = 0; i<listOfBalls.size();i++) {
			if(listOfBalls.get(i).x>=GAME_WIDTH-BALL_DIAMETER) {
				listOfBalls.get(i).setXDirection(-listOfBalls.get(i).xVelocity);
			}
			if(listOfBalls.get(i).x<=0) {
				listOfBalls.get(i).setXDirection(-listOfBalls.get(i).xVelocity);
			}
			if(listOfBalls.get(i).y<=0) {
				listOfBalls.get(i).setYDirection(-listOfBalls.get(i).yVelocity);
			}
			//listOfBalls.get(i) has to glance off the paddle
			if(listOfBalls.get(i).intersects(paddle)) {
				if(listOfBalls.get(i).yVelocity>0) {
					listOfBalls.get(i).yVelocity++;
				}
				else {
					listOfBalls.get(i).yVelocity--;
				}
				listOfBalls.get(i).setYDirection(-listOfBalls.get(i).yVelocity);
			}
			//check if listOfBalls.get(i) touches the bricks
			for(int j = 0;j<bricks.length;j++) {
				if(listOfBalls.get(i).intersects(bricks[j])&&bricks[j].destroyed==false) {
					if(listOfBalls.get(i).centerX()>bricks[i].x && listOfBalls.get(i).centerX()<bricks[j].x+BRICK_EDGE) {
						listOfBalls.get(i).setYDirection(-listOfBalls.get(i).yVelocity);
						bricks[j].destroyed = true;
						brickCrushingEffect = new PlayMusic();
						brickCrushingEffect.playMusic(MUSIC_BRICK);
						destroyedBricksCounter++;	
						bonusSpawn(listOfBalls.get(i));
					}
					if(listOfBalls.get(i).centerY()>bricks[j].y && listOfBalls.get(i).centerY()<bricks[j].y+BRICK_EDGE) {
						listOfBalls.get(i).setXDirection(-listOfBalls.get(i).xVelocity);
						bricks[j].destroyed = true;
						brickCrushingEffect = new PlayMusic();
						brickCrushingEffect.playMusic(MUSIC_BRICK);
						destroyedBricksCounter++;
						bonusSpawn(listOfBalls.get(i));
					}
				}
			}
		}
		//picking up bonuses
		for(int i = 0; i<listOfBonuses.size();i++) {
			if(paddle.intersects(listOfBonuses.get(i))&& listOfBonuses.get(i).visible==true) {
				switch(listOfBonuses.get(i).id) {
				case 0:
					paddle.width = paddle.width+20;
					listOfBonuses.get(i).visible = false;
					break;
				case 1:
					Random random = new Random();
					int yCoordinate = random.nextInt(GAME_WIDTH/2);
					listOfBalls.add(new Ball(listOfBalls.get(0).x,yCoordinate,BALL_DIAMETER,BALL_DIAMETER));
					listOfBonuses.get(i).visible = false;
					break;
				}
			}
		}	
	}
	public int gameEvaluation() {
		int counter = 0;
		for(int i = 0;i<listOfBalls.size();i++) {
			if(listOfBalls.get(i).y<GAME_HEIGHT) {
				counter++;
			}
		}
		return counter;
	}
	public int isWon() {
		int won = 0;
		for(int i = 0; i<bricks.length;i++) {
			if(bricks[i].destroyed==false) {
				won++;
			}
		}
		return won;
	}
	public void gameOver(Graphics g) {
		g.setColor(Color.RED);
		g.setFont(new Font("Ink Free", Font.BOLD,75));
		FontMetrics metrics1 = getFontMetrics(g.getFont());
		g.drawString("GameOver", (GAME_WIDTH-metrics1.stringWidth("GameOver"))/2, GAME_HEIGHT/2);
	}
	public void youWon(Graphics g) {
		g.setColor(Color.GREEN);
		g.setFont(new Font("Ink Free", Font.BOLD,75));
		FontMetrics metrics1 = getFontMetrics(g.getFont());
		g.drawString("You won", (GAME_WIDTH-metrics1.stringWidth("You won"))/2, GAME_HEIGHT/2);
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		//game loop
				long lastTime = System.nanoTime();
				double amountOfTicks = 60.0;
				double ns = 1000000000/amountOfTicks;
				double delta = 0;
				while(true) {
					long now = System.nanoTime();
					delta += (now - lastTime)/ns;
					lastTime = now; 
					if(delta >= 1) {
						move();
						checkCollision();
						repaint();
						delta--;
					}
				}
	}
	public class AL extends KeyAdapter{
		public void keyPressed(KeyEvent e) {
			paddle.keyPressed(e);
			if(e.getKeyCode()==KeyEvent.VK_SPACE) {
				if(startGame == false) {
					startGame = true;
				}
				else {
					startGame = false;
				}
			}
			
		}
		public void keyReleased(KeyEvent e) {
			paddle.keyReleased(e);
		}
	}

}
