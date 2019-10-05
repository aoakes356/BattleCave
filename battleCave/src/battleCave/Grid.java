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
          if(temp.grounded){
            findNeighbors(temp);
          }
        }
      }
    }
  }

  public void collision(GameObject obj){
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
    ArrayList<Block> updated = new ArrayList<>();
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
    System.out.println("Activating block! "+button);
    e = mapCoord(e.getX(),e.getY());
    if(button == 1) {
      activateBlock((int) e.getX(), (int) e.getY());
    }else if(button == 0){
      destroyBlock((int) e.getX(), (int) e.getY());
    }

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
      Block nblock = new Block(x,y,100);
      this.blocks.get(x).set(y,nblock);
      this.blocks.get(x).get(y).setActive(true);
      findNeighbors(nblock);
    }
  }

  public void findNeighbors(Block nblock){
    int x = nblock.gridX;
    int y = nblock.gridY;
    Block up, down, left, right;
    if(x > 0) {
      left = blocks.get(x - 1).get(y);
    }else {
      left = null;
    }if(x < blocks.size()-1) {
      right = blocks.get(x + 1).get(y);
    }else {
      right = null;
    }if(y > 0) {
      up = blocks.get(x).get(y - 1);
    }else {
      up = null;
    }if(y < blocks.get(x).size()-1) {
      down = blocks.get(x).get(y + 1);
    }else {
      down = null;
    }
    if(left != null && left.grounded) {
      nblock.grounded = true;
      nblock.isStatic = true;
    }else if(right != null && right.grounded) {
      nblock.grounded = true;
      nblock.isStatic = true;
    }else if(up != null && up.grounded) {
      nblock.grounded = true;
      nblock.isStatic = true;
    }else if(down != null && down.grounded) {
      nblock.grounded = true;
      nblock.isStatic = true;
    }
    nblock.left = left;
    nblock.right = right;
    nblock.above = up;
    nblock.below = down;
  }

  public static boolean isRooted(Block b, ArrayList<Block> visited){
    if(b == null){return false;}
    boolean u,d,l,r;
    visited.add(b);
    if(b.rooted){
      b.grounded = true;
      System.out.println("The Root has been found!");
      return true;
    }else{
      if(b.below != null && !visited.contains(b.below)){
        d = isRooted(b.below,visited);
        b.gridY = b.below.gridY-1;
      }else{
        d = false;
      }if(b.above != null && !visited.contains(b.above)){
        u = isRooted(b.above, visited);
        b.gridY = b.above.gridY+1;
      }else{
        u = false;
      }if(b.left != null && !visited.contains(b.left)){
        l = isRooted(b.left, visited);
        b.gridY =  b.left.gridY;
      }else{
        l = false;
      }if(b.right != null && !visited.contains(b.right)){
        r = isRooted(b.right, visited);
        b.gridY = b.right.gridY;
      }else{
        r = false;
      }
      b.grounded = d||u||l||r;
      b.isStatic = b.grounded;
      b.setPosition(Grid.coordMap(b.gridX, b.gridY));
      return b.grounded;
    }
  }

  public void destroyBlock(int x, int y){
    Block start = blocks.get(x).get(y);
    if(start != null){
      ArrayList<Block> visited = new ArrayList<>();
      if(start.above != null) {
        start.above.below = null;
      }if(start.below != null) {
        start.below.above = null;
      }if(start.left != null) {
        start.left.right = null;
      }if(start.right != null) {
        start.right.left = null;
      }
      boolean u = isRooted(start.above, visited);
      boolean d = isRooted(start.below, visited);
      boolean l = isRooted(start.left, visited);
      boolean r = isRooted(start.right, visited);
      if(u){System.out.println("Up is rooted.");}
      if(d){System.out.println("Down is rooted.");}
      if(l){System.out.println("Left is rooted.");}
      if(r){System.out.println("Right is rooted.");}
      if(!(u&&d&&l&&r)){
        System.out.println("Nothing is rooted!!!");
      }
    }else{
      System.out.println("Block that you wish to destroy is null");
    }
    blocks.get(x).set(y,null);
  }
}
