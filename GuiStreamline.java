/** Bijan Afghani
  * cs8bwahy
  * bafghani@ucsd.edu
  * GuiStreamline.java creates a scene and grid of shapes to represent
  * the game Streamline that we created in PSA 3
  **/
import javafx.scene.*;
import javafx.scene.shape.*;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.animation.*;
import javafx.animation.PathTransition.*;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.scene.Group;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.*;
import javafx.util.Duration;
/** This class designs a UI for the Streamline game we built in PSA 3
  * It creates a grid of shapes to represent obstacles and trails
  * as well as the goal and player.
  **/
public class GuiStreamline extends Application {
    static final double SCENE_WIDTH = 500;
    static final double SCENE_HEIGHT = 600;
    static final String TITLE = "CSE 8b Streamline GUI";
    static final String USAGE = 
        "Usage: \n" + 
        "> java GuiStreamline               - to start a game with default" +
            " size 6*5 and random obstacles\n" + 
        "> java GuiStreamline <filename>    - to start a game by reading g" +
            "ame state from the specified file\n" +
        "> java GuiStreamline <directory>   - to start a game by reading a" +
            "ll game states from files in\n" +
        "                                     the specified directory and " +
            "playing them in order\n";

    static final Color TRAIL_COLOR = Color.PALEVIOLETRED;
    static final Color GOAL_COLOR = Color.MEDIUMAQUAMARINE;
    static final Color OBSTACLE_COLOR = Color.DIMGRAY;

    // Trail radius will be set to this fraction of the size of a board square.
    static final double TRAIL_RADIUS_FRACTION = 0.1;

    // Squares will be resized to this fraction of the size of a board square.
    static final double SQUARE_FRACTION = 0.8;
    
    Scene mainScene;
    Group levelGroup;                   // For obstacles and trails
    Group rootGroup;                    // Parent group for everything else
    Player playerRect;                  // GUI representation of the player
    RoundedSquare goalRect;             // GUI representation of the goal

    Shape[][] grid;                     // Same dimensions as the game board
    
    Streamline game;                    // The current level
    ArrayList<Streamline> nextGames;    // Future levels

    MyKeyHandler myKeyHandler;          // for keyboard input

    // returns the width of the board for the current level
    public int getBoardWidth() { 
        return game.currentState.board[0].length;
    }

    // the height of the board for the current level
    public int getBoardHeight() {
        return game.currentState.board.length;
    }
    
    // Find a size for a single square of the board that will fit nicely
    // in the current scene size.
    public double getSquareSize() {
        /* For example, given a scene size of 1000 by 600 and a board size
           of 5 by 6, we have room for each square to be 200x100. Since we
           want squares not rectangles, return the minimum which is 100 
           in this example. */
        
        if ((SCENE_HEIGHT / getBoardHeight()) 
                > (SCENE_WIDTH / getBoardWidth())) {
            return (double) SCENE_WIDTH / getBoardWidth();
        }
        return (double) SCENE_HEIGHT / getBoardHeight();   
    }
    
    // Destroys and recreates grid and all trail and obstacle shapes.
    // Assumes the dimensions of the board may have changed.
    public void resetGrid() {
        rootGroup.getChildren().remove(levelGroup);
        levelGroup = new Group();
        rootGroup.getChildren().add(levelGroup);


        grid = new Shape[getBoardHeight()][getBoardWidth()];

        RoundedSquare Obstacle;

        Circle Trail;
        

            for (int i = 0; i<grid.length ; i++) {
                for (int j = 0; j<grid[i].length ; j++) {
                    //adds rounded squares for obstacles
                    if (game.currentState.board[i][j] == GameState.OBSTACLE_CHAR) {
                        Obstacle = new RoundedSquare(0, 0,
                            SQUARE_FRACTION*getSquareSize());
                        double[] obstaclePos = boardIdxToScenePos(
                            j, i);
                        Obstacle.setCenterX(obstaclePos[0]);
                        Obstacle.setCenterY(obstaclePos[1]);
                        Obstacle.setFill(OBSTACLE_COLOR);
                        grid[i][j] = Obstacle;
                        levelGroup.getChildren().add(grid[i][j]);
                    }
                    //adds transparent circles for trail in every free space
                    //Their color will be changed once they become trail chars
                    //in updateTrailColor
                    if (game.currentState.board[i][j] == GameState.SPACE_CHAR) {
                            Trail = new Circle(0, 0,
                                TRAIL_RADIUS_FRACTION*getSquareSize(), Color.TRANSPARENT);
                            double[] trailPos = boardIdxToScenePos(
                            j, i);
                            Trail.setCenterX(trailPos[0]);
                            Trail.setCenterY(trailPos[1]);
                            grid[i][j] = Trail;
                            levelGroup.getChildren().add(grid[i][j]);
                    }
                }
            }
        }
      
    

    // Sets the fill color of all trail Circles making them visible or not
    // depending on if that board position equals TRAIL_CHAR.
    public void updateTrailColors() {

        for (int i = 0 ; i < game.currentState.board.length ; i++ ) {
            for (int j = 0 ; j < game.currentState.board[i].length ; j++ ) {
                //fills in circles for trails
                if (game.currentState.board[i][j] == GameState.TRAIL_CHAR) {
                    grid[i][j].setFill(TRAIL_COLOR);
                }
                else {
                    //makes circles transparent for spaces
                    if (game.currentState.board[i][j] == GameState.SPACE_CHAR) {
                    grid[i][j].setFill(Color.TRANSPARENT);
                    }
                }
            }
        }
    }
    
    /** 
     * Coverts the given board column and row into scene coordinates.
     * Gives the center of the corresponding tile.
     * 
     * @param boardCol a board column to be converted to a scene x
     * @param boardRow a board row to be converted to a scene y
     * @return scene coordinates as length 2 array where index 0 is x
     */
    static final double MIDDLE_OFFSET = 0.5;
    public double[] boardIdxToScenePos (int boardCol, int boardRow) {
        double sceneX = ((boardCol + MIDDLE_OFFSET) * 
            (mainScene.getWidth() - 1)) / getBoardWidth();
        double sceneY = ((boardRow + MIDDLE_OFFSET) * 
            (mainScene.getHeight() - 1)) / getBoardHeight();
        return new double[]{sceneX, sceneY};
    }

    /** Makes trail markers visible and changes player position.
      * To be called when the user moved the player and the GUI needs to be 
      * updated to show the new position.
      * Parameters are the old position, new position, and whether it was an
      * undo movement.
      *
      * @param fromCol the initial playerCol before movement
      * @param fromRow the intial playerRow before movement
      * @param toCol the new playerCol after movement
      * @param toRow the new playerRow after movement
      * @param isUndo true if user input is u, false otherwise
      */
    public void onPlayerMoved(int fromCol, int fromRow, int toCol, int toRow, 
        boolean isUndo)
    {
        // If the position is the same, just return
        if (fromCol != toCol || fromRow != toRow) {

            double[] playerPos = boardIdxToScenePos(toCol, toRow);
            double[] rectPos = boardIdxToScenePos(fromCol, fromRow);
             
            //creates new path 
            Path path = new Path();
            path.getElements().add(new MoveTo(rectPos[0], rectPos[1]));
            path.getElements().add(new LineTo(playerPos[0], playerPos[1]));

            //creates path animation
            PathTransition pathTransition = new PathTransition();
            pathTransition.setDuration(Duration.millis(100));
            pathTransition.setNode(playerRect);
            pathTransition.setPath(path);

            //runs animation
            pathTransition.play();

            int moveCDir = toCol-fromCol < 0 ? -1 : 1;
            int moveRDir = toRow-fromRow < 0 ? -1 : 1;
            if(toCol == fromCol) moveCDir = 0;
            if(toRow == fromRow) moveRDir = 0;

            Shape obstacle = null;
            double obstPos[] = null;

            if(toCol == fromCol && toCol + moveCDir < 
                getBoardWidth() && toCol + moveCDir > 0) {
                obstacle = grid[toRow][toCol + moveCDir];
                obstPos = boardIdxToScenePos(toCol + moveCDir, toRow);
            }
            if(toRow != fromRow && toRow + moveRDir <
             getBoardHeight() && toRow + moveRDir > 0) {
                obstacle = grid[toRow + moveRDir][toCol];
                obstPos = boardIdxToScenePos(toCol, toRow + moveRDir);
            }

            if(obstacle != null && obstPos != null) {
                path = new Path();
                path.getElements().add(new MoveTo(obstPos[0], obstPos[1]));
                path.getElements().add(new LineTo(obstPos[0] + (moveCDir * 10),
                 obstPos[1] + (moveRDir * 10)));

                pathTransition = new PathTransition();
                pathTransition.setDuration(Duration.millis(100));
                pathTransition.setNode(obstacle);
                pathTransition.setPath(path);
                pathTransition.setAutoReverse(true);
                pathTransition.setCycleCount(2);

                pathTransition.play();
            }
            
            //updates trail and player position
            //playerRect.setCenterX(playerPos[0]);
            //playerRect.setCenterY(playerPos[1]);
            updateTrailColors();
            //checks if game is over
            if (toRow == game.currentState.goalRow 
                && toCol == game.currentState.goalCol) {
                onLevelFinished();
            }

        }

    }
    
    /** To be called when a key is pressed
      * calls onPlayerMoved if key pressed is a player movement
      *
      * @param keyCode the keyCode of the KeyEvent passed by the handler.
      * The KeyCode represents the key on the keyboard that has been pressed.
      */
    void handleKeyCode(KeyCode keyCode) {

        int fCol;
        int tCol;
        int fRow;
        int tRow;

        switch (keyCode) {
            //if keyCode is UP
            case UP :
                fCol = game.currentState.playerCol;
                fRow = game.currentState.playerRow;
                game.recordAndMove(Direction.UP);
                tCol = game.currentState.playerCol;
                tRow = game.currentState.playerRow;
                onPlayerMoved(fCol, fRow, tCol, tRow, false);
                break;
            //if keyCode is DOWN
            case DOWN :
                fCol = game.currentState.playerCol;
                fRow = game.currentState.playerRow;
                game.recordAndMove(Direction.DOWN);
                tCol = game.currentState.playerCol;
                tRow = game.currentState.playerRow;
                onPlayerMoved(fCol, fRow, tCol, tRow, false);
                break;
            //if keyCode is LEFT
            case LEFT :
                fCol = game.currentState.playerCol;
                fRow = game.currentState.playerRow;
                game.recordAndMove(Direction.LEFT);
                tCol = game.currentState.playerCol;
                tRow = game.currentState.playerRow;
                onPlayerMoved(fCol, fRow, tCol, tRow, false);
                break;
            //if keyCode is RIGHT
            case RIGHT :
                fCol = game.currentState.playerCol;
                fRow = game.currentState.playerRow;
                game.recordAndMove(Direction.RIGHT);
                tCol = game.currentState.playerCol;
                tRow = game.currentState.playerRow;
                onPlayerMoved(fCol, fRow, tCol, tRow, false);
                break;
            //if u, undo
            case U :
                fCol = game.currentState.playerCol;
                fRow = game.currentState.playerRow;
                game.undo();
                tCol = game.currentState.playerCol;
                tRow = game.currentState.playerRow;
                onPlayerMoved(fCol, fRow, tCol, tRow, true);
                break;
            //if o, save and quit
            case O :
                game.saveToFile();
                System.exit(0);
            //if q, quit
            case Q :
                System.exit(0);
            //else
            default:
                System.out.println("Possible commands:\n w - up\n " + 
                    "a - left\n s - down\n d - right\n u - undo\n " + 
                    "q - quit level");

                break;
        }
    }

    /** This nested class handles keyboard input and calls handleKeyCode()
      * @param e the KeyEvent, a keyboard key that has been pressed
      * This KeyEvent has a keyCode that will be passed to handleKeyCode()
      */
    class MyKeyHandler implements EventHandler<KeyEvent> {
        @Override
        public void handle(KeyEvent e) {
                handleKeyCode(e.getCode());
        }
    }

    // To be called whenever the UI needs to be completely redone to reflect
    // a new level
    //recreates grid with obstacles and trails and
    //resets goal and player position
    public void onLevelLoaded() {
        //recreates grid with obstacles and trails
        resetGrid();
        //sets new square size
        double squareSize = getSquareSize() * SQUARE_FRACTION;

        // Update the player position
        double[] playerPos = boardIdxToScenePos(
            game.currentState.playerCol, game.currentState.playerRow);
        playerRect.setSize(squareSize);
        playerRect.setCenterX(playerPos[0]);
        playerRect.setCenterY(playerPos[1]);
        //updates goal position
        double[] goalPos = boardIdxToScenePos(
            game.currentState.goalCol, game.currentState.goalRow);
        goalRect.setSize(squareSize);
        goalRect.setCenterX(goalPos[0]);
        goalRect.setCenterY(goalPos[1]);
    }

    // Called when the player reaches the goal. Shows the winning animation
    // and loads the next level if there is one.
    static final double SCALE_TIME = 175;  // milliseconds for scale animation
    static final double FADE_TIME = 250;   // milliseconds for fade animation
    static final double DOUBLE_MULTIPLIER = 2;
    public void onLevelFinished() {
        // Clone the goal rectangle and scale it up until it covers the screen

        // Clone the goal rectangle
        Rectangle animatedGoal = new Rectangle(
            goalRect.getX(),
            goalRect.getY(),
            goalRect.getWidth(),
            goalRect.getHeight()
        );
        animatedGoal.setFill(goalRect.getFill());

        // Add the clone to the scene
        List<Node> children = rootGroup.getChildren();
        children.add(children.indexOf(goalRect), animatedGoal);

        // Create the scale animation
        ScaleTransition st = new ScaleTransition(
            Duration.millis(SCALE_TIME), animatedGoal
        );
        st.setInterpolator(Interpolator.EASE_IN);
        
        // Scale enough to eventually cover the entire scene
        st.setByX(DOUBLE_MULTIPLIER * 
            mainScene.getWidth() / animatedGoal.getWidth());
        st.setByY(DOUBLE_MULTIPLIER * 
            mainScene.getHeight() / animatedGoal.getHeight());

        /*
         * This will be called after the scale animation finishes.
         * If there is no next level, quit. Otherwise switch to it and
         * fade out the animated cloned goal to reveal the new level.
         */
        st.setOnFinished(e1 -> {

            /* checks if there is no next game and if so, quit */
            if (nextGames.size() == 0) {
                System.exit(0);
            }
            /* updates the instances variables game and nextGames 
            to switch to the next level */
            else {
                game = nextGames.get(0);
                nextGames.remove(0);
            }

            // Update UI to the next level, but it won't be visible yet
            // because it's covered by the animated cloned goal
            onLevelLoaded();

            /*  uses a FadeTransition on animatedGoal, with FADE_TIME as
                the duration. Uses setOnFinished() to schedule code to
                run after this animation is finished. When the animation
                finishes, remove animatedGoal from rootGroup. */
            FadeTransition ft = new 
                FadeTransition(Duration.millis(FADE_TIME), animatedGoal);
            ft.setFromValue(1.0);
            ft.setToValue(0.3);
            ft.setOnFinished(e2 -> {
                rootGroup.getChildren().remove(animatedGoal);
            });
        });
        
        // Start the scale animation
        st.play();
    }

    /** 
     * Performs file IO to populate game and nextGames using filenames from
     * command line arguments.
     */
    public void loadLevels() {
        game = null;
        nextGames = new ArrayList<Streamline>();
        
        List<String> args = getParameters().getRaw();
        if (args.size() == 0) {
            System.out.println("Starting a default-sized random game...");
            game = new Streamline();
            return;
        }

        // at this point args.length == 1
        
        File file = new File(args.get(0));
        if (!file.exists()) {
            System.out.printf("File %s does not exist. Exiting...", 
                args.get(0));
            return;
        }

        // if is not a directory, read from the file and start the game
        if (!file.isDirectory()) {
            System.out.printf("Loading single game from file %s...\n", 
                args.get(0));
            game = new Streamline(args.get(0));
            return;
        }

        // file is a directory, walk the directory and load from all files
        File[] subfiles = file.listFiles();
        Arrays.sort(subfiles);
        for (int i=0; i<subfiles.length; i++) {
            File subfile = subfiles[i];
            
            // in case there's a directory in there, skip
            if (subfile.isDirectory()) continue;

            // assume all files are properly formatted games, 
            // create a new game for each file, and add it to nextGames
            System.out.printf("Loading game %d/%d from file %s...\n",
                i+1, subfiles.length, subfile.toString());
            nextGames.add(new Streamline(subfile.toString()));
        }

        // Switch to the first level
        game = nextGames.get(0);
        nextGames.remove(0);
    }
    
    /**
     * The main entry point for all JavaFX Applications
     * Initializes instance variables, creates the scene, and sets up the UI
     * @param primaryStage the stage that will be displayed on the scene
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Populate game and nextGames
        loadLevels();

        // Initialize the scene and our groups
        rootGroup = new Group();
        mainScene = new Scene(rootGroup, SCENE_WIDTH, SCENE_HEIGHT, 
            Color.GAINSBORO);
        levelGroup = new Group();
        rootGroup.getChildren().add(levelGroup);
        
        //initialize goalRect and playerRect, add them to rootGroup,
        //call onLevelLoaded(), and set up keyboard input handling

        goalRect = new RoundedSquare( 0, 0, getSquareSize()*SQUARE_FRACTION);
        goalRect.setFill(GOAL_COLOR);
        rootGroup.getChildren().add(goalRect);

        playerRect = new Player();
        playerRect.setSize(getSquareSize()*SQUARE_FRACTION);
        rootGroup.getChildren().add(playerRect);

        onLevelLoaded();

        //keyboard input handling
        //creates new handler,myKeyHandler,
        //and delegates key pressed to be
        //handled by myKeyHandler
        myKeyHandler = new MyKeyHandler();
        mainScene.setOnKeyPressed(myKeyHandler);

        
        // Make the scene visible
        primaryStage.setTitle(TITLE);
        primaryStage.setScene(mainScene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    /** 
     * Execution begins here, but at this point we don't have a UI yet
     * The only thing to do is call launch() which will eventually result in
     * start() above being called.
     */
    public static void main(String[] args) {
        if (args.length != 0 && args.length != 1) {
            System.out.print(USAGE);
            return;
        }

        launch(args);
    }
}

