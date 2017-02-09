package com.havban.congklak;

import com.havban.congklak.models.Board;
import com.havban.congklak.models.Hole;
import com.havban.congklak.models.Player;
import com.havban.congklak.models.Position;
import com.havban.congklak.models.impl.DefaultBoard;
import com.havban.congklak.models.impl.DefaultPlayer;

import java.util.Map;
import java.util.Scanner;

/**
 * Created by hidayat.febiansyah on 2/3/17.
 */
public class Game implements Runnable{

    private static String ENV_BOARD_SIZE = "CONGKLAK_BOARD_SIZE";
    private static String ENV_SEED_PER_HOLE = "CONGKLAK_SEED_PER_HOLE";

    private static int DEFAULT_BOARD_SIZE = 7;
    private static int DEFAULT_SEED_PER_HOLE = 7;

    private boolean isRunning = true;
    private Scanner scanner;

    private Board board;

    private static final int P1_SEQ = 1;
    private static final int P2_SEQ = 2;
    private Player p1, p2;

    public Game(int boardSize, int seedPerHole){
        init(boardSize, seedPerHole);
    }

    public Game(){

        int boardSize = DEFAULT_BOARD_SIZE;
        int seedPerHole = DEFAULT_SEED_PER_HOLE;

        try {

            Map<String, String> env = System.getenv();

            if(env.containsKey(ENV_BOARD_SIZE)){
                try {
                    boardSize = Integer.parseInt(env.get(ENV_BOARD_SIZE));
                } catch (NumberFormatException e){
                    throw new Exception("Please check your \""+ENV_BOARD_SIZE+"\" env variable for valid integer");
                }
            }
            if(env.containsKey(ENV_SEED_PER_HOLE)){
                try {
                    seedPerHole = Integer.parseInt(env.get(ENV_SEED_PER_HOLE));
                } catch (NumberFormatException e){
                    throw new Exception("Please check your \""+ENV_SEED_PER_HOLE+"\" env variable for valid integer");
                }
            }

        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }

        init(boardSize, seedPerHole);

    }

    private void init(int boardSize, int seedPerHole){

        scanner = new Scanner(System.in);

        System.out.println(board);

        System.out.println("Welcome to congklak\nPlayer 1 first, then player 2\n");

        System.out.println("Please input Player 1's name: ");
        String player1Name = scanner.nextLine();

        System.out.println("Please input Player 2's name: ");
        String player2Name = scanner.nextLine();

        p1 = new DefaultPlayer(P1_SEQ, player1Name);
        p2 = new DefaultPlayer(P2_SEQ, player2Name);

        board = new DefaultBoard(boardSize, seedPerHole, p1, p2);

    }

    public void run(){

        int input;

        while(isRunning){
            printBoard();

            input = requestInput(p1, board);

            board.walk(p1, new Position(p1, input));

            input = requestInput(p2, board);

            board.walk(p2, new Position(p2, input));

        }
    }

    public void exit(){
        isRunning = false;
    }

    public boolean isRunning(){ return isRunning; }

    private int requestInput(Player p, Board board){
        String inputStr;
        int input;
        input = -1;
        while(input<0) {
            try {
                System.out.println(p+", please choose hole: ");
                inputStr = scanner.nextLine();
                input = Integer.parseInt(inputStr);

                Hole h = board.getHole(p, input);

                if(input<0 || input>board.getSize()
                        || ( h != null && h.getNumberOfSeed() < 1))
                    throw new NumberFormatException();

            } catch (NumberFormatException nfe){
                input = -1;
                System.err.println("Please input only valid hole number and not empty");
            }
        }

        return input;
    }

    private void printBoard(){
        System.out.println(board.toString());
    }
}