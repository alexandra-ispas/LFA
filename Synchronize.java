import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Synchronize {
    public static class Elem {
        public int value;
        public int cost;

        public Elem(int value, int cost) {
            this.value = value;
            this.cost = cost;
        }
    }

    /**
     *
     * @param graph the input data
     * @param initial_states
     * @return the accessible states in the graph
     */
    public static Set<Integer> findAccessible(Map<Integer, Map<Integer, Integer>> graph,
                                              List<Integer> initial_states ) {
        Set<Integer> accessed = new HashSet<> ();
        Stack<Integer> stack = new Stack<> ();
        for (int elem : initial_states) {
            stack.push ( elem );
        }
        while (!stack.isEmpty ()) {
            int node = stack.pop ();
            accessed.add ( node );
            Map<Integer, Integer> adjacent = graph.get ( node );
            for (Map.Entry<Integer, Integer> entry : adjacent.entrySet ()) {
                if (!accessed.contains ( entry.getValue () )) {
                    stack.add ( entry.getValue () );
                }
            }
        }
        return accessed;
    }

    public static Set<Integer> findProductive(Map<Integer, ArrayList<Elem>> parents,
                                              List<Integer> final_states) {
        Set<Integer> productive = new HashSet<> ();
        Stack<Integer> stack = new Stack<> ();
        for(int elem : final_states) {
            stack.push ( elem );
        }
        while (!stack.isEmpty ()) {
            int node = stack.pop ();
            productive.add ( node );
            ArrayList<Elem> adjacent = parents.get ( node );
            if(adjacent == null) continue;
            for (Elem e : adjacent) {
                if (!productive.contains ( e.value)) {
                    stack.push ( e.value );
                }
            }
        }
        return productive;
    }

    public static void Synchronized_task(Stack<Integer> states, int[][] M, int m, List<Integer> final_states, boolean type) {
        StringBuilder result = new StringBuilder ();
        // synchronization algorithm
        while(states.size () > 1) {
            int s = states.pop ();
            int t = states.pop ();

            Stack<Integer> stack1 = new Stack<> ();
            Stack<Integer> stack2 = new Stack<> ();
            Stack<String> sequence = new Stack<> ();

            Set<Integer> visited = new HashSet<> ();
            sequence.push ( "" );
            stack1.push ( s );
            stack2.push ( t );

            int ok = 0;
            while(!stack1.isEmpty () && !stack2.isEmpty () && ok == 0) {
                int s_child = stack1.pop ();
                int t_child = stack2.pop ();
                String config = sequence.pop ();

                visited.add ( s_child );
                visited.add ( t_child );

                for (int i = 0; i < m && ok == 0; i++) {
                    int s_next = M[s_child][i];
                    int t_next = M[t_child][i];
                    if( s_next == t_next ){
                        result.append ( config ).append ( i ).append ( " " );
                        states.push ( s_next );
                        ok = 1;
                    } else if (!visited.contains ( s_next )) {
                        stack1.push ( s_next );
                        stack2.push ( t_child );
                        sequence.push ( config + i + " " );
                    } else if (!visited.contains ( t_next )) {
                        stack1.push ( s_child );
                        stack2.push ( t_next );
                        sequence.push ( config + i + " " );
                    }
                }
            }
        }
        // now all the robots are in the same cell
        // move all of them simultaneously to the exit
        if ( type ) {
            Stack<Integer> stack = new Stack<> ();
            Stack<String> sequence = new Stack<> ();
            Set<Integer> visited = new HashSet<> ();
            stack.push ( states.pop () );
            sequence.push ( "" );
            int ok = 0;
            while (!stack.isEmpty () && ok == 0) {
                int s_child = stack.pop ();
                String config = sequence.pop ();
                visited.add ( s_child );
                for (int i = 0; i < m && ok == 0; i++) {
                    int s_next = M[s_child][i];
                    if (final_states.contains ( s_next )) {
                        // the exit is found
                        ok = 1;
                        result.append ( config ).append ( i ).append ( " " );
                    } else {
                        if (!visited.contains ( s_next )) {
                            stack.push ( s_next );
                            sequence.push ( config + i + " " );
                        }
                    }
                }
            }
        }
        System.out.println ( result );
    }

    public static void main(String[] args) {
        int m = 0, n = 0, s = 0, f = 0;
        //<parent, <input value, child>>
        Map<Integer, Map<Integer, Integer>> graph = new HashMap<> ();
        //<child, <parent, input value>
        Map<Integer, ArrayList<Elem>> parents = new HashMap<> ();
        List<Integer> initial_states = new ArrayList<> ();
        List<Integer> final_states = new ArrayList<> ();

        BufferedReader reader = new BufferedReader ( new InputStreamReader ( System.in ) );
        try {
            String[] values = reader.readLine ().split ( "\\s+" );
            n = Integer.parseInt ( values[0] );
            m = Integer.parseInt ( values[1] );
            s = Integer.parseInt ( values[2] );
            f = Integer.parseInt ( values[3] );

            if(!args[0].equals ( "synchronize" )) {
                for (int i = 0; i < n; i++) {
                    Map<Integer, Integer> children = new HashMap<> ();
                    values = reader.readLine ().split ( "\\s+" );
                    for (int q = 0; q < m; q++) {
                        int child = Integer.parseInt ( values[q] );
                        children.put ( q, child );
                        Elem e = new Elem ( i, q );
                        ArrayList<Elem> aux = new ArrayList<> ();
                        aux.add ( e );
                        ArrayList<Elem> res = parents.putIfAbsent ( child, aux );
                        if (res != null) {
                            res.add ( e );
                            parents.put ( child, res );
                        }
                    }
                    graph.put ( i, children );
                }
                if (s != 0) {
                    initial_states = Arrays.stream ( reader.readLine ().split ( "\\s+" ) ).map (
                            Integer::parseInt ).collect ( Collectors.toList () );
                }
                if (f != 0) {
                    final_states = Arrays.stream ( reader.readLine ().split ( "\\s+" ) ).map (
                            Integer::parseInt ).collect ( Collectors.toList () );
                }
            }

        } catch (IOException e) {
            e.printStackTrace ();
        }
        if( args[0].equals ( "accessible" )) {
            Set<Integer> accessed = findAccessible ( graph, initial_states );
            for (int elem : accessed) {
                System.out.println ( elem );
            }
        } else if (Objects.equals ( args[0], "productive" )) {
            Set<Integer> productive = findProductive ( parents, final_states );
            for (int elem : productive) {
                System.out.println ( elem );
            }
        } else if (Objects.equals ( args[0], "useful" )) {
            Set<Integer> accessed = findAccessible ( graph, initial_states );
            Set<Integer> productive = findProductive ( parents, final_states );
            List<Integer> result = accessed.stream ().filter ( productive::contains ).collect(
                    Collectors.toList());
            for (int elem : result) {
                System.out.println ( elem );
            }
        } else if (Objects.equals ( args[0], "synchronize" )) {
            int[][] M = new int[n+1][m+1];
            String[] values;
            try {
                for (int i = 0; i < n; i++) {
                    values = reader.readLine ().split ( "\\s+" );
                    for (int q = 0; q < m; q++) {
                        M[i][q] = Integer.parseInt ( values[q] );
                    }
                }
                if (s != 0) {
                    initial_states = Arrays.stream ( reader.readLine ().split ( "\\s+" ) ).map (
                            Integer::parseInt ).collect ( Collectors.toList () );
                }
                if (f != 0) {
                    final_states = Arrays.stream ( reader.readLine ().split ( "\\s+" ) ).map (
                            Integer::parseInt ).collect ( Collectors.toList () );
                }
            } catch (IOException e) {
                e.printStackTrace ();
            }
            if(s == 0 && f == 0) {
                List<Integer> states = IntStream.range ( 0, n ).boxed ().collect (  Collectors.toList ());
                Stack<Integer> aux = new Stack<> ();
                aux.addAll ( states );
                Synchronized_task ( aux, M, m, null, false );
            } else if (s == 0) {
                List<Integer> final_states1 = final_states;
                List<Integer> states = IntStream.range ( 0, n )
                        .filter ( x -> !final_states1.contains ( x ) )
                        .boxed ().collect (  Collectors.toList ());
                Stack<Integer> aux = new Stack<> ();
                aux.addAll ( states );
                Synchronized_task ( aux, M, m, final_states, true );
            }else if (f == 0) {
                Stack<Integer> aux = new Stack<> ();
                aux.addAll ( initial_states );
                Synchronized_task ( aux, M, m, null, false );
            } else {
                Stack<Integer> aux = new Stack<> ();
                aux.addAll ( initial_states );
                Synchronized_task ( aux, M, m, final_states, true );
            }
        }
    }
}