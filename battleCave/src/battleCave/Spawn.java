package battleCave;

import jig.ResourceManager;
import jig.Vector;
import org.newdawn.slick.Graphics;


public class Spawn extends Block {

  public Spawn(Vector pos, float health, int gx, int gy) {
    this(pos.getX(),pos.getY(),health,gx,gy);
  }
  public Spawn(float x, float y, float health, int gx, int gy) {
    super(x,y,health,gx,gy);
    removeImage(ResourceManager.getImage(BounceGame.BASIC_BLOCK_RSC));
    addImage(ResourceManager.getImage(BounceGame.SPAWN_POINT_RSC));
    cost = 0;
    setActive(true);
  }

public void collision(GameObject obj){
    super.collision(obj);
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
