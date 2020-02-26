package com.mps.sushi.json.format;

import java.util.List;
import java.util.Map;

public class ReportItems {
	
	private String title;
	private Map<String, String> itemId;
	private Map<String, String> itemContributor;
	private Map<String, String> itemDates;	
	private Map<String, String> itemAttributes;
	private ReportItems itemParent;
	private ReportItems itemComponent;
	private String database;
	private String platform;	
	private String publisher;
	private Map<String, String> publisherId;
	private String dataType;
	private String sectionType;
	private String yop;
	private String accessType;
	private String accessMethod;
	private List<Performance> performance;
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public Map<String, String> getItemId() {
		return itemId;
	}

	public void setItemId(Map<String, String> itemId) {
		this.itemId = itemId;
	}
	
	public String getPlatform() {
		return platform;
	}
	
	public void setPlatform(String platform) {
		this.platform = platform;
	}
	
	public String getPublisher() {
		return publisher;
	}
	
	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}
	
	public Map<String, String> getPublisherId() {
		return publisherId;
	}
	
	public void setPublisherId(Map<String, String> publisherId) {
		this.publisherId = publisherId;
	}
	
	public String getDataType() {
		return dataType;
	}
	
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	
	public String getSectionType() {
		return sectionType;
	}
	
	public void setSectionType(String sectionType) {
		this.sectionType = sectionType;
	}
	
	public String getYop() {
		return yop;
	}
	
	public void setYop(String yop) {
		this.yop = yop;
	}
	
	public String getAccessType() {
		return accessType;
	}
	
	public void setAccessType(String accessType) {
		this.accessType = accessType;
	}
	
	public String getAccessMethod() {
		return accessMethod;
	}
	
	public void setAccessMethod(String accessMethod) {
		this.accessMethod = accessMethod;
	}

	public List<Performance> getPerformance() {
		return performance;
	}

	public void setPerformance(List<Performance> performance) {
		this.performance = performance;
	}
	
	public Map<String, String> getItemContributor() {
		return itemContributor;
	}

	public void setItemContributor(Map<String, String> itemContributor) {
		this.itemContributor = itemContributor;
	}

	public Map<String, String> getItemDates() {
		return itemDates;
	}

	public void setItemDates(Map<String, String> itemDates) {
		this.itemDates = itemDates;
	}

	public Map<String, String> getItemAttributes() {
		return itemAttributes;
	}

	public void setItemAttributes(Map<String, String> itemAttributes) {
		this.itemAttributes = itemAttributes;
	}

	public ReportItems getItemParent() {
		return itemParent;
	}

	public void setItemParent(ReportItems itemParent) {
		this.itemParent = itemParent;
	}

	public ReportItems getItemComponent() {
		return itemComponent;
	}

	public void setItemComponent(ReportItems itemComponent) {
		this.itemComponent = itemComponent;
	}
	
	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	

	
	
	
	

}
