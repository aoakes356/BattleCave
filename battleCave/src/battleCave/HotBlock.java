package battleCave;

import jig.ResourceManager;
import jig.Vector;

public class HotBlock extends Block{
  public HotBlock(Vector pos, int health, int gx, int gy){
    this(pos.getX(),pos.getY(),health,gx,gy);
  }
  public HotBlock(float x, float y, int health, int gx, int gy){
    super(x,y,health,gx,gy);
    removeImage(ResourceManager.getImage(BounceGame.BASIC_BLOCK_RSC));
    addImage(ResourceManager.getImage(BounceGame.HOT_BLOCK_RSC));
    cost = 20;
  }

  @Override
  public int get_id() {
    return GameObject.HOTBLOCK_ID;
  }
}
