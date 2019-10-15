package battleCave;

import jig.Vector;
/***Created this class so a vector is not created for EVERY SINGLE CALCULATION ***/
public class Physics {
  public PhysVector position;
  public PhysVector velocity;
  public PhysVector acceleration;
  public PhysVector force;
  public float mass;
  public float maxAcceleration;

  public Physics(){
    this(0,0,0);
  }
  public Physics(float x, float y, float mass){
    this(x,y,0,0, mass);
  }
  public Physics(PhysVector pos, PhysVector vel, float mass){
    this(pos.x,pos.y,vel.x,vel.y,mass);
  }
  public Physics(float x, float y,float vx, float vy, float mass){
    position = new PhysVector(x,y);
    velocity = new PhysVector(vx,vy);
    acceleration = new PhysVector(0,0);
    force = new PhysVector(0,0);
    maxAcceleration = 300f;
    this.mass = mass;
  }

  public void addForce(float fx, float fy){
    force.add(fx,fy);
  }
  public void addForce(PhysVector v){
    force.add(v);
  }
  public void addForce(Vector v){
    force.add(v.getX(),v.getY());
  }

  public void addAcceleration(float ax, float ay){
    acceleration.add(ax,ay);
  }
  public void addAcceleration(PhysVector v){
    acceleration.add(v);
  }
  public void addAcceleration(Vector v){
    acceleration.add(v.getX(),v.getY());
  }
  public void setMaxAcceleration(float acceleration){
    maxAcceleration = acceleration;
  }

  public void setVelocity(PhysVector v){
    velocity = v;
  }

  public void update(int delta){
    acceleration.add(force.x/mass,force.y/mass);
    acceleration.scale(delta);
    float length = acceleration.length();
    if(length > maxAcceleration){
      acceleration.scale(maxAcceleration/length);
    }
    velocity.add(acceleration);
    force.x = 0;
    force.y = 0;
    acceleration.x = 0;
    acceleration.y = 0;
  }


}
