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
	private String _beginTime;
	private String _endTime;
	private int _timeStep;
	
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
			if(_db.getAtomicString("beginTime") == null) _db.createAtomicString("beginTime", "");
			if(_db.getAtomicString("endTime") == null) _db.createAtomicString("endTime", "");
			if(_db.getAtomicInteger("timeStep") == null) _db.createAtomicInteger("timeStep", 0);
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
		_db.close();
	}

	public String getBeginTime() {
		if(_db != null)
			return _db.getAtomicString("beginTime").get();
		return _beginTime;
	}

	public void setBeginTime(String beginTime) {
		if(_db != null) {
			if(_db.getAtomicString("beginTime") == null) {
				_db.createAtomicString("beginTime", beginTime);
			} else {
				_db.getAtomicString("beginTime").set(beginTime);
			}
		}
		_beginTime = beginTime;
	}
	
	public String getEndTime() {
		if(_db != null)
			return _db.getAtomicString("endTime").get();
		return _endTime;
	}

	public void setEndTime(String endTime) {
		if(_db != null) {
			if(_db.getAtomicString("endTime") == null) {
				_db.createAtomicString("endTime", endTime);
			} else {
				_db.getAtomicString("endTime").set(endTime);
			}
		}
		_endTime = endTime;
	}
	
	public int getTimeStep() {
		if(_db != null)
			return _db.getAtomicInteger("timeStep").get();
		return _timeStep;
	}

	public void setTimeStep(int timeStep) {
		if(_db != null) {
			if(_db.getAtomicInteger("timeStep") == null) {
				_db.createAtomicInteger("timeStep", timeStep);
			} else {
				_db.getAtomicInteger("timeStep").set(timeStep);
			}
		}
		_timeStep = timeStep;
	}
}
