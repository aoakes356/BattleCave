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
		g.drawString("Bounces: " + bounces, 10, 30);
		for (Bang b : bg.explosions)
			b.render(g);
	}

  @Override
  public void mouseClicked(int button, int x, int y, int clickCount) {
    super.mouseClicked(button, x, y, clickCount);
    System.out.println(""+x+y+button);
    if(g != null){
      g.clickHandler(new Vector(x,y), button);
    }
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