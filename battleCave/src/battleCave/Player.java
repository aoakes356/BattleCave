package battleCave;

import jig.Vector;
import org.newdawn.slick.Graphics;

import java.util.ArrayList;

public class Player extends Living {

  ArrayList<Projectile> projeciles;

  public Player(float x, float y) {
    super(x, y);
    projeciles = new ArrayList<>();
  }

  public void attack(Vector dir){
    Projectile p = new Projectile(getX(),getY(),dir,1);
    projeciles.add(p);
  }

  public void keyHandler(int key, boolean pressed){
    super.keyHandler(key,pressed);
  }

  public void clickHandler(int button, int x, int y){
    if(button == 0){
      attack(new Vector(x,y));
    }
  }

  public void update(int delta){
    super.update(delta);
    for(Projectile p: projeciles){
      p.update(delta);
    }
  }

  public void render(Graphics g){
    super.render(g);
    for(Projectile p : projeciles){
      p.render(g);
    }
  }

}
