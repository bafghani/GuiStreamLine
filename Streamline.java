/** Bijan Afghani
  * cs8bwahy
  * This file implements the methods written in GameState
  * in order to run the Streamline game
  * It runs a loop and relies on helper methods
  * in order to create an instance of the game
  */

import java.util.*;
import java.io.*;

/** This class implements the methods within GameState
  * The methods are called in order to create an instance
  * of the streamline game
  */
public class Streamline {

    final static int DEFAULT_HEIGHT = 6;
    final static int DEFAULT_WIDTH = 5;

    final static String OUTFILE_NAME = "saved_streamline_game";

    GameState currentState;
    List<GameState> previousStates;
    //This method creates a new gameboard called currentState and initializes a list
    //to store all previous boards
    public Streamline() {
        int playerRow = DEFAULT_HEIGHT-1;
        int playerCol = 0;
        int goalRow = 0;
        int goalCol = DEFAULT_WIDTH-1;
        this.currentState = new GameState(DEFAULT_HEIGHT,DEFAULT_WIDTH,
                                playerRow,playerCol,goalRow,goalCol);
        currentState.addRandomObstacles(3);
        this.previousStates = new ArrayList();
    }
    //This method checks to see if a load file exists
    //@param: takes in a filename to check if that file exists
    public Streamline(String filename) {
        try {
            loadFromFile(filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //this file loads data from a saved game and initializes a game board
    //with the stored gamestate values from the load file
    //@param: filename - loads that file
    protected void loadFromFile(String filename) throws IOException {
        this.previousStates = new ArrayList();
        FileReader fileReader = new FileReader(filename);
        Scanner reader = new Scanner(fileReader);
        int height;
        int width;
        int playerRow;
        int playerCol;
        int goalRow;
        int goalCol;
        height = reader.nextInt();
        width = reader.nextInt();
        reader.nextLine();
        playerRow = reader.nextInt();
        playerCol = reader.nextInt();
        reader.nextLine();
        goalRow = reader.nextInt();
        goalCol = reader.nextInt();
        if (playerRow == goalRow && playerCol == goalCol) return;
        this.currentState = new GameState(height, width,
                                 playerRow, playerCol, goalRow, goalCol);
        String row;
        for (int i=0 ; i<height+1 ; i++) {
            row = reader.nextLine();
            for (int j=0 ; j<row.length() ; j++) {
                this.currentState.board[i-1][j] = row.charAt(j);
            }
        }
        reader.close();
    }
    //This method saves the current state to the previous states list 
    //It then calls the correct method from GameState to move the player
    //@param: direction - the direction the player wants to move
    void recordAndMove(Direction direction) {
        GameState copy = new GameState(this.currentState);
        if (this.previousStates.size() != 0) {
        if (copy.equals(this.previousStates.get(this.previousStates.size()-1)) == false) {
        this.previousStates.add(copy);
        }
        }
        if (this.previousStates.size() == 0) {
            this.previousStates.add(copy);
        }
        if (direction == null) return;
        if (direction == Direction.RIGHT) this.currentState.move(Direction.RIGHT);
        if (direction == Direction.UP) this.currentState.move(Direction.UP);
        if (direction == Direction.LEFT) this.currentState.move(Direction.LEFT);
        if (direction == Direction.DOWN) this.currentState.move(Direction.DOWN);
    } 
    //loads the most recent board from previousStates if there is one
    //deletes the loaded board from previousStates
    void undo() {
        if (previousStates == null) return;
        if (previousStates.size() == 0) return;
        currentState = previousStates.get(previousStates.size()-1);
        previousStates.remove(previousStates.size()-1);
    }
    //This method loops until level passed,
    //allowing the player to input their movements
    //It outputs the toString from GameState,
    //displaying the board, and allows the user
    //to input their next move.
    void play() {
        Scanner reader = new Scanner(System.in);
        String input;
        boolean levelPassed = false;
        while (levelPassed == false) {
            System.out.println(currentState.toString());
            System.out.print("> ");
            input = reader.next();
            if (input.equals("w")) recordAndMove(Direction.UP);
            else if (input.equals("a")) recordAndMove(Direction.LEFT);
            else if (input.equals("s")) recordAndMove(Direction.DOWN);
            else if (input.equals("d")) recordAndMove(Direction.RIGHT);
            else if (input.equals("u")) undo();
            else if (input.equals("o")) saveToFile();
            else if (input.equals("q")) break;
            else {
                System.out.println("Input w,a,s,d,u,o, or q");
                System.out.print("> ");
                input = reader.next();
            }
            if ((currentState.playerRow == currentState.goalRow)
                && (currentState.playerCol == currentState.goalCol)) {
                levelPassed = true;
            }
        }
        if (levelPassed == true) {
        System.out.println(currentState.toString());
        System.out.println( "Level Passed!");
        }
    }
    //This method saves the currentState board into a file
    //with the correct formatting in order to be reloaded later
    void saveToFile() {
        try {
            FileWriter fileWriter = new FileWriter(OUTFILE_NAME);
            PrintWriter writer = new PrintWriter(fileWriter);
            char[][] board = this.currentState.board;
            if (board == null) return;
            int height = board.length;
            int width = board[0].length;
            int playerRow = this.currentState.playerRow;
            int playerCol = this.currentState.playerCol;
            int goalRow = this.currentState.goalRow;
            int goalCol = this.currentState.goalCol;
            writer.print((int) height);
            writer.print(" ");
            writer.print( (int) width);
            writer.print("\n");
            writer.print( (int) playerRow);
            writer.print(" ");
            writer.print( (int) playerCol);
            writer.print("\n");
            writer.print( (int) goalRow);
            writer.print(" ");
            writer.print( (int) goalCol);
            writer.print("\n");
            for (int i = 0 ; i<height ; i++) {
                for (int j = 0 ; j<width; j++) {
                 writer.print( (char) currentState.board[i][j]);
                }
                writer.print("\n");
            }
            writer.close();
            System.out.println
            ("Saved current state to: " + OUTFILE_NAME);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
