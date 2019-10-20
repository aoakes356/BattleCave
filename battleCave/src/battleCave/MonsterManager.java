package battleCave;

import jig.Vector;
import org.newdawn.slick.Graphics;

import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Iterator;

public class MonsterManager {
  private WeightManager weights;
  private Grid grid;
  private Ground ground;
  public ArrayList<Monster> monsters;
  public ArrayList<Vector> spawnPoints;
  public boolean autoSpawn;
  int elapsed;
  public int spawnRate; // Spawns per minute.
  public Living target;
  public MonsterManager(Grid g, WeightManager w, Ground ground){
    weights = w;
    grid = g;
    this.ground = ground;
    monsters = new ArrayList<>();
    spawnPoints = new ArrayList<>();
    autoSpawn = false;
    spawnPoints = new ArrayList<>();
    Spawn s1 = new Spawn(new Vector(Grid.coordMapX(1,40),Grid.coordMapY(grid.height-2,40)));
    Spawn s2 = new Spawn(new Vector(Grid.coordMapX(grid.width-2,40),Grid.coordMapY(grid.height-2,40)));
    spawnPoints.add(s1.getPosition());
    spawnPoints.add(s2.getPosition());
    grid.forceBlock(s1.gridX,s1.gridY,s1);
    grid.forceBlock(s2.gridX,s2.gridY,s2);
    elapsed = 0;
    spawnRate = 10;
    target = null;
  }

  public void addMonsters(){
    Monster newMonster;
    for(Vector v: spawnPoints){
      newMonster = new Monster(v.getX(),v.getY(), grid, weights);
      newMonster.setTarget(target);
      monsters.add(newMonster);
    }
  }

  public void setTarget(Living l){
    target = l;
    for(Monster m : monsters){
      m.setTarget(l);
    }
  }

  public boolean collisionTest(GameObject obj){
    for(Monster m : monsters){
      if(m.collisionTest(obj)){
        if(obj.get_id() == GameObject.PROJECTILE_ID){
          Projectile p = (Projectile)obj;
          m.setHealth(m.getHealth()-p.damage);
          p.setActive(false);
        }
        return true;
      }
    }
    return false;
  }

  public void update(int delta){
    Monster m;
    if(autoSpawn) {
      elapsed += delta;
      if (elapsed >= 60000 / spawnRate) {
        addMonsters();
        elapsed = 0;
      }
    }
    for(Iterator<Monster> it = monsters.iterator(); it.hasNext();){
      m = it.next();
      m.update(delta);
      m.gridCollision(grid,ground);
      if(m.getHealth() < 0){
        grid.addMoney(m.getPrice());
        it.remove();
      }
    }
    Spawn s1;
    Block temp;
    for(Vector v:spawnPoints){
      temp =grid.getAnyBlock(v);
      if(temp == null || temp.get_id() != GameObject.SPAWN_BLOCK_ID) {
        s1 = new Spawn(v);
        grid.clickHandler(v,0,GameObject.BLOCK_ID);
        grid.clickHandler(v,1,GameObject.SPAWN_BLOCK_ID);
      }else{
        s1 = (Spawn)grid.getAnyBlock(v);
      }
      s1.update(delta);
    }
  }

  public void killAll(){
    monsters.clear();
  }

  public void render(Graphics g){
    for(Monster m: monsters){
      m.render(g);
    }
    Block temp;
    for(Vector v:spawnPoints){
      temp = grid.getAnyBlock(v);
      if(temp != null){
        temp.render(g);
      }
    }
  }


  public void setAutoSpawn(boolean a){
    autoSpawn = a;
  }
}
