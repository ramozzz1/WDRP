package org.wdrp.core.model;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.NavigableSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mapdb.BTreeKeySerializer;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Fun;

import com.google.common.collect.Lists;

public class Weather {
	private static Logger logger = Logger.getLogger(Weather.class);
	
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

	public String toGeoJSON(String time) {
		JSONObject featureCollection = new JSONObject();
	    try {
	        JSONArray featureList = new JSONArray();
	        for (Cloud c : getClouds(time)) {
	        	double[][] coordinates = c.getCoordinates();
	        	
	        	JSONObject polygon = new JSONObject();
	            polygon.put("type", "Polygon");
	            String coordinatesString = "[[";
	            String prefix = "";
	        	for (int i = 0; i < coordinates.length; i++) {
	        		coordinatesString += prefix + "[" + coordinates[i][0]+","+coordinates[i][1] +","+0+"]";
	        		prefix = ",";
	        	}
	        	coordinatesString += prefix + "[" + coordinates[0][0]+","+coordinates[0][1] +","+0+"]";
	            coordinatesString += "]]";
	        	polygon.put("coordinates", new JSONArray(coordinatesString));
	        	
	        	JSONObject feature = new JSONObject();
	        	feature.put("geometry", polygon);
	        	feature.put("properties", new JSONObject());
	        	feature.put("type", "Feature");
	        	featureList.put(feature);
	        }
	        
            featureCollection.put("features", featureList);
            featureCollection.put("type", "FeatureCollection");
	    } catch (JSONException e) {
	    	logger.error("can't save json object: "+e.toString());
	    }
	    // output the result
	    System.out.println(featureCollection.toString());
		return featureCollection.toString();
	}

	public int getNumberOfTimeSteps() throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("HH:mm");
		Date begin = format.parse(getBeginTime());
		Date end = format.parse(getEndTime());
		
		int diffMinutes = (int)((end.getTime() - begin.getTime())/1000)/60;
		return diffMinutes/getTimeStep();
	}

	public String addTimeStepAndGetTime(int i) throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("HH:mm");
		Date begin = format.parse(getBeginTime());
		Date dateAfter = new Date(begin.getTime() + (getTimeStep() * i * 60000));
		
		return format.format(dateAfter);
	}

	public int getIndexFromTime(String time) throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("HH:mm");
		Date begin = format.parse(getBeginTime());
		Date givenDate = format.parse(time);
		
		int diffMinutes = (int)((givenDate.getTime() - begin.getTime())/1000)/60;
		return diffMinutes/getTimeStep();
	}
}
