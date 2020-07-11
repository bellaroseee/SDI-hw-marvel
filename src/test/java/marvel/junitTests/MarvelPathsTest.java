package marvel.junitTests;

import graph.*;
import marvel.MarvelPaths;
import org.junit.Before;
import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;

import java.util.LinkedList;

import static org.junit.Assert.assertEquals;

public class MarvelPathsTest {

    public DiGraph<String, String> graph1 = new DiGraph<>();
    public DiGraph<String, String> graph2 = new DiGraph<>();

    @Before
    public void setUp() {
       graph1 =  MarvelPaths.loadGraph("dummy2.tsv");
    }

    @Test(expected = AssertionError.class)
    public void testLoadGraphEmptyFilename() {
        DiGraph<String, String> graph = MarvelPaths.loadGraph("");
    }

    @Test(expected = AssertionError.class)
    public void testLoadGraphNullFilename() {
        DiGraph<String, String>  graph = MarvelPaths.loadGraph(null);
    }

    @Test(expected = Exception.class)
    public void testLoadGraphNonExistentFilename() {
        DiGraph<String, String>  graph = MarvelPaths.loadGraph("dummy.tsv");
    }

    @Test(expected = Exception.class)
    public void testNullColumn() {
        DiGraph<String, String>  graph = MarvelPaths.loadGraph("dummy1.tsv");
    }

    @Test(expected = AssertionError.class)
    public void testFindPathNullGraph() {
        LinkedList<DiGraph.LabeledEdge<String, String>> path = MarvelPaths.findPath(null, "a string", "another string");
    }

    @Test(expected = AssertionError.class)
    public void testFindPathEmptyGraph() {
        LinkedList<DiGraph.LabeledEdge<String, String>> path = MarvelPaths.findPath(graph2, "a string", "another string");
    }

    @Test(expected = AssertionError.class)
    public void testFindPathNullStartNode() {
        LinkedList<DiGraph.LabeledEdge<String, String>> path = MarvelPaths.findPath(graph1, null, "Bennet, James");
    }

    @Test(expected = AssertionError.class)
    public void testFindPathNullDestNode() {
        LinkedList<DiGraph.LabeledEdge<String, String>> path = MarvelPaths.findPath(graph1, "Bennet, James", null);
    }

    @Test(expected = Exception.class)
    public void testFindPathNonExistentDestNode() {
        LinkedList<DiGraph.LabeledEdge<String, String>> path = MarvelPaths.findPath(graph1, "Bennet, James", "dummy");
    }

    @Test(expected = Exception.class)
    public void testFindPathNonExistentStartNode() {
        LinkedList<DiGraph.LabeledEdge<String, String>> path = MarvelPaths.findPath(graph1, "dummy", "Bennet, James");
    }

    @Test
    public void testLoadAndBuildGraph() {
        DiGraph<String, String> graph = MarvelPaths.loadGraph("dummy3.tsv");
        assertEquals(16, graph.size());
        graph.addNode("node A");
        graph.addNode("node B");
        graph.addEdge("node A", "node B", "label A to B");
        assertEquals(18, graph.size());
    }

    @Test
    public void testBuildAndLoadGraph() {
        DiGraph<String, String> graph = new DiGraph<>();
        graph.addNode("node A");
        graph.addNode("node B");
        graph.addEdge("node A", "node B", "label A to B");
        assertEquals(2, graph.size());
        graph = MarvelPaths.loadGraph("dummy3.tsv");
        assertEquals(16, graph.size());
    }
}
