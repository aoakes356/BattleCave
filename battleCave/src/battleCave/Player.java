package battleCave;

import jig.Vector;
import org.newdawn.slick.Graphics;

import java.util.ArrayList;

public class Player extends Living {

  ArrayList<Projectile> projectiles;

  public Player(float x, float y) {
    super(x, y);
    projectiles = new ArrayList<>();
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
    for(Projectile p: projectiles){
      p.update(delta);
    }
  }

  public void render(Graphics g){
    super.render(g);
    for(Projectile p : projectiles){
      p.render(g);
    }
  }

}
