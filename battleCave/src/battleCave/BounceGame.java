package battleCave;

import java.util.ArrayList;

import jig.Entity;
import jig.ResourceManager;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import javax.swing.*;

/**
 * A Simple Game of Bounce.
 * 
 * The game has three states: StartUp, Playing, and GameOver, the game
 * progresses through these states based on the user's input and the events that
 * occur. Each state is modestly different in terms of what is displayed and
 * what input is accepted.
 * 
 * In the playing state, our game displays a moving rectangular "ball" that
 * bounces off the sides of the game container. The ball can be controlled by
 * input from the user.
 * 
 * When the ball bounces, it appears broken for a short time afterwards and an
 * explosion animation is played at the impact site to add a bit of eye-candy
 * additionally, we play a short explosion sound effect when the game is
 * actively being played.
 * 
 * Our game also tracks the number of bounces and syncs the game update loop
 * with the monitor's refresh rate.
 * 
 * Graphics resources courtesy of qubodup:
 * http://opengameart.org/content/bomb-explosion-animation
 * 
 * Sound resources courtesy of DJ Chronos:
 * http://www.freesound.org/people/DJ%20Chronos/sounds/123236/
 * 
 * 
 * @author wallaces
 * 
 */
public class BounceGame extends StateBasedGame {
	
	public static final int STARTUPSTATE = 0;
	public static final int PLAYINGSTATE = 1;
	public static final int GAMEOVERSTATE = 2;
	public static final int BATTLESTATE = 3;
  public static final String MUSIC_RSC = "battleCave/resource/gameSong.wav";

	public static final String BALL_BALLIMG_RSC = "battleCave/resource/ball.png";
	public static final String BALL_BROKENIMG_RSC = "battleCave/resource/brokenball.png";
	public static final String GAMEOVER_BANNER_RSC = "battleCave/resource/GameOver.png";
	public static final String STARTUP_BANNER_RSC = "battleCave/resource/PressSpace.png";
	public static final String BANG_EXPLOSIONIMG_RSC = "battleCave/resource/explosion.png";
	public static final String BANG_EXPLOSIONSND_RSC = "battleCave/resource/explosion.wav";
	public static final String GROUND_RSC = "battleCave/resource/ground.png";
	public static final String BASIC_BLOCK_RSC = "battleCave/resource/simpleBlock.png";
	public static final String STATIC_BLOCK_RSC = "battleCave/resource/staticBlock.png";
	public static final String EMPTYHOVER_BLOCK_RSC = "battleCave/resource/Empty.png";
  public static final String EMPTY_BLOCK_RSC = "battleCave/resource/EmptyHover.png";
  public static final String EMPTY_MENU_RSC = "battleCave/resource/ItemMenu.png";
  public static final String LIVING_THING_RSC = "battleCave/resource/survive.png";
  public static final String BASIC_PROJECTILE_RSC = "battleCave/resource/basicProjectile.png";
  public static final String BASIC_MONSTER_RSC = "battleCave/resource/Monster.png";
  public static final String SPAWN_POINT_RSC = "battleCave/resource/SpawnPoint.png";
  public static final String BATTLE_BUTTON_RSC = "battleCave/resource/BattleButton.png";
  public static final String WINDOW_BLOCK_RSC = "battleCave/resource/Window.png";
  public static final String HOT_BLOCK_RSC = "battleCave/resource/HotBlock.png";
  public static final String HARD_BLOCK_RSC = "battleCave/resource/HardBlock.png";
  public static final String HARDEST_BLOCK_RSC = "battleCave/resource/HardestBlock.png";
  public static final String HELL_BAT_RSC = "battleCave/resource/HellBat.png";
  public static final String HEALTH_BUTTON_RSC = "battleCave/resource/HealthButton.png";
  public static final String DAMAGE_BUTTON_RSC = "battleCave/resource/DamageButton.png";
	public final int ScreenWidth;
	public final int ScreenHeight;

	Ball ball;
	public Ground ground;
	public Block block;
	public Grid grid;
	public ItemBar items;
	public WeightManager weightMgr;
	public Player creature;
	public MonsterManager mmgr;
	public CaveButton battlebtn;
  public healthButton hb;
  public DamageButton db;
	ArrayList<Bang> explosions;

	/**
	 * Create the BounceGame frame, saving the width and height for later use.
	 * 
	 * @param title
	 *            the window's title
	 * @param width
	 *            the window's width
	 * @param height
	 *            the window's height
	 */
	public BounceGame(String title, int width, int height) {
		super(title);
		ScreenHeight = height;
		ScreenWidth = width;

		Entity.setCoarseGrainedCollisionBoundary(Entity.CIRCLE);
		//Entity.setDebug(true);
		explosions = new ArrayList<Bang>(10);
				
	}


	@Override
	public void initStatesList(GameContainer container) throws SlickException {
		addState(new StartUpState());
		addState(new GameOverState());
		addState(new PlayingState());
	  addState(new BattleState());
		// the sound resource takes a particularly long time to load,
		// we preload it here to (1) reduce latency when we first play it
		// and (2) because loading it will load the audio libraries and
		// unless that is done now, we can't *disable* sound as we
		// attempt to do in the startUp() method.
		ResourceManager.loadSound(BANG_EXPLOSIONSND_RSC);	

		// preload all the resources to avoid warnings & minimize latency...
		ResourceManager.loadImage(BALL_BALLIMG_RSC);
    ResourceManager.loadImage(GROUND_RSC);
		ResourceManager.loadImage(BALL_BROKENIMG_RSC);
		ResourceManager.loadImage(GAMEOVER_BANNER_RSC);
		ResourceManager.loadImage(STARTUP_BANNER_RSC);
		ResourceManager.loadImage(BANG_EXPLOSIONIMG_RSC);
		ResourceManager.loadImage(BASIC_BLOCK_RSC);
		ResourceManager.loadImage(STATIC_BLOCK_RSC);
		ResourceManager.loadImage(EMPTY_BLOCK_RSC);
		ResourceManager.loadImage(EMPTYHOVER_BLOCK_RSC);
		ResourceManager.loadImage(EMPTY_MENU_RSC);
		ResourceManager.loadImage(LIVING_THING_RSC);
		ResourceManager.loadImage(BASIC_PROJECTILE_RSC);
		ResourceManager.loadImage(BASIC_MONSTER_RSC);
		ResourceManager.loadImage(SPAWN_POINT_RSC);
		ResourceManager.loadImage(BATTLE_BUTTON_RSC);
		ResourceManager.loadImage(WINDOW_BLOCK_RSC);
		ResourceManager.loadImage(HOT_BLOCK_RSC);
		ResourceManager.loadImage(HARD_BLOCK_RSC);
		ResourceManager.loadImage(HARDEST_BLOCK_RSC);
		ResourceManager.loadImage(HELL_BAT_RSC);
    ResourceManager.loadImage(HEALTH_BUTTON_RSC);
    ResourceManager.loadImage(DAMAGE_BUTTON_RSC);

    ResourceManager.loadMusic(MUSIC_RSC);
    grid = new Grid(this,40);
    grid.setSelected(GameObject.EMPTY_BLOCK_ID);
		ground = new Ground(ScreenWidth/2.0f,ScreenHeight-16);
		ball = new Ball(ScreenWidth / 2, ScreenHeight / 2, .1f, .2f);
    block = new Block(ScreenWidth/3, ScreenHeight/3);
    items = new ItemBar(this);
    weightMgr = new WeightManager(grid,null);
    mmgr = new MonsterManager(grid,weightMgr,ground);
    creature = new Player(ScreenWidth/2.0f,ScreenHeight/2.0f,grid,ground,mmgr);
    mmgr.setTarget(creature);
    weightMgr.setTarget(creature);
    battlebtn = new CaveButton(ScreenWidth-40,ScreenHeight-20);
    hb = new healthButton(ScreenWidth-100,ScreenHeight-20,creature);
    db = new DamageButton(ScreenWidth-135,ScreenHeight-20);
	}

	public static void main(String[] args) {
		AppGameContainer app;
		try {
			app = new AppGameContainer(new BounceGame("Bounce!", 1600, 800));
			app.setDisplayMode(1600, 800, false);
			app.setVSync(true);
			app.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}

	}


}
