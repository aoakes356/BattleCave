package battleCave;

import jig.ResourceManager;

public class healthButton extends CaveButton{
  public healthButton(float x, float y, Player p) {
    super(x, y);
    setCurrentImage(BounceGame.HEALTH_BUTTON_RSC);
  }

  public void clickHandler(float x, float y, int button, BounceGame bg){
    if(bg.grid.money >= 100 && button == 0 && contains(x, y)) {
      bg.creature.setMaxHealth(bg.creature.getMaxHealth() + 100);
      bg.creature.setHealth(bg.creature.getMaxHealth());
      bg.grid.setMoney(bg.grid.money-100);
    }
  }


}
