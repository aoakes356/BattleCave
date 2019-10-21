package battleCave;

import jig.Vector;
import org.newdawn.slick.Graphics;

import java.util.ArrayList;
import java.util.Iterator;

public class Player extends Living {

  private ArrayList<Projectile> projectiles;
  private Ground ground;
  private MonsterManager manager;
  private HealthBar health;
  public Player(float x, float y, Grid g, Ground grnd, MonsterManager m) {
    super(x, y, g);
    ground = grnd;
    manager = m;
    projectiles = new ArrayList<>();
    setMaxHealth(150);
    setHealth(150);
    health = new HealthBar(-getHealth()/2.0f,-35f,this);
  }

  public void attack(Vector dir){
    Projectile p = new Projectile(getX(),getY(),dir,1);
    projectiles.add(p);
  }

  public void keyHandler(int key, boolean pressed){
    super.keyHandler(key,pressed);
  }

  public void clickHandler(int button, int x, int y){
    if(button == 0){
      attack(new Vector(x,y).subtract(getPosition()));
    }
  }

  public void update(int delta){
    super.update(delta);
    Projectile p;
    for(Iterator<Projectile> it = projectiles.iterator(); it.hasNext();){
      p = it.next();
      p.update(delta);
      p.gridCollision(grid,ground,manager);
      if(p.gridPosition.getX() > grid.width-1 || p.gridPosition.getX() <= 0 || p.gridPosition.getY() > grid.height-1 || p.gridPosition.getY() <= 0){
        p.setActive(false);
      }
      if(!p.active){
        it.remove();
      }
    }
    health.update(delta);
  }

  public void render(Graphics g){
    super.render(g);
    health.render(g);
    for(Projectile p : projectiles){
      p.render(g);
    }
  }


  public int get_id(){
    return GameObject.PLAYER_ID;
  }



}
