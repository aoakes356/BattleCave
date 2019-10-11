package battleCave;

import jig.Entity;
import jig.ResourceManager;
import jig.Vector;
import org.newdawn.slick.Graphics;

import java.util.ArrayList;

public class ItemBar extends Entity {
  private boolean active;
  private ArrayList<Integer> items;   // Contains items to be displayed
  private ArrayList<Vector> coords;   // (relative)Coordinates to the square in which the items are shown.
  public int selected;
  public ItemBar(BounceGame bg){
    super(bg.ScreenWidth/2f, bg.ScreenHeight/1.5f);
    coords = new ArrayList<>();
    coords.add(new Vector(-100,-70));
    active = false;
    items = new ArrayList<>();
    items.add(GameObject.BLOCK_ID);
    selected = GameObject.EMPTY_BLOCK_ID;
    addImage(ResourceManager.getImage(BounceGame.EMPTY_MENU_RSC));
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

  public void clickHandler(int button, int x, int y){
    Vector relative = new Vector(x,y).subtract(getPosition());
    System.out.println("Relative click coordinates: "+relative.getX()+", "+relative.getY());
    if(Math.abs(relative.getX()) > 130 || Math.abs(relative.getY()) > 100){
      return;
    }
    Vector dist;
    int count = 0;
    int id = -1;
    for(Vector v: coords){
      dist = v.subtract(relative);
      if(Math.abs(dist.getX()) < 20 && Math.abs(dist.getY()) < 20){
        // That is the box that was clicked.
        id = items.get(count);
      }
      count++;
    }
    if(id != -1){
      selected = id;
      System.out.println("new block selected.");
    }
    System.out.println("The click is in the box.");
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public void addItem(int id, Vector relativeCoords){
    if(!items.contains(id)) {
      items.add(id);
      coords.add(relativeCoords);
    }

  }

}
