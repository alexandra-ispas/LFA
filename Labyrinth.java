import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Labyrinth {
    public static void main(String[] args) {
        BufferedReader reader = new BufferedReader ( new InputStreamReader ( System.in ) );
        int l = 0, c = 0, r = 0, o = 0;
        int[][] M = new int[0][];
        int[] pos_lines = new int[0], pos_cols = new int[0], exit_lines = new int[0], exit_cols =
                new int[0];
        try {
            String[] values = reader.readLine ().split ( "\\s+" );
            l = Integer.parseInt ( values[0] ); 
            c = Integer.parseInt ( values[1] ); 
            r = Integer.parseInt ( values[2] );
            o = Integer.parseInt ( values[3] );

            M = new int[l][c];
            pos_lines = new int[r];
            pos_cols = new int[r];

            exit_lines = new int[o];
            exit_cols = new int[o];
            for(int i = 0; i < l; i++) {
                values = reader.readLine ().split ( "\\s+" ); 
                for(int q = 0; q < c; q++) {
                    M[i][q] = Integer.parseInt ( values[q] );
                }
            }
            // read the positions where the robots are placed
            readStates ( reader, r, pos_lines, pos_cols );
            // read the final positions
            readStates ( reader, o, exit_lines, exit_cols );
        } catch (IOException e) {
            e.printStackTrace ();
        }
        int n = l * c;
        int m = 4;
        int[][] matrix = new int[n][m];
        System.out.println (n + " " + m + " " + r + " " + o);

        for(int i = 0; i < l; i++) {
            for(int j = 0; j < c; j++) {
                int line = (i) * c + j;
                int south = (i + 1) * c + j;
                int north = (i - 1) * c + j;
                int west = (i) * c + (j - 1);
                int east = (i) * c + (j + 1);
                //check if the robot can go east
                if(((M[i][j]) & 1 ) == 1) {
                    matrix[line][0] = line;
                } else {
                    matrix[line][0] = east;
                }
                //check if the robot can go north
                if(((M[i][j] >> 1) & 1 ) == 1) {
                    matrix[line][1] = line;
                } else {
                    matrix[line][1] = north;
                }
                //check if the robot can go west
                if(((M[i][j] >> 2) & 1 ) == 1) {
                    matrix[line][2] = line;
                } else {
                    matrix[line][2] = west;
                }
                //check if the robot can go south
                if(((M[i][j] >> 3) & 1 ) == 1) {
                    matrix[line][3] = line;
                } else {
                    matrix[line][3] = south;
                }
            }
        }
        for(int i = 0; i < n; i++) {
            System.out.println (matrix[i][0] + " " + matrix[i][1] + " " + matrix[i][2] + " " + matrix[i][3] + " ");
        }

        for(int i = 0; i < r; i++) {
            System.out.print ( (pos_lines[i] * c + pos_cols[i]) + " " );
        }
        System.out.println ("");

        for(int i = 0; i < o; i++) {
            System.out.print ( (exit_lines[i] * c + exit_cols[i]) + " " );
        }
        System.out.println ("");
    }

    private static void readStates(BufferedReader reader, int r, int[] pos_lines, int[] pos_cols)
            throws IOException {
        String[] values;
        values = reader.readLine ().split ( "\\s+" );
        for(int i = 0; i < 2 * r; i += 2) {
            pos_lines[i / 2] = Integer.parseInt ( values[i] );
            pos_cols[i / 2] = Integer.parseInt ( values[i + 1] );
        }
    }
}
