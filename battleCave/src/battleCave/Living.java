package battleCave;

import jig.Collision;
import jig.ResourceManager;
import jig.Vector;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;

import java.util.ArrayList;

public class Living extends GameObject{
  private int health;
  private boolean noClimbing;
  private float maxSpeed;
  private float jump;
  private boolean left;
  public boolean goLeft;
  public boolean goRight;
  private boolean crouch;
  public boolean up;
  private boolean grounded;
  private Vector gridPosition;
  private Vector previousGridPosition;
  private String currentImage;
  private boolean climbing;
  public Grid grid;
  public int maxHealth;
  public Living(float x, float y, Grid g) {
    super(x, y);
    left =  false;
    grid = g;
    crouch = false;
    jump = 1.0f;
    maxSpeed = 1.0f;
    health = 100;
    maxHealth = health;
    gridPosition = Grid.mapCoord(x,y,40);
    //physics.setMaxAcceleration(1.0f);
    addImageWithBoundingBox(ResourceManager.getImage(BounceGame.LIVING_THING_RSC));
    currentImage = BounceGame.LIVING_THING_RSC;
    climbing = false;
    noClimbing = false;

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
    if(noClimbing){
      climbing = false;
    }
    previousGridPosition = gridPosition;
    gridPosition = Grid.mapCoord(getX(),getY(),40);
    if(gridPosition.getX() < 0){
      gridPosition.setX(0);
    }else if(gridPosition.getX() > grid.width-1){
      gridPosition.setX(grid.width-1);
    }if(gridPosition.getY() < 0){
      gridPosition.setY(0);
    }else if(gridPosition.getY() > grid.height-1){
      gridPosition.setY(grid.height-1);
    }
    if(goLeft){
      physics.addForce(-.005f*maxSpeed, 0);
    }else if(goRight){
      physics.addForce(.005f*maxSpeed, 0);
    }if(up){
      if(grounded || climbing) {
        physics.setVelocity(new PhysVector( physics.velocity.x,-1.3f*jump));
        grounded = false;
      }
      up = false;
    }
    super.physics.addForce(super.physics.velocity.cloneVec().scale((-.01f)));
    if(!grounded && !climbing) {
      super.physics.addAcceleration(0, (.000981f) * 6);
    }else if(!climbing){
      this.setPosition(getX(),Grid.coordMapY((int)gridPosition.getY(),40));
    }
    if(previousGridPosition.getX() != gridPosition.getX() || previousGridPosition.getY() != gridPosition.getY() || grid.changed){
      grounded = false;
      climbing = false;
    }
    if(!goLeft && !goRight){
      climbing = false;
    }
    super.update(delta);


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
    ArrayList<Boolean> tests = new ArrayList<>();
    tests.add(c1);
    tests.add(c2);
    tests.add(c3);
    tests.add(c4);
    tests.add(c5);
    tests.add(c6);
    tests.add(c7);
    tests.add(c8);
    tests.add(c9);
    if(c1||c2||c3||c4||c5||c6||c7||c8||c9){
      int count = 0;
      for(Boolean test: tests){
        if(test){
          count++;
        }
      }
      if(count >= 2 && grid.getAnyBlock(gridPosition).get_id() == GameObject.BLOCK_ID &&grid.mode==Grid.BATTLE_MODE){
        setHealth(getHealth()-10*count);
      }
      return true;
    }
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
        if(b.gridX == (int)gridPosition.getX() && b.gridY >= (int)gridPosition.getY()) {
          grounded = true;
        }else if(((b.gridX > (int)gridPosition.getX() && goRight) ||(b.gridX < (int)gridPosition.getX() && goLeft)) && b.gridY >= (int)gridPosition.getY()){
          climbing = true;
        }
      }
      if(block || ground) {
        int loops = 0;
        while (c != null) {
          loops++;
          if (ground || block) {
            translate(c.getMinPenetration().scale(.1f));
            c = collides(obj);
          }
          if(loops > 1000){
            PhysVector dir = new PhysVector(getPosition().subtract(obj.getPosition()).unit());
            physics.setVelocity(dir.scale(1/dir.length()));
            //this.setPosition(Grid.coordMap((int)gridPosition.getX(),(int)gridPosition.getY(),40));
            break;
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

  public int getMaxHealth(){return maxHealth;}

  public void setMaxHealth(int max){
    maxHealth = max;
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

  public void setNoClimbing(boolean c){
    noClimbing = c;
  }

  public boolean isGrounded(){
    return grounded;
  }

  public boolean isClimbing(){
    return climbing;
  }

  public void damage(int d){
    setHealth(getHealth()-d);
  }
}
