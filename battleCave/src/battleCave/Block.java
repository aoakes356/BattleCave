package battleCave;

import jig.Collision;
import jig.ResourceManager;
import jig.Vector;
import org.newdawn.slick.Graphics;

import java.util.ArrayList;

//Block size is 40x40
public class Block extends GameObject {
  public float health;
  public float currentHealth;
  public int gridX;
  public int gridY;
  public boolean drag;
  public boolean isStatic;
  private boolean active;
  public boolean grounded;
  public boolean rooted;
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
    drag = true;
    isStatic = false;
    active = false;
    grounded = false;
    rooted = false;
  }
  public Block(float x, float y, float health, int gx, int gy){
    super(x,y);
    addImageWithBoundingBox(ResourceManager
        .getImage(BounceGame.BASIC_BLOCK_RSC));
    this.health = health;
    this.currentHealth = health;
    gridX = gx;
    gridY = gy;
    drag = true;
    isStatic = false;
    active = false;
    grounded =false;
    rooted = false;
  }

  public Block(int x, int y, float health){
    this(Grid.coordMapX(x),Grid.coordMapY(y),health,x,y);

  }

  public void matchNeighbors(){
    PhysVector vel;
    if(left != null){
      vel = left.physics.velocity;
      left.physics.velocity = vel.cloneVec();
    }if(right != null){
      vel = right.physics.velocity;
      right.physics.velocity = vel.cloneVec();
    }if(above != null){
      vel = above.physics.velocity;
      above.physics.velocity = vel.cloneVec();
    }if(below != null){
      vel = below.physics.velocity;
      below.physics.velocity = vel.cloneVec();
    }
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
      if(staticNeighbors()){
        isStatic = true;
        grounded = true;
      }
      if (isStatic) {
        // Keep in its grid position.
        setPosition(Grid.coordMap(gridX, gridY));
        super.physics.velocity.scale(0);
        super.physics.acceleration.scale(0);
        super.physics.force.scale(0);
      } else {
        matchNeighbors();
        if (health <= 0) {
          active = false;
          return;
        }
        if (!grounded) {
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

  public boolean staticNeighbors(){
    boolean l,r,u,d;
    if(left != null && left.grounded){
      l = true;
    }else{
      l = false;
    }if(right != null && right.grounded){
      r = true;
    }else{
      r = false;
    }if(above != null && above.grounded){
      u = true;
    }else {
      u = false;
    }if(below != null && below.grounded){
      d = true;
    }else{
      d = false;
    }
    return l||r||u||d;
  }

  @Override
  public void collision(GameObject obj){
    Collision c = collides(obj);
    boolean collide = true;
    if(obj != this) {
      if (c != null) {
        if (obj.get_id() == GameObject.GROUND_ID || obj.get_id() == GameObject.BLOCK_ID) {
          if(obj.get_id() == GameObject.GROUND_ID){
            grounded = true;
            rooted = true;
            isStatic = true;
            Grid.isRooted(this,new ArrayList<>());
          }else{
            Block b = (Block)obj;
            grounded = b.grounded;
            isStatic = b.isStatic;

            if(!b.isStatic && !b.grounded){
              collide = false;
            }
          }
          if(collide){
            Vector pos = Grid.mapCoord(this.getX(), this.getY());
            Vector gp = Grid.coordMap((int) pos.getX(), (int) pos.getY());
            this.gridX = (int) pos.getX();
            this.gridY = (int) pos.getY();
            setPosition(gp.getX(), gp.getY());
            physics.velocity.scale(0);
            physics.force.scale(0);
            physics.acceleration.scale(0);
          }

        }
        while (c != null) {

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
