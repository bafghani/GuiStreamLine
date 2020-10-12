/** Bijan Afghani
  * cs8bwahy
  * bafghani@ucsd.edu
  * Player.java creates a RoundedSquare shape and sizes it to the grid.
  * It also gives this player tile a unique color and border outline.
  **/
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeType;
/** Player creates a new RoundedSquare to represent the player
  * This tile is given its own fill color and outline color.
  **/
public class Player extends RoundedSquare {
  final static double STROKE_FRACTION = 0.1;

    public Player() {
        //TODO: set a fill color, a stroke color, and set the stroke type to
        //      centered
        setFill(Color.VIOLET);
  setStroke(Color.PURPLE);
        setStrokeType(StrokeType.CENTERED);
}
    /*sets size to grid size and updates stroke width*/
@Override
    public void setSize(double size) {
        //  updates the stroke width based on the size and 
        //STROKE_FRACTION
        super.setSize(size);
        setStroke(Color.PURPLE);
        setStrokeWidth(getSize()*STROKE_FRACTION);
        //calls super setSize(), bearing in mind that the size
        //parameter we are passed here includes stroke but the
        //superclass's setSize() does not include the stroke

    }
}
