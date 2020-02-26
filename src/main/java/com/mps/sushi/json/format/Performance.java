package com.mps.sushi.json.format;

import java.util.List;

public class Performance {
	
	private DateRange period;	
	private List<Instance> instance;
	
	public DateRange getPeriod() {
		return period;
	}
	
	public void setPeriod(DateRange period) {
		this.period = period;
	}

	public List<Instance> getInstance() {
		return instance;
	}

	public void setInstance(List<Instance> instance) {
		this.instance = instance;
	}
	
	
	
}
