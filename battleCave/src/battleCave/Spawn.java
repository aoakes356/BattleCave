package battleCave;

import jig.ResourceManager;
import jig.Vector;
import org.newdawn.slick.Graphics;


public class Spawn extends Block {

  public Spawn(Vector pos) {
    super(pos.getX(),pos.getY());
    removeImage(ResourceManager.getImage(BounceGame.BASIC_BLOCK_RSC));
    addImage(ResourceManager.getImage(BounceGame.SPAWN_POINT_RSC));
    setGrounded();
    setActive(true);
    cost = 0;
  }


  public void update(int delta){
    super.update(delta);
  }

  public void render(Graphics g){
    super.render(g);
  }

  @Override
  public int get_id() {
    return GameObject.SPAWN_BLOCK_ID;
  }
}
