package com.mps.insight.c4.report;

import java.util.ArrayList;
import java.util.List;

import com.mps.insight.global.InsightConstant;

public class DynamicMonthCreater {

	/**
	 * 
	 * @param reportType=ex.- JR1,BR2,ARBT etc
	 * @param fromDate :- this should be in format of MM-YYYY (e.g :- 01-2016) 
	 * @param toDate :-  this should be in format of MM-YYYY (e.g :- 12-2017)
	 * @return string :- like Jan-2018,Feb-2018
	 */
	
	public String getMonthQuery(String reportType,String fromDate, String toDate) {
		
		String finalString="";
		String temp="";
		temp=getMonthListNew(reportType, fromDate, toDate);
		finalString="("+convertCommaToPlus(temp)+") as `Reporting Period Total`,"+temp;
		
		return finalString;
	}
	
	public String getHeaderTotalQuery(String reportType,String fromDate, String toDate) {
		String monthList=getMonthListNew(reportType, fromDate, toDate);
		String resultstr="";
		String tempstr="";
		String totalstr="";
		StringBuilder sb=new StringBuilder();
		StringBuilder sbtotal=new StringBuilder();
		String[] arrMonth=monthList.split(",");
		for (String month : arrMonth) {
			sb.append("SUM("+month+") as "+month+",");
			sbtotal.append("SUM("+month+"),");
		}
		tempstr=sbtotal.toString().substring(0,sbtotal.toString().lastIndexOf(","));
		resultstr=sb.toString().substring(0,sb.toString().lastIndexOf(","));
		totalstr="("+convertCommaToPlus(tempstr)+") as `Reporting Period Total`,"+resultstr;
		return totalstr;
	}
	
	public String convertCommaToPlus(String str){
		
		
		return str.replace(",", "+");
	}
	
	public String getMonthListNew(String reportType,String fromDate, String toDate) {

		String[] toMonthYear = toDate.split("-");
		String[] fromMonthYear = fromDate.split("-");
		String[] months = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
		int toMonth = Integer.parseInt(toMonthYear[0]);
		int toYear = Integer.parseInt(toMonthYear[1]);
		int fromMonth = Integer.parseInt(fromMonthYear[0]);
		int fromYear = Integer.parseInt(fromMonthYear[1]);
		int startMonth = fromMonth - 1;
		int endMonth = toMonth - 1;
		String temp="";
		StringBuffer sb = new StringBuffer();
		for (int i = fromYear; i <= toYear; i++) {
			if (fromYear == toYear) {
				startMonth = fromMonth - 1;
				endMonth = toMonth - 1;
			}else{
			if (i == toYear) {
				startMonth = 0;
				endMonth = toMonth - 1;
			}
			if (i == fromYear) {
				endMonth = 11;
			} else {
				startMonth = 0;
			}
			}
			for (int j = startMonth; j <= endMonth; j++) {
					sb.append(months[j] + "_" + i + ",");
			}
		}
		temp=sb.toString().substring(0,sb.toString().lastIndexOf(","));
		
		return temp;
	}
	
	public String getMonthList(String reportType,String fromDate, String toDate) {

		String[] toMonthYear = toDate.split("-");
		String[] fromMonthYear = fromDate.split("-");
		String[] months = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
		int toMonth = Integer.parseInt(toMonthYear[0]);
		int toYear = Integer.parseInt(toMonthYear[1]);
		int fromMonth = Integer.parseInt(fromMonthYear[0]);
		int fromYear = Integer.parseInt(fromMonthYear[1]);
		int startMonth = fromMonth - 1;
		int endMonth = toMonth - 1;
		String temp="";
		StringBuffer sb = new StringBuffer();
		for (int i = fromYear; i <= toYear; i++) {
			if (fromYear == toYear) {
				startMonth = fromMonth - 1;
				endMonth = toMonth - 1;
			}else{
			if (i == toYear) {
				startMonth = 0;
				endMonth = toMonth - 1;
			}
			if (i == fromYear) {
				endMonth = 11;
			} else {
				startMonth = 0;
			}
			}
			for (int j = startMonth; j <= endMonth; j++) {
					sb.append(months[j] + "_" + i + " as `"+months[j] + "-" + i+"`,");
			}
		}
		temp=sb.toString().substring(0,sb.toString().lastIndexOf(","));
		
		return temp;
	}
	
	public List<String> getJR1TypeTotalMonthList(String reportType,String fromDate, String toDate) {

		List<String> list=new ArrayList<>();
		String[] toMonthYear = toDate.split("-");
		String[] fromMonthYear = fromDate.split("-");
		String[] months = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
		int toMonth = Integer.parseInt(toMonthYear[0]);
		int toYear = Integer.parseInt(toMonthYear[1]);
		int fromMonth = Integer.parseInt(fromMonthYear[0]);
		int fromYear = Integer.parseInt(fromMonthYear[1]);
		int startMonth = fromMonth - 1;
		int endMonth = toMonth - 1;
		String temp="";
		String temp1="";
		String temp3="";
		String temp4="";
		StringBuffer sb = new StringBuffer();
		StringBuilder sb1 = new StringBuilder();
		StringBuilder sbt1 = new StringBuilder();
		StringBuilder sbt2 = new StringBuilder();
		StringBuilder htmlquery = new StringBuilder();
		StringBuilder pdfquery = new StringBuilder();
		StringBuilder totalhtml = new StringBuilder();
		StringBuilder totalpdf = new StringBuilder();
		htmlquery.append("(");
		pdfquery.append("(");
		sb1.append("(");
		sbt2.append("(");
		for (int i = fromYear; i <= toYear; i++) {
			if (fromYear == toYear) {
				startMonth = fromMonth - 1;
				endMonth = toMonth - 1;
			}else{
			if (i == toYear) {
				startMonth = 0;
				endMonth = toMonth - 1;
			}
			if (i == fromYear) {
				endMonth = 11;
			} else {
				startMonth = 0;
			}
			}
			for (int j = startMonth; j <= endMonth; j++) {
					sb.append(",("+months[j] + "_" + i + "_html+").append(months[j] + "_" + i + "_pdf) as `"+months[j] + "-" + i+"`");
					sb1.append(months[j] + "_" + i + "_html+").append(months[j] + "_" + i + "_pdf+");
					sbt1.append(",(SUM("+months[j] + "_" + i + "_html)+SUM(").append(months[j] + "_" + i + "_pdf)) as `"+months[j] + "-" + i+"`");
					sbt2.append("SUM("+months[j] + "_" + i + "_html)+SUM(").append(months[j] + "_" + i + "_pdf)+");
					htmlquery.append(months[j] + "_" + i + "_html+");
					pdfquery.append(months[j] + "_" + i + "_pdf+");
					totalhtml.append("SUM("+months[j] + "_" + i + "_html)+");
					totalpdf.append("SUM("+months[j] + "_" + i + "_pdf)+");
			}
		}
		temp=sb1.toString().substring(0,sb1.toString().lastIndexOf("+"));
		temp1=sbt2.toString().substring(0,sbt2.toString().lastIndexOf("+"));
		temp4=htmlquery.toString().substring(0,htmlquery.toString().lastIndexOf("+"))+"),"+
				pdfquery.toString().substring(0,pdfquery.toString().lastIndexOf("+"))+")";
		temp3=totalhtml.toString().substring(0,totalhtml.toString().lastIndexOf("+"))+" as `Reporting Period HTML`,"
		+totalpdf.toString().substring(0,totalpdf.toString().lastIndexOf("+"))+" as `Reporting Period PDF`";
		
		list.add(temp+"),"+temp4+sb.toString());
		list.add(temp1+") as `Reporting Period Total`,"+temp3+sbt1.toString());
		return list;
	}
	//SUM(M_201901) as `Jan-2019`,SUM(M_201902) as `Feb-2019`,SUM(M_201903) as `Mar-2019`,
	public String createMonthQueryC5(int toMonth, int toYear, int fromMonth,
			int fromYear) {

		int monthloop = (toYear - fromYear) * 12 + (toMonth - fromMonth + 1);
		StringBuilder sb = new StringBuilder();
		int startMonth = fromMonth;
		String zero = "";
		for (int j = 0; j < monthloop; j++) {
			if (startMonth > 12) {
				startMonth = 1;
				fromYear = fromYear + 1;
				
				if (startMonth < 10) {
					zero = "0";
				} else {
					zero = "";
				}
				
				sb.append("SUM(M_")
				.append(fromYear)
				.append(zero)
				.append(startMonth)
				.append(") as `")
				.append(InsightConstant.MONTH_ARRAY[startMonth] + "-"
						+ fromYear).append("`,");
				startMonth++;
				
			} else {
				if (startMonth < 10) {
					zero = "0";
				} else {
					zero = "";
				}
				sb.append("SUM(M_")
						.append(fromYear)
						.append(zero)
						.append(startMonth)
						.append(") as `")
						.append(InsightConstant.MONTH_ARRAY[startMonth] + "-"
								+ fromYear).append("`,");
				startMonth++;
			}

		}

		return sb.toString();
	}
	
	
	public String createMonthQueryC5MonthName(int toMonth, int toYear, int fromMonth,
			int fromYear) {

		int monthloop = (toYear - fromYear) * 12 + (toMonth - fromMonth + 1);
		StringBuilder sb = new StringBuilder();
		int startMonth = fromMonth;
		String zero = "";
		for (int j = 0; j < monthloop; j++) {
			if (startMonth > 12) {
				startMonth = 1;
				fromYear = fromYear + 1;
				
				if (startMonth < 10) {
					zero = "0";
				} else {
					zero = "";
				}
				
				sb.append("SUM(`"+InsightConstant.MONTH_ARRAY[startMonth] + "-"
						+ fromYear+"`) AS `"+InsightConstant.MONTH_ARRAY[startMonth] + "-"
						+ fromYear).append("`,");
				startMonth++;
				
			} else {
				if (startMonth < 10) {
					zero = "0";
				} else {
					zero = "";
				}
				sb.append("SUM(`"+InsightConstant.MONTH_ARRAY[startMonth] + "-"
						+ fromYear+"`) AS `"+InsightConstant.MONTH_ARRAY[startMonth] + "-"
						+ fromYear).append("`,");
				startMonth++;
			}

		}

		return sb.toString();
	}
	
	//M_201901,M_201902,M_201903,
	public String MonthGroupByQuery(int toMonth, int toYear, int fromMonth,
			int fromYear) {

		int monthloop = (toYear - fromYear) * 12 + (toMonth - fromMonth + 1);
		StringBuilder sb = new StringBuilder();
		int startMonth = fromMonth;
		String zero = "";
		for (int j = 0; j < monthloop; j++) {
			if (startMonth > 12) {
				startMonth = 1;
				fromYear = fromYear + 1;
				
				if (startMonth < 10) {
					zero = "0";
				} else {
					zero = "";
				}
				
				sb.append("M_")
				.append(fromYear)
				.append(zero)
				.append(startMonth)
				.append(",");
				startMonth++;
				
			} else {
				if (startMonth < 10) {
					zero = "0";
				} else {
					zero = "";
				}
				sb.append("M_")
						.append(fromYear)
						.append(zero)
						.append(startMonth)
					    .append(",");
				startMonth++;
			}

		}

		return sb.toString();
	}
	//(M_201901+M_201902+M_201903) 
	public String createTotalMonthQueryC5(int toMonth, int toYear, int fromMonth,
			int fromYear,String as) {

		int monthloop = (toYear - fromYear) * 12 + (toMonth - fromMonth + 1);
		StringBuilder sb = new StringBuilder();
		int startMonth = fromMonth;
		String zero = "";
		sb.append("(");
		for (int j = 0; j < monthloop; j++) {
			if (startMonth > 12) {
				startMonth = 1;
				fromYear = fromYear + 1;
				
				if (startMonth < 10) {
					zero = "0";
				} else {
					zero = "";
				}
				sb.append("M_")
				.append(fromYear)
				.append(zero)
				.append(startMonth).append("+");
				startMonth++;
			} else {
				if (startMonth < 10) {
					zero = "0";
				} else {
					zero = "";
				}
				sb.append("M_")
						.append(fromYear)
						.append(zero)
						.append(startMonth).append("+");
				startMonth++;
			}

		}
		sb.deleteCharAt(sb.toString().lastIndexOf("+"));
		sb.append(") ");
		return sb.toString();
	}
	//SUM(M_201901) as `Jan-2019`,SUM(M_201902) as `Feb-2019`,SUM(M_201903) as `Mar-2019`,
	public String createSumMonthQueryC5(int toMonth, int toYear, int fromMonth,
			int fromYear) {

		int monthloop = (toYear - fromYear) * 12 + (toMonth - fromMonth + 1);
		StringBuilder sb = new StringBuilder();
		int startMonth = fromMonth;
		String zero = "";
		for (int j = 0; j < monthloop; j++) {
			if (startMonth > 12) {
				startMonth = 1;
				fromYear = fromYear + 1;
				
				if (startMonth < 10) {
					zero = "0";
				} else {
					zero = "";
				}
				sb.append("SUM(M_")
						.append(fromYear)
						.append(zero)
						.append(startMonth)
						.append(") as `")
						.append(InsightConstant.MONTH_ARRAY[startMonth] + "-"
								+ fromYear).append("`,");
			} else {
				if (startMonth < 10) {
					zero = "0";
				} else {
					zero = "";
				}
				sb.append("SUM(M_")
						.append(fromYear)
						.append(zero)
						.append(startMonth)
						.append(") as `")
						.append(InsightConstant.MONTH_ARRAY[startMonth] + "-"
								+ fromYear).append("`,");
				startMonth++;
			}

		}

		return sb.toString();
	}
	//(SUM(M_201901)+SUM(M_201902)+SUM(M_201903)) as Reporting_Period_Total,
	public String createTotalMonthSumQueryC5(int toMonth, int toYear, int fromMonth,
			int fromYear) {

		int monthloop = (toYear - fromYear) * 12 + (toMonth - fromMonth + 1);
		StringBuilder sb = new StringBuilder();
		int startMonth = fromMonth;
		String zero = "";
		sb.append("(");
		for (int j = 0; j < monthloop; j++) {
			if (startMonth > 12) {
				startMonth = 1;
				fromYear = fromYear + 1;
				
				if (startMonth < 10) {
					zero = "0";
				} else {
					zero = "";
				}
				sb.append("SUM(M_")
						.append(fromYear)
						.append(zero)
						.append(startMonth).append(")+");
				
			} else {
				if (startMonth < 10) {
					zero = "0";
				} else {
					zero = "";
				}
				sb.append("SUM(M_")
						.append(fromYear)
						.append(zero)
						.append(startMonth).append(")+");
				startMonth++;
			}

		}
		sb.deleteCharAt(sb.toString().lastIndexOf("+"));
		sb.append(") as Reporting_Period_Total,");
		return sb.toString();
	}
	//SUM(M_201901)+SUM(M_201902)+SUM(M_201903)
	public String createTotalMonthSumQueryC55(int toMonth, int toYear, int fromMonth,
			int fromYear) {

		int monthloop = (toYear - fromYear) * 12 + (toMonth - fromMonth + 1);
		StringBuilder sb = new StringBuilder();
		int startMonth = fromMonth;
		String zero = "";
		sb.append("");
		for (int j = 0; j < monthloop; j++) {
			if (startMonth > 12) {
				startMonth = 1;
				fromYear = fromYear + 1;
				
				if (startMonth < 10) {
					zero = "0";
				} else {
					zero = "";
				}
				sb.append("SUM(M_")
						.append(fromYear)
						.append(zero)
						.append(startMonth).append(")+");
				
			} else {
				if (startMonth < 10) {
					zero = "0";
				} else {
					zero = "";
				}
				sb.append("SUM(M_")
						.append(fromYear)
						.append(zero)
						.append(startMonth).append(")+");
				startMonth++;
			}

		}
		sb.deleteCharAt(sb.toString().lastIndexOf("+"));
		return sb.toString();
	}
	//D_201901 as `Jan-2019`,D_201902 as `Feb-2019`,D_201903 as `Mar-2019`,
	public String createDailyhQueryC5(int toMonth, int toYear, int fromMonth,
			int fromYear) {

		int monthloop = (toYear - fromYear) * 12 + (toMonth - fromMonth + 1);
		StringBuilder sb = new StringBuilder();
		int startMonth = fromMonth;
		String zero = "";
		for (int j = 0; j < monthloop; j++) {
			if (startMonth > 12) {
				startMonth = 1;
				fromYear = fromYear + 1;
			} else {
				if (startMonth < 10) {
					zero = "0";
				} else {
					zero = "";
				}
				sb.append("D_")
						.append(fromYear)
						.append(zero)
						.append(startMonth)
						.append(" as `")
						.append(InsightConstant.MONTH_ARRAY[startMonth] + "-"
								+ fromYear).append("`,");
				startMonth++;
			}

		}

		return sb.toString();
	}
	//(D_201901+D_201902+D_201903) as Reporting_Period_Total,
	public String createTotalDailyQueryC5(int toMonth, int toYear, int fromMonth,
			int fromYear) {

		int monthloop = (toYear - fromYear) * 12 + (toMonth - fromMonth + 1);
		StringBuilder sb = new StringBuilder();
		int startMonth = fromMonth;
		String zero = "";
		sb.append("(");
		for (int j = 0; j < monthloop; j++) {
			if (startMonth > 12) {
				startMonth = 1;
				fromYear = fromYear + 1;
			} else {
				if (startMonth < 10) {
					zero = "0";
				} else {
					zero = "";
				}
				sb.append("D_")
						.append(fromYear)
						.append(zero)
						.append(startMonth).append("+");
				startMonth++;
			}

		}
		sb.deleteCharAt(sb.toString().lastIndexOf("+"));
		sb.append(") as Reporting_Period_Total,");
		return sb.toString();
	}
	// M_201901 AS `Jan-2019`,M_201902 AS `Feb-2019`,M_201903 AS `Mar-2019` 
	public String sumMonthQueryC5(int toMonth, int toYear, int fromMonth,
			int fromYear) {

		int monthloop = (toYear - fromYear) * 12 + (toMonth - fromMonth + 1);
		StringBuilder sb = new StringBuilder();
		int startMonth = fromMonth;
		String zero = "";
		for (int j = 0; j < monthloop; j++) {
			if (startMonth > 12) {
				startMonth = 1;
				fromYear = fromYear + 1;
				
				if (startMonth < 10) {
					zero = "0";
				} else {
					zero = "";
				}
				sb.append("M_")
						.append(fromYear)
						.append(zero)
						.append(startMonth)
						.append(" AS `")
						.append(InsightConstant.MONTH_ARRAY[startMonth] + "-"
								+ fromYear).append("`,");
			} else {
				if (startMonth < 10) {
					zero = "0";
				} else {
					zero = "";
				}
				sb.append("M_")
						.append(fromYear)
						.append(zero)
						.append(startMonth)
						.append(" AS `")
						.append(InsightConstant.MONTH_ARRAY[startMonth] + "-"
								+ fromYear).append("`,");
				startMonth++;
			}

		}
		String str=sb.substring(0,sb.length()-1);
		return str.toString();
	}
	//M_201901,M_201902,M_201903
	public String createMonthSumQueryC5(int toMonth, int toYear, int fromMonth,
			int fromYear) {
		
		int monthloop = (toYear - fromYear) * 12 + (toMonth - fromMonth + 1);
		StringBuilder sb = new StringBuilder();
		int startMonth = fromMonth;
		String zero = "";
		for (int j = 0; j < monthloop; j++) {
			if (startMonth > 12) {
				startMonth = 1;
				fromYear = fromYear + 1;
				
				if (startMonth < 10) {
					zero = "0";
				} else {
					zero = "";
				}
				
				sb.append("M_")
				.append(fromYear)
				.append(zero)
				.append(startMonth)
				.append(",");
				
			} else {
				if (startMonth < 10) {
					zero = "0";
				} else {
					zero = "";
				}
				sb.append("M_")
						.append(fromYear)
						.append(zero)
						.append(startMonth)
						.append(",");
				startMonth++;
			}

	}
		String str=sb.substring(0,sb.length()-1);
		return str.toString();

	}	
}
