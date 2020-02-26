package com.mps.insight.product;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.json.JsonObject;

import com.mps.insight.dao.InsightDAO;
import com.mps.insight.dao.MyDataTable;
import com.mps.insight.dto.RequestMetaData;
import com.mps.insight.dto.UserDTO;
import com.mps.insight.dto.UserFavoriteDTO;
import com.mps.insight.global.TableMapper;

public class FavoriteSetting {
	
	private RequestMetaData rmd;
	
	public FavoriteSetting(RequestMetaData rmd){
		this.rmd = rmd;
	}
	//private static final Logger LOG = LoggerFactory.getLogger(FavoriteSetting.class);
	PublisherSettings pubsetting=null;
	public JsonObject getAccountsForFavorite(String searchCode,int webmartID) throws Exception {
		double iTracker=0.0;
		InsightDAO insightDao = null;
		MyDataTable mdt=null;
		JsonObject tempObject=null;
		
		int setNo=0;
		//Redis redis=new Redis();
		String publisher_name;
		try {
			pubsetting=new PublisherSettings(rmd);
			iTracker=1.0;
			publisher_name=pubsetting.getPublisherCode(webmartID);
			iTracker=2.0;
			insightDao =rmd.getInsightDao();
			iTracker=3.0;
			searchCode=searchCode.replaceAll("'","\\\\");
			String accountName1=searchCode;
			searchCode="%"+searchCode+"%";
			accountName1=accountName1+"*";
			iTracker=4.0;
			if(accountName1.indexOf(" ")!=-1){
				StringBuilder temp1=new StringBuilder("");
				String[] temp=accountName1.split(" ");
				for(int i=0;i<temp.length;i++){
					temp1.append(temp[i]+" +");				
				}
				temp1.deleteCharAt(temp1.length()-1);
				accountName1="+"+""+temp1;
			}
			
			if(searchCode.contains("[{")){
				searchCode = searchCode.substring(0, searchCode.indexOf("["))+"%";
				
			}
			iTracker=5.0;
			if(setNo==0){
				setNo=pubsetting.getSetNo(webmartID);
			}
			//insightDao = InsightDAO.getInstance();
			rmd.log("getPublisherIDFromWebmartID : Search Account =" + searchCode);
			//int pubID=pubsetting.getPublisherIDFromWebmartID(searchCode);
			StringBuilder stb = new StringBuilder();
			iTracker=6.0;
			/*stb.append("SELECT a.id,acc.webmart_id,acc.code,acc.name,acc.sub_type,")
			.append("( (1.3 * (MATCH(first_word) AGAINST ('"+accountName1+"' IN BOOLEAN MODE))) + (0.6 * (MATCH(other_words) ")
			.append("AGAINST ('"+accountName1+"' IN BOOLEAN MODE))) ) AS relevance FROM ")
			.append("srch_accounts acc LEFT JOIN accounts a ON acc.set_no=a.set_no WHERE acc.set_no="+setNo)
			.append(" AND acc.code=a.code AND (MATCH (first_word,other_words) AGAINST ('"+accountName1+"' IN BOOLEAN MODE) OR ")
			.append("acc.code LIKE '"+searchCode+"') AND a.sub_type IN (SELECT id FROM publisher_account_types ")
			.append("WHERE group_type='Account') ORDER BY relevance DESC,acc.name DESC LIMIT 0,100");
			*/
			
			//before changed
			/*stb.append("SELECT id, webmart_id, `code`, `name`, sub_type, '0.0' AS relevance ")
			.append("FROM accounts ")
			.append("WHERE (CODE LIKE '"+searchCode+"' OR NAME LIKE '"+searchCode+"') ")
			.append("AND set_no="+setNo+" ORDER BY NAME ASC");*/
			
			//after c5_accounts
			
			stb.append("SELECT `account_Code` as `code`, `account_name` as `name` ")
			.append("FROM "+TableMapper.TABALE.get("c5_accounts")+" ")
			.append("WHERE (account_Code LIKE '"+searchCode+"' OR account_name LIKE '"+searchCode+"') ")
			.append("AND STATUS=1 ORDER BY account_name ASC");
			
			
			iTracker=7.0;
			mdt = insightDao.executeSelectQueryMDT(stb.toString());
			iTracker=8.0;
			tempObject=mdt.getJson();
			rmd.log(" tempObject : "+tempObject.toString());
			//LOG.info(reportSection.toString());
		} catch (Exception e) {
			rmd.exception("FavoriteSetting : getAccountsForFavorite : iTracker : "+iTracker+" : "+e.toString());
		} finally {
			 
			/*if (insightDao != null) {
				insightDao.disconnect();
			}
			insightDao = null;*/
			mdt=null;
		}
		
		return tempObject;
	}

	public String addFavoritesAccounts(String favoriteLevel, String[] arrAccountIds, UserDTO userDto) throws Exception {
		double iTracker=0.0;
		InsightDAO insightDao = null;
		MyDataTable mdt=null;
		UserFavoriteDTO userFavorite = null;
		String result="";
		rmd.log("Start addFavoritesAccounts : Connection : Success");
		//Iterate for each account id and verify already exists or not.
		String publisher_name;
		try {
			pubsetting=new PublisherSettings(rmd);
			iTracker=1.0;
			publisher_name=pubsetting.getPublisherCode(userDto.getWebmartID());
			iTracker=2.0;
			insightDao =rmd.getInsightDao();
			iTracker=3.0;
			if(arrAccountIds.length != 0) {
				Account account = null;
				boolean flag;
				//String accountCode;
				StringBuilder stb;
				DateFormat dateFormat;
				String updatedAt = null;
				for(String accountId : arrAccountIds) {
					account = new Account(rmd);
					/*accountCode = account.getAccountCodeById(Long.parseLong(accountId));*/
					userFavorite = new UserFavoriteDTO();
					stb = new StringBuilder();
					dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
					updatedAt = null;

				    //If Favorite Level "user"
					iTracker=4.0;
				    if(favoriteLevel.equalsIgnoreCase("user")) {
				    	userFavorite.setUserCode(userDto.getUserCode());
				    	userFavorite.setWebmartId(userDto.getWebmartID());
				    	userFavorite.setAccountCode(accountId);
				    	userFavorite.setUpdatedBy(userDto.getUserCode());
						updatedAt = dateFormat.format(new Date(System.currentTimeMillis()));
						iTracker=5.0;
						//Verify if Favorites account already exixt of user.
						flag = account.verifyFavoritesUser(userFavorite.getUserCode(), userFavorite.getAccountCode(),userDto.getWebmartID());
						iTracker=6.0;
						if(flag == false) {
							stb.append("INSERT INTO "+TableMapper.TABALE.get("user_favorites_table")+" ("+TableMapper.TABALE.get("uf_user_code")+", "+TableMapper.TABALE.get("uf_account_code")+", "+TableMapper.TABALE.get("uf_account_code")+", updated_by, updated_at) ");
							stb.append("VALUES (");
							stb.append("'"+userFavorite.getUserCode()+"'");
							stb.append(", '"+userFavorite.getAccountCode()+"', 'USER', '"+userFavorite.getUpdatedBy());
							stb.append("', '"+updatedAt+"')");
							//insightDao = InsightDAO.getInstance();
							insightDao.executeInsertUpdateQuery(stb.toString());
							result="Added";
						}else{
						result="Error User Exist";
						}
				    }//If Favorite Level "publisher"
				    else if(userDto.getUserType().equalsIgnoreCase("publisher")) {
				    	iTracker=7.0;
				    	userFavorite.setWebmartId(userDto.getWebmartID());
				    	iTracker=8.0;
				    	userFavorite.setAccountCode(accountId);
				    	iTracker=9.0;
				    	userFavorite.setUpdatedBy(userDto.getUserCode());
				    	iTracker=10.0;
						updatedAt = dateFormat.format(new Date(System.currentTimeMillis()));
						iTracker=11.0;
						//Verify if Favorites account already exixt of publisher.
						flag = account.verifyFavoritesPublisher(userFavorite.getAccountCode(),userDto.getWebmartID());
						iTracker=12.0;
						if(flag == false) {
							stb.append("INSERT INTO "+TableMapper.TABALE.get("user_favorites_table")+" ("+TableMapper.TABALE.get("uf_user_code")+", "+TableMapper.TABALE.get("uf_account_code")+", "+TableMapper.TABALE.get("uf_level")+", updated_by, updated_at) ");
							stb.append("VALUES ('"+userDto.getUserCode()+"', '"+userFavorite.getAccountCode()+"', 'PUBLISHER', '"+userFavorite.getUpdatedBy());
							stb.append("', '"+updatedAt+"')");
							//insightDao = InsightDAO.getInstance();
							insightDao.executeInsertUpdateQuery(stb.toString());
							result="Added";
						}else{
							result="Error User Exist";
							}
				    }
				}
			}
		}
		catch (Exception e) {
			rmd.exception("FavoriteSetting : addFavoritesAccounts : iTracker : "+iTracker+" : "+e.toString());
			result="Exception in while adding";
		} 
		finally {
			
			/*if (insightDao != null) {
				insightDao.disconnect();
			}*/
			if(mdt!=null){
				mdt.destroy();
			}
			mdt = null;
			//insightDao = null;
		}
		return result;
	}
	
	public String removeFavorite(String str,int webmartID){
		double iTracker=0.0;
		InsightDAO insightDao = null;

		String publisher_name;
		try {
			pubsetting=new PublisherSettings(rmd);
			iTracker=1.0;
			publisher_name=pubsetting.getPublisherCode(webmartID);
			iTracker=2.0;
			insightDao =rmd.getInsightDao();
			StringBuilder stb = new StringBuilder();
			iTracker=3.0;
					stb = new StringBuilder();
					stb.append("DELETE FROM `"+TableMapper.TABALE.get("user_favorites_table")+"` WHERE "+TableMapper.TABALE.get("uf_account_code")+"='"+str+"'");
					insightDao.executeInsertUpdateQuery(stb.toString());
					rmd.log("query " + stb.toString());
		} catch (Exception e) {
			rmd.exception("FavoriteSetting : removeFavorite : iTracker : "+iTracker+" : "+e.toString());
			return "Fail";
		} finally {

			/*if (insightDao != null) {
				insightDao.disconnect();
			}
			insightDao = null;*/
		}
		
		return "removed";
	}
}
