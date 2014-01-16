package storage;

import gnu.trove.map.hash.THashMap;

import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.mapdb.BTreeKeySerializer;
import org.mapdb.BTreeMap;
import org.mapdb.DB;
import org.mapdb.DBMaker;

import algorithm.TransitNodeRoutingAlgorithm.PPDist;

public class DBHashMap<K,V> implements Map<K, V> {

	private DB db;
	private BTreeMap<K, V> map;

	public DBHashMap () {
		this.db = DBMaker
				.newTempFileDB()
				.transactionDisable()
				.cacheHardRefEnable()
				.syncOnCommitDisable()
				.asyncWriteEnable()
				.mmapFileEnableIfSupported()
				.freeSpaceReclaimQ(0)
				.fullChunkAllocationEnable()
				.deleteFilesAfterClose()
				.closeOnJvmShutdown()
				//.randomAccessFileEnableKeepIndexMapped()
                .make();
		
		this.map = db.createTreeMap("map"+System.nanoTime()).keySerializer(BTreeKeySerializer.ZERO_OR_POSITIVE_LONG).make();
	}
	
	public DBHashMap(THashMap<K,V> m) {
		this();
		this.map.putAll(m);
	}
	
	@Override
	public String toString() {
		return this.map.descendingMap().toString();
	}

	@Override
	public void clear() {
		this.map.clear();
	}

	@Override
	public boolean containsValue(Object arg0) {
		return this.map.containsValue(arg0);
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		return this.map.entrySet();
	}

	@Override
	public boolean isEmpty() {
		return this.map.isEmpty();
	}

	@Override
	public Set<K> keySet() {
		return this.map.keySet();
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> arg0) {
		this.map.putAll(arg0);
	}

	@Override
	public V remove(Object arg0) {
		return this.map.remove(arg0);
	}

	@Override
	public int size() {
		return this.map.size();
	}

	@Override
	public Collection<V> values() {
		return this.map.values();
	}

	@Override
	public boolean containsKey(Object key) {
		return this.map.containsKey(key);
	}

	@Override
	public V get(Object key) {
		return this.map.get(key);
	}

	@Override
	public V put(K key, V value) {
		return this.map.put(key, value);
	}
}
