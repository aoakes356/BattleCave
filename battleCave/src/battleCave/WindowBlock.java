package battleCave;

import jig.ResourceManager;
import jig.Vector;

public class WindowBlock extends Block{
  public WindowBlock(Vector pos, int health, int gx, int gy){
    this(pos.getX(),pos.getY(),health,gx,gy);
  }
  public WindowBlock(float x, float y, int health, int gx, int gy){
    super(x,y,health,gx,gy);
    removeImage(ResourceManager.getImage(BounceGame.BASIC_BLOCK_RSC));
    addImage(ResourceManager.getImage(BounceGame.WINDOW_BLOCK_RSC));
    cost = 15;
  }

  @Override
  public int get_id() {
    return GameObject.WINDOW_ID;
  }
}
