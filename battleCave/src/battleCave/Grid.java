package battleCave;

import jig.Vector;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Graphics;

import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class Grid {
  public static final int BUILD_MODE = 0;
  public static final int BATTLE_MODE = 1;
  public static final int SHOW_PATH = 2;  // This is also battle mode but it shows enemy pathing info as well.
  public ArrayList<ArrayList<Block>> blocks;
  public int mode;
  public int width, height;
  public int widthOffset, heightOffset;
  public Grid(BounceGame bg){
    blocks = new ArrayList<>();
    width = bg.ScreenWidth/40;
    height = bg.ScreenHeight/40;
    widthOffset = (bg.ScreenWidth%40)/2;  // Use these values to center the grid on the screen.
    heightOffset = (bg.ScreenHeight%40)/2;
    ArrayList<Block> temp;
    float x, y;
    for(int i = 0; i < width; i++){
      temp = new ArrayList<>();
      blocks.add(temp);
      for(int j = 0; j < height; j++){
        temp.add(null);
        /*x = i*40+20;
        y = j*40+20;
        temp.add(new Block(x,y,100,i,j));*/
      }

    }
  }

  public void collisionCheck(){  // Check for collisions internally.
    Block temp;
    for (int i = 0; i < this.blocks.size(); i++) {
      for (int j = 0; j < this.blocks.get(i).size(); j++) {
        temp = (this.blocks.get(i)).get(j);
        if(temp != null) {
          collision(temp);
        }
      }
    }
  }

  public void collision(GameObject obj){
    System.out.println("Checking for collision");
    if(obj.get_id() == GameObject.BLOCK_ID){
      Block temp = (Block)obj;
      if(!temp.getActive()){
        return;
      }
      Block temp2;
      Vector gridpos = mapCoord(temp.getPosition().getX(),temp.getPosition().getY());
      int i = (int)gridpos.getX();
      int yRange = temp.gridY;
      int yMin, yMax;
      if(yRange -1 >= 0){
        yMin = yRange-1;
      }else{
        yMin = 0;
      }
      if(yRange+1 <= this.blocks.get(i).size()){
        yMax = yRange+1;
      }else{
        yMax = this.blocks.get(i).size();
      }
      for (int j = yMin; j < yMax; j++) {
        if(j == gridpos.getY()){continue;}
        //System.out.println("Collision check on: ("+temp.gridX+","+temp.gridY+") against ("+temp.gridX+","+j+")");
        temp2 = (this.blocks.get(i)).get(j);
        if(temp2 != null && temp2.getActive()) {
          temp2.collision(obj);
        }
      }
    }else {
      Block temp;
      for (int i = 0; i < this.blocks.size(); i++) {
        for (int j = 0; j < this.blocks.get(i).size(); j++) {
          temp = (this.blocks.get(i)).get(j);
          if(temp != null) {
            temp.collision(obj);
          }
        }
      }
    }

  }

  public void update(int delta){
    Block temp;
    for(int i = 0; i < this.blocks.size(); i++){
      for(int j = 0; j < this.blocks.get(i).size(); j++){
        temp = (this.blocks.get(i)).get(j);
        if(temp != null) {
          temp.update(delta);
          this.blocks.get(i).remove(j);
          this.blocks.get(i).add(temp.gridY,temp);
        }
      }
    }

  }

  public void render(Graphics g){
    Block temp;
    for(int i = 0; i < this.blocks.size(); i++){
      for(int j = 0; j < this.blocks.get(i).size(); j++){
        temp = (this.blocks.get(i)).get(j);
        if(temp != null) {
          temp.render(g);
        }
      }
    }
  }

  public void clickHandler(Vector e, int button){
    System.out.println("Activating block!");
    e = mapCoord(e.getX(),e.getY());
    activateBlock((int)e.getX(),(int)e.getY());

  }

  public static Vector coordMap(int x, int y){
    x = x*40+20;
    y = y*40+20;
    return new Vector(x,y);
  }
  public static float coordMapX(int x){
    return x*40+20;
  }

  public static float coordMapY(int y){
    return y*40+20;
  }

  public static int mapCoordX(float x){
   return (int)(x)/40;
  }
  public static int mapCoordY(float y){
    return (int)(y)/40;
  }
  public static Vector mapCoord(float x, float y){
    int gx = (int)(x)/40;
    int gy = (int)(y)/40;
    return new Vector(gx,gy);
  }

  public void activateBlock(int x, int y){
    Block temp;
    temp = this.blocks.get(x).get(y);
    if(temp != null){

    }else{
      this.blocks.get(x).set(y,new Block(x,y,100));
      this.blocks.get(x).get(y).setActive(true);
    }
  }
}
