package GameObject.MapPackage;

import GameObject.GameObject;
import GameObject.MapPackage.BonusPackage.Bonus;
import GameObject.MapPackage.BonusPackage.LifeBonus;
import GameObject.MapPackage.BonusPackage.SpeedBonus;
import GameObject.MapPackage.ObstaclesObjects.*;
import GameObject.TankObjects.Bot;
import GameObject.TankObjects.Bullet;
import GameObject.TankObjects.Player;
import GameObject.TankObjects.Tank;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import javax.swing.plaf.nimbus.State;
import java.util.ArrayList;
import java.util.Random;

public class Map {

    private final int TILES = 20;
    private final int MAP_DIMENSION = 32;
    private final double SHIFT = 0.33;
    private final int FRAME_UPPER_BOUND = 640;
    private final int FRAME_LOWER_BOUND = 0;
    private boolean isGameOver = false;
    private boolean isPaused = false;
    private Scene mapScene;
    private Stage mapStage;
    private int playerCount;
    private int level;
    private int remainingBots;
    private int botCount;
    private GameObject[][] gameObjects;
    private int[][] obstaclesMap;
    private Pane mapPane;
    private Player players[];
    private ArrayList<Bullet> bullets;
    private ArrayList<Tank> tanks;
    private ArrayList<Bot> bots;
    private ArrayList<Bonus> bonuses;
    private ArrayList<GameObject> objectHolder;
    private ArrayList<Destructible> destructibles;
    private int lifeBonusCount;
    private int speedBonusCount;
    private Random rand;

    /* GameObject.GameObject File Decode
    * 0 = GameObject.GameObject.MapPackage.ObstaclesObjects.Brick, 1 = Wall, 2 = GameObject.GameObject.MapPackage.ObstaclesObjects.Bush, 3 = GameObject.MapPackage.ObstaclesObjects.Water
    * 4 = GameObject.GameObject.TankObjects.Player, 5 = GameObject.GameObject.TankObjects.Bot
    * */
    public Map(int playerCount, int level, int[][] obstaclesMap){
        createObjectArrays();
        this.obstaclesMap = obstaclesMap;
        this.playerCount = playerCount;
        this.level = level;
        initMapObjects();
        intToObject();
        addObjects();
        initPlayers();
        lifeBonusCount = 0;
        speedBonusCount = 0;
        rand = new Random();
    }

    //Init all objects
    private void initMapObjects(){
        mapPane = new Pane();
        gameObjects = new GameObject[TILES][TILES];
        players = new Player[playerCount];
        mapPane.setPrefWidth(FRAME_UPPER_BOUND);
        mapPane.setPrefHeight(FRAME_UPPER_BOUND+60);
        botCount = 10 + 2 * level; // WOW lol
        remainingBots = botCount;
    }

    //Create map holder arrays
    private void createObjectArrays(){
        bullets = new ArrayList<>();
        bots = new ArrayList<>();
        bonuses = new ArrayList();
        objectHolder = new ArrayList<GameObject>();
        tanks = new ArrayList<Tank>();
        destructibles = new ArrayList<Destructible>();
    }

    //Decide how to spawn a bot
    public void spawnBot(){
        double x_loc = 0.0;
        double y_loc = 0.0;
        boolean found_empty = true;
        do{
            found_empty = true;
            x_loc = rand.nextDouble()* ( FRAME_UPPER_BOUND - 23);
            y_loc = rand.nextDouble()* ( FRAME_UPPER_BOUND - 23);
            for( GameObject object : objectHolder){
                if( object.getView().getBoundsInParent().intersects( x_loc, y_loc,23, 23)){
                    found_empty = false;
                }
            }
        }while(!found_empty);

        Bot bot = new Bot( x_loc, y_loc);
        mapPane.getChildren().add( bot.getView());
        bots.add( bot);
        tanks.add( bot);
        objectHolder.add( bot);
        remainingBots--;
    }

    public void createBonus( int type) {
        double x_loc = 0.0;
        double y_loc = 0.0;
        boolean found_empty = true;
        do {
            found_empty = true;
            x_loc = rand.nextDouble() * (FRAME_UPPER_BOUND - MAP_DIMENSION);
            y_loc = rand.nextDouble() * (FRAME_UPPER_BOUND - MAP_DIMENSION);
            for (GameObject object : objectHolder) {
                if (object.getView().getBoundsInParent().intersects(x_loc, y_loc, MAP_DIMENSION, MAP_DIMENSION)) {
                    found_empty = false;
                }
            }
        } while (!found_empty);
        if (type == 0 && lifeBonusCount < 2) { // there should be a time between the creation of bonuses and the bonuses should not be released on the obstacles
            Bonus lifeBonus = new LifeBonus(x_loc, y_loc);
            lifeBonus.setReleased(true);
            mapPane.getChildren().addAll(lifeBonus.getView());
            lifeBonusCount++;
            bonuses.add(lifeBonus);
            objectHolder.add(lifeBonus);
        } else if (type == 1 && speedBonusCount < 2) {
            Bonus speedBonus = new SpeedBonus(x_loc, y_loc);
            speedBonus.setReleased(true);
            mapPane.getChildren().addAll(speedBonus.getView());
            speedBonusCount++;
            bonuses.add(speedBonus);
        }
    }


    private void initPlayers(){
        for(int i = 0; i < playerCount; i++){
            players[i] = new Player(i, i);
        }
        for( Player player : players){
            tanks.add(player);
            objectHolder.add( player);
            mapPane.getChildren().addAll(player.getView());
        }
    }

    //Update Methods
    //Update of Tanks
    public void updateTanks(){
        updatePlayer();
        updateBots();
        tanks.removeIf( Tank::isDead);
        objectHolder.removeIf(GameObject::isDestructed);
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    private void updatePlayer(){
        boolean isAllPlayersDead = true;
        for ( Player player : players){
            if ( !player.isDead()){
                player.draw();
                isAllPlayersDead = false;
            }
            else{
                mapPane.getChildren().remove(player.getView());
                player.setDestructed(true);
            }
        }
        if( isAllPlayersDead){
            isGameOver = true;
        }
    }

    private void updateBots() {
        for ( Bot bot : bots){
            if ( !bot.isDead())
                bot.draw();
            else{
                bot.setDestructed( true);
                mapPane.getChildren().remove(bot.getView());
            }
        }
        bots.removeIf(Tank::isDead);
    }

    //Update of Bullets
    public void updateBullets(){
        for( Bullet bullet : bullets) {
            if (bullet.isCrushed() || bullet.getyLoc() > FRAME_UPPER_BOUND) {
                bullet.setDestructed(true);
                mapPane.getChildren().remove(bullet.getView());
            }
            else {
                bullet.move();
            }
        }
    }

    //Update Methods
    public void updateDestructibles() {
        for( Destructible destructible: destructibles) {
            if (destructible.isDestructed()) {
                mapPane.getChildren().remove(destructible.getView());
                if(destructible instanceof Statue){
                    isGameOver = true;
                }
            }
            else
                destructible.draw();
        }
    }

    public void updateBonuses() {
        for( Bonus bonus : bonuses) {
            for( Player player: players){
                if( player.getView().getBoundsInParent().intersects(
                        bonus.getView().getBoundsInParent()
                )){
                    //Write bonus taken codes
                    bonus.setTaken(true);
                }
            }
            if( bonus.isTaken() && bonus instanceof LifeBonus) {
                mapPane.getChildren().remove(bonus.getView());
                objectHolder.remove(bonus);
                for( Player player: players) {
                    player.incrementHealth();
                }
            }
            else if( bonus.isTaken() && bonus instanceof SpeedBonus) {
                mapPane.getChildren().remove(bonus.getView());
                objectHolder.remove(bonus);
                for( Player player: players) {
                    player.incrementSpeed();
                }
            }
            else
                bonus.draw();
        }
        bonuses.removeIf(Bonus::isTaken);
    }

    public Player getPlayer(int index){
        try {
            return players[index];
        }catch (IndexOutOfBoundsException e){
            e.printStackTrace();
        }
        return null;
    }

    public Player[] getPlayers(){
        return players;
    }

    public Pane getMapPane() {
        return mapPane;
    }

    private void intToObject(){
        for(int i = 0; i < TILES; i++){
            for(int j = 0; j < TILES; j++) {
                int cordinate_x = i * MAP_DIMENSION;
                int cordinate_y = j * MAP_DIMENSION;
                Tile tile = new Tile(cordinate_x ,cordinate_y);
                mapPane.getChildren().addAll( tile.getView());
                tile.draw();
                if(obstaclesMap[i][j] == 0){
                    continue;
                }
                else {
                    if (obstaclesMap[i][j] == 1) {
                        Brick brick = new Brick(cordinate_x,cordinate_y, 0);
                        objectHolder.add( brick);
                        destructibles.add( brick);
                        brick.draw();
                    } else if (obstaclesMap[i][j] == 2) {
                        Bush bush = new Bush( cordinate_x, cordinate_y);
                        objectHolder.add( bush);
                        bush.draw();
                    } else if (obstaclesMap[i][j] == 3) {
                        IronWall ironWall = new IronWall( cordinate_x, cordinate_y);
                        objectHolder.add( ironWall);
                        ironWall.draw();
                    } else if (obstaclesMap[i][j] == 4) {
                        Water water = new Water(cordinate_x,cordinate_y);
                        objectHolder.add( water);
                        water.draw();
                    }
                    else if (obstaclesMap[i][j] == 5) {
                        Brick brick = new Brick(cordinate_x,cordinate_y, 1);
                        objectHolder.add( brick);
                        destructibles.add( brick);
                        brick.draw();
                    }
                    else if (obstaclesMap[i][j] == 6) {
                        Brick brick = new Brick(cordinate_x,cordinate_y, 2);
                        objectHolder.add( brick);
                        destructibles.add( brick);
                        brick.draw();
                    }
                    else if (obstaclesMap[i][j] == 7) { // Statue
                        Statue statue = new Statue(cordinate_x,cordinate_y);
                        objectHolder.add( statue);
                        destructibles.add( statue);
                        statue.draw();
                    }
                }
            }
        }
    }

    public void addObjects() {
        for (GameObject gameObject : objectHolder) {
            gameObject.draw();
            mapPane.getChildren().add(gameObject.getView());
        }
    }

    public void fire(Tank tank){
        Bullet fired = tank.fire();
        mapPane.getChildren().addAll(fired.getView());
        bullets.add(fired);
    }
    public int getLevel() {
        return level;
    }

    public void addObjects(GameObject[][] gameObjects){
        this.gameObjects = gameObjects;
    }
    public void updateObjects(){
        for(int i = 0; i < bullets.size(); i++){
            bullets.get(i).move();
        }
    }


    public void finishMap(){

    }

    public boolean tryNextMove( Tank tank, int dir){
        if( !checkBoundaries( tank))
            return false;
        ImageView tankView = tank.getView();
        for( GameObject gameObject : objectHolder){
            tankView.setVisible(true);
            if( tankView.getBoundsInParent().intersects( gameObject.getView().getBoundsInParent())){
                if( gameObject.isPassableByTanks()){
                    if(gameObject.isHideable()){
                        tankView.setVisible( false);
                        return true;
                    }
                }else{
                    if( gameObject == tank)
                        continue;
                    switch ( dir){
                        case 0:
                            tank.setxLoc( gameObject.getxLoc() - tankView.getFitWidth()-SHIFT);
                            break;
                        case 1:
                            tank.setxLoc(gameObject.getxLoc() + gameObject.getView().getFitWidth()+SHIFT);
                            break;
                        case 2:
                            tank.setyLoc( gameObject.getyLoc() - tankView.getFitHeight()-SHIFT);
                            break;
                        case 3:
                            tank.setyLoc( gameObject.getyLoc() + gameObject.getView().getFitHeight()  +SHIFT);
                            break;
                    }
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isMapFinished(){
        return (remainingBots == 0 && getAliveBots() == 0);
    }

    private boolean checkBoundaries( Tank tank) {
        if( tank.getxLoc() < FRAME_LOWER_BOUND){
            tank.setxLoc( FRAME_LOWER_BOUND);
            return false;
        }else if( tank.getxLoc() > FRAME_UPPER_BOUND - tank.getView().getFitWidth()) {
            tank.setxLoc(FRAME_UPPER_BOUND - tank.getView().getFitWidth());
            return false;
        }
        if( tank.getyLoc() < FRAME_LOWER_BOUND) {
            tank.setyLoc(FRAME_LOWER_BOUND);
            return false;
        }
        else if( tank.getyLoc() > FRAME_UPPER_BOUND - tank.getView().getFitHeight()) {
            tank.setyLoc(FRAME_UPPER_BOUND - tank.getView().getFitHeight());
            return false;
        }
        return true;
    }



    public boolean bonusTaken(Bonus bonus, Tank tank, int dir) {
        ImageView tankView = tank.getView();
        ImageView bonusView = bonus.getView();

        for( GameObject gameObject : objectHolder) {
            if( tankView.getBoundsInParent().intersects( bonusView.getBoundsInParent())) {
                bonusView.setVisible(false);
                bonus.setTaken(true);
            }
        }
        return true;
    }

    // getters and setters

    public int getRemainingBots() {
        return remainingBots;
    }


    public int getAliveBots(){ return bots.size(); }


    public ArrayList<GameObject> getGameObjects() {
        return objectHolder;
    }


    public ArrayList<Tank> getTanks() {
        return tanks;
    }


    public GameObject[][] getGameObjectsArray(){
        return gameObjects;
    }

    public ArrayList<Bot> getBots() {
        return bots;
    }

    public ArrayList<Bullet> getBullets() {
        return bullets;
    }
    public Stage getMapStage() {
        return mapStage;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void setPaused(boolean paused) {
        isPaused = paused;
    }
}