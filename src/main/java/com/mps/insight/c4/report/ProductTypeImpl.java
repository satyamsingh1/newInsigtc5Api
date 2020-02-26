package com.mps.insight.c4.report;

import java.util.HashSet;
import java.util.Set;

import javax.json.JsonObject;

import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.product.PublisherSettings;

public class ProductTypeImpl {
	private RequestMetaData rmd= null; 
	
	public ProductTypeImpl(RequestMetaData rmd){
		this.rmd = rmd;
	}
	
	private int webmartID;
	//InsightDAO insightDao;
	//private static final Logger LOG = LoggerFactory.getLogger(ProductTypeImpl.class);
	public ProductTypeImpl(int webmartID) throws Exception {
		super();
		this.webmartID = webmartID;
		//insightDao=	InsightDAO.getInstance();
	}

	public int getWebmartID() {
		return webmartID;
	}

	public void setWebmartID(int webmartID) {
		this.webmartID = webmartID;
	}
	public JsonObject getPubGraph(){
		
		
		return null;
		
	}
	
	public Set<String> getProductList() throws Exception{
		PublisherSettings ps=new PublisherSettings(rmd);
		JsonObject jo=ps.getProductTrendType(rmd.getWebmartID());
		Set<String> product=jo.keySet();
		Set<String> productList=new HashSet<String>();
		for (String string : product) {
			rmd.log(string);
			productList.add(string);
		}
		return productList;
		
	}
}
