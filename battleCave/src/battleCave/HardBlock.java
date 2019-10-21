package battleCave;

import jig.ResourceManager;
import jig.Vector;

public class HardBlock extends Block {
  public HardBlock(Vector pos, int health, int gx, int gy){
    this(pos.getX(),pos.getY(),health,gx,gy);
  }
  public HardBlock(float x, float y, int health, int gx, int gy){
    super(x,y,health,gx,gy);
    removeImage(ResourceManager.getImage(BounceGame.BASIC_BLOCK_RSC));
    addImage(ResourceManager.getImage(BounceGame.HARD_BLOCK_RSC));
    cost = 20;
    super.health = 200;
    heal();
  }

  @Override
  public int get_id() {
    return GameObject.HARDBLOCK_ID;
  }
}
