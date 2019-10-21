package battleCave;

import jig.ResourceManager;
import jig.Vector;

public class HardestBlock extends Block{
  public HardestBlock(Vector pos, int health, int gx, int gy){
    this(pos.getX(),pos.getY(),health,gx,gy);
  }
  public HardestBlock(float x, float y, int health, int gx, int gy){
    super(x,y,health,gx,gy);
    removeImage(ResourceManager.getImage(BounceGame.BASIC_BLOCK_RSC));
    addImage(ResourceManager.getImage(BounceGame.HARDEST_BLOCK_RSC));
    cost = 40;
    super.health = 400;
    heal();
  }

  @Override
  public int get_id() {
    return GameObject.HARDESTBLOCK_ID;
  }
}
