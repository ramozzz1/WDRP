package org.wdrp.core.model;

import java.util.Set;

public class Weather {
	private Set<Cloud> clouds;
	
	public Set<Cloud> getClouds() {
		return clouds;
	}

	public void setClouds(Set<Cloud> clouds) {
		this.clouds = clouds;
	}
}
