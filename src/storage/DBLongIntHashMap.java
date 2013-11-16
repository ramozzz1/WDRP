package storage;

import gnu.trove.map.hash.THashMap;

public class DBLongIntHashMap extends DBHashMap<Long, Integer> {

	public DBLongIntHashMap(THashMap<Long,Integer> map) {
		super(map);
	}
}
