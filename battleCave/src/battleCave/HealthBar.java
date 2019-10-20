package battleCave;

import jig.Vector;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class HealthBar extends GameObject{
  private float relativeX, relativeY;
  private Living reference;
  private float MaxHealth;
  public HealthBar(float x, float y, Living obj) {
    super(x+obj.getX(), y+obj.getY());
    relativeX = x;
    relativeY = y;
    reference = obj;
    MaxHealth = obj.getMaxHealth();
  }

  public void update(int delta){
    setPosition(relativeX+reference.getX(),relativeY+reference.getY());
    super.update(delta);
  }

  public void render(Graphics g){
    super.render(g);
    Color save = g.getColor();
    g.setColor(Color.red);
    g.drawLine(getX(),getY(),getX()+MaxHealth,getY());
    g.setColor(Color.green);
    g.drawLine(getX(),getY(),reference.getHealth()+getX(),getY());
    g.setColor(save);
  }
}
