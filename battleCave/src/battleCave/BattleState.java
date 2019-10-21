package battleCave;

import jig.ResourceManager;
import jig.Vector;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import java.util.ArrayList;

public class BattleState extends BasicGameState {
  int bounces;
	public Grid g;
	public BounceGame bounceGame;
	private boolean pressed;
	private int button;
	private boolean itemPressed;
	private boolean clicked;
	private int clickedX, clickedY, clickedButton;
	private int moneySave;
	private ArrayList<ArrayList<Block>> blockSave;
  @Override
  public int getID() {
    return BounceGame.BATTLESTATE;
  }

  public void enter(GameContainer container, StateBasedGame game) throws SlickException {
    bounces = 0;
    container.setSoundOn(true);
    ResourceManager.getMusic(BounceGame.MUSIC_RSC).play();
    g = ((BounceGame)game).grid;
    itemPressed = false;
    clicked = false;
    g.setMode(Grid.BATTLE_MODE);
    bounceGame = (BounceGame)game;
    bounceGame.mmgr.setAutoSpawn(true);
    blockSave = new ArrayList<>();
    g.brokenValue = 0;
    for(int i = 0; i < g.blocks.size(); i++){
      blockSave.add(new ArrayList<>());
      for(Block b:g.blocks.get(i)){
        blockSave.get(i).add(Grid.cloneBlock(b));
      }
    }
  }

  @Override
  public void init(GameContainer container, StateBasedGame game) throws SlickException {

  }

  @Override
  public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
    BounceGame bg = (BounceGame)game;
    bg.grid.render(g);
    bg.ground.render(g);
    bg.items.render(g);
    bg.creature.render(g);
    bg.mmgr.render(g);
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
    }else if(key == Input.KEY_TAB){
	    bounceGame.mmgr.setShowPathing(!bounceGame.mmgr.showPathing);
    }
    bounceGame.creature.keyHandler(key,true);
  }

  public void keyReleased(int key, char c){
    bounceGame.creature.keyHandler(key,false);
  }
  @Override
  public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
    Input input = container.getInput();
    if(!ResourceManager.getMusic(BounceGame.MUSIC_RSC).playing()){
      ResourceManager.getMusic(BounceGame.MUSIC_RSC).play();
    }
    BounceGame bg = (BounceGame)game;
    if(clicked){
      bg.creature.clickHandler(button,input.getMouseX(),input.getMouseY());
      clicked = false;
    }
    if(pressed){
      bg.creature.clickHandler(button,input.getMouseX(),input.getMouseY());
    }
    Vector temp1 = bg.creature.getGridPos();
    Vector temp2;
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
		if(bg.creature.getHealth() <= 0){
      g.loadSave(blockSave);
		  bg.enterState(BounceGame.PLAYINGSTATE);
    }

  }
}
