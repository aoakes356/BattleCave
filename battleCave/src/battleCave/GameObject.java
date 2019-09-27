package battleCave;

import jig.Collision;
import jig.Entity;
import jig.ResourceManager;
import jig.Vector;
import org.newdawn.slick.Graphics;

public class GameObject extends Entity {
  public static final int GAMEOBJ_ID = 0;

  public GameObject(final float x, final float y){
    super(x,y);
  }

  @Override
  public void render(Graphics g){
    super.render(g);
  }

  public void update(int delta){

  }

  public Vector collision(GameObject g_obj){
    Collision c = collides(g_obj);
    if(c != null) {
      return c.getMinPenetration();
    }else{
      return new Vector(0,0);
    }
  }

  public int get_id(){
    return GAMEOBJ_ID;
  }

}
