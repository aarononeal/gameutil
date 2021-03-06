package net.bytten.gameutil.algorithms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import net.bytten.gameutil.Vec2I;


/**
 * Horribly messy (but compact and generic) A* implementation.
 */
public class AStar<Id extends Comparable<Id>> {
    public static interface IClient<Id> {
        public Collection<Id> getNeighbors(Id roomId);
        public Vec2I getVec2I(Id roomId);
    }
    
    protected class DistanceComparator implements Comparator<Id> {
        public int compare(Id id1, Id id2) {
            double s1 = fScore.get(id1),
                   s2 = fScore.get(id2);
            if (s1 < s2) return -1;
            if (s1 == s2) return 0;
            return 1;
        }
    }
    protected final DistanceComparator DISTCMP = new DistanceComparator();
    
    protected Map<Id,Double> gScore = new TreeMap<Id,Double>(),
                                 fScore = new TreeMap<Id,Double>();
    protected Map<Id,Id> cameFrom = new TreeMap<Id,Id>();
    protected Set<Id> closedSet = new TreeSet<Id>();
    protected Queue<Id> openSet = new PriorityQueue<Id>(110, DISTCMP);
    protected IClient<Id> client;
    protected Id to;
    protected Set<Id> fromSet;
    
    public AStar(IClient<Id> client, Set<Id> fromSet, Id to) {
        this.client = client;
        this.to = to;
        this.fromSet = new TreeSet<Id>(fromSet);
    }
    
    public AStar(Set<Id> from, Id to) {
        this(null, from, to);
    }
    
    public AStar(IClient<Id> client, Id from, Id to) {
        this.client = client;
        this.to = to;
        this.fromSet = new TreeSet<Id>();
        this.fromSet.add(from);
    }
    
    public AStar(Id from, Id to) {
        this(null, from, to);
    }
    
    public void setClient(IClient<Id> client) {
        this.client = client;
    }
    
    public Id getCameFrom(Id id) {
        return cameFrom.get(id);
    }
    
    protected double heuristicDistance(Vec2I pos) {
        // Manhattan distance heuristic
        Vec2I toPos = client.getVec2I(to);
        return Math.abs(toPos.x - pos.x) + Math.abs(toPos.y - pos.y);
    }
    
    protected double edgeCost(Id from, Id to) {
        return client.getVec2I(from).distance(client.getVec2I(to));
    }
    
    protected void updateFScore(Id id) {
        fScore.put(id, gScore.get(id) + heuristicDistance(client.getVec2I(id)));
    }
    
    public List<Id> solve() {
        /* See this page for the algorithm:
         * http://en.wikipedia.org/wiki/A*_search_algorithm
         */
        
        for (Id from: fromSet) {
            openSet.add(from);
            gScore.put(from, 0.0);
            updateFScore(from);
        }
        while (!openSet.isEmpty()) {
            Id current = openSet.remove();
            
            if (current.equals(to))
                return reconstructPath();
            
            closedSet.add(current);
            
            for (Id neighbor: client.getNeighbors(current)) {
                
                if (closedSet.contains(neighbor))
                    continue;
                
                double dist = edgeCost(current, neighbor);
                double g = gScore.get(current) + dist;
                
                if ((!openSet.contains(neighbor) || g < gScore.get(neighbor)) &&
                        !Double.isInfinite(g)) {
                    cameFrom.put(neighbor, current);
                    gScore.put(neighbor, g);
                    updateFScore(neighbor);
                    openSet.add(neighbor);
                }
            }
        }
        return null;
    }
    
    protected Id nextStep() {
        List<Id> path = solve();
        if (path == null || path.size() == 0) return null;
        return path.get(0);
    }
    
    protected List<Id> reconstructPath() {
        List<Id> result = new ArrayList<Id>();
        Id current = to;
        while (!fromSet.contains(current)) {
            result.add(current);
            current = cameFrom.get(current);
        }
        Collections.reverse(result);
        return result;
    }

}
