package battleCave;

import jig.Collision;
import jig.ResourceManager;
import jig.Vector;
import org.newdawn.slick.Graphics;

public class Projectile extends GameObject{
  public boolean active;
  public GameObject hit;
  public Vector gridPosition;
  public int damage;
  public Projectile(float x, float y, Vector dir, float speed) {
    super(x, y);
    gridPosition = new Vector(Grid.mapCoord(x,y,40));
    dir = dir.unit();
    PhysVector v = new PhysVector(dir.getX()*speed,dir.getY()*speed);
    physics.setVelocity(v);
    addImageWithBoundingBox(ResourceManager.getImage(BounceGame.BASIC_PROJECTILE_RSC));
    rotate((Math.toDegrees(Math.atan2(dir.getY(), dir.getX())))+180 );
    active = true;
    damage = 35;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public boolean gridCollision(Grid g, Ground ground, MonsterManager monsters){
    int xMin,xMax,yMin,yMax;
    int gx = (int)gridPosition.getX();
    int gy = (int)gridPosition.getY();
    if(getX() < 0){
      setX(0);
      gx = 0;
    }else if(gx > g.width-1){
      setX(Grid.coordMapX(g.width-1,g.blockSize)+g.blockSize);
      gx = g.width-1;
    }
    if(getY() < 0){
      setY(0);
      gy = 0;
    }else if(gy > g.height-1){
      setY(Grid.coordMapY(g.height-1,g.blockSize));
      gy = g.height-1;
    }

    if(gx > 0){
      xMin = gx-1;
    }else{
      xMin = 0;
    }
    if(gx < g.width-1){
      xMax = gx+1;
    }else{
      xMax = g.width-1;
    }
    if(gy > 0){
      yMin = gy-1;
    }else{
      yMin = 0;
    }
    if(gy < g.height-1){
      yMax = gy+1;
    }else{
      yMax = g.height-1;
    }
    boolean c1 = collisionTest(g.blocks.get(xMin).get(yMin));
    boolean c2 =collisionTest(g.blocks.get(xMin).get(yMax));
    boolean c3 =collisionTest(g.blocks.get(xMax).get(yMin));
    boolean c4 =collisionTest(g.blocks.get(xMin).get(gy));
    boolean c5 =collisionTest(g.blocks.get(xMax).get(gy));
    boolean c6 =collisionTest(g.blocks.get(gx).get(yMin));
    boolean c7 =collisionTest(g.blocks.get(gx).get(yMax));
    boolean c8 =collisionTest(g.blocks.get(gx).get(gy));
    boolean c9 = collisionTest(ground);
    boolean c10 = monsters.collisionTest(this);
    if(c1||c2||c3||c4||c5||c6||c7||c8||c9||c10){
      active = false;
      return true;
    }
    return false;
  }

  public boolean collisionTest(GameObject g_obj) {
    if (g_obj.get_id() == GameObject.EMPTY_BLOCK_ID || g_obj.get_id() == GameObject.SPAWN_BLOCK_ID||g_obj.get_id()==GameObject.WINDOW_ID){
      return false;
    }
    Collision c = collides(g_obj);
    if(c != null) {
      active = false;
      hit = g_obj;
      return true;
    }
    return false;
  }

  public void update(int delta){
    if(active) {
      super.update(delta);
      gridPosition = new Vector(Grid.mapCoord(getX(),getY(),40));
    }
  }

  public void render(Graphics g){
    if(active) {
      super.render(g);
    }
  }

  public int get_id(){
    return GameObject.PROJECTILE_ID;
  }

}
