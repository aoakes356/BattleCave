package battleCave;

import jig.Vector;
import org.lwjgl.Sys;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;

public class Grid {
  public static final int BUILD_MODE = 0;
  public static final int BATTLE_MODE = 1;
  public static final int SHOW_PATH = 2;  // This is also battle mode but it shows enemy pathing info as well.
  public ArrayList<ArrayList<Block>> blocks;
  public int mode;
  public int width, height;
  public int widthOffset, heightOffset;
  public static final int PRESSED = 0;
  public static final int RELEASED = 1;
  public boolean pressed;
  public int blockSize;
  public int money;
  private int selected;
  private ArrayList<Cluster> chunks;
  private int blockCount;
  public boolean changed;
  public int brokenValue;
  public Grid(BounceGame bg,int blockSize){
    this.blockSize = blockSize;
    money = 150;
    blockCount = 0;
    blocks = new ArrayList<>();
    mode = BUILD_MODE;
    width = bg.ScreenWidth/blockSize;
    height = bg.ScreenHeight/blockSize;
    widthOffset = (bg.ScreenWidth%blockSize)/2;  // Use these values to center the grid on the screen.
    heightOffset = (bg.ScreenHeight%blockSize)/2;
    ArrayList<Block> temp;
    chunks = new ArrayList<>();
    pressed = false;
    changed = true;
    for(int i = 0; i < width; i++){
      temp = new ArrayList<>();
      blocks.add(temp);
      for(int j = 0; j < height; j++){
        temp.add(new EmptyBlock(coordMap(i,j,blockSize)));
        /*x = i*40+20;
        y = j*40+20;
        temp.add(new Block(x,y,100,i,j));*/
      }

    }
    brokenValue = 0;
  }

  public static boolean isBlock(GameObject obj){
    int id = obj.get_id();
    if(id == GameObject.SPAWN_BLOCK_ID || id == GameObject.BLOCK_ID || id == GameObject.WINDOW_ID || id == GameObject.HOTBLOCK_ID || id == GameObject.HARDBLOCK_ID || id == GameObject.HARDESTBLOCK_ID){
      return true;
    }
    return false;
  }

  public boolean isAvailable(int x, int y){ // Check if a grid space contains part or all of a block.
    Vector gpos;
    for(ArrayList<Block> column: blocks){
      for(Block b: column){
        if(b.get_id() == GameObject.EMPTY_BLOCK_ID || b.grounded){continue;}
        gpos = mapCoord(b.getX(),b.getY(),blockSize);
        if(gpos.getX() == x && (gpos.getY() == y || gpos.getY()+1 == y || gpos.getY()-1 == y)){
          return false;
        }
      }
    }
    return true;
  }

  public void collisionCheck(){  // Check for collisions internally.
    Block temp;
    boolean save;
    ArrayList<Block> column;
    Vector gridpos;
    for (Iterator<ArrayList<Block>> i = blocks.iterator(); i.hasNext();) {
      column = i.next();
      for (Iterator<Block> j = column.iterator(); j.hasNext();) {
        temp = j.next();

        if(temp != null && temp.get_id() != GameObject.EMPTY_BLOCK_ID) {
          gridpos = mapCoord(temp.getPosition().getX(),temp.getPosition().getY(),blockSize);
          if((int)gridpos.getX() >= width || (int)gridpos.getY() >= height){
            column.set(column.indexOf(temp),null);
            return;
          }
          collision(temp);
          if(temp.grounded){
            findNeighbors(temp);
          }
        }
      }
    }
  }

  public void collision(GameObject obj){
    if(isBlock(obj)){
      Block temp = (Block)obj;
      if(!temp.getActive()){
        return;
      }
      Block temp2;
      Vector gridpos = mapCoord(temp.getPosition().getX(),temp.getPosition().getY(),blockSize);
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
        if(temp2 != null && temp2.get_id() != GameObject.EMPTY_BLOCK_ID && temp2.getActive()) {
          temp2.collision(obj);
        }
      }
    }else {
      Block temp;
      for (int i = 0; i < this.blocks.size(); i++) {
        for (int j = 0; j < this.blocks.get(i).size(); j++) {
          temp = (this.blocks.get(i)).get(j);
          if(temp != null && temp.get_id() != GameObject.EMPTY_BLOCK_ID) {
            temp.collision(obj);
          }
        }
      }
    }

  }

  public void update(int delta){
    Block temp;
    int previous;
    ArrayList<Cluster> clusters;
    ArrayList<Block> visited = new ArrayList<>();
    for(int i = 0; i < this.blocks.size(); i++){
      for(int j = 0; j < this.blocks.get(i).size(); j++){
        temp = (this.blocks.get(i)).get(j);
        if(!temp.getActive()){
          clusters = destroyBlock(temp.gridX,temp.gridY);
          if(clusters != null){
            chunks.addAll(clusters);
          }
        }else if(temp != null) {
          previous = temp.gridY;
          temp.update(delta);
          if (temp.gridX < width && temp.gridY < height) {
            if(previous != temp.gridY) {
              if(this.blocks.get(i).get(previous) == temp && this.blocks.get(i).get(temp.gridY).get_id() == GameObject.EMPTY_BLOCK_ID) {
                this.blocks.get(i).set(previous, new EmptyBlock(coordMap(i,j,blockSize)));
                this.blocks.get(i).set(temp.gridY, temp);
              }else{
                temp.setGridY(previous);
              }
            }
          } else {
            blocks.get(i).set(j, new EmptyBlock(coordMap(i,j,blockSize)));
          }

        }
      }
    }
    for(Iterator<Cluster> i = chunks.iterator();i.hasNext();){
      Cluster c = i.next();
      c.update(delta);
      if(c.isStatic){
        i.remove();
      }
    }
    if(chunks.size() > 0){
      changed = true;
    }
  }

  public static void drawLine(Graphics g, Block a, Block b, ArrayList<Block> visited){
    // Draw a line between block a and block b
    if(a != null && b != null && a.get_id() != GameObject.EMPTY_BLOCK_ID && b.get_id() != GameObject.EMPTY_BLOCK_ID) {
      g.drawLine(a.getX(), a.getY(), b.getX(), b.getY());
      visited.add(a);
      visited.add(b);
    }
  }
  public void drawLines(Graphics g){
    ArrayList<Block> visited = new ArrayList<>();
    for(ArrayList<Block> column: blocks) {
      for(Block b:column) {
          drawLine(g, b, b.left, visited);
          drawLine(g, b, b.right, visited);
          drawLine(g, b, b.above, visited);
          drawLine(g, b, b.below, visited);
      }
    }
  }

  public void render(Graphics g){
    Block temp;
    for(int i = 0; i < this.blocks.size(); i++){
      for(int j = 0; j < this.blocks.get(i).size(); j++){
        temp = (this.blocks.get(i)).get(j);
        if(temp != null) {
          if(mode == BUILD_MODE) {
            temp.render(g);
          }else if(temp.get_id() != GameObject.EMPTY_BLOCK_ID){
            temp.render(g);
          }
        }
      }
    }
    drawLines(g);
  }

  public void clickHandler(Vector e, int button, int id){
    if(mode == BUILD_MODE) {
      e = mapCoord(e.getX(), e.getY(), blockSize);
      if ((int) e.getX() >= blocks.size() || (int) e.getY() >= blocks.get((int) e.getX()).size()) {
        return;
      }
      ArrayList<Cluster> clusters;
      if(id == GameObject.EMPTY_BLOCK_ID){return;}
      if (button == 1) {
        activateBlock((int) e.getX(), (int) e.getY(), id);
      } else if (button == 0) {
        clusters = destroyBlock((int) e.getX(), (int) e.getY());
        if (clusters != null) {
          chunks.addAll(clusters);
        }
      }
    }
  }

  public void hover(float x, float y){
    Vector e = mapCoord(x,y,blockSize);
    if((int)e.getX() >= blocks.size() || (int)e.getY() >= blocks.get((int)e.getX()).size()){
      return;
    }
    Block b = blocks.get((int)e.getX()).get((int)e.getY());
    if(b.get_id() == GameObject.EMPTY_BLOCK_ID){
      EmptyBlock space = (EmptyBlock)b;
      space.hover();
    }
  }

  public static Vector coordMap(int x, int y, int blockSize){
    x = x*blockSize+20;
    y = y*blockSize+20;
    return new Vector(x,y);
  }


  public static float coordMapX(int x, int blockSize){
    return x*blockSize+blockSize/2;
  }

  public static float coordMapY(int y, int blockSize){
    return y*blockSize+blockSize/2;
  }

  public static int mapCoordX(float x, int blockSize){
   return (int)(x)/blockSize;
  }
  public static int mapCoordY(float y, int blockSize){
    return (int)(y)/blockSize;
  }
  public static Vector mapCoord(float x, float y, int blockSize){
    int gx = (int)(x)/blockSize;
    int gy = (int)(y)/blockSize;
    return new Vector(gx,gy);
  }

  public static Block cloneBlock(Block b){
    Block nblock;
    if(isBlock(b)){
      if(b.get_id() == GameObject.BLOCK_ID) {
        nblock = new Block(b.gridX, b.gridY, 1000);
      }else if(b.get_id() == GameObject.EMPTY_BLOCK_ID){
        nblock = new EmptyBlock(coordMap(b.gridX,b.gridY,40));
      }else if(b.get_id() == GameObject.SPAWN_BLOCK_ID){
        nblock = new Spawn(coordMap(b.gridX,b.gridY,40),1000,b.gridX,b.gridY);
      }else if(b.get_id() == GameObject.WINDOW_ID){
        nblock = new WindowBlock(coordMap(b.gridX,b.gridY,40),1000,b.gridX,b.gridY);
      }else if(b.get_id() == GameObject.HOTBLOCK_ID){
        nblock = new HotBlock(coordMap(b.gridX,b.gridY,40),1000,b.gridX,b.gridY);
      }else if(b.get_id() == GameObject.HARDBLOCK_ID){
        nblock = new HardBlock(coordMap(b.gridX,b.gridY,40),2000,b.gridX,b.gridY);
      } else if (b.get_id() == GameObject.HARDESTBLOCK_ID) {
        nblock = new HardestBlock(coordMap(b.gridX,b.gridY,40),4000,b.gridX,b.gridY);
      } else {
        nblock = new Block(b.gridX, b.gridY, 100);
      }
    }else{
      nblock = new EmptyBlock(coordMap(b.gridX,b.gridY,40));
    }
    nblock.right = b.right;
    nblock.left = b.left;
    nblock.above = b.above;
    nblock.below = b.below;
    if(b.grounded){
      nblock.setGrounded();
    }else{
      nblock.setChanging();
    }if(b.rooted){
      nblock.setRooted(true);
    }else{
      nblock.setRooted(false);
    }if(b.isStatic()){
      nblock.setStatic();
    }
    nblock.setActive(true);
    return nblock;
  }

  public void activateBlock(int x, int y, int id){
    Block temp;
    temp = this.blocks.get(x).get(y);
    if((temp == null || !isBlock(temp))&&isAvailable(x,y)){
      Block nblock;
      if(id == GameObject.BLOCK_ID) {
        nblock = new Block(x, y, 1000);
      }else if(id == GameObject.EMPTY_BLOCK_ID){
        nblock = new EmptyBlock(coordMap(x,y,40));
      }else if(id == GameObject.SPAWN_BLOCK_ID){
        nblock = new Spawn(coordMap(x,y,40),100,x,y);
      }else if(id == GameObject.WINDOW_ID){
        nblock = new WindowBlock(coordMap(x,y,40),1000,x,y);
      }else if(id == GameObject.HOTBLOCK_ID){
        nblock = new HotBlock(coordMap(x,y,40),1000,x,y);
      }else if(id == GameObject.HARDBLOCK_ID){
        nblock = new HardBlock(coordMap(x,y,40),20000,x,y);
      } else if (id == GameObject.HARDESTBLOCK_ID) {
        nblock = new HardestBlock(coordMap(x,y,40),40000,x,y);
      } else {
        nblock = new Block(x, y, 1000);
      }
      if(money - nblock.cost >= 0 && blockCount <= 500) {
        this.blocks.get(x).set(y, nblock);
        this.blocks.get(x).get(y).setActive(true);
        findNeighbors(nblock);
        money -= nblock.cost;
        blockCount++;
        changed = true;
      }

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
    nblock.setChanging();
    if(left != null  && left.grounded) {
      nblock.setGrounded();
    }else if(right != null && right.grounded) {
      nblock.setGrounded();
    }else if(up != null && up.grounded) {
      nblock.setGrounded();
    }else if(down != null && down.grounded) {
      nblock.setGrounded();
    }else if(nblock.rooted){
      nblock.setGrounded();
    }
    nblock.left = left;
    nblock.right = right;
    nblock.above = up;
    nblock.below = down;

  }

  public static boolean isRooted(Block b, ArrayList<Block> visited){
    if(b == null || b.get_id() == GameObject.EMPTY_BLOCK_ID){return false;}
    boolean u,d,l,r;
    visited.add(b);

    if(b.below != null && !visited.contains(b.below)){
      d = isRooted(b.below,visited);
    }else{
      d = false;
    }if(b.above != null && !visited.contains(b.above)){
      u = isRooted(b.above, visited);
    }else{
      u = false;
    }if(b.left != null && !visited.contains(b.left)){
      l = isRooted(b.left, visited);
    }else{
      l = false;
    }if(b.right != null && !visited.contains(b.right)){
      r = isRooted(b.right, visited);
    }else{
      r = false;
    }
    if(b.rooted){
      b.setGrounded();
      return true;
    }
    if(d||u||l||r){
      b.setGrounded();
    }else{
      b.setChanging();
    }
    return b.grounded;

  }


  public ArrayList<Cluster> destroyBlock(int x, int y){
    Block start = blocks.get(x).get(y);
    ArrayList<Cluster> clusters = null;
    if(start != null && start.get_id() != GameObject.EMPTY_BLOCK_ID){
      money += start.cost;
      brokenValue += start.cost;
      blockCount--;

      changed = true;
      ArrayList<Block> visitedUp = new ArrayList<>();
      ArrayList<Block> visitedDown = new ArrayList<>();
      ArrayList<Block> visitedLeft = new ArrayList<>();
      ArrayList<Block> visitedRight = new ArrayList<>();
      if(start.above != null) {
        start.above.below = null;
      }if(start.below != null) {
        start.below.above = null;
      }if(start.left != null) {
        start.left.right = null;
      }if(start.right != null) {
        start.right.left = null;
      }

      boolean u = isRooted(start.above, visitedUp);
      boolean d = isRooted(start.below, visitedDown);
      boolean l = isRooted(start.left, visitedLeft);
      boolean r = isRooted(start.right, visitedRight);
      Block first;
      clusters = new ArrayList<>();
      if(u){
        for(Block b: visitedUp){
          b.setGrounded();
        }
        u = false;
      }else{
        u = true;
        if(visitedUp.size() > 0){
          first = visitedUp.get(0);
          if(!(visitedDown.contains(first) || visitedLeft.contains(first) ||visitedRight.contains(first))){
            clusters.add(new Cluster(visitedUp));
          }
        }
      }
      if(d){
        for(Block b: visitedDown){
          b.setGrounded();
        }
        d = false;
      }else{
        d = true;
        if(visitedDown.size() >0 ){
          first = visitedDown.get(0);
          if(!(visitedLeft.contains(first) || visitedRight.contains(first) || visitedUp.contains(first))){
            clusters.add(new Cluster(visitedDown));
          }
        }
      }
      if(l){
        l = false;
        for(Block b:visitedLeft){
          b.setGrounded();
        }
      }else{
        l = true;
        if(visitedLeft.size() > 0){
          first = visitedLeft.get(0);
          if(!(visitedDown.contains(first) || visitedRight.contains(first) || visitedUp.contains(first))){
            clusters.add(new Cluster(visitedLeft));
          }
        }
      }
      if(r){
        r = false;
        for(Block b: visitedRight){
          b.setGrounded();
        }
      }else{
        r = true;
        if(visitedRight.size() > 0){
          first = visitedRight.get(0);
          if(!(visitedDown.contains(first) || visitedLeft.contains(first) || visitedUp.contains(first))){
            clusters.add(new Cluster(visitedRight));
          }
        }
      }
      if(!(u&&d&&l&&r)){
        System.out.println("Nothing will fall!");
      }
      blocks.get(x).set(y,new EmptyBlock(coordMap(x,y,blockSize)));
    }else{
    }
    if(clusters != null && clusters.size() == 0){
      clusters = null;
    }
    return clusters;
  }

  public void setMoney(int money){
    this.money = money;
  }

  public void addMoney(int money){
    this.money += money;
  }

  public void removeMoney(int money){
    this.money -= money;
  }

  public void setMode(int m){
    mode = m;
    if(mode == Grid.BATTLE_MODE){
      brokenValue = 0;
    }
  }

  public Block getAnyBlock(Vector v){
    return getAnyBlock((int)v.getX(),(int)v.getY());
  }

  public Block getAnyBlock(int x, int y){
    if(x < blocks.size() && y < blocks.get(x).size()) {
      Block temp = blocks.get(x).get(y);
      return temp;
    }
    return null;
  }

  public Block getBlock(int x, int y){
    if(x < blocks.size() && y < blocks.get(x).size()) {
      Block temp = blocks.get(x).get(y);
      if(temp.get_id() != GameObject.EMPTY_BLOCK_ID){
        return temp;
      }else{
        return null;
      }
    }
    return null;
  }

  public int getSelected() {
    return selected;
  }

  public void setSelected(int selected) {
    this.selected = selected;
  }

  public void setChanged(boolean change){
    changed = change;
  }
  public void forceBlock(int x, int y, Block b){
    if(x < width && x > 0 && y < height && y > 0) {
      blocks.get(x).set(y, b);
    }
  }

  public Block getNearestBlock(Vector pos){
    float dist;
    float min = 10000000;
    Block nearest = null;
    for(ArrayList<Block> column: blocks){  // Naive approach for now.
      for(Block b: column) {
        if (b.get_id() != GameObject.EMPTY_BLOCK_ID && b.get_id() != GameObject.SPAWN_BLOCK_ID) {
          dist = b.getGridPos().subtract(pos).length();
          if (dist < min && ((nearest == null) || nearest.getY() < b.getY())) {
            min = dist;
            nearest = b;
          }
        }
      }
    }
    return nearest;
  }


  public void loadSave(ArrayList<ArrayList<Block>> save){
    blocks.clear();
    blockCount = 0;
    int value = 0;
    Block current;
    ArrayList<Block> visited = new ArrayList<>();
    for(int i = 0; i < save.size();i++){
      blocks.add(new ArrayList<>());
      for(int j = 0; j < save.get(i).size(); j++){
        current = save.get(i).get(j);
        blocks.get(i).add(current);
        current.setPosition(coordMap(i,j,40));
        current.setGrid(i,j);
        if(isBlock(current)) {
          blockCount++;
        }
      }
    }
    /*for(int i = 0; i < save.size();i++) {
      for (int j = 0; j < save.get(i).size(); j++) {
        if(isBlock(blocks.get(i).get(j))) {
        }
      }
    }*/
    money -= brokenValue;
  }


}
