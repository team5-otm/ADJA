package at.vcity.androidim.tools;

import at.vcity.androidim.types.UserInfo;

/*
 * This class can store friendInfo and check userkey and username combination 
 * according to its stored data
 */
public class UserController 
{
	
	private static UserInfo[] usersInfo = null;
	//private static FriendInfo[] unapprovedFriendsInfo = null;
	private static String activeUser;
	
	public static void setFriendsInfo(UserInfo[] userInfo)
	{
		UserController.usersInfo = userInfo;
	}
	
	
	
	public static UserInfo checkUser(String username, String userKey)
	{
		UserInfo result = null;
		if (usersInfo != null) 
		{
			for (int i = 0; i < usersInfo.length; i++) 
			{
				if ( usersInfo[i].userName.equals(username) && 
						usersInfo[i].userKey.equals(userKey)
					)
				{
					result = usersInfo[i];
					break;
				}				
			}			
		}		
		return result;
	}
	
	public static void setActiveFriend(String userName){
		activeUser = userName;
	}
	
	public static String getActiveUser()
	{
		return activeUser;
	}



	public static UserInfo getUserInfo(String username) 
	{
		UserInfo result = null;
		if (usersInfo != null) 
		{
			for (int i = 0; i < usersInfo.length; i++) 
			{
				if ( usersInfo[i].userName.equals(username) )
				{
					result = usersInfo[i];
					break;
				}				
			}			
		}		
		return result;
	}



//	public static void setUnapprovedFriendsInfo(FriendInfo[] unapprovedFriends) {
//		unapprovedFriendsInfo = unapprovedFriends;		
//	}



	public static UserInfo[] getUsersInfo() {
		return usersInfo;
	}


//
//	public static FriendInfo[] getUnapprovedFriendsInfo() {
//		return unapprovedFriendsInfo;
//	}
//	
	
	

}
