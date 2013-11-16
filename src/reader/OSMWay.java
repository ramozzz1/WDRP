package reader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OSMWay {
	
	private List<Long> nodesRef;
	private Map<String,String> tags;
	
	public OSMWay() {
		this.nodesRef = new ArrayList<Long>();
		this.tags = new HashMap<String,String>();
	}
	
	public OSMWay(String type, String roadType) {
		this.nodesRef = new ArrayList<Long>();
		this.tags = new HashMap<String,String>();
	}

	public String getRoadType() {
		return tags.get("highway");
	}
	
	public void addNodeRef(long ref) {
		nodesRef.add(ref);
	}
	
	public void addTag(String key, String value) {
		tags.put(key, value);
	}

	public int getNumOfNodeRef() {
		return nodesRef.size();
	}

	public List<Long> getNodesRef() {
		return nodesRef;
	}

	public boolean isHighway() {
		return tags.get("highway") != null;
	}
}
