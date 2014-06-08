package at.vcity.androidim.tools;

import at.vcity.androidim.types.CompanyInfo;
import at.vcity.androidim.types.FriendInfo;

/*
 * This class can store friendInfo and check userkey and username combination 
 * according to its stored data
 */
public class CompanyController 
{
	
	private static CompanyInfo[] companysInfo = null;
	//private static FriendInfo[] unapprovedFriendsInfo = null;
	private static String activeCompany;
	
	public static void setCompanysInfo(CompanyInfo[] companyInfo)
	{
		CompanyController.companysInfo = companyInfo;
	}
	
	
	
	public static CompanyInfo checkCompany(String companyname, String companykey)
	{
		CompanyInfo result = null;
		if (companysInfo != null) 
		{
			for (int i = 0; i < companysInfo.length; i++) 
			{
				if ( companysInfo[i].companyName.equals(companyname) && 
						companysInfo[i].companyKey.equals(companykey)
					)
				{
					result = companysInfo[i];
					break;
				}				
			}			
		}		
		return result;
	}
	
	public static void setActiveCompany(String companyName){
		activeCompany = companyName;
	}
	
	public static String getActiveCompany()
	{
		return activeCompany;
	}



	public static CompanyInfo getCompanyInfo(String companyname) 
	{
		CompanyInfo result = null;
		if (companysInfo != null) 
		{
			for (int i = 0; i < companysInfo.length; i++) 
			{
				if ( companysInfo[i].companyName.equals(companyname) )
				{
					result = companysInfo[i];
					break;
				}				
			}			
		}		
		return result;
	}



//	public static void setUnapprovedFriendsInfo(FriendInfo[] unapprovedFriends) {
//		unapprovedFriendsInfo = unapprovedFriends;		
//	}



	public static CompanyInfo[] getCompanysInfo() {
		return companysInfo;
	}



	public static FriendInfo[] getUnapprovedFriendsInfo() {
		return unapprovedFriendsInfo;
	}
	
	
	

}
