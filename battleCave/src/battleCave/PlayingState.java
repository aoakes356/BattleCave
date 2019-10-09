package battleCave;

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
	private boolean pressed;
	private int button;
	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
	}

	@Override
	public void enter(GameContainer container, StateBasedGame game) {
		bounces = 0;
		container.setSoundOn(true);
		g = ((BounceGame)game).grid;
	}
	@Override
	public void render(GameContainer container, StateBasedGame game,
			Graphics g) throws SlickException {
		BounceGame bg = (BounceGame)game;
		//bg.block.render(g);
    bg.grid.render(g);
		bg.ground.render(g);
		if(pressed){
		  bg.grid.clickHandler(new Vector(container.getInput().getMouseX(),container.getInput().getMouseY()),button);
    }
		g.drawString("Bounces: " + bounces, 10, 30);
		for (Bang b : bg.explosions)
			b.render(g);
	}


  @Override
  public void mousePressed(int button, int x, int y){
	  super.mousePressed(button,x,y);
	  pressed = true;
	  this.button = button;

  }
  @Override
  public void mouseReleased(int button, int x, int y){
    super.mousePressed(button,x,y);
    pressed = false;
  }


  @Override
	public void update(GameContainer container, StateBasedGame game,
			int delta) throws SlickException {
		Input input = container.getInput();
		BounceGame bg = (BounceGame)game;
		//bg.block.collision(bg.ground);
		bg.grid.collision(bg.ground);
		bg.grid.update(delta);
		bg.grid.collisionCheck();

		//bg.block.update(delta);


	}

	@Override
	public int getID() {
		return BounceGame.PLAYINGSTATE;
	}
	
}