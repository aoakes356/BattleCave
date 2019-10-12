package battleCave;

import jig.Collision;
import jig.Entity;
import jig.ResourceManager;
import jig.Vector;
import org.newdawn.slick.Graphics;

public class GameObject extends Entity {
  public static final int GAMEOBJ_ID = 0;
  public static final int GROUND_ID = 1;
  public static final int BLOCK_ID = 2;
  public static final int EMPTY_BLOCK_ID = 3;
  public  Physics physics;

  public GameObject(final float x, final float y){
    super(x,y);
    physics = new Physics(x,y,1.0f);
  }

  @Override
  public void render(Graphics g){
    super.render(g);
  }

  public void update(int delta){
    physics.update(delta);
    PhysVector v = physics.velocity;
    super.setPosition(getPosition().getX()+v.x*delta,getPosition().getY()+v.y*delta);
  }


  public void collision(GameObject g_obj){
    Collision c = collides(g_obj);
    if(c != null) {
      while(c != null){
        translate(c.getMinPenetration().scale(.5f));
        c = collides(g_obj);
      }
    }
  }

  public int get_id(){
    return GAMEOBJ_ID;
  }

}