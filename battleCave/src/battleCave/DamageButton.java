package battleCave;

public class DamageButton extends CaveButton {
  public DamageButton(float x, float y) {
    super(x, y);
    setCurrentImage(BounceGame.DAMAGE_BUTTON_RSC);
  }

  @Override
  public void clickHandler(float x, float y, int button, BounceGame bg ){
    if(bg.grid.money >= 100 && button == 0) {
      bg.creature.setDamage(bg.creature.getDamage() + 10);
      bg.grid.setMoney(bg.grid.money-100);
    }
  }
}
