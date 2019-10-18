package battleCave;

import jig.Collision;
import jig.ResourceManager;
import jig.Vector;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;

import java.util.ArrayList;

public class Living extends GameObject{
  private int health;
  private float maxSpeed;
  private float jump;
  private boolean left;
  private boolean goLeft;
  private boolean goRight;
  private boolean crouch;
  private boolean up;
  private boolean grounded;
  private Vector gridPosition;
  private String currentImage;
  public Living(float x, float y) {
    super(x, y);
    left =  false;
    crouch = false;
    jump = 1.2f;
    maxSpeed = 1.0f;
    health = 100;
    gridPosition = Grid.mapCoord(x,y,40);
    //physics.setMaxAcceleration(1.0f);
    addImageWithBoundingBox(ResourceManager.getImage(BounceGame.LIVING_THING_RSC));
    currentImage = BounceGame.LIVING_THING_RSC;

  }

  public void keyHandler(int key, boolean pressed){
    if(pressed) {
      if (key == Input.KEY_A) {
        left = true;
        goLeft = true;
      } else if (key == Input.KEY_D) {
        left = false;
        goRight = true;
      }else if(key == Input.KEY_SPACE){
        up = true;
      }
    }else{
      if (key == Input.KEY_A) {
        goLeft = false;
        if(grounded){
          //physics.velocity.scale(0);
        }
      } else if (key == Input.KEY_D) {
        goRight = false;
        if(grounded) {
          //physics.velocity.scale(0);
        }
      }else if(key == Input.KEY_SPACE){
        up = false;
      }

    }

  }

  @Override
  public void update(int delta){
    gridPosition = Grid.mapCoord(getX(),getY(),40);
    if(goLeft){
      physics.addForce(-.005f, 0);
    }else if(goRight){
      physics.addForce(.005f, 0);
    }if(up){
      if(grounded) {
        physics.addForce(0, -.08f);
        grounded = false;
      }
      up = false;
    }
    super.update(delta);
    super.physics.addForce(super.physics.velocity.cloneVec().scale(-.01f));
    if(!grounded) {
      super.physics.addAcceleration(0, .000981f * 6);
    }

  }

  @Override
  public void render(Graphics g){
    super.render(g);

  }

  public boolean gridCollision(Grid g, Ground ground){
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
    if(c1||c2||c3||c4||c5||c6||c7||c8||c9){
      grounded = true;
      return true;
    }
    grounded = false;
    return false;
  }

  public boolean collisionTest(GameObject obj){
    if(obj.get_id() == GameObject.EMPTY_BLOCK_ID){
      return false;
    }
    Collision c = collides(obj);
    boolean block = false, ground = false;
    if(c != null) {
      if(obj.get_id() == GameObject.GROUND_ID){
        grounded = true;
        ground = true;
      }else if(obj.get_id() == GameObject.BLOCK_ID){
        block = true;
        Block b = (Block)obj;
        grounded = true;
      }
      if(block || ground) {
        int loops = 0;
        while (c != null) {
          System.out.println("Stuck in the living collision loop.");
          loops++;
          if (ground || block) {
            translate(c.getMinPenetration().scale(.1f));
            c = collides(obj);
          }
          if(loops > 100){
            loops = 0;
            this.setPosition(Grid.coordMap((int)gridPosition.getX(),(int)gridPosition.getY(),40));
          }
        }
      }

      return true;
    }else{
      return false;
    }

  }


  public int getHealth() {
    return health;
  }

  public void setHealth(int health) {
    this.health = health;
  }

  public float getSpeed() {
    return maxSpeed;
  }

  public void setSpeed(float speed) {
    this.maxSpeed = speed;
  }

  public float getJump() {
    return jump;
  }

  public void setJump(float jump) {
    this.jump = jump;
  }

  public Vector getGridPos(){
    return gridPosition;
  }

  public static void settle(Living alive, int blockSize){
    alive.setY(Grid.coordMapY((int)alive.gridPosition.getY(),blockSize));
  }

  public String getCurrentImage() {
    return currentImage;
  }

  public void setCurrentImage(String currentImage) {
    removeImage(ResourceManager.getImage(getCurrentImage()));
    removeShape(getShapes().get(0));
    this.currentImage = currentImage;
    addImageWithBoundingBox(ResourceManager.getImage(currentImage));
  }
}
