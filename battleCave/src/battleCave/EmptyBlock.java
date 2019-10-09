package battleCave;

import jig.ResourceManager;
import jig.Vector;

public class EmptyBlock extends Block{
  public boolean hovered;
  public EmptyBlock(Vector pos){
    this(pos.getX(),pos.getY());
  }
  public EmptyBlock(float x, float y) {
    super(x,y);
    gridX = Grid.mapCoordX(x);
    gridY = Grid.mapCoordY(y);
    removeImage(ResourceManager.getImage(BounceGame.BASIC_BLOCK_RSC));
    addImage(ResourceManager.getImage(BounceGame.EMPTY_BLOCK_RSC));
    currentImage = BounceGame.EMPTY_BLOCK_RSC;
    setActive(true);
  }

  public void hover(){
    removeImage(ResourceManager.getImage(currentImage));
    currentImage = BounceGame.EMPTYHOVER_BLOCK_RSC;
    addImage(ResourceManager.getImage(currentImage));
    hovered = true;
  }
  public void unhover(){
    removeImage(ResourceManager.getImage(currentImage));
    currentImage = BounceGame.EMPTY_BLOCK_RSC;
    addImage(ResourceManager.getImage(currentImage));
  }
  public void update(int delta){
    if(hovered){
      hovered = false;
    }else {
      unhover();
    }
  }
  @Override
  public int get_id(){
    return GameObject.EMPTY_BLOCK_ID;
  }
}
