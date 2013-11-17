package storage;

import gnu.trove.map.hash.THashMap;

import java.io.File;

import org.mapdb.BTreeKeySerializer;
import org.mapdb.BTreeMap;
import org.mapdb.DB;
import org.mapdb.DBMaker;

public class DBHashMap<K,V> {

	private DB db;
	private BTreeMap<K, V> map;

	public DBHashMap () {
		this.db = DBMaker
				.newTempFileDB()
				.transactionDisable()
				.asyncFlushDelay(100)
				.closeOnJvmShutdown()
				.deleteFilesAfterClose()
				.cacheHardRefEnable()
				.randomAccessFileEnableKeepIndexMapped()
                .make();
		
		this.map = db.createTreeMap("map"+System.nanoTime()).keySerializer(BTreeKeySerializer.ZERO_OR_POSITIVE_LONG).make();
	}
	
	public DBHashMap(THashMap<K,V> m) {
		this();
		this.map.putAll(m);
	}
	
	public void put(K key, V value) {
		this.map.put(key, value);
	}
	
	public V get(K key) {
		return this.map.get(key);
	}

	public boolean containsKey(K key) {
		return this.map.containsKey(key);
	}
	
	@Override
	public String toString() {
		return this.map.descendingMap().toString();
	}
}
