package battleCave;

import jig.Collision;
import jig.ResourceManager;
import jig.Vector;
import org.newdawn.slick.Graphics;

//Block size is 40x40
public class Block extends GameObject {
  public float health;
  public float currentHealth;
  public int gridX;
  public int gridY;
  public boolean gravity;
  public boolean drag;
  public boolean isStatic;
  private boolean active;
  public Block above, below, left, right;
  public Block(float x, float y) {
    this(x,y,100.0f);
  }

  public Block(float x, float y, float health){
    super(x,y);
    addImageWithBoundingBox(ResourceManager
        .getImage(BounceGame.BASIC_BLOCK_RSC));
    this.health = health;
    this.currentHealth = health;
    gravity = true;
    drag = true;
    isStatic = false;
    active = false;
  }
  public Block(float x, float y, float health, int gx, int gy){
    super(x,y);
    addImageWithBoundingBox(ResourceManager
        .getImage(BounceGame.BASIC_BLOCK_RSC));
    this.health = health;
    this.currentHealth = health;
    gridX = gx;
    gridY = gy;
    gravity = true;
    drag = true;
    isStatic = false;
    active = false;
  }

  public Block(int x, int y, float health){
    this(Grid.coordMapX(x),Grid.coordMapY(y),health,x,y);

  }

  @Override
  public void render(Graphics g) {
    if(active) {
      super.render(g);
    }
  }

  @Override
  public void update(int delta){
    if(active) {
      super.update(delta);
      Vector pos = Grid.mapCoord(this.getX(), this.getY());
      this.gridX = (int) pos.getX();
      this.gridY = (int) pos.getY();
      if (isStatic) {
        // Keep in its grid position.
        setPosition(Grid.coordMap(gridX, gridY));
      } else {
        if (health <= 0) {
          active = false;
          return;
        }
        if (gravity) {
          super.physics.addAcceleration(0, .000981f);
        }
        if (drag) {
          super.physics.addForce(super.physics.velocity.cloneVec().scale(-.0105f * super.physics.velocity.length()));
        }
      }
    }
  }

  public void hit(float dmg){
    currentHealth -= dmg;
  }

  public void heal(){
    currentHealth = health;
  }

  @Override
  public void collision(GameObject obj){
    Collision c = collides(obj);
    if(obj != this) {
      if (c != null) {
        if (obj.get_id() == GameObject.GROUND_ID || obj.get_id() == GameObject.BLOCK_ID) {
          gravity = false;
          drag = false;
          isStatic = true;
          Vector pos = Grid.mapCoord(this.getX(), this.getY());
          Vector gp = Grid.coordMap((int) pos.getX(), (int) pos.getY());
          this.gridX = (int) pos.getX();
          this.gridY = (int) pos.getY();
          setPosition(gp.getX(), gp.getY());
          physics.velocity.scale(0);
        }
        while (c != null) {
          System.out.println(obj.get_id());

          translate(c.getMinPenetration().scale(.5f));
          c = collides(obj);
        }
      }
    }
  }

  public void setActive(boolean active){
    this.active = active;
    this.isStatic = false;
  }

  public boolean getActive(){
    return active;
  }

  @Override
  public int get_id(){
    return GameObject.BLOCK_ID;
  }
}
