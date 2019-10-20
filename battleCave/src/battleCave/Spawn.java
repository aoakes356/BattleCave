package battleCave;

import jig.ResourceManager;
import jig.Vector;

public class Spawn extends EmptyBlock {

  public Spawn(Vector pos) {
    super(pos);
    removeImage(ResourceManager.getImage(currentImage));
    addImage(ResourceManager.getImage(BounceGame.SPAWN_POINT_RSC));
  }


  @Override
  public int get_id() {
    return GameObject.SPAWN_BLOCK_ID;
  }
}
