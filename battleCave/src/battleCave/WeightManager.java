package battleCave;

import jig.Vector;

import java.util.*;

public class WeightManager {
  private Grid grid;
  private ArrayList<ArrayList<Float>> weights;
  private Living target;
  private Block secondary;
  private static final float diagonal =(float) Math.sqrt(2);
  public boolean block;
  public boolean live;

  public WeightManager(Grid g, Living t){
    grid = g;
    target = t;
    live = true;
    block = false;
    generatePath();
  }

  public float edgeWeight(Vector blockPos, Vector startPos){
    Block b = grid.getAnyBlock(blockPos);
    float dist;
    if(live){
      dist = target.getGridPos().subtract(blockPos).length();
    }else{
      dist = secondary.getGridPos().subtract(blockPos).length();
    }
    float health = b.health;
    float weight = dist;
    if((int)blockPos.getX() != (int)startPos.getX() && (int)blockPos.getY() != (int)startPos.getY()){
      weight += (diagonal*100);
    }else if((int)blockPos.getY() > (int)startPos.getY() && (int)blockPos.getX() == (int)startPos.getX()){
      weight += 110;// Straight up is bad.
    }else{
      weight += 10;
    }

    return weight;
  }

  public Float getWeight(Vector v){
    return weights.get((int)v.getX()).get((int)v.getY());
  }

  public void setWeight(float weight, Vector pos){
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
      if(current.getX() >= grid.blocks.size()-1 || current.getY() >= grid.blocks.get(0).size()-1 || current.getX() < 0 || current.getY() < 0){
        it.remove();
      }
    }
    return neighbors;
  }

  public void setNeighbors(Vector blockPos){
    ArrayList<Vector> neighbors = getNeighbors(blockPos);
    float currentWeight = getWeight(blockPos);
    //System.out.println(currentWeight);
    float neighborWeight;
    float edge;
    for(Vector neighbor: neighbors) {
      if (grid.getAnyBlock(neighbor) != null) {
        neighborWeight = getWeight(neighbor);
        edge = edgeWeight(neighbor,blockPos);
        //System.out.println("Edge: "+edge);
        if (edge + currentWeight < neighborWeight) {
          setWeight(edge + currentWeight, neighbor);
        }
      }else{
        setWeight(100000000f,neighbor);
      }
    }
  }

  public void generatePath(){
    if((live && target != null && target.getGridPos() != null) || (block&&secondary != null && secondary.getGridPos() != null)){
      // A*
      HashSet<Vector> visited = new HashSet<>();
      weights = new ArrayList<>();
      ArrayList<Vector> neighbors;
      for(int i = 0; i < grid.blocks.size(); i++){
        weights.add(new ArrayList<>(grid.blocks.get(0).size()));
        for(int j = 0; j < grid.blocks.get(0).size(); j++){
          weights.get(i).add(100000000f);   // Initialize all weights to a big value.
        }
      }
      Vector targetPos;
      if(live) {
        targetPos = target.getGridPos();
      }else if(block){
        targetPos = secondary.getGridPos();
      }else{
        targetPos = null;
      }
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
          setWeight(0,next);
          start = false;
        }
        setNeighbors(next);
        neighbors = getNeighbors(next);
        for(Vector neighbor: neighbors){
          if(!visited.contains(neighbor)){
            p.add(neighbor);
            visited.add(neighbor);
          }
        }
      }
    }
  }

  public ArrayList<ArrayList<Float>> getWeights() {
    return weights;
  }

  public void setTarget(Block b){
    secondary = b;
    block = true;
    live = false;
    generatePath();
  }

  public void setTarget(Living tar){
    target = tar;
    live = true;
    block = false;
    generatePath();
  }
}
