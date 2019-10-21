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
  public boolean showPathing;
  public MonsterManager(Grid g, WeightManager w, Ground ground){
    weights = w;
    grid = g;
    this.ground = ground;
    monsters = new ArrayList<>();
    spawnPoints = new ArrayList<>();
    autoSpawn = false;
    spawnPoints = new ArrayList<>();
    Spawn s1 = new Spawn(Grid.coordMap(grid.width-1,grid.height-2,40),100,grid.width-1,grid.height-2);
    Spawn s2 = new Spawn(Grid.coordMap(0,grid.height-2,40),100,0,grid.height-2);
    spawnPoints.add(s1.getPosition());
    spawnPoints.add(s2.getPosition());
    grid.clickHandler(s1.getPosition(),1,GameObject.SPAWN_BLOCK_ID);
    grid.clickHandler(s2.getPosition(),1,GameObject.SPAWN_BLOCK_ID);
    elapsed = 0;
    spawnRate = 10;
    target = null;
    showPathing = false;
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
        spawnRate++;
        elapsed = 0;
      }
    }
    for(Iterator<Monster> it = monsters.iterator(); it.hasNext();){
      m = it.next();
      m.setShowPathing(showPathing);
      m.update(delta);
      m.gridCollision(grid,ground);
      if(m.getHealth() < 0){
        grid.addMoney(m.getPrice());
        it.remove();
      }
    }
    Block temp;
    for(Vector v:spawnPoints){
      temp =grid.getAnyBlock(Grid.mapCoord(v.getX(),v.getY(),40));
      if(temp == null || temp.get_id() != GameObject.SPAWN_BLOCK_ID) {
        if(grid.mode == Grid.BUILD_MODE) {
          grid.clickHandler(v, 0, 2);
          grid.clickHandler(v, 1, GameObject.SPAWN_BLOCK_ID);
        }else{
          int gx = Grid.mapCoordX(v.getX(), 40), gy = Grid.mapCoordY(v.getY(),40);
          Spawn s1 = new Spawn(v,100,gx,gy);
          grid.forceBlock(Grid.mapCoordX(v.getX(),40),Grid.mapCoordY(v.getY(),40),s1);
        }
      }
    }
  }

  public void killAll(){
    monsters.clear();
  }

  public void render(Graphics g){
    for(Monster m: monsters){
      m.render(g);
    }
  }


  public void setAutoSpawn(boolean a){
    autoSpawn = a;
  }
  public void setShowPathing(boolean s){
    showPathing = s;
  }
}
