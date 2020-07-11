package marvel;

import graph.*;
import java.util.*;

/**
 * Utility class to load graph from a file and find a shortest path between 2 nodes.
 * Graph is loaded using MarvelParser and shortest path is searched with Breadth-First-Search algorithm.
 */
public class MarvelPaths {
    // If MarvelPaths is an ADT, AF and RI would normally go here

    /**
     * Main function. Interactive Program that allows user to search through Marvel database and find
     * connection between 2 characters.
     *
     * @param args passed in command line
     */
    public static void main(String[] args) {
        DiGraph<String, String> graph = loadGraph("marvel.tsv");
        Scanner input = new Scanner(System.in);
        String ans = "";
        System.out.println("Welcome to Marvel Character Connector");
        do {
            String char1, char2;
            System.out.println("Enter 2 characters to discover the connection between them!");
            System.out.print("Character 1: ");
            char1 = getInput(input.nextLine(), graph, input);
            System.out.print("Character 2: ");
            char2 = getInput(input.nextLine(), graph, input);

            // find and display path
            if (!char1.isEmpty() && !char2.isEmpty()) {
                List<DiGraph.LabeledEdge<String, String>> path = findPath(graph, char1, char2);
                if (path == null) {
                    System.out.println("Sorry! " + char1 + " and " + char2 + " don't seem to be connected.");
                } else if (path.size() == 0) {
                    System.out.println(char1 + " is connected to itself!");
                } else {
                    System.out.println(char1 + " and " + char2 + " are connected.");
                    for (DiGraph.LabeledEdge e : path) {
                        System.out.println("\t" + e.getFrom() + " to " + e.getTo() + " by the book " + e.getLabel());
                    }
                }
            } else {
                System.out.println("unable to find path!");
            }

            System.out.print("\nWould you like to go again? (Y/N) ");
            ans = input.nextLine();
        } while (ans.equalsIgnoreCase("y"));
        System.out.println("Goodbye!");
        input.close();
    }

    private static String getInput(String n, DiGraph<String, String> g, Scanner input) {
        Set<String> res = new HashSet<>();
        // General Idea
        // if the user input is exactly like how the character is written in the graph, add to the set
        // otherwise, search any matches for the user input using regex. Add any match to the set
        // when done searching the whole graph,
        // - display to the user all matches to user input
        // - prompts user to select the character
        // - return the selected character
        if (g.containsNode(n)) {
            res.add(n);
        } else {
            String regex = "(?i)(.*)" + n + "(.*)";
            for (String s : g.listNodes()) {
                if (s.matches("(?i)^" + n + "($)")) res.add(s);
                if (s.contains("/")) {
                    String[] split = s.split("/");
                    for (String ss : split) {
                        if (ss.length() == 1) continue;
                        if (n.toLowerCase().startsWith(ss.toLowerCase())) res.add(s);
                        if (s.matches(regex)) res.add(s);
                    }
                }
                if (s.contains(",")) {
                    String[] split = s.split(",");
                    for (String ss : split) {
                        if (s.matches(regex)) res.add(s);
                    }
                }
                if (s.matches(regex)) res.add(s);
            }
        }
        if (res.isEmpty() || res.size() == 0) {
            System.out.println("Sorry! it seems that " + n + " is not a marvel character.");
            return "";
        }
        int i = 1;
        System.out.println("Showing all matches for " + n);
        for (String s : res) {
            System.out.println(i + " " + s);
            i++;
        }
        String[] t = res.toArray(new String[0]);
        do {
            System.out.print("Enter the number: ");
            i = input.nextInt();
        } while (i < 1 || i > res.size());
        input.nextLine();
        return t[i-1];
    }

    /**
     * Loads the data from filename and builds a DiGraph
     *
     * @param filename to load
     *
     * @spec.requires filename is valid and the file exists.
     * @return a DiGraph with characters as nodes and books as edges
     */
    public static DiGraph<String, String> loadGraph(String filename) {
        assert (filename != null) && (!filename.isEmpty()) : "unable to load graph from invalid filename";
        HashMap<String, Set<String>>  characterMap = MarvelParser.parseData(filename);
        DiGraph<String, String> graph = new DiGraph<>();
        Set<String> bookSet = characterMap.keySet();

        // General idea
        // for each book int the character map, get the set of heroes that appear in that book
        // for each hero, add to the graph if graph doesn't already have it
        // iterate through the same hero set, and add an edge to each other heroes
        for (String book : bookSet) {
            Set<String> heroSet = characterMap.get(book);
            for (String hero: heroSet) {
                if (!graph.containsNode(hero)) {
                    graph.addNode(hero);
                }
                Iterator<String> it = heroSet.iterator();
                while (it.hasNext()) {
                    String heroTo = it.next();
                    if (!hero.equalsIgnoreCase(heroTo)) {
                        graph.addEdge(hero, heroTo, book);
                    }
                }
            }
        }
        return graph;
    }

    /**
     * Finds the shortest path with Breadth-First-Search in the graph
     * from node u to node v.
     * null is returned if no path is found.
     *
     * @param graph on which a path from u to v is searched
     * @param u starting node
     * @param v destination node
     * @throws IllegalArgumentException when u or v are not in graph
     *
     * @spec.requires graph != null and u != null and v != null and graph is not empty
     * @return a LinkedList of Labeled Edges representing a path from u to v of least lexicographical order
     */
    public static LinkedList<DiGraph.LabeledEdge<String, String>> findPath(DiGraph<String, String> graph, String u, String v) {
        assert (graph != null) : "unable to find path between " + u + " and " + v + " in an empty graph.";
        assert (!graph.isEmpty()) : "unable to find path between " + u + " and " + v + " in an empty graph.";
        assert (u != null) && (v != null) : "unable to find path between empty nodes";
        if(!graph.containsNode(u)) throw new IllegalArgumentException(u + " is not in the graph.");
        if(!graph.containsNode(v)) throw new IllegalArgumentException(v + " is not in the graph.");

        // followed the given pseudo code for BFS
        String start = u;
        String dest = v;
        Queue<String> queue = new LinkedList<>();
        // key : visited node, value = path from start to that node
        HashMap<String, LinkedList<DiGraph.LabeledEdge<String, String>>> map = new HashMap<>();

        queue.add(start);
        map.put(start, new LinkedList<>());

        while(!queue.isEmpty()) {
            String n = queue.remove();
            if (n.equalsIgnoreCase(dest)) {
                LinkedList<DiGraph.LabeledEdge<String, String>> p = map.get(n);
                for (DiGraph.LabeledEdge pEdge : p) {
                }
                return p;
            }
            List<DiGraph.LabeledEdge<String, String>> edgeList = graph.listChildren(n);

            edgeList.sort(Comparator.comparing(DiGraph.LabeledEdge::getFrom));

            for (DiGraph.LabeledEdge<String, String> edge : edgeList) {
                String m = edge.getTo();
                if (!map.containsKey(m)) {
                    LinkedList<DiGraph.LabeledEdge<String, String>> p = new LinkedList<>();
                    LinkedList<DiGraph.LabeledEdge<String, String>> pTest = map.get(n);
                    for (int i = 0; i < pTest.size(); i++) {
                        p.add(pTest.get(i));
                    }
                    p.add(edge);
                    map.put(m, p);
                    queue.add(m);
                }
            }
        }
        return null;
    }

    class comparatorName implements Comparator {

        @Override
        public int compare(Object o1, Object o2) {
            return 0;
        }
    }
}
