package com.mps.insight.impl;

public class SearchAccountImpl {

	public String getAccountCodeFromSearch(String account, int webmartID) {
		String searchTerm = null;
		//String searchTerm1 = null;
		if (account.contains("[{")) {
			searchTerm = account.substring(account.indexOf("[{") + 2, account.indexOf("}]"));
			
		} else if (account.contains(" - ")) {
			searchTerm = account.substring(account.indexOf(" - ")+3);
		} else {
			searchTerm = account;
		}
		return searchTerm;
	}

}
