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
	private String _timeStep;
	
	public Weather(String fileName, String beginTime, String endTime, String timeStep) {
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
		
		setBeginTime(beginTime);
		setEndTime(endTime);
		setTimeStep(timeStep);
	}
	
	public Weather() {
		this(null,null,null,null);
	}
	
	public Weather(String fileName) {
		this(fileName,null,null,null);
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
		return _beginTime;
	}

	public void setBeginTime(String beginTime) {
		if(_db != null)
			_db.createAtomicString("beginTime", beginTime).get();
		_beginTime = beginTime;
	}
	
	public String getEndTime() {
		return _endTime;
	}

	public void setEndTime(String endTime) {
		if(_db != null)
			_db.createAtomicString("endTime", endTime).get();
		_endTime = endTime;
	}
	
	public String getTimeStep() {
		return _timeStep;
	}

	public void setTimeStep(String timeStep) {
		if(_db != null)
			_db.createAtomicString("timeStep", timeStep).get();
		_timeStep = timeStep;
	}
}
