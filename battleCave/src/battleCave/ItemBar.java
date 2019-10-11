package battleCave;

import jig.Entity;
import org.newdawn.slick.Graphics;

import java.util.ArrayList;

public class ItemBar extends Entity {
  private boolean active;
  private ArrayList<Integer> items;   // Contains items to be displayed

  public ItemBar(BounceGame bg){
    super(bg.ScreenWidth/2f, bg.ScreenHeight/1.2f);
    active = false;
    items = new ArrayList<>();
    items.add(GameObject.BLOCK_ID);
  }

  public void update(int delta){
    if(active){

    }
  }

  public void render(Graphics g){
    if(active) {
      super.render(g);
    }

  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public void addItem(int id){
    if(!items.contains(id)) {
      items.add(id);
    }
  }

}
