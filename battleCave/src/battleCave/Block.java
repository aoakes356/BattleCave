package battleCave;

import jig.ResourceManager;
import org.newdawn.slick.Graphics;

//Block size is 40x40
public class Block extends GameObject {
  public float health;
  public float currentHealth;

  public Block(float x, float y) {
    this(x,y,100.0f);
  }

  public Block(float x, float y, float health){
    super(x,y);
    addImageWithBoundingBox(ResourceManager
        .getImage(BounceGame.BASIC_BLOCK_RSC));
    this.health = health;
    this.currentHealth = health;
  }

  @Override
  public void render(Graphics g) {
    super.render(g);
  }

  @Override
  public void update(int delta){
    super.update(delta);
    super.physics.addAcceleration(0,.000981f);
    super.physics.addForce(super.physics.velocity.cloneVec().scale(-.0105f*super.physics.velocity.length()));
  }

  public void hit(float dmg){
    currentHealth -= dmg;
  }

  public void heal(){
    currentHealth = health;
  }

}
