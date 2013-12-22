package algorithm;

import gnu.trove.map.hash.TLongLongHashMap;

import java.util.Set;

import model.Arc;
import model.Graph;
import model.LatLonPoint;
import model.Path;

public abstract class AbstractRoutingAlgorithm {
	public Graph graph;
	public Set<Long> visitedNodesMarks;
	protected static final long NULL_NODE = -1L;
	
	public AbstractRoutingAlgorithm(Graph graph) {
		this.graph = graph;
	}
	
	public abstract int computeShortestPath(long sourceId, long targetId);

	public abstract void precompute();
	
	public abstract String getName();

	public abstract Path extractPath(long targetId);

	public abstract Set<Long> getVisitedNodes();
	
	/**
	 * Construct path from nodeId to the first element of previous (i.e) the one that was inserted the first (source)
	 * Note: shortcut edges are transformed into "real" edges
	 * @param previous
	 * @param targetId
	 * @return
	 */
	public Path contructPath(TLongLongHashMap previous, long targetId) {
		Path p = new Path();
		
		long currNode = targetId;
		if(previous.containsKey(currNode)) {
			do {				
				long prevNode = previous.get(currNode);
				Arc a = graph.getEdge(prevNode, currNode);
				if(a != null && a.isShortcut()) {
					//Path shPath = new Path(); 
					convertShortcutArcToPath(a, prevNode, currNode, p);
					//System.out.println(shPath);
					System.out.println(p);
					//shPath.connect(p);
					//p = shPath;
				}
				else
					p.addNode(graph.getNode(currNode), a);
				currNode = prevNode;
			} while(currNode != NULL_NODE);
		}
		
		return p;
	}
	
	private void convertShortcutArcToPath(Arc a,long fromNode, long toNode, Path p) {
		if(a != null && a.isShortcut()) {
			long shortcutNode = a.getShortcutNode();
			
			//replace the shortcut by adding edge from the shortcut node to the current node
			Arc arcSHFrom = graph.getEdge(shortcutNode, toNode);
			if(!arcSHFrom.isShortcut())
				p.addNode(graph.getNode(shortcutNode), arcSHFrom);
			else {
				convertShortcutArcToPath(arcSHFrom,shortcutNode,toNode,p);
			}
			
			//replace the shortcut by adding edge from the previous node to the shortcut node
			Arc arcSHTo = graph.getEdge(fromNode, shortcutNode);
			if(!arcSHTo.isShortcut())
				p.addNode(graph.getNode(fromNode), arcSHTo);
			else {
				convertShortcutArcToPath(arcSHTo,fromNode,shortcutNode,p);
			}
		}
	}
}
