import java.util.*;
import java.io.*;
import java.lang.*;





class Sudoku
{
    /* SIZE is the size parameter of the Sudoku puzzle, and N is the square of the size.  For 
     * a standard Sudoku puzzle, SIZE is 3 and N is 9. */
    static int SIZE, N;

    /* The grid contains all the numbers in the Sudoku puzzle.  Numbers which have
     * not yet been revealed are stored as 0. */
    int Grid[][];
    
    public int[] resizeArray(int[] array){
         int j = 0;
    for( int i=0;  i<array.length;  i++ )
    {
        if (array[i] != 0)
            array[j++] = array[i];
    }
    int [] newArray = new int[j];
    System.arraycopy( array, 0, newArray, 0, j );
    return newArray;
    }
    
    public boolean checkRow(int row, int number){
        for(int col=0; col < N; col++){
            if (Grid[row][col]==number) return false;
        }
        return true;
    }

    public boolean checkCol(int col, int number){
        for(int row=0; row < N; row++){
            if (Grid[row][col]==number) return false;
        }
        return true;
        
    }

    public boolean checkSquare(int row, int col, int number){
      int rowSection = row/SIZE * SIZE;
      int colSection = col/SIZE * SIZE;
      for (int i = rowSection; i < (rowSection+SIZE); i++){
        for (int j = colSection; j < (colSection+SIZE); j++){
          if(Grid[i][j]==number) return false;
        }
      }
      return true;
    }
    
    boolean checkAll(int row, int col, int i){
      // Check if i already in column
      for (int c=0; c<N; c++)
        if (Grid[row][c] == i) return false;
      // Check if i already in row
      for (int r=0; r<N; r++)
        if (Grid[r][col] == i) return false;
      // Check if i already in square
      int rowSection = row/SIZE;
      int colSection = col/SIZE;
      for (int r=SIZE*rowSection; r < SIZE*rowSection+SIZE; r++){
        for (int c=SIZE*colSection; c < SIZE*colSection+SIZE; c++){
          if (Grid[r][c] == i) return false;
        }
      }
      return true;
    }
    
    //Comparator
    public static Comparator<Possibility> sizeComparator = new Comparator<Possibility>(){
      @Override
      public int compare(Possibility p1, Possibility p2) {
        return (int) (p1.getSize() - p2.getSize());
      }
    };
    
    //Possible numbers for a cell
    public class Possibility {
      private int size;
      private int[] contains;
      private int locationx;
      private int locationy;
      public Possibility(int[] poss, int inSize, int locx, int locy){
        contains = poss;
        size = contains.length;
        locationx=locx;
        locationy=locy;
      }
      public int getSize(){
        return size;
      }
      public int getLocationX(){ // returns array with x and y coordinates, row|column
        return locationx;
      }
      public int getLocationY(){
        return locationy;
      }
      public int[] getArray(){
        return contains;
      }
    }
    public void placeNumber(int row, int col)throws Exception{ //give placeNumber some intel so it does not have to waste time trying numbers we already know lead to nowhere 
      // If row number exceeds the max row, we are done
      if (row > N-1)throw new Exception( "Success!" ) ;
        if (Grid[row][col]==0){
          for (int i=1;i<=N;i++){
            if(checkRow(row,i) && checkCol(col,i) && checkSquare(row,col,i)){
              Grid[row][col]=i;
              
              findNext(row,col);
            }
          }
          Grid[row][col]=0;
        }
        else {
          findNext(row,col);
        }
    }
    
    public void placeNumber(PriorityQueue<Possibility> intel)throws Exception{ //give placeNumber some intel so it does not have to waste time trying numbers we already know lead to nowhere  
          Possibility p=intel.poll();
          //System.out.println("intel");
         // System.out.println(intel.size());
           if (p == null)throw new Exception( "Success!" );
          int[] arrayOfPossibilities = new int[p.getSize()];
          arrayOfPossibilities =  p.getArray();
          int x = p.getLocationX();
          int y = p.getLocationY();
          if (Grid[x][y]==0){
          for (int i=0;i<arrayOfPossibilities.length;i++){
            //System.out.println(arrayOfPossibilities[i]);
            if(checkAll(x,y,arrayOfPossibilities[i])){
              Grid[x][y]=arrayOfPossibilities[i];
              placeNumber(intel); // pass down the intel
            }
            //Grid[x][y]=0;
             //placeNumber(intel);
          }
          Grid[x][y]=0;
          intel.add(p);//need to push it back if a "leaf" resolved. Really complicated, but its because PriorityQueues are objects and they are passed by reference, which means things you do in a recursion actually affects the other recursions
         
          //populatePossibilities(intel);
          }
          else {
            //System.out.println("never");
          placeNumber(intel);
          }
          //Grid[x][y]=0;
    }
    
    /* The solve() method should remove all the unknown characters ('x') in the Grid
     * and replace them with the numbers from 1-9 that satisfy the Sudoku puzzle. */
    public void findNext(int row, int col)throws Exception{
        if (col < (N-1)){
            placeNumber(row, col+1);
        } else {
            placeNumber(row+1, 0);
        }
    }
    
    public void populatePossibilities( PriorityQueue<Possibility> pQueue){ //Populates Possibility objects adepending on the present grid nd returns a queue that prioritizes possibilities with lowest size
      int counter=0;
      for (int row=0; row<N; row++){
        for (int col=0; col<N; col++){
          if (Grid[row][col]==0){
          int[] tmp = new int[N];
          int tmpCounter=0;
          for(int j=1; j<=N; j++){
            if (checkAll(row, col, j)){
              tmp[tmpCounter]=j;
              tmpCounter++;
            }
          }
          counter++;
          tmp=resizeArray(tmp);//must resize so I know my priority: cells with smallest numbers of possibility can be dealt with first
            pQueue.add(new Possibility(tmp, tmp.length,row, col));
        }
      }
    }
    }
    public void fastSolve(){
      PriorityQueue<Possibility> possibilityPriorityQueue = new PriorityQueue<Possibility>(SIZE*SIZE*SIZE, sizeComparator);
      populatePossibilities(possibilityPriorityQueue);
      while(possibilityPriorityQueue.peek().getSize()==1){ //double the while loop so I can update the possibilities by "rounds" and not each time I add a number
   while(possibilityPriorityQueue.peek().getSize()==1){ //fill cells that have only 1 possible number that fits
            Possibility p = possibilityPriorityQueue.poll();
            if(p == null) break;
            Grid[p.getLocationX()][p.getLocationY()] = p.getArray()[0];
        }
   possibilityPriorityQueue = new PriorityQueue<Possibility>(SIZE*SIZE*SIZE, sizeComparator);//reinistialize the possibilities
    populatePossibilities(possibilityPriorityQueue);
    }
      
      
      
      solve(possibilityPriorityQueue);//After fastSolve cant do anything anymore, pass down the possiblities to a smart version of solve to see what it can do with it
    }
    
    public void solve(PriorityQueue<Possibility> intel){//pass some intel to help solve() place numbers faster
    try {
        placeNumber(intel);
      }
      catch(Exception e){
        System.out.println("And the Verdict is: " + e);
      }
    }
    
    public void solve(){ 
      try {
        placeNumber(0,0);
        
      }
      catch(Exception e){
        System.out.println("And the Verdict is: " + e);
      }
      
    }
    
    
    /*****************************************************************************/
    /* NOTE: YOU SHOULD NOT HAVE TO MODIFY ANY OF THE FUNCTIONS BELOW THIS LINE. */
    /*****************************************************************************/
 
    /* Default constructor.  This will initialize all positions to the default 0
     * value.  Use the read() function to load the Sudoku puzzle from a file or
     * the standard input. */
    public Sudoku( int size )
    {
        SIZE = size;
        N = size*size;

        Grid = new int[N][N];
        for( int i = 0; i < N; i++ ) 
            for( int j = 0; j < N; j++ ) 
                Grid[i][j] = 0;
    }


    /* readInteger is a helper function for the reading of the input file.  It reads
     * words until it finds one that represents an integer. For convenience, it will also
     * recognize the string "x" as equivalent to "0". */
    static int readInteger( InputStream in ) throws Exception
    {
        int result = 0;
        boolean success = false;

        while( !success ) {
            String word = readWord( in );

            try {
                result = Integer.parseInt( word );
                success = true;
            } catch( Exception e ) {
                // Convert 'x' words into 0's
                if( word.compareTo("x") == 0 ) {
                    result = 0;
                    success = true;
                }
                // Ignore all other words that are not integers
            }
        }

        return result;
    }


    /* readWord is a helper function that reads a word separated by white space. */
    static String readWord( InputStream in ) throws Exception
    {
        StringBuffer result = new StringBuffer();
        int currentChar = in.read();
 String whiteSpace = " \t\r\n";
        // Ignore any leading white space
        while( whiteSpace.indexOf(currentChar) > -1 ) {
            currentChar = in.read();
        }

        // Read all characters until you reach white space
        while( whiteSpace.indexOf(currentChar) == -1 ) {
            result.append( (char) currentChar );
            currentChar = in.read();
        }
        return result.toString();
    }


    /* This function reads a Sudoku puzzle from the input stream in.  The Sudoku
     * grid is filled in one row at at time, from left to right.  All non-valid
     * characters are ignored by this function and may be used in the Sudoku file
     * to increase its legibility. */
    public void read( InputStream in ) throws Exception
    {
        for( int i = 0; i < N; i++ ) {
            for( int j = 0; j < N; j++ ) {
                Grid[i][j] = readInteger( in );
            }
        }
    }


    /* Helper function for the printing of Sudoku puzzle.  This function will print
     * out text, preceded by enough ' ' characters to make sure that the printint out
     * takes at least width characters.  */
    void printFixedWidth( String text, int width )
    {
        for( int i = 0; i < width - text.length(); i++ )
            System.out.print( " " );
        System.out.print( text );
    }


    /* The print() function outputs the Sudoku grid to the standard output, using
     * a bit of extra formatting to make the result clearly readable. */
    public void print()
    {
        // Compute the number of digits necessary to print out each number in the Sudoku puzzle
        int digits = (int) Math.floor(Math.log(N) / Math.log(10)) + 1;

        // Create a dashed line to separate the boxes 
        int lineLength = (digits + 1) * N + 2 * SIZE - 3;
        StringBuffer line = new StringBuffer();
        for( int lineInit = 0; lineInit < lineLength; lineInit++ )
            line.append('-');

        // Go through the Grid, printing out its values separated by spaces
        for( int i = 0; i < N; i++ ) {
            for( int j = 0; j < N; j++ ) {
                printFixedWidth( String.valueOf( Grid[i][j] ), digits );
                // Print the vertical lines between boxes 
                if( (j < N-1) && ((j+1) % SIZE == 0) )
                    System.out.print( " |" );
                System.out.print( " " );
            }
            System.out.println();

            // Print the horizontal line between boxes
            if( (i < N-1) && ((i+1) % SIZE == 0) )
                System.out.println( line.toString() );
        }
    }


    /* The main function reads in a Sudoku puzzle from the standard input, 
     * unless a file name is provided as a run-time argument, in which case the
     * Sudoku puzzle is loaded from that file.  It then solves the puzzle, and
     * outputs the completed puzzle to the standard output. */
    public static void main( String args[] ) throws Exception
    {
        InputStream in;
        if( args.length > 0 ) 
            in = new FileInputStream( args[0] );
        else
            in = System.in;

        // The first number in all Sudoku files must represent the size of the puzzle.  See
        // the example files for the file format.
        int puzzleSize = readInteger( in );
        if( puzzleSize > 100 || puzzleSize < 1 ) {
            System.out.println("Error: The Sudoku puzzle size must be between 1 and 100.");
            System.exit(-1);
        }

        Sudoku s = new Sudoku( puzzleSize );

        // read the rest of the Sudoku puzzle
        s.read( in );

        // Solve the puzzle.  We don't currently check to verify that the puzzle can be
        // successfully completed.  You may add that check if you want to, but it is not
        // necessary.
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        s.print();
        String userName = br.readLine();
        s.fastSolve();
        s.print();
        String userName2 = br.readLine();
        s.solve();

        // Print out the (hopefully completed!) puzzle
        s.print();
        System.out.println(Math.ceil(5/3));
    }
}
