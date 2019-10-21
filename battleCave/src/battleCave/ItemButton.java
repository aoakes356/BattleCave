package battleCave;

public class ItemButton extends CaveButton {
  public int id;
  public boolean clicked;
  public ItemButton(float x, float y, int id) {
    super(x, y);
    clicked = false;
    this.id = id;
  }

  public void clickHandler(float x, float y, int button){
    clicked = true;
  }

  public void update(int delta){
    super.update(delta);
    clicked = false;
  }
}
