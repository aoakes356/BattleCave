package battleCave;

import java.nio.charset.IllegalCharsetNameException;
import java.util.Iterator;

import jig.Vector;

import org.newdawn.slick.*;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;


/**
 * This state is active when the Game is being played. In this state, sound is
 * turned on, the bounce counter begins at 0 and increases until 10 at which
 * point a transition to the Game Over state is initiated. The user can also
 * control the ball using the WAS & D keys.
 * 
 * Transitions From StartUpState
 * 
 * Transitions To GameOverState
 */
class PlayingState extends BasicGameState {
	int bounces;
	public Grid g;
	public BounceGame bounceGame;
	private boolean pressed;
	private int button;
	private boolean itemPressed;
	private boolean clicked;
	private int clickedX, clickedY, clickedButton;
	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
	}

	@Override
	public void enter(GameContainer container, StateBasedGame game) {
		bounces = 0;
		container.setSoundOn(true);
		g = ((BounceGame)game).grid;
		itemPressed = false;
		clicked = false;
		bounceGame = (BounceGame)game;
    bounceGame.grid.setMode(Grid.BUILD_MODE);
    bounceGame.mmgr.setAutoSpawn(false);
    bounceGame.mmgr.killAll();
    bounceGame.creature.setHealth(bounceGame.creature.getMaxHealth());

	}
	@Override
	public void render(GameContainer container, StateBasedGame game,
			Graphics g) throws SlickException {
		BounceGame bg = (BounceGame)game;
		//bg.block.render(g);
    bg.grid.render(g);
		bg.ground.render(g);
		bg.items.render(g);
		bg.creature.render(g);
		bg.mmgr.render(g);
		g.drawString("Money: " + bg.grid.money, 10, 30);
	}


	@Override
  public void mouseClicked(int button, int x, int y, int count){
	  clicked = true;
	  clickedX = x;
	  clickedY = y;
	  clickedButton = button;
	  System.out.println("Mouse clicked: "+button);
  }

  @Override
  public void mousePressed(int button, int x, int y){
	  super.mousePressed(button,x,y);
	  System.out.println("Mouse pressed: "+button);
	  pressed = true;
	  this.button = button;

  }
  @Override
  public void mouseReleased(int button, int x, int y){
    super.mousePressed(button,x,y);
    pressed = false;
  }
  @Override
  public void keyPressed(int key, char c){
	  if(key == Input.KEY_I){
	    itemPressed = !itemPressed;
    }else if(key == Input.KEY_ENTER){
      bounceGame.enterState(BounceGame.BATTLESTATE);
    }
    bounceGame.creature.keyHandler(key,true);
  }

  public void keyReleased(int key, char c){
    bounceGame.creature.keyHandler(key,false);
  }


  @Override
	public void update(GameContainer container, StateBasedGame game,
			int delta) throws SlickException {
		Input input = container.getInput();
		BounceGame bg = (BounceGame)game;
		//bg.block.collision(bg.ground);
    if(pressed && !bg.items.isActive()){
      bg.grid.clickHandler(new Vector(container.getInput().getMouseX(),container.getInput().getMouseY()),button, bg.grid.getSelected());
    }else{
      bg.grid.hover(container.getInput().getMouseX(),container.getInput().getMouseY());
    }
    if(itemPressed){
      bg.items.setActive(true);
    }else{
      bg.items.setActive(false);
    }
    if(clicked){
     //bg.creature.clickHandler(button,input.getMouseX(),input.getMouseY());
    }
    if(bg.items.isActive() && clicked){
      bg.items.clickHandler(clickedButton,clickedX,clickedY);
      clicked = false;
    }else if(clicked){
      bg.grid.clickHandler(new Vector(clickedX,clickedY),button,bg.grid.getSelected());
      clicked = false;
    }
    Vector temp1 = bg.creature.getGridPos();
    Vector temp2;
    bg.grid.setSelected(bg.items.selected);
		bg.grid.collision(bg.ground);
		bg.grid.update(delta);
		bg.creature.update(delta);
    bg.creature.gridCollision(bg.grid,bg.ground);
    if(bg.creature.getPosition().getX() > bg.ScreenWidth-10){
      bg.creature.setX(bg.ScreenWidth-10);
    }if(bg.creature.getPosition().getY() > bg.ScreenHeight-10){
      bg.creature.setY(bg.ScreenHeight-10);
    }
    temp2 = bg.creature.getGridPos();
    if(temp1.getX() != temp2.getX() || temp1.getY() != temp2.getY() || bg.grid.changed){
      bg.weightMgr.generatePath();
      bg.grid.setChanged(false);
    }
		bg.grid.collisionCheck();
		bg.items.update(delta);
		bg.mmgr.update(delta);

		//bg.block.update(delta);


	}

	@Override
	public int getID() {
		return BounceGame.PLAYINGSTATE;
	}
	
}