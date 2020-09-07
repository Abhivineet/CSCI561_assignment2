
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.*;

class Utilities {

  public boolean boardChecker(int[][] a, int[][] b) {
    for (int i = 0; i < 5; i++) {
      for (int j = 0; j < 5; j++) {
        if (a[i][j] != b[i][j]) {
          return false;
        }
      }
    }
    return true;
  }

  ArrayList<int[]> validRegion(int[] loc) {
    ArrayList<int[]> valid;
    valid = validPoints(loc);
    if (this.validFinder(new int[]{loc[0] + 1, loc[1] + 1})) {
      valid.add(new int[]{loc[0] + 1, loc[1] + 1});
    }
    if (this.validFinder(new int[]{loc[0] - 1, loc[1] - 1})) {
      valid.add(new int[]{loc[0] - 1, loc[1] - 1});
    }
    if (this.validFinder(new int[]{loc[0] - 1, loc[1] + 1})) {
      valid.add(new int[]{loc[0] - 1, loc[1] + 1});
    }
    if (this.validFinder(new int[]{loc[0] + 1, loc[1] - 1})) {
      valid.add(new int[]{loc[0] + 1, loc[1] - 1});
    }
    return valid;
  }

  boolean validFinder(int[] loc) {
    return loc[0] >= 0 && loc[0] < 5 && loc[1] >= 0 && loc[1] < 5;
  }

  ArrayList<int[]> validPoints(int[] loc) {
    ArrayList<int[]> valid = new ArrayList<int[]>();
    if (this.validFinder(new int[]{loc[0] + 1, loc[1]})) {
      valid.add(new int[]{loc[0] + 1, loc[1]});
    }
    if (this.validFinder(new int[]{loc[0] - 1, loc[1]})) {
      valid.add(new int[]{loc[0] - 1, loc[1]});
    }
    if (this.validFinder(new int[]{loc[0], loc[1] + 1})) {
      valid.add(new int[]{loc[0], loc[1] + 1});
    }
    if (this.validFinder(new int[]{loc[0], loc[1] - 1})) {
      valid.add(new int[]{loc[0], loc[1] - 1});
    }
    return valid;
  }

  int callOtherPlayer(int player) {
    return (player % 2) + 1;
  }

  int[] convertToArray(String str) {
    String[] items = str.replaceAll("\\[", "").replaceAll("]", "").replaceAll("\\s", "")
        .split(",");
    int[] results = new int[items.length];

    for (int i = 0; i < items.length; i++) {
      results[i] = Integer.parseInt(items[i]);
    }
    return results;
  }


}

public class my_player {

  Utilities utils = new Utilities();
  public int play;
  public int[][] prevBoard;
  public int[][] currentBoard;
  int helperInt;


  my_player() throws IOException {
    prevBoard = new int[5][5];
    currentBoard = new int[5][5];
    readInput("input.txt");
    helperReader();
  }

  public void helperReader() throws IOException {
    File helperFile = new File("helper.txt");
    int[][] initBoard = {{0, 0, 0, 0, 0}, {0, 0, 0, 0, 0}, {0, 0, 0, 0, 0}, {0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0}};
    helperFile.createNewFile();
    if (utils.boardChecker(initBoard, this.prevBoard)) {
      FileWriter fileWriter = new FileWriter(helperFile, false);
      if (this.play == 1) {
        helperInt = 2;
      } else {
        helperInt = 3;
      }
      fileWriter.write(Integer.toString(helperInt));
      fileWriter.close();
    } else {
      BufferedReader bufferedReader = new BufferedReader(new FileReader(helperFile));
      int tempHelperInt = Integer.parseInt(bufferedReader.readLine());
      helperInt = tempHelperInt + 2;
      FileWriter fileWriter = new FileWriter(helperFile, false);
      fileWriter.write(Integer.toString(helperInt));
      fileWriter.close();
    }
    helperInt--;
  }

  public Move alphaBetaSearch(State state, int maxDepth, Move initialState) {
    int beta = Integer.MAX_VALUE;
    int alpha = Integer.MIN_VALUE;
    int terminal = -1;
    //Move initialState = new Move(null, state.board, this.play, true, 0);
    int v = maxValue(state, alpha, beta, maxDepth, terminal, null, initialState);
    return state.selectedMove;
  }

  public int maxValue(State state, int alpha, int beta, int maxDepth, int terminal,
      Move aMove, Move initialState) {
    if ((state.depth == maxDepth) || (terminal == 1)) {
      return state.getUtility(this.play, aMove, initialState);
    }
    int count = 0;
    int moveLocation = 0;
    int v = Integer.MIN_VALUE;
    for (Move move : state.moves) {
      int temp = v;
      state.doMove(move);
      if (move.pass) {
        terminal++;
      }
      int min = minValue(state, alpha, beta, maxDepth, terminal, move, initialState);
      v = Math.max(v, min);
      state.undoMove(move);
      if (move.pass) {
        terminal--;
      }
      if (v > temp) {
        moveLocation = count;
      }
      if (v >= beta) {
        state.selectedMove = move;
        return v;
      }
      alpha = Math.max(alpha, v);
      count++;
    }
    state.selectedMove = state.moves.get(moveLocation);
    return v;
  }

  public int minValue(State state, int alpha, int beta, int maxDepth, int terminal,
      Move aMove, Move initialState) {
    if ((state.depth == maxDepth) || (terminal == 1)) {
      return state.getUtility(this.play, aMove, initialState);
    }
    int count = 0;
    int moveLocation = 0;
    int v = Integer.MAX_VALUE;
    for (Move move : state.moves) {
      int temp = v;
      state.doMove(move);
      if (move.pass) {
        terminal++;
      }
      v = Math.min(v, maxValue(state
          , alpha, beta,
          maxDepth, terminal, move, initialState));
      state.undoMove(move);
      if (move.pass) {
        terminal--;
      }
      if (v < temp) {
        moveLocation = count;
      }
      if (v <= alpha) {
        state.selectedMove = move;
        return v;
      }
      beta = Math.min(v, beta);
      count++;
    }
    state.selectedMove = state.moves.get(moveLocation);
    return v;
  }

  public void readInput(String a) throws IOException {
    File file = new File(a);
    BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
    play = Integer.parseInt(bufferedReader.readLine().trim());
    for (int i = 0; i < 10; i++) {
      String inputString = bufferedReader.readLine().trim();
      int r = 0;
      for (final char c : inputString.toCharArray()) {
        if (i >= 5) {
          int temp = Character.getNumericValue(c);
          currentBoard[i - 5][r] = temp;
        } else {
          prevBoard[i][r] = Character.getNumericValue(c);
        }
        r++;
      }
    }
    bufferedReader.close();
  }

  static void outputMove(Move move) {
    String writeThis = "";
    if (move.pass) {
      writeThis = writeThis + "PASS";
    } else {
      char[] moveLocationString = Arrays.toString(move.location).toCharArray();
      writeThis = writeThis + moveLocationString[1];
      writeThis = writeThis + ",";
      writeThis = writeThis + moveLocationString[4];
    }
    Writer writer = null;
    try {
      writer = new BufferedWriter(new OutputStreamWriter(
          new FileOutputStream("output.txt"), StandardCharsets.UTF_8));
      writer.write(writeThis);
    } catch (IOException ex) {
      //
    } finally {
      try {
        assert writer != null;
        writer.close();
      } catch (Exception ex) {/*ignore*/}
    }
  }

  public static void main(String[] args) throws IOException {
    long startTime = System.nanoTime();
    Utilities utils = new Utilities();
    my_player player = new my_player();
    State currState = new State(player.currentBoard,
        new State(player.prevBoard, null, utils.callOtherPlayer(player.play), 0), player.play, 0);
    State currStateCopy = new State(player.currentBoard,
        new State(player.prevBoard, null, utils.callOtherPlayer(player.play), 0), player.play, 0);
    Move move;
    Move initialState = new Move(null, currStateCopy.board, currState.player, true, 0);
    if (player.helperInt > 20) {
      int temp = 24 - player.helperInt;
      if (temp > 1) {
        //move = player.alphaBetaSearch(currState, 24 - player.helperInt + 1, initialState);
        move = player.alphaBetaSearch(currState, 2, initialState);
      }
      if (temp == 1) {
        move = player.alphaBetaSearch(currState, 2, initialState);
      } else {
        move = player.alphaBetaSearch(currState, 1, initialState);
      }

    } else {
      move = player.alphaBetaSearch(currState, 3, initialState);
    }
    outputMove(move);
    System.out.println(Arrays.toString(move.location));
    long endTime = System.nanoTime();
    float duration = (float) (endTime - startTime) / 1000000000;
    System.out.println(Float.toString(duration));
  }
}


class Move implements Comparable<Move> {

  Utilities utils = new Utilities();
  int[][] initialBoard = new int[5][5];
  int[][] board = new int[5][5];
  int[] location;
  int player;
  boolean pass;
  float heuristic;
  int heuristicMove;
  int regions;

  HashMap<Integer, ArrayList<int[]>> pieceList = new HashMap<Integer, ArrayList<int[]>>();
  HashMap<Integer, ArrayList<int[]>> pieceListCopy = new HashMap<Integer, ArrayList<int[]>>();
  HashMap<Integer, ArrayList<Node>> connectedPieces = new HashMap<Integer, ArrayList<Node>>();
  HashMap<Integer, HashMap<String, int[]>> pieceSets = new HashMap<Integer, HashMap<String, int[]>>();
  HashMap<Integer, ArrayList<int[]>> initialPieceList = new HashMap<Integer, ArrayList<int[]>>();

  boolean valid = true;

  Move(int[] lco, int[][] parentBoard, int player, boolean pass, int heuristicMove) {
    this.location = lco;
    this.player = player;
    this.pass = pass;
    this.heuristicMove = heuristicMove;
    this.initialPieceList.put(0, new ArrayList<int[]>());
    this.initialPieceList.put(1, new ArrayList<int[]>());
    this.initialPieceList.put(2, new ArrayList<int[]>());
    this.pieceSets.put(0, new HashMap<String, int[]>());
    this.pieceSets.put(1, new HashMap<String, int[]>());
    this.pieceSets.put(2, new HashMap<String, int[]>());
    this.connectedPieces.put(1, new ArrayList<Node>());
    this.connectedPieces.put(2, new ArrayList<Node>());
    this.pieceList.put(0, new ArrayList<int[]>());
    this.pieceList.put(1, new ArrayList<int[]>());
    this.pieceList.put(2, new ArrayList<int[]>());
    this.pieceListCopy.put(0, new ArrayList<int[]>());
    this.pieceListCopy.put(1, new ArrayList<int[]>());
    this.pieceListCopy.put(2, new ArrayList<int[]>());
    for (int i = 0; i < 5; i++) {
      for (int j = 0; j < 5; j++) {
        this.board[i][j] = parentBoard[i][j];
        this.initialBoard[i][j] = parentBoard[i][j];
        if (parentBoard[i][j] == 0) {
          this.initialPieceList.get(0).add(new int[]{i, j});
        }
        if (parentBoard[i][j] == 1) {
          this.initialPieceList.get(1).add(new int[]{i, j});
        }
        if (parentBoard[i][j] == 2) {
          this.initialPieceList.get(2).add(new int[]{i, j});
        }
      }
    }

    if (!pass) {
      makeNewBoard();
    }
    piecesInitializer();
    connectedSetsMaker(1);
    connectedSetsMaker(2);
    if (!pass) {
      pieceRemover();
    }
    heuristicCalc();
    if (this.heuristicMove == 1) {
      regions = regionFinder(this.player);
    }
  }

  void heuristicCalc() {
    if (pass) {
      this.heuristic = -10;
    } else {
      int opponentInitialCount = this.initialPieceList.get(utils.callOtherPlayer(this.player))
          .size();
      int opponentFinalCount = this.pieceList.get(utils.callOtherPlayer(this.player)).size();
      int opponentDiff = opponentFinalCount - opponentInitialCount;

      int liberties = 0;
      int opponentLiberties = 0;

      for (Node node : this.connectedPieces.get(this.player)) {
        liberties += node.associatedLibertiesSet.size();
      }
      for (Node node : this.connectedPieces.get(utils.callOtherPlayer(this.player))) {
        opponentLiberties += node.associatedLibertiesSet.size();
      }
      this.heuristic = (float) ((liberties - 0.5*opponentLiberties - 0.8*opponentDiff) - 0.00001 * (Math.abs(this.location[0] - 2) + Math.abs(this.location[1] - 2)));
    }
  }

  int regionFinder(int piece) {
    int returnCounter = 0;
    HashSet<String> visitedSet = new HashSet<String>();
    ArrayList<int[]> pointsCopy = this.pieceListCopy.get(piece);
    for (int[] stone : this.pieceListCopy.get(piece)) {
      int counter = 0;
      if (visitedSet.contains(Arrays.toString(stone))) {
        continue;
      }
      Stack<String> searchStack = new Stack<String>();
      searchStack.push(Arrays.toString(stone));
      while (!searchStack.isEmpty()) {
        String temp = searchStack.pop();
        int[] poppedPoint = utils.convertToArray(temp);
        ArrayList<int[]> validPoints = utils.validRegion(poppedPoint);
        int l = 0;
        for (int[] validPoint : validPoints) {
          if (Arrays.toString(stone).equalsIgnoreCase(Arrays.toString(validPoint))
              && counter != 0) {
            returnCounter++;
            continue;
          }
          if (this.pieceSets.get(piece).containsKey(Arrays.toString(validPoint)) && !searchStack
              .contains(Arrays.toString(validPoint)) && !visitedSet
              .contains(Arrays.toString(validPoint))) {
            searchStack.push(Arrays.toString(validPoint));
          }
        }
        visitedSet.add(Arrays.toString(poppedPoint));
        counter++;
      }
    }
    return returnCounter;
  }

  void piecesInitializer() {
    //Initialises the various ArrayLists with the locations of each piece ('1' has an ArrayList that stores whichever location has an the piece 1)
    for (int i = 0; i < 5; i++) {
      for (int j = 0; j < 5; j++) {
        int[] tempLoc = new int[]{i, j};
        if (this.board[i][j] == 0) {
          this.pieceList.get(0).add(tempLoc);
          this.pieceListCopy.get(0).add(tempLoc);
          this.pieceSets.get(0).put(Arrays.toString(tempLoc), tempLoc);
          //Adding the liberties as a set for O(1) search while checking if a location surrounding a node has an empty space or not
        } else if (this.board[i][j] == 1) {
          this.pieceList.get(1).add(tempLoc);
          this.pieceListCopy.get(1).add(tempLoc);
          this.pieceSets.get(1).put(Arrays.toString(tempLoc), tempLoc);
        } else if (board[i][j] == 2) {
          this.pieceList.get(2).add(tempLoc);
          this.pieceListCopy.get(2).add(tempLoc);
          this.pieceSets.get(2).put(Arrays.toString(tempLoc), tempLoc);
        }
      }
    }
  }

  void connectedSetsMaker(int piece) {

    HashSet<String> visitedSet = new HashSet<String>();
    ArrayList<int[]> pointsCopy = this.pieceListCopy.get(piece);
    for (int[] stone : this.pieceListCopy.get(piece)) {
      if (visitedSet.contains(Arrays.toString(stone))) {
        continue;
      }
      Node node = new Node(piece);
      Stack<String> searchStack = new Stack<String>();
      searchStack.push(Arrays.toString(stone));
      while (!searchStack.isEmpty()) {
        String temp = searchStack.pop();
        int[] poppedPoint = utils.convertToArray(temp);
        node.nodeAdder(poppedPoint);
        ArrayList<int[]> validPoints = utils.validPoints(poppedPoint);
        int l = 0;
        for (int[] validPoint : validPoints) {
          if (this.pieceSets.get(piece).containsKey(Arrays.toString(validPoint)) && !searchStack
              .contains(Arrays.toString(validPoint)) && !visitedSet
              .contains(Arrays.toString(validPoint))) {
            searchStack.push(Arrays.toString(validPoint));
          }
          if (this.pieceSets.get(0).containsKey(Arrays.toString(validPoint))) {
            node.libertyAdder(validPoint);
          }
        }
        visitedSet.add(Arrays.toString(poppedPoint));
      }
      this.connectedPieces.get(piece).add(node);
    }
  }

  void pieceRemover() {
    String tempLocationString = Arrays.toString(this.location);
    ArrayList<Node> removerConnectedPieces = this.connectedPieces
        .get(utils.callOtherPlayer(this.player));
    ArrayList<Node> removerOpponentPieces = this.connectedPieces.get(this.player);
    for (Node playerNode : removerConnectedPieces) {
      playerNode.associatedLibertiesSet.remove(tempLocationString);
      if (playerNode.associatedLibertiesSet.isEmpty()) {
        for (int j = 0; j < playerNode.piecesList.size(); j++) {
          int[] removeThis = playerNode.piecesList.get(j);
          this.board[removeThis[0]][removeThis[1]] = 0;
          for (Node node : removerOpponentPieces) {
            if (node.neighbors.contains(Arrays.toString(removeThis))) {
              node.libertyAdder(removeThis);
            }
          }
        }
      }
    }

    for (Node playerNode : removerOpponentPieces) {
      playerNode.associatedLibertiesSet.remove(tempLocationString);
      if (playerNode.associatedLibertiesSet.isEmpty()) {
        this.valid = false;
        break;
      }
    }
    ArrayList<int[]> validPoints = utils.validPoints(this.location);
    int count = 0;
    for (int[] point : validPoints) {
      if (this.board[point[0]][point[1]] == utils.callOtherPlayer(this.player)) {
        count += 1;
      }
    }
    if (count == validPoints.size()) {
      this.valid = false;
    }
  }

  void makeNewBoard() {
    this.board[this.location[0]][this.location[1]] = this.player;
  }

  @Override
  public boolean equals(Object other) {
    if (!(other instanceof Move)) {
      return false;
    }

    Move that = (Move) other;

    // Custom equality check here.
    return this.heuristic == that.heuristic;
  }

  @Override
  public int hashCode() {
    int hashCode = 1;

    hashCode = hashCode * 37 + Arrays.toString(this.location).hashCode();

    return hashCode;
  }

  @Override
  public int compareTo(Move o) {
    return (Float.compare(this.heuristic, o.heuristic));
  }
}

class Node {

  Utilities utils = new Utilities();

  int piece;
  HashSet<String> piecesSet;
  ArrayList<int[]> piecesList;
  HashSet<String> associatedLibertiesSet;
  HashSet<String> neighbors;

  Node(int piece) {
    this.piecesList = new ArrayList<int[]>();
    this.piecesSet = new HashSet<String>();
    this.associatedLibertiesSet = new HashSet<String>();
    this.neighbors = new HashSet<>();
    this.piece = piece;
  }

  void nodeAdder(int[] location) {
    this.piecesList.add(location);
    this.piecesSet.add(Arrays.toString(location));
    for (int[] point : utils.validPoints(location)) {
      if (!piecesSet.contains(Arrays.toString(point))) {
        neighbors.add(Arrays.toString(point));
      }
    }
    neighbors.removeAll(piecesSet);
  }

  void libertyAdder(int[] location) {
    this.associatedLibertiesSet.add(Arrays.toString(location));
  }

}

class State {

  Utilities utils = new Utilities();
  int[][] board;
  State parentState;
  int depth;
  int player;
  ArrayList<Move> moves = new ArrayList<Move>();
  Move selectedMove;


  public State(int[][] board, State parentBoard, int player, int depth) {
    this.board = board;
    this.parentState = parentBoard;
    this.depth = depth;
    this.player = player;
    makeMove();
    reducer();
  }

  public void doMove(Move move) {
    this.board = move.board;
    this.depth = this.depth + 1;
    this.player = utils.callOtherPlayer(this.player);
    makeMove();
    reducer();
  }

  public void undoMove(Move move) {
    this.board = move.initialBoard;
    this.depth = this.depth - 1;
    this.player = utils.callOtherPlayer(this.player);
    makeMove();
    reducer();
  }

  public int getUtility(int play, Move passedMove, Move initialState) {

    int returnValue = 0;
    //Get the difference between the initial state and the last move ka state!!

    int killedPieces = initialState.initialPieceList.get(utils.callOtherPlayer(play)).size()
        - passedMove.pieceList.get(utils.callOtherPlayer(play)).size();
    int martyrs =
        initialState.initialPieceList.get(play).size() - passedMove.pieceList.get(play).size();
    int libertyCount = 0;
    int opponentLibertyCount = 0;
    int mineInDanger = 0;
    int oppoInDanger = 0;
    int pieceCount = passedMove.pieceList.get(play).size();
    int opponentPieceCount = passedMove.pieceList.get(utils.callOtherPlayer(play)).size();

    int noPotDanger = 0;
    int noDanger = 0;
    int noOppDanger = 0;
    int noOppPotDanger = 0;
    int minePotDanger = 0;
    int oppoPotDanger = 0;

    HashSet<String> onlyMyLib = new HashSet<>();
    HashSet<String> oppLib = new HashSet<>();
    for (Node node : passedMove.connectedPieces.get(play)) {
      libertyCount += node.associatedLibertiesSet.size();
      onlyMyLib.addAll(node.associatedLibertiesSet);
      if (node.associatedLibertiesSet.size() == 1) {
        mineInDanger++;
        noDanger += node.piecesList.size();
      }
      if (node.associatedLibertiesSet.size() == 2) {
        minePotDanger++;
        noPotDanger += node.piecesList.size();
      }
    }
    for (Node node : passedMove.connectedPieces.get(utils.callOtherPlayer(play))) {
      opponentLibertyCount += node.associatedLibertiesSet.size();
      oppLib.addAll(node.associatedLibertiesSet);
      if (node.associatedLibertiesSet.size() == 2) {
        oppoPotDanger++;
        noOppPotDanger += node.piecesList.size();
      }
      if (node.associatedLibertiesSet.size() == 1) {
        oppoInDanger++;
        noOppDanger += node.piecesList.size();
      }
    }
    onlyMyLib.removeAll(oppLib);
    if (play == 1) {
      int term1 = (libertyCount) - (opponentLibertyCount);
      int term2 = (pieceCount) - (opponentPieceCount);
      returnValue+= term1;
      returnValue+= term2;
      returnValue+= 5*killedPieces;
      returnValue-= 4*martyrs;
      returnValue-=10*mineInDanger;
      returnValue+=3*oppoInDanger;
      returnValue+=onlyMyLib.size();

    } else {
      int term1 = (libertyCount) - (opponentLibertyCount);
      int term2 = (pieceCount) - (opponentPieceCount);
      returnValue+= term1;
      returnValue+=term2;
      returnValue += 3*killedPieces;
      returnValue-= 3*martyrs;
      returnValue-=5*mineInDanger;
      returnValue+=oppoInDanger;
    }

    return returnValue;
  }

  void makeMove() {
    this.moves = new ArrayList<Move>();

    for (int i = 0; i < 5; i++) {
      for (int j = 0; j < 5; j++) {
        if (this.board[i][j] == 0) {
          Move t = new Move(new int[]{i, j}, this.board, this.player, false, 0);
          if (t.valid) {
            moves.add(t);
          }
        }
      }
    }
    Move passMove = new Move(null, this.board, this.player, true, 0);
    moves.add(passMove);
    Collections.sort(moves);
    Collections.reverse(moves);
  }

  public void reducer() {
    ArrayList<Move> delete = new ArrayList<Move>();
    for (Move move : this.moves) {
      if (this.parentState != null && utils.boardChecker(move.board, this.parentState.board)) {
        delete.add(move);
      } else if (!move.valid) {
        delete.add(move);
      }
    }
    this.moves.removeAll(delete);
  }
}