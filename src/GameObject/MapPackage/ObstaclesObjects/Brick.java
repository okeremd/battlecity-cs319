package GameObject.MapPackage.ObstaclesObjects;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.nio.file.Paths;

/**
 * Created by kaan on 10/28/2017.
 */
public class Brick extends Destructible {

    private final String IMG_DIR_BRICK = "./MediaFiles/image1.png";
    private final String IMG_DIR_MINI_BRICK = "./MediaFiles/image1.png";
    private final String IMG_DIR_SMALL_BRICK = "./MediaFiles/image1.png";
    private final double center_shift = 16;//center shifts 0.25 when brick gets damaged.
    private final int NORMAL_DIMENSION = 32;
    private final int DAMAGED_DIMENSION = 16;

    //Constructor
    public Brick( double xLoc, double yLoc, int imageNum){
        if( imageNum == 0)
            view = new ImageView(new Image(Paths.get(IMG_DIR_BRICK).toUri().toString()));
        else if( imageNum == 1)
            view = new ImageView(new Image(Paths.get(IMG_DIR_MINI_BRICK).toUri().toString()));
        else if( imageNum == 2)
            view = new ImageView(new Image(Paths.get(IMG_DIR_SMALL_BRICK).toUri().toString()));

        this.xLoc = xLoc;
        this.yLoc = yLoc;
        view.setFitWidth( NORMAL_DIMENSION);
        view.setFitHeight( NORMAL_DIMENSION);
    }

    public void getDamaged( int dir){
        super.getDamaged(dir);
        setDamagedImage( dir);
    }



    public void setDamagedImage(int dir) {
        super.setDamaged( true);
        switch ( dir){
            case 0: {
                xLoc = ( xLoc + center_shift);
                view.setFitHeight( NORMAL_DIMENSION);
                view.setFitWidth(DAMAGED_DIMENSION);
                break;
            }
            case 1: {
                view.setFitHeight( NORMAL_DIMENSION);
                view.setFitWidth(DAMAGED_DIMENSION);
                break;
            }
            case 2: {
                yLoc = ( yLoc + center_shift);
                view.setFitHeight( DAMAGED_DIMENSION);
                view.setFitWidth(NORMAL_DIMENSION);
                break;
            }
            case 3: {
                view.setFitHeight( DAMAGED_DIMENSION);
                view.setFitWidth(NORMAL_DIMENSION);
                break;
            }
            default: break;
        }
        super.draw();
    }

    public void getDestructed(){
        isDestructed = true;
    }

    @Override
    public boolean isPassableByTanks() {
        return false;
    }

    @Override
    public boolean isPassableByBullets() {
        return true;
    }

    @Override
    public boolean isHideable() {
        return false;
    }
}
