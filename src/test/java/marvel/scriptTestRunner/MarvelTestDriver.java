/*
 * Copyright (C) 2020 Kevin Zatloukal.  All rights reserved.  Permission is
 * hereby granted to students registered for University of Washington
 * CSE 331 for use solely during Spring Quarter 2020 for purposes of
 * the course.  No other use, copying, distribution, or modification
 * is permitted without prior written consent. Copyrights for
 * third-party components of this work must be honored.  Instructors
 * interested in reusing these course materials should contact the
 * author.
 */

package marvel.scriptTestRunner;

import graph.DiGraph;
import marvel.MarvelPaths;

import java.io.*;
import java.util.*;

/**
 * This class implements a testing driver which reads test scripts from
 * files for testing Graph, the Marvel parser, and your BFS algorithm.
 */
public class MarvelTestDriver {

    public static void main(String[] args) {
        // You only need a main() method if you choose to implement
        // the 'interactive' test driver, as seen with GraphTestDriver's sample
        // code. You may also delete this method entirely if you don't want to
        // use the interactive test driver.
        try {
            if (args.length > 1) {
                printUsage();
                return;
            }

            MarvelTestDriver td;

            if (args.length == 0) {
                td = new MarvelTestDriver(new InputStreamReader(System.in), new OutputStreamWriter(System.out));
                System.out.println("Running in interactive mode.");
                System.out.println("Type a line in the script testing language to see the output.");
            } else {
                String fileName = args[0];
                File tests = new File(fileName);

                System.out.println("Reading from the provided file.");
                System.out.println("Writing the output from running those tests to standard out.");
                if (tests.exists() || tests.canRead()) {
                    td = new MarvelTestDriver(new FileReader(tests), new OutputStreamWriter(System.out));
                } else {
                    System.err.println("Cannot read from " + tests.toString());
                    printUsage();
                    return;
                }
            }

            td.runTests();

        } catch (IOException e) {
            System.err.println(e.toString());
            e.printStackTrace(System.err);
        }
    }

    private static void printUsage() {
        System.err.println("Usage:");
        System.err.println("  Run the gradle 'build' task");
        System.err.println("  Open a terminal at hw-marvel/build/classes/java/test");
        System.err.println("  To read from a file: java marvel.scriptTestRunner.MarvelTestDriver <name of input script>");
        System.err.println("  To read from standard in (interactive): java marvel.scriptTestRunner.MarvelTestDriver");
    }

    //COPIED ITEMS

    private final Map<String, DiGraph<String, String>> graphs = new HashMap<>();
    private final PrintWriter output;
    private final BufferedReader input;

    // Leave this constructor public
    public MarvelTestDriver(Reader r, Writer w) {
        // See GraphTestDriver as an example.
        input = new BufferedReader(r);
        output = new PrintWriter(w);
    }

    // Leave this method public
    public void runTests() throws IOException {
        String inputLine;
        while ((inputLine = input.readLine()) != null) {
            if ((inputLine.trim().length() == 0) ||
                    (inputLine.charAt(0) == '#')) {
                // echo blank and comment lines
                output.println(inputLine);
            } else {
                // separate the input line on white space
                StringTokenizer st = new StringTokenizer(inputLine);
                if (st.hasMoreTokens()) {
                    String command = st.nextToken();

                    List<String> arguments = new ArrayList<String>();
                    while (st.hasMoreTokens()) {
                        arguments.add(st.nextToken());
                    }

                    executeCommand(command, arguments);
                }
            }
            output.flush();
        }
    }

    private void executeCommand(String command, List<String> arguments) {
        try {
            switch (command) {
                case "CreateGraph":
                    createGraph(arguments);
                    break;
                case "AddNode":
                    addNode(arguments);
                    break;
                case "AddEdge":
                    addEdge(arguments);
                    break;
                case "ListNodes":
                    listNodes(arguments);
                    break;
                case "ListChildren":
                    listChildren(arguments);
                    break;
                case "LoadGraph":
                    loadGraph(arguments);
                    break;
                case "FindPath" :
                    findPath(arguments);
                    break;
                default:
                    output.println("Unrecognized command: " + command);
                    break;
            }
        } catch (Exception e) {
            output.println("Exception: " + e.toString());
        }
    }

    /////////////////////////////////////////
    ///// copies from GraphTestDriver ///////
    /////////////////////////////////////////


    private void createGraph(List<String> arguments) {
        if (arguments.size() != 1) {
            throw new CommandException("Bad arguments to CreateGraph: " + arguments);
        }

        String graphName = arguments.get(0);
        createGraph(graphName);
    }

    private void createGraph(String graphName) {
        DiGraph<String, String> g = new DiGraph<>();
        if (!graphs.containsKey(graphName)) {
            graphs.put(graphName, g);
            output.println("created graph " + graphName);
            return;
        }
        output.println();
    }

    private void addNode(List<String> arguments) {
        if (arguments.size() != 2) {
            throw new CommandException("Bad arguments to AddNode: " + arguments);
        }

        String graphName = arguments.get(0);
        String nodeName = arguments.get(1);

        addNode(graphName, nodeName);
    }

    private void addNode(String graphName, String nodeName) {
        DiGraph<String, String> g = graphs.get(graphName);
        g.addNode(nodeName);
        // added node n1 to graph1
        output.println("added node " + nodeName + " to " + graphName);
    }

    private void addEdge(List<String> arguments) {
        if (arguments.size() != 4) {
            throw new CommandException("Bad arguments to AddEdge: " + arguments);
        }

        String graphName = arguments.get(0);
        String parentName = arguments.get(1);
        String childName = arguments.get(2);
        String edgeLabel = arguments.get(3);

        addEdge(graphName, parentName, childName, edgeLabel);
    }

    private void addEdge(String graphName, String parentName, String childName,
                         String edgeLabel) {
        DiGraph<String, String> g = graphs.get(graphName);
        g.addEdge(parentName, childName, edgeLabel);
        // added edge e1 from n1 to n2 in graph1
        output.println("added edge " + edgeLabel + " from " + parentName +
                " to " + childName + " in " + graphName);
    }

    private void listNodes(List<String> arguments) {
        if (arguments.size() != 1) {
            throw new CommandException("Bad arguments to ListNodes: " + arguments);
        }

        String graphName = arguments.get(0);
        listNodes(graphName);
    }

    private void listNodes(String graphName) {
        DiGraph<String, String> g = graphs.get(graphName);
        List<String> nodes = g.listNodes();
        // graph1 contains: n1 ...
        output.print(graphName + " contains:");
        if (nodes.isEmpty()) {
            output.print("\n");
            return;
        }
        for (int i = 0; i < nodes.size(); i++) {
            output.print((i == nodes.size()-1) ? (" " + nodes.get(i) + "\n") : (" " + nodes.get(i) + " "));
        }
    }

    private void listChildren(List<String> arguments) {
        if (arguments.size() != 2) {
            throw new CommandException("Bad arguments to ListChildren: " + arguments);
        }

        String graphName = arguments.get(0);
        String parentName = arguments.get(1);
        listChildren(graphName, parentName);
    }

    private void listChildren(String graphName, String parentName) {
        DiGraph<String, String> g = graphs.get(graphName);
        List<DiGraph.LabeledEdge<String, String>> children = g.listChildren(parentName);
        children.sort(Comparator.comparing(DiGraph.LabeledEdge::getTo));
        // the children of n1 in graph1 are: n2(e1) n3(e2)
        output.print("the children of " + parentName + " in " + graphName + " are:");
        if (children.isEmpty()) {
            output.print("\n");
            return;
        }
        for (int i = 0; i < children.size(); i++) {
            output.print((i == children.size() - 1) ?
                    (" " + children.get(i).getTo() + "(" + children.get(i).getLabel() + ")\n")
                    : (" " + children.get(i).getTo() + "(" + children.get(i).getLabel() + ") "));
        }
    }


    ///////////////////////////////////////
    ////// new things for marvel path /////
    ///////////////////////////////////////

    private void loadGraph(List<String> arguments) {
        if (arguments.size() != 2) {
            throw new CommandException("Bad arguments to LoadGraph: " + arguments);
        }

        String graphName = arguments.get(0);
        String filename = arguments.get(1);
        loadGraph(graphName, filename);
    }

    private void loadGraph(String graphName, String filename) {
        try {
            if (!graphs.containsKey(graphName)) {
                DiGraph<String, String> g = MarvelPaths.loadGraph(filename);
                graphs.put(graphName, g);
                output.println("loaded graph " + graphName);
            }
        } catch (Exception e) {
            output.println();
        }
    }


    private void findPath(List<String> arguments) {
        if (arguments.size() != 3) {
            throw new CommandException("Bad arguments to findPath: " + arguments);
        }

        boolean flag = false;
        String graphName = arguments.get(0);
        String node1 = arguments.get(1).replace("_", " ");
        if (!graphs.get(graphName).containsNode(node1)) {
            output.println("unknown character " + node1);
            flag = true;
        }
        String node2 = arguments.get(2).replace("_", " ");
        if (!graphs.get(graphName).containsNode(node2)) {
            output.println("unknown character " + node2);
            flag = true;
        }
        if (flag) {
            return;
        }
        findPath(graphName, node1, node2);
    }

    private void findPath(String graphName, String node1, String node2) {
        output.println("path from " + node1 + " to " + node2 + ":");
        LinkedList<DiGraph.LabeledEdge<String, String>> lst = MarvelPaths.findPath(graphs.get(graphName), node1, node2);
        if (lst == null) {
            output.println("no path found");
            return;
        }
        if (lst.size() == 0) {
            return;
        }
        for(DiGraph.LabeledEdge e : lst) {
            output.println(e.getFrom() + " to " + e.getTo() + " via " + e.getLabel());
        }
    }

    /**
     * This exception results when the input file cannot be parsed properly
     **/
    static class CommandException extends RuntimeException {

        public CommandException() {
            super();
        }

        public CommandException(String s) {
            super(s);
        }

        public static final long serialVersionUID = 3495;
    }
}
