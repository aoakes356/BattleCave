package battleCave;

import jig.Vector;
import org.lwjgl.Sys;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;

import java.util.*;


public class Monster extends Living {
  public ArrayList<Vector> cachedPath;
  private static final float diagonal =(float) Math.sqrt(2);
  public Vector nextBlock;
  public Living target;
  private Grid grid;
  public boolean drawPath;
  public Monster(float x, float y, Grid g) {
    super(x, y);
    grid = g;
    setCurrentImage(BounceGame.BASIC_MONSTER_RSC);
    target = null;
    drawPath = false;
    cachedPath = new ArrayList<>();
  }

  public void keyHandler(int key, boolean pressed){
    if(key == Input.KEY_ENTER){
      try {
        generatePath();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  public float edgeWeight(Vector blockPos){
    float dist = target.getGridPos().subtract(blockPos).length();
    float health = grid.getAnyBlock(blockPos).health;
    float weight = dist + health;
    if((int)blockPos.getX() != (int)getGridPos().getX() && (int)blockPos.getY() != (int)getGridPos().getY()){
      weight += diagonal*10;
    }else{
      weight += 10;
    }

    return weight;
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

  public void setNeighbors(Vector blockPos, ArrayList<ArrayList<Float>> weights) throws Exception {
    ArrayList<Vector> neighbors = getNeighbors(blockPos);
    float currentWeight = getWeight(blockPos,weights);
    //System.out.println(currentWeight);
    float neighborWeight;
    float edge;
    for(Vector neighbor: neighbors) {
      if (grid.getAnyBlock(neighbor) != null) {
        neighborWeight = getWeight(neighbor, weights);
        edge = edgeWeight(neighbor);
        //System.out.println("Edge: "+edge);
        if (edge + currentWeight < neighborWeight) {
          setWeight(edge + currentWeight, neighbor, weights);
        }
      }else{
        setWeight(100000000f,neighbor,weights);
      }
    }
  }

  private static boolean contains(Vector v, ArrayList<Vector> vectors){
    for(Vector temp: vectors){
      if(((int)v.getX())==((int)temp.getX()) && ((int)v.getY())==((int)temp.getY()) ){
        return true;
      }
    }
    return false;
  }

  public void generatePath() throws Exception {

    if(target != null && target.getGridPos() != null && getGridPos() != null){
      // A*
      ArrayList<Vector> visited = new ArrayList<>();
      ArrayList<ArrayList<Float>> weights = new ArrayList<>();
      ArrayList<Vector> neighbors;
      for(int i = 0; i < grid.blocks.size(); i++){
        weights.add(new ArrayList<>(grid.blocks.get(0).size()));
        for(int j = 0; j < grid.blocks.get(0).size(); j++){
          weights.get(i).add(100000000f);   // Initialize all weights to a big value.
        }
      }
      Vector targetPos = target.getGridPos();
      Comparator<Vector> vectorComparator = new Comparator<Vector>() {
        @Override
        public int compare(Vector o1, Vector o2) {
          return (int)(weights.get((int)o1.getX()).get((int)o1.getY())-weights.get((int)o2.getX()).get((int)o2.getY()));
        }
      };
      PriorityQueue<Vector> p = new PriorityQueue<>(10, vectorComparator);
      p.add(targetPos);
      Vector next;
      boolean start = true;
      while (!p.isEmpty()){
        next = p.remove();
        if(start){
          setWeight(0,next,weights);
          start = false;
        }
        setNeighbors(next,weights);
        neighbors = getNeighbors(next);
        for(Vector neighbor: neighbors){
          if(!visited.contains(neighbor)){
            p.add(neighbor);
            visited.add(neighbor);
          }else{
            System.out.println("redundant");
          }
        }
        if((int)next.getX() == (int)getGridPos().getX() && (int)next.getY() == (int)getGridPos().getY()){
          break;
        }
        System.out.println("Next Node!");
      }
      for(ArrayList<Float> row:weights){
        //System.out.println(row);
      }
      cachedPath = new ArrayList<>();
      next = getGridPos();
      targetPos = target.getGridPos();
      while(!(next.getX() == targetPos.getX() && next.getY() == targetPos.getY())){
        next = getLowestNeighbor(next,weights,cachedPath);
        if(next == null){
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
    if(cachedPath == null){
      return;
    }
    Block current = null, previous = null;
    //System.out.println(cachedPath);
    for(Vector v: cachedPath){
      previous = current;
      current = grid.getAnyBlock(v);
      if(previous != null){
        drawPath(g,previous,current,new ArrayList<>());
      }
    }
  }

  public void setTarget(Living target){
    this.target = target;
  }

  public void update(int delta){
    super.update(delta);
   try {
      generatePath();
    } catch (Exception e) {
      e.printStackTrace();
    }
    //System.out.println(cachedPath);
  }

  public void render(Graphics g){
    super.render(g);
    path(g);
  }



}
