package battleCave;

import jig.ResourceManager;
import jig.Vector;
import org.newdawn.slick.Graphics;

public class Projectile extends GameObject{
  public Projectile(float x, float y, Vector dir, float speed) {
    super(x, y);
    dir = dir.unit();
    PhysVector v = new PhysVector(dir.getX()*speed,dir.getY()*speed);
    physics.setVelocity(v);
    addImageWithBoundingBox(ResourceManager.getImage(BounceGame.BASIC_PROJECTILE_RSC));
    rotate((Math.toDegrees(Math.atan2(dir.getY(), dir.getX())))+180 );

  }

  public void update(int delta){
    super.update(delta);
  }

  public void render(Graphics g){
    super.render(g);
  }
}
