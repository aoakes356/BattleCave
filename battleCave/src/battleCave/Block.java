package battleCave;

import jig.Collision;
import jig.ResourceManager;
import jig.Vector;
import org.lwjgl.Sys;
import org.newdawn.slick.Game;
import org.newdawn.slick.Graphics;

import java.util.ArrayList;

//Block size is 40x40
public class Block extends GameObject {
  public float health;
  public boolean superOnly;
  public float currentHealth;
  public int gridX;
  public int gridY;
  public boolean drag;
  private boolean isStatic;
  private boolean active;
  public boolean grounded;
  public boolean rooted;
  public boolean hasCluster;
  public Block above, below, left, right;
  public String currentImage;
  public int cost;
  public Block(){this(0,0,100.0f);}
  public Block(float x, float y) {
    this(x,y,100.0f);
  }

  public Block(float x, float y, float health){
    super(x,y);
    addImageWithBoundingBox(ResourceManager
        .getImage(BounceGame.BASIC_BLOCK_RSC));
    currentImage = BounceGame.BASIC_BLOCK_RSC;
    this.health = health;
    this.currentHealth = health;
    cost = 10;
    drag = true;
    isStatic = false;
    active = false;
    grounded = false;
    rooted = false;
    hasCluster = false;
    superOnly = false;
  }
  public Block(float x, float y, float health, int gx, int gy){
    super(x,y);
    addImageWithBoundingBox(ResourceManager
        .getImage(BounceGame.BASIC_BLOCK_RSC));
    currentImage = BounceGame.BASIC_BLOCK_RSC;
    this.health = health;
    this.currentHealth = health;
    gridX = gx;
    gridY = gy;
    drag = true;
    isStatic = false;
    active = false;
    grounded =false;
    rooted = false;
    hasCluster = false;
    superOnly = false;
    cost = 10;
  }

  public Block(int x, int y, float health){
    this(Grid.coordMapX(x,40),Grid.coordMapY(y,40),health,x,y);

  }



  @Override
  public void render(Graphics g) {
    if(active) {
      super.render(g);
    }
  }

  @Override
  public void update(int delta){
    if(superOnly){
     super.update(delta);
    }else if(active) {
      Vector pos = Grid.mapCoord(this.getX(), this.getY(), 40);
      this.gridX = (int) pos.getX();
      this.gridY = (int) pos.getY();
      if (grounded) {
        // Keep in its grid position.
        if(currentImage != BounceGame.STATIC_BLOCK_RSC) {
          removeImage(ResourceManager.getImage(currentImage));
          addImage(ResourceManager.getImage(BounceGame.STATIC_BLOCK_RSC));
          currentImage = BounceGame.STATIC_BLOCK_RSC;
        }
        setGrid(Grid.mapCoord(getX(),getY(),40));
        setPosition(Grid.coordMap(gridX, gridY, 40));
        super.physics.velocity.scale(0);
        super.physics.acceleration.scale(0);
        super.physics.force.scale(0);
      } else if(isStatic){
        setGrid(Grid.mapCoord(getX(),getY(),40));
        staticPath(this, null);
        super.physics.velocity.scale(0);
        super.physics.acceleration.scale(0);
        super.physics.force.scale(0);
        if(currentImage != BounceGame.BASIC_BLOCK_RSC) {
          removeImage(ResourceManager.getImage(currentImage));
          addImage(ResourceManager.getImage(BounceGame.BASIC_BLOCK_RSC));
          currentImage = BounceGame.BASIC_BLOCK_RSC;
        }
      }else {
        if(currentImage != BounceGame.BASIC_BLOCK_RSC) {
          removeImage(ResourceManager.getImage(currentImage));
          addImage(ResourceManager.getImage(BounceGame.BASIC_BLOCK_RSC));
          currentImage = BounceGame.BASIC_BLOCK_RSC;
        }
        //staticPath(this,null);
        if (health <= 0) {
          active = false;
          return;
        }
        if (!grounded&&!isStatic) {
          super.physics.addAcceleration(0, .000981f);
        }
        if (drag&&!isStatic) {
          super.physics.addForce(super.physics.velocity.cloneVec().scale(-.0105f * super.physics.velocity.length()));
        }
      }
      super.update(delta);
    }
  }

  public void hit(float dmg){
    currentHealth -= dmg;
  }

  public void heal(){
    currentHealth = health;
  }


  public static boolean staticNeighbors(Block b){
    boolean l=false,r=false,u=false,d = false;
    if(b.left != null && b.left.grounded){
      l = true;
    }else if(b.above != null && b.above.grounded){
      u = true;
    }else if(b.right != null && b.right.grounded){
      r = true;
    }else if(b.below != null && b.below.grounded){
      d = true;
    }
    return l||r||u||d;
  }

  public static boolean staticPath(Block b, ArrayList<Block> visited){
    if(visited == null){
      visited = new ArrayList<>();
    }
    if(b == null || b.get_id() == GameObject.EMPTY_BLOCK_ID || visited.contains(b)){
      return false;
    }
    visited.add(b);
    boolean res = staticPath(b.left,visited)||staticPath(b.right,visited)||staticPath(b.below,visited)||staticPath(b.above,visited);
    if(staticNeighbors(b)){
      //b.setStatic();
      b.setGrounded();
      return true;
    }else{
      if(res){
        //b.setStatic();
        b.setGrounded();
      }
      return res;
    }
  }

  @Override
  public void collision(GameObject obj){
    /*if(obj.get_id() == GameObject.BLOCK_ID){
      Block b = ((Block)obj);
      if(b.grounded){
        b.setPosition(Grid.coordMap(b.gridX, b.gridY));
        if(grounded){
          setPosition(Grid.coordMap(gridX, gridY));
          return;
        }
      }
    }*/
    Collision c = collides(obj);
    boolean collide = true;
    if(obj != this) {
      if (c != null) {
        if (obj.get_id() == GameObject.GROUND_ID || obj.get_id() == GameObject.BLOCK_ID) {
          if(obj.get_id() == GameObject.GROUND_ID){
            grounded = true;
            rooted = true;
            Vector pos = Grid.mapCoord(this.getX(), this.getY(), 40);
            Vector gp = Grid.coordMap((int) pos.getX(), (int) pos.getY(), 40);
            setPosition(gp.getX(), gp.getY());
          }else{
            Block b = (Block)obj;
            grounded = b.grounded;
          }
        }
        Block temp3;
        while (c != null) {
          System.out.println("Stuck in Block collision loop");
          if(obj.get_id() == GameObject.BLOCK_ID){
            temp3 = (Block)obj;
            if(temp3.grounded && !grounded){
              translate(c.getMinPenetration().scale(.5f));
              c = collides(obj);
            }else if(grounded && !temp3.grounded){
              temp3.translate(c.getMinPenetration().scale(-.5f));
              c = collides(obj);
            }else{
              if(!isStatic && temp3.isStatic){
                System.out.println("not static, and is static.");
                translate(c.getMinPenetration().scale(.01f));
                c = collides(obj);
              }else if(grounded && temp3.grounded){
                System.out.println("Both are grounded?");
                translate(c.getMinPenetration().scale(.01f));
                c = collides(obj);
              }else if(!grounded && !temp3.grounded){
                System.out.println("Both are !!NOT!! grounded?");
                translate(c.getMinPenetration().scale(.01f));
                System.out.println("minpen: "+c.getMinPenetration().getX()+", "+c.getMinPenetration().getY());
                c = collides(obj);
              }else{
                c = null;
              }
            }
          }else if(grounded) {
            translate(c.getMinPenetration().scale(.01f));
            c = collides(obj);
          }else{
            c = null;
          }
        }
      }
    }
  }

  public void setActive(boolean active){
    this.active = active;
    this.isStatic = false;
  }

  public void setStatic(){
    this.isStatic = true;
  }
  public void setChanging(){
    this.grounded = false;
    this.isStatic = false;
  }

  public void setGrounded(){
    this.grounded = true;
  }

  public boolean getActive(){
    return active;
  }

  public void setVelocity(float x, float y){
    physics.velocity.setY(y);
    physics.velocity.setX(x);
  }

  public void setGrid(int x, int y){
    gridY = y;
    gridX = x;
  }

  public void setGrid(Vector g){
    gridY = (int)g.getY();
    gridX = (int)g.getX();
  }

  public void setGridY(int y){
    gridY = y;
  }

  public void setGridX(int x){
    gridX = x;
  }

  public void setCluster(boolean cluster){
    hasCluster = cluster;
  }

  @Override
  public int get_id(){
    return GameObject.BLOCK_ID;
  }
}
