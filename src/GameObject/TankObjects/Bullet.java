package GameObject.TankObjects;

import GameObject.GameObject;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.awt.geom.Point2D;
import java.nio.file.Paths;

/**
 * Created by kaan on 10/28/2017.
 */
public class Bullet extends GameObject {

    //Variables
    //direction of the bullet
    private final int BULLET_VIEW_DIMENSION = 10;
    private final double BULLET_VELOCITY = 4;
    private boolean isCrushed = false;
    private int dir;
    private int id;


    //Constructor
    public Bullet( int id, double xLoc, double yLoc, int dir) {
        this.xLoc = xLoc;
        this.yLoc = yLoc;
        this.id = id;
        this.dir = dir;
        setDirImage();
        view = new ImageView( image);
        view.setFitHeight(BULLET_VIEW_DIMENSION);
        view.setFitWidth(BULLET_VIEW_DIMENSION);
        super.draw();
        super.setVelocity( new Point2D.Double(BULLET_VELOCITY,BULLET_VELOCITY));
    }

    private void setDirImage() {
        switch (dir){
            case 0: super.setImage(new Image(Paths.get("."+"/MediaFiles/bullet_right.png").toUri().toString()));
                    break;
            case 1: super.setImage(new Image(Paths.get("."+"/MediaFiles/bullet_left.png").toUri().toString()));
                    break;
            case 2: super.setImage(new Image(Paths.get("."+"/MediaFiles/bullet_down.png").toUri().toString()));
                    break;
            case 3: super.setImage(new Image(Paths.get("."+"/MediaFiles/bullet_up.png").toUri().toString()));
                    break;
            default: super.setImage(new Image(Paths.get("."+"/MediaFiles/bullet_right.png").toUri().toString()));
                    break;
        }
    }

    //methods
    /*Destruct method destroys the current bullet
    if any collision occurs
    returns whether destruction is successful.
     */
    public boolean destruct(int xLoc, int yLoc) {
        //This method will be filled
        return true;
    }

    //This method moves the bullet through the map
    //in given direction
    public void move() {
        if (!isCrushed()) {
            if (dir == 0) {
                xLoc = ( xLoc + super.getVelocity().getX());
            } else if(dir == 1) {
                xLoc = (xLoc - super.getVelocity().getX());
            }else if(dir == 2) {
                yLoc = (yLoc + super.getVelocity().getY());
            }else if(dir == 3) {
                yLoc = (yLoc - super.getVelocity().getY());
            }
            super.draw();
        }
    }


    //Setters and Getters
    public int getDir() {
        return dir;
    }

    public int getId() {
        return id;
    }

    public boolean isCrushed() {
        return isCrushed;
    }
    public void setCrushed(boolean crushed) {
        isCrushed = crushed;
    }

    @Override
    public boolean isPassableByTanks() {
        return false;
    }

    @Override
    public boolean isPassableByBullets() {
        return false;
    }

    @Override
    public boolean isHideable() {
        return false;
    }
}
