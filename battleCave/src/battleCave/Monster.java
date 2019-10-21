package battleCave;

import jig.Vector;
import org.lwjgl.Sys;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;

import java.util.*;


public class Monster extends Living {
  public ArrayList<Vector> cachedPath;
  public Vector nextBlock;
  private static final float diagonal =(float) Math.sqrt(2);
  public Living target;
  public Block secondary;
  public boolean live;
  public boolean b;
  private Grid grid;
  private int price;
  private WeightManager weightManager;
  public boolean drawPath;
  private HealthBar h;
  private boolean showPathing;
  public Monster(float x, float y, Grid g, WeightManager w) {
    super(x, y, g);
    grid = g;
    showPathing = false;
    setCurrentImage(BounceGame.BASIC_MONSTER_RSC);
    target = null;
    drawPath = false;
    cachedPath = new ArrayList<>();
    weightManager = w;
    price = 10;
    b =false;
    live = true;
    setSpeed(.5f);
    setNoClimbing(true);
    h = new HealthBar(-maxHealth/2.0f,-35,this);
  }

  public void keyHandler(int key, boolean pressed){
    if(key == Input.KEY_ENTER){
    }
  }

  public Float getWeight(Vector v,ArrayList<ArrayList<Float>> weights){
    return weights.get((int)v.getX()).get((int)v.getY());
  }

  public void setWeight(float weight, Vector pos, ArrayList<ArrayList<Float>> weights){
    weights.get((int)pos.getX()).set((int)pos.getY(),weight);
  }

  public ArrayList<Vector> getNeighbors(Vector blockPos){
    ArrayList<Vector> neighbors = new ArrayList<>();
    neighbors.add(blockPos.add(new Vector(0,1)));
    neighbors.add(blockPos.add(new Vector(1,1)));
    neighbors.add(blockPos.add(new Vector(1,0)));
    neighbors.add(blockPos.add(new Vector(-1,0)));
    neighbors.add(blockPos.add(new Vector(0,-1)));
    neighbors.add(blockPos.add(new Vector(-1,-1)));
    neighbors.add(blockPos.add(new Vector(-1,1)));
    neighbors.add(blockPos.add(new Vector(1,-1)));
    Vector current;
    for(Iterator<Vector> it = neighbors.iterator(); it.hasNext();){
      current = it.next();
      if(current.getX() >= grid.blocks.size() || current.getY() >= grid.blocks.get(0).size() || current.getX() < 0 || current.getY() < 0){
        it.remove();
      }
    }
    for(Vector neighbor: neighbors){
      if(neighbor.getX() >= 40 || neighbor.getY() >= 20){
        System.out.println("!!NEIGHBORS!!");
        System.out.println(neighbor);
        System.out.println("----------------");
      }
    }
    return neighbors;
  }

  public void followPath(){
    if(cachedPath != null && cachedPath.size() > 0) {
      Vector next = cachedPath.get(0);
      if(cachedPath.size() <= 1){
        if(live){
          System.out.println("DAAAMAMAMMAMAMAAAGGGIIINNGG!!!");
          target.damage(1);
        }
      }
      Block nextBlock = grid.getAnyBlock(next);
      if(nextBlock.get_id() != GameObject.EMPTY_BLOCK_ID && !nextBlock.isStatic()){
        // Diagonal
        // Attack it stair case order.
        nextBlock.damage(1);
        if(live) {
          if (nextBlock.below != null) {
            nextBlock.below.damage(1);
          }
          if (nextBlock.above != null) {
            nextBlock.above.damage(1);
          }
        }
      }
      if(next.getX() > getGridPos().getX()){
        goRight = true;
      }else{
        goRight = false;
      }if(next.getX() < getGridPos().getX()){
        goLeft = true;
      }else{
        goLeft = false;
      }
      if(next.getY() > getGridPos().getY()){
        up = false;
      }else if(next.getY() < getGridPos().getY()){
        if((int)next.getX() == (int)getGridPos().getX() && (target.isGrounded() || target.isClimbing())){
          // Change target to nearest block, this character can't go straight up.
          System.out.println("!!!!!!!!!Changing targets!!!!!!!!!!!!");
          Block b = grid.getNearestBlock(getGridPos());
          secondary = b;
          if(b != null && b.getActive()) {
            weightManager.setTarget(b);
            live = false;
            this.b = true;
          }
        }
        up = true;
      }else{
        up = false;
      }

    }
  }

  public Vector getLowestNeighbor(Vector blockPos, ArrayList<ArrayList<Float>> weights, ArrayList<Vector> visited){
    ArrayList<Vector> neighbors = getNeighbors(blockPos);
    Vector lowest = null;
    Float lweight = 100000000f;
    Float w;
    for(Vector neighbor: neighbors){
      if(neighbor != null) {
        if ((w = getWeight(neighbor, weights)) < lweight && !visited.contains(neighbor)) {
          lowest = neighbor;
          lweight = w;
        }
      }

    }
    return lowest;
  }


  public void generatePath(ArrayList<ArrayList<Float>> weights){
      cachedPath = new ArrayList<>();
      Vector next = getGridPos();
      Vector targetPos;
      if(live) {
        targetPos=target.getGridPos();
      }else if(b){
        targetPos = secondary.getGridPos();
      }else{
        targetPos = null;
      }
      if(targetPos != null) {
        while (!(next.getX() == targetPos.getX() && next.getY() == targetPos.getY())) {
          next = getLowestNeighbor(next, weights, cachedPath);
          if (next == null) {
            return;
          }
          cachedPath.add(next);
          System.out.println("caching!!");
        }
      }
  }

  public static void drawPath(Graphics g, Block a, Block b, ArrayList<Block> visited){
    // Draw a line between block a and block b
    if(a != null && b != null) {
      g.drawLine(a.getX(), a.getY(), b.getX(), b.getY());
      visited.add(a);
      visited.add(b);
    }
  }

  public void path(Graphics g){
    if(cachedPath == null || !showPathing){
      return;
    }
    Block current = null, previous = null;
    //System.out.println(cachedPath);
    for(Vector v: cachedPath){
      g.drawString(weightManager.getWeight(v)+"",Grid.coordMapX((int)v.getX(),40),Grid.coordMapY((int)v.getY(),40));
      previous = current;
      current = grid.getAnyBlock(v);
      drawPath(g,previous,current,new ArrayList<>());
    }
  }

  public void setTarget(Living target){
    this.target = target;
  }

  public void update(int delta){
    super.update(delta);
    h.update(delta);
    if((secondary == null || !secondary.getActive() )&& !live){
      weightManager.setTarget(target);
      live = true;
      b = false;
      secondary = null;
    }else if(b && secondary!=null&&secondary.getActive()){
      if(weightManager.live){
        weightManager.setTarget(secondary);
      }
    }
    generatePath(weightManager.getWeights());
    followPath();
  }

  public void render(Graphics g){
    h.render(g);
    super.render(g);
    path(g);
  }

  public int get_id(){
    return GameObject.MONSTER1_ID;
  }

  public int getPrice() {
    return price;
  }

  public void setPrice(int p){
    price = p;
  }
  public void setShowPathing(boolean s){
    showPathing = s;
  }
}
