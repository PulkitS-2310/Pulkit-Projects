package cs3110.hw3;

import java.util.*;

public class NetworkAnalyzer {
    private final Graph<String> graph = new Graph<>();

    public NetworkAnalyzer(List<String> hosts, Map<String, String> simplexConnections, Map<String, String> duplexConnections) {
        /* TODO */
        for (String host : hosts) {
            graph.addVertex(host);
        }
        for (Map.Entry<String, String> simplex : simplexConnections.entrySet()) {
            graph.addEdge(simplex.getKey(), simplex.getValue());
        }
        for (Map.Entry<String, String> duplex : duplexConnections.entrySet()) {
            graph.addEdge(duplex.getKey(), duplex.getValue());
            graph.addEdge(duplex.getValue(), duplex.getKey());
        }
    }
/*
 * It helps us to take the maximum delivery time from each node and then takes out the
 * maximum from those values which gives us the max delivery time from one node to another.
 */
    public long findMaxDeliveryTime() {
        /* TODO */
        long maxDeliveryTime = 0;
        for (String source : graph.getVertices()) {
            Map<String, Long> distances = graph.getShortestPaths(source);
            for (Map.Entry<String, Long> entry : distances.entrySet()) {
                maxDeliveryTime = Math.max(maxDeliveryTime, entry.getValue());

            }
        }

        return maxDeliveryTime;
    }
/*
 * We first go through all the elements of simplexConnectionsToRemove and verify whether
 * each key and value has an edge and if it does then it removes the edge and add it to our
 * new arraylist, then we iterate through all the vertices to check whether all the vertices have been
 * visited or not. If any vertex has not been visited then we call the dfs method (helper method) and
 * and later increase the count and then returns it. The method returns 0 if the graph is empty.
 */
    public int countMinToReconnect(Map<String, String> simplexConnectionsToRemove) {
        Set<String> visited = new HashSet<>();
        int Count = 0;
        List<Map.Entry<String, String>> remove = new ArrayList<>();
        for (Map.Entry<String, String> entry : simplexConnectionsToRemove.entrySet()) {
            if (graph.hasEdge(entry.getKey(), entry.getValue())) {
                graph.removeEdge(entry.getKey(), entry.getValue());
                remove.add(entry);
            }
        }
        for (String node : graph.getVertices()) {
            if (!visited.contains(node)) {
                dfs(node, visited);
                Count++;
            }
        }

        return Math.max(0, Count - 1);
    }

/*
 * Over here we first add the node to the hash set that contains all the visited nodes.
 * In the for loop we first check for all the outgoing edges of a node and verify whether
 * all of its neighbors have been added to the hash set or not.If not, then they are added
 * through recursion. In the second for loop we check for all the incoming edges and verify whether
 * whether that node has been  added to the hash set or not. If not, then it is added to the hashset.
 */

    private void dfs(String node, Set<String> visited) {
        visited.add(node);

        for (String neighbor : graph.getNeighbors(node)) {
            if (!visited.contains(neighbor)) {
                dfs(neighbor, visited);
            }
        }

        for (String other : graph.getVertices()) {
            for (String neighbor : graph.getNeighbors(other)) {
                if (neighbor.equals(node) && !visited.contains(other)) {
                    dfs(other, visited);
                    break;
                }
            }
        }
    }


}

