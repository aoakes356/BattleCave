package battleCave;

import jig.ResourceManager;

public class HellBat extends Monster{
  public HellBat(float x, float y, Grid g, WeightManager w) {
    super(x, y, g, w);
    removeImage(ResourceManager.getImage(getCurrentImage()));
    setCurrentImage(BounceGame.HELL_BAT_RSC);
    setFly(true);
  }
}
