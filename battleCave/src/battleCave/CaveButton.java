package battleCave;

import jig.ResourceManager;

public class CaveButton extends GameObject{
  public boolean clicked;
  public String currentImage;
  public CaveButton(float x, float y) {
    super(x, y);
    addImageWithBoundingBox(ResourceManager.getImage(BounceGame.BATTLE_BUTTON_RSC));
    currentImage = BounceGame.BATTLE_BUTTON_RSC;
  }

  public boolean contains(float x, float y){
    float maxX = this.getShapes().get(0).getMaxX();
    float maxY = this.getShapes().get(0).getMaxY();
    float minX = this.getShapes().get(0).getMinX();
    float minY = this.getShapes().get(0).getMinY();
    float relX = getX()-x;
    float relY = getY()-y;
    if(relX < maxX && relX > minX && relY < maxY && relY > minY){
      return true;
    }else{
      return false;
    }
  }

  public void clickHandler(float x, float y, int button, BounceGame bg){
    if(contains(x,y) && button == 0){
      bg.enterState(BounceGame.BATTLESTATE);
    }
  }

  public void setCurrentImage(String image){
    removeImage(ResourceManager.getImage(currentImage));
    removeShape(getShapes().get(0));
    currentImage = image;
    addImageWithBoundingBox(ResourceManager.getImage(currentImage));
  }

}
