package org.wdrp.core.model;

import java.io.File;
import java.util.List;
import java.util.NavigableSet;
import java.util.TreeSet;

import org.mapdb.BTreeKeySerializer;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Fun;

import com.google.common.collect.Lists;

public class Weather {
	private DB _db;
	private NavigableSet<Fun.Tuple2<String, Cloud>> _clouds;
	
	public Weather(String fileName) {
		if(fileName != null) {
			_db = DBMaker
					.newFileDB(new File(fileName))
					//.newDirectMemoryDB()
					.transactionDisable()
					.asyncWriteEnable()
					.closeOnJvmShutdown()
					.make();
			
			_clouds = _db.createTreeSet("clouds").serializer(BTreeKeySerializer.TUPLE2).counterEnable().makeOrGet();
		}
		else {
			_clouds = new TreeSet<Fun.Tuple2<String,Cloud>>();
		}
	}
	
	public Weather() {
		this(null);
	}
	
	public void addCloud(String time, Cloud c) {
		_clouds.add(Fun.t2(time,c));
	}
	
	public Iterable<Cloud> getClouds(String time) {
		return Fun.filter(_clouds, time);
	}
	
	public List<Cloud> getCloudsAsList(String time) {
		return Lists.newArrayList(getClouds(time));
	}

	public void close() {
		_db.commit();
		_db.close();
	}
}
