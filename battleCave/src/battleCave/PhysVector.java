package battleCave;

import jig.Vector;

public class PhysVector {
  public float x;
  public float y;

  public PhysVector(){
    this(0f,0f);
  }
  public PhysVector(float x, float y){
    this.x = x;
    this.y = y;
   }

  public void add(PhysVector v){
    this.x += v.x;
    this.y += v.y;
  }
  public void add(float x, float y){
    this.x += x;
    this.y += y;
  }
  public void mult(float x, float y){ // Elementwise multiplication.
    this.x *= x;
    this.y *= y;
  }
  public void mult(PhysVector v){
    this.x *= v.x;
    this.y *= v.y;
  }
  public void div(float x, float y){
    this.x /= x;
    this.y /= y;
  }
  public void div(PhysVector v){
    this.x /= v.x;
    this.y /= v.y;
  }
  public float dot(PhysVector v){
    return this.x * v.x + this.y * v.y;
  }
  public float dot(float x, float y){
    return this.x * x + this.y * y;
  }
  public PhysVector scale(float scalar){
    this.x *= scalar;
    this.y *= scalar;
    return this;
  }

  public float length(){
    return (float)Math.sqrt(x*x+y*y);
  }

  public PhysVector cloneVec(){
    return new PhysVector(this.x, this.y);
  }
  public Vector vCloneVec() {
    return new Vector(this.x, this.y);
  }
  public void setX(float x){
    this.x = x;
  }

  public void setY(float y){
    this.y = y;
  }

}
