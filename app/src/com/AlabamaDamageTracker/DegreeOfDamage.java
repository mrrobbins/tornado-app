package com.AlabamaDamageTracker;

public class DegreeOfDamage {
	
	public Long id = null;
	public String indicatorAbbreviation = null;
	public String description = null;
	public Integer lowestWindspeed = null;
	public Integer expectedWindspeed = null;
	public Integer highestWindspeed = null;
	
	public String toString() {
		return String.format("%d - %s", id, description);
	}
	
	public String toStringShort() {
		String name = toString();
		return name.length() < 40 ? name : name.substring(0, 40).trim().concat("...");
	}

}
