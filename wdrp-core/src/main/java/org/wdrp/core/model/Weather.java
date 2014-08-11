package org.wdrp.core.model;

import java.util.List;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class Weather {
	private Multimap<String,Cloud> clouds;
	
	public Weather() {
		clouds = ArrayListMultimap.create();
	}
	
	public void addCloud(String time, Cloud c) {
		clouds.put(time, c);
	}
	
	public List<Cloud> getClouds(String time) {
		return (List<Cloud>) clouds.get(time);
	}
	
	public Multimap<String,Cloud> getClouds() {
		return clouds;
	}
}
