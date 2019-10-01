package battleCave;

import jig.ResourceManager;
import jig.Vector;
import org.newdawn.slick.Graphics;

public class Ground extends GameObject {

  public Ground(float x, float y) {
    super(x, y);
    addImageWithBoundingBox(ResourceManager
        .getImage(BounceGame.GROUND_RSC));

  }

  @Override
  public void render(Graphics g) {
    super.render(g);
  }

  @Override
  public void update(int delta){
    super.update(delta);
  }

  public void collision(GameObject g){
    super.collision(g);
  }

}
