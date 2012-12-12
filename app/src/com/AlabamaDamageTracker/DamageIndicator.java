package com.AlabamaDamageTracker;

public class DamageIndicator {
	
	public Long id = null;
	public String description = null;
	public String abbreviation = null;
	
	public DamageIndicator() {}
	public DamageIndicator(Long id, String desc, String abbrev) {
		this.id = id;
		this.description = desc;
		this.abbreviation = abbrev;
	}
	
	public String toString() {
		return String.format("%d - %s", id, description);
	}
	
	public String toStringShort() {
		String name = toString();
		return name.length() < 40 ? name : name.substring(0, 40).trim().concat("...");
	}
}


