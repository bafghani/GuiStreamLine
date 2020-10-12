/** Bijan Afghani
 *  cs8bwahy
 *  This file represents the gameboard for the game Streamline.
 *  It contains methods and constructors that manipulate a 2D array
 *  that represents the gameboard
 */

import java.util.*;

/** This class populates a 2D array that will be used as a gameboard
  * The 2D array will be manipulated by the methods within this class
  * in order to interpret the game Streamline */
public class GameState {

    // Used to populate char[][] board below and to display the
    // current state of play.
    final static char TRAIL_CHAR = '.';
    final static char OBSTACLE_CHAR = 'X';
    final static char SPACE_CHAR = ' ';
    final static char CURRENT_CHAR = 'O';
    final static char GOAL_CHAR = '@';
    final static char NEWLINE_CHAR = '\n';

    // This represents a 2D map of the board
    char[][] board;

    // Location of the player
    int playerRow;
    int playerCol;

    // Location of the goal
    int goalRow;
    int goalCol;

    // true means the player completed this level
    boolean levelPassed;

    // initialize a board of given parameters, fill the board with SPACE_CHAR
    // set corresponding fields to parameters.
    //@param: The dimensions of the board and location of player and goal
    public GameState(int height, int width, int playerRow, int playerCol, 
            int goalRow, int goalCol) {
        board=new char[height][width];
        for (int i=0;i<board.length;i++) {
            for (int j=0;j<board[i].length;j++) {
                board[i][j]=SPACE_CHAR;
            }
        }
        this.playerRow=playerRow;
        this.playerCol=playerCol;
        this.goalRow=goalRow;
        this.goalCol=goalCol;

    }

    // copy constructor
    //@param: the GameState being copied
    public GameState(GameState other) {

        playerRow = other.playerRow;
        playerCol = other.playerCol;
        goalRow = other.goalRow;
        goalCol = other.goalCol;

        char[][] boardcopy=other.board;
        if (this.levelPassed==true) other.levelPassed=true;
        else other.levelPassed = false;
        int height = boardcopy.length;
        int width = boardcopy[0].length;
        board = new char[height][width];

        for (int i=0;i<this.board.length;i++) {
            for (int j=0;j<this.board[i].length;j++) {
                board[i][j]=boardcopy[i][j];            }
        }
    }
    // add count random blocks into this.board
    // avoiding player position and goal position
    //@param: count - the number of obstacles to be added
    void addRandomObstacles(int count) {
        //edge cases
        int max = 0;
        for (int i = 0 ; i<board.length ; i++) {
            for (int j = 0 ; j<board[0].length ; j++) {
                if (board[i][j] == SPACE_CHAR) max++;
            }
        }
        if (count>max) return;
        if (count<=0) return;
        int row=0;
        int col=0;
        int k = 0;
        boolean hasObject=false;
        Random rand1 = new Random();
        Random rand2 = new Random();
        //loops until there are count number of obstacles
        while (k < count) {
            row = rand1.nextInt((this.board.length));
            col = rand2.nextInt((this.board[0].length));
            //player/goal checker
            if (row == this.playerRow && col == this.playerCol) hasObject = true;
            if (row == this.goalRow && col == this.goalCol) hasObject = true;
            //adds obstacle if possible
            if (hasObject == false && this.board[row][col] == SPACE_CHAR) {
                this.board[row][col] = OBSTACLE_CHAR;
                k++;
            }
            //if current attempt to add obstacle fails,
            //iterate through the loop again
            if (hasObject == true) {
                hasObject = false;
            }
        }
    }
    // rotate clockwise once
    // rotation should account for all instance var including board, current 
    // position, goal position 
    void rotateClockwise() {
        //rotates board
        char[][] rotatedBoard= new char[board[0].length][board.length];
        int newplayerRow;
        int newplayerCol;
        int newgoalRow;
        int newgoalCol;
        for (int i=0;i<rotatedBoard.length;i++) {
            for (int j=0;j<rotatedBoard[i].length;j++) {
                rotatedBoard[i][j]=this.board[board.length-1-j][i];
            }
        }
        board=rotatedBoard;
        //rotates current position and goal position
        newplayerRow=playerCol;
        newplayerCol=rotatedBoard[0].length-1-playerRow;
        newgoalRow=goalCol;
        newgoalCol=rotatedBoard[0].length-1-goalRow;
        this.playerRow=newplayerRow;
        this.playerCol=newplayerCol;
        this.goalRow=newgoalRow;
        this.goalCol=newgoalCol;



    }


    // move current position towards right until stopped by obstacle / edge
    // leave a trail of dots for all positions that we're walked through
    // before stopping
    void moveRight() {
        
        
        for (int j=playerCol;j<board[0].length-1;j++) {
            if (board[playerRow][j+1]==OBSTACLE_CHAR) {
                return;
            }
            else if (board[playerRow][j+1]==SPACE_CHAR) {
                board[playerRow][j]=TRAIL_CHAR;
                playerCol++;
            }
            else if (board[playerRow][j+1]==TRAIL_CHAR) {
                return;
            }
    if (playerRow==goalRow && playerCol==goalCol) {
        levelPassed = true;
        return;

}
}
}

    // move towards any direction given
    // accomplish this by rotating, move right, rotating back
    //@param: direction - the direction the player wants to move
    void move(Direction direction) {
        // down = rotate once , move right, rotate back
        // left= rotate twice, move right, rotate back
        // up = rotate three times, move right, rotate back
        if (direction==Direction.RIGHT) {
            moveRight();
        }
        if (direction==Direction.UP) {
            rotateClockwise();
            moveRight();
            rotateClockwise();
            rotateClockwise();
            rotateClockwise();
        }
        if (direction==Direction.LEFT) {
            rotateClockwise();
            rotateClockwise();
            moveRight();
            rotateClockwise();
            rotateClockwise();
        }
        if (direction==Direction.DOWN) {
            rotateClockwise();
            rotateClockwise();
            rotateClockwise();
            moveRight();
            rotateClockwise();
        }
        else return;
    }


    @Override
        // compare two game state objects, returns true if all fields match
        //@ param: other - the GameState object being compared to the current board
        public boolean equals(Object other) {

            // check for any conditions that should return false
            char[][] compareBoard;
            if (other == null) return false;
            if (this.board == null) return false;
            if (board == null) return false;
            //checks if other is a gamestate object and casts it as such
            if (other instanceof GameState) {
                GameState toCompare = (GameState) other;
                compareBoard = toCompare.board;
                if (toCompare.board == null) return false;
                if (toCompare.board.length!=this.board.length) return false;
                if (toCompare.board[0].length!=this.board[0].length) {
                return false;
                }

                //Compares player and goal positions
                if (toCompare.playerRow!=this.playerRow) return false;
                if (toCompare.playerCol!=this.playerCol) return false;
                if (toCompare.goalRow!=this.goalRow) return false;
                if (toCompare.goalCol!=this.goalCol) return false;
                if (toCompare.levelPassed!=this.levelPassed) return false;
            }
            else return false;
            //compares indexes of 2D arrays
            for (int i=0;i<board.length;i++){
                for (int j=0; j<board[i].length;j++) {
                    if (this.board[i][j]!=compareBoard[i][j]){
                        return false;
                    }
                }
            }

            // We have exhausted all possibility of mismatch, they're identical
            return true;
        }


    @Override
    //outputs 2D array with the corresponding characters
    //@return: String - the current GameBoard
        public String toString() {
            String currentBoard="";
            for (int i=0;i<((board[0].length+2)*2)-1;i++) {
                currentBoard+='-';
            }
            currentBoard+= (char) NEWLINE_CHAR;


            for (int i=0;i<board.length;i++) {
                currentBoard+='|';
                currentBoard+=(char) SPACE_CHAR;
                char[] nextChar = { SPACE_CHAR, SPACE_CHAR };
                
                for (int j = 0;j<board[0].length;j++) {
                    if(i == playerRow && j == playerCol) {
                        
                        nextChar[0] = CURRENT_CHAR;
                    
                    } else if(i == goalRow && j == goalCol) {
                        nextChar[0] = GOAL_CHAR;
                    } else {
                        nextChar[0] = board[i][j];
                    }

                    currentBoard+=String.valueOf(nextChar);
                }

                currentBoard+='|';
                currentBoard+=(char)NEWLINE_CHAR;

            }
            for (int i=0;i<((board[0].length+2)*2)-1;i++) {
                currentBoard+='-';
            }
            currentBoard+=NEWLINE_CHAR;
            return currentBoard;
        }
        //public static void main (String[] args) {
        //GameState test=new GameState(6,5,5,0,0,4);
        //System.out.println(test.toString());
        //test.move(Direction.RIGHT);
        //System.out.println("RIGHT");
        //System.out.println(test.toString());
        //test.move(Direction.DOWN);
        //System.out.println("DOWN");
        //System.out.println(test.toString());
        //test.move(Direction.UP);
        //System.out.println("UP");
        //System.out.println(test.toString());
        //test.move(Direction.LEFT);
        //System.out.println("LEFT");
        //System.out.println(test.toString());
        //test.move(Direction.DOWN);
        //System.out.println("DOWN");
        //System.out.println(test.toString());
        //}

        }

