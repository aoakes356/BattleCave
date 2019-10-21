package battleCave;

import jig.Entity;
import jig.ResourceManager;
import jig.Shape;
import jig.Vector;
import org.newdawn.slick.Graphics;

import java.util.ArrayList;
import java.util.BitSet;

public class ItemBar extends Entity {
  private boolean active;
  public int selected;
  private int buttonCount;
  private ArrayList<ItemButton> buttons;
  public ItemBar(BounceGame bg){
    super(bg.ScreenWidth/2f, bg.ScreenHeight/1.5f);
    addImageWithBoundingBox(ResourceManager.getImage(BounceGame.EMPTY_MENU_RSC));
    active = false;
    buttons = new ArrayList<>();
    buttonCount = 0;
    addItem(GameObject.BLOCK_ID,BounceGame.BASIC_BLOCK_RSC);
    addItem(GameObject.WINDOW_ID,BounceGame.WINDOW_BLOCK_RSC);
    addItem(GameObject.HOTBLOCK_ID,BounceGame.HOT_BLOCK_RSC);
    addItem(GameObject.HARDBLOCK_ID,BounceGame.HARD_BLOCK_RSC);
    addItem(GameObject.HARDESTBLOCK_ID, BounceGame.HARDEST_BLOCK_RSC);
    selected = GameObject.EMPTY_BLOCK_ID;
  }

  public void update(int delta){
    if(active){
      for(ItemButton b: buttons){
        b.update(delta);
      }
    }
  }

  public void render(Graphics g){
    if(active) {
      super.render(g);
      for(ItemButton b: buttons){
        b.render(g);
      }
    }

  }

  public void clickHandler(int button, int x, int y){
    for(ItemButton b: buttons){
      b.clickHandler(x,y,button);
      if(b.clicked){
        selected = b.id;
        b.clicked = false;
      }
    }

  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public Vector calculateButtonPos(){
    int gridX = buttonCount%5;
    int gridY = buttonCount/5;
    Shape s = getShapes().get(0);
    float relX = getX()-s.getMaxX();
    float relY = getY()-s.getMaxY();
    return new Vector(gridX*50+relX+30,gridY*50+relY+30);
  }

  public void addItem(int id, String image){
    Vector nextPos = calculateButtonPos();
    ItemButton b = new ItemButton(nextPos.getX(),nextPos.getY(),id);
    b.setCurrentImage(image);
    buttons.add(b);
    buttonCount++;
  }

}
