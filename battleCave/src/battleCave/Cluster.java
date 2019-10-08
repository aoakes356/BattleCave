package battleCave;

import java.util.ArrayList;

public class Cluster extends GameObject{

  ArrayList<Block> cluster;
  public boolean isStatic;
  public Cluster(){
    this(0,0);
  }

  public Cluster(float x, float y){
    super(x,y);
    this.isStatic = false;
    this.cluster = new ArrayList<>();
  }
  public Cluster(ArrayList<Block> blocks){
    super(0,0);
    cluster = new ArrayList<>();
    cluster.addAll(blocks);
  }

  public void add(Block b){
    cluster.add(b);
    b.setChanging();
    b.setStatic();
  }

  public void addAll(ArrayList<Block> blocks){
    cluster.addAll(blocks);
    for(Block b: blocks){
      b.setChanging();
      b.setStatic();
    }
  }

  public void update(int delta){
    if(!isStatic) {
      super.physics.addAcceleration(0, .000981f);
      super.physics.addForce(super.physics.velocity.cloneVec().scale(-.0105f * super.physics.velocity.length()));
      for(Block b: cluster){
        if(b.grounded){
          isStatic = true;
          super.physics.velocity.scale(0);
          super.physics.acceleration.scale(0);
          super.physics.force.scale(0);
          return;
        }
      }
      for(Block b: cluster){
        b.setStatic();
        b.setPosition(b.getPosition().getX()+physics.velocity.x*delta,b.getPosition().getY()+physics.velocity.y*delta);
        //b.setPosition(b.getX()+this.getX(),b.getY()+this.getY());
        b.setGrid(Grid.mapCoord(getX(),getY()));
      }
    }else{
      super.physics.velocity.scale(0);
      super.physics.acceleration.scale(0);
      super.physics.force.scale(0);
    }
    super.update(delta);
  }

}
