package cs3110.hw3;


import java.util.*;

public class Graph<T> {
    private HashMap<T, Set<T>> adjacencyList;
    private int edges;

    /* This class must have a default constructor. */
    public Graph() {
        adjacencyList = new HashMap<>();
        this.edges = 0;
    }

    /**
     * Adds a vertex to the graph.
     *
     * @param u
     * @return False if vertex u was already in the graph, True otherwise.
     */
    public boolean addVertex(T u) {
        /* TODO */
        if (!hasVertex(u)) {
            adjacencyList.put(u, new HashSet<>());
            return true;
        }
        return false;
    }

    /**
     * Adds edge (u,v) to the graph.
     *
     * @param u
     * @param v
     * @return Returns false if the edge was already present, true otherwise.
     */
    public boolean addEdge(T u, T v) {
        /* TODO */
        if (!hasVertex(u)) {
            addVertex(u);
        }
        if (!hasVertex(v)) {
            addVertex(v);
        }
        if (!hasEdge(u, v)) {
            adjacencyList.get(u).add(v);
            edges++;
            return true;
        }

        return false;

    }
    protected boolean removeEdge(T u, T v) {
        if(hasEdge(u,v)){
            adjacencyList.get(u).remove(v);
            edges--;
            return true;
        }
        return false;
    }

    /**
     * @return |V|
     */
    public int getVertexCount() {
        /* TODO */
        return adjacencyList.size();
    }

    /**
     * @param v
     * @return True if vertex v is present in the graph, false otherwise.
     */
    public boolean hasVertex(T v) {
        /* TODO */
        return adjacencyList.containsKey(v);
    }

    /**
     * Provides access to every vertex in the graph.
     *
     * @return An object that iterates over V.
     */
    public Iterable<T> getVertices() {
        /* TODO */
        return adjacencyList.keySet();
    }

    /**
     * @return |E|
     */
    public int getEdgeCount() {
        /* TODO */
        return edges;
    }

    /**
     * @param u One endpoint of the edge.
     * @param v The other endpoint of the edge.
     * @return True if edge (u,v) is present in the graph, false otherwise.
     */
    public boolean hasEdge(T u, T v) {
        /* TODO */
        if (!hasVertex(u) || !hasVertex(v)) {
            return false;
        }
        return adjacencyList.get(u).contains(v);
    }

    /**
     * Returns all neighbors of vertex u.
     *
     * @param u A vertex.
     * @return The neighbors of u.
     */
    public Iterable<T> getNeighbors(T u) {
        /* TODO */
        return adjacencyList.getOrDefault(u,Collections.emptySet());
    }


    /**
     * @param u
     * @param v
     * @return True if v is a neighbor of u.
     */
    public boolean isNeighbor(T u, T v) {
        if (!hasVertex(u) || !hasVertex(v)) {
            return false;
        }
        if (adjacencyList.get(u).contains(v)) {
            return true;
        }
        return false;
    }

    /**
     * Finds the length of the shortest path from vertex s to all other vertices in the graph.
     *
     * @param s The source vertex.
     * @return A map of shortest path distances. Every reachable vertex v should be present as a key mapped to the length of the shortest s->v path.
     */
    public Map<T, Long> getShortestPaths(T s) {
        /* TODO */
        if (!hasVertex(s)) {
            return Collections.emptyMap();
        }
        Map<T, Long> distance = new HashMap<>();
        Set<T> visited = new HashSet<>();
        Queue<T> queue = new LinkedList<>();
        queue.add(s);
        distance.put(s, 0L);
        while (!queue.isEmpty()) {
            T u = queue.poll();
            visited.add(u);
            for (T v : adjacencyList.get(u)) {
                if (!visited.contains(v)) {
                    visited.add(v);
                    distance.put(v, distance.get(u) + 1);
                    queue.add(v);

                }
            }
        }
     return distance;
    }

}
