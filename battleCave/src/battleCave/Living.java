package battleCave;

import jig.Collision;
import jig.ResourceManager;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;

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

  public Living(float x, float y) {
    super(x, y);
    left =  false;
    crouch = false;
    jump = 1.2f;
    maxSpeed = 1.0f;
    health = 100;
    addImageWithBoundingBox(ResourceManager.getImage(BounceGame.LIVING_THING_RSC));

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
      } else if (key == Input.KEY_D) {
        goRight = false;
      }else if(key == Input.KEY_SPACE){
        up = false;
      }

    }

  }

  @Override
  public void update(int delta){
    super.update(delta);
    if(goLeft){
      physics.addForce(-.005f, 0);
    }else if(goRight){
      physics.addForce(.005f, 0);
    }if(up){
      if(grounded) {
        physics.addForce(0, -.05f);
        grounded = false;
      }
      up = false;
    }
    if(grounded){
      super.physics.addForce(super.physics.velocity.cloneVec().scale(-.021f * super.physics.velocity.length()));
    }
    super.physics.addAcceleration(0, .000981f);
    super.physics.addForce(super.physics.velocity.cloneVec().scale(-.0105f * super.physics.velocity.length()));
  }

  @Override
  public void render(Graphics g){
    super.render(g);

  }

  public void collision(GameObject obj){
    Collision c = collides(obj);
    boolean block = false, ground = false;
    if(c != null) {
      if(obj.get_id() == GameObject.GROUND_ID){
        grounded = true;
        ground = true;
      }else if(obj.get_id() == GameObject.BLOCK_ID){
      }
      while(c != null){
        translate(c.getMinPenetration().scale(.5f));
        c = collides(obj);
      }
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
}
