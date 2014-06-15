package at.vcity.androidim.interfaces;

import java.io.UnsupportedEncodingException;


public interface IAppManager {
	
	public String getUsername();
	public String sendMessage(String username,String tousername, String message) throws UnsupportedEncodingException;
	public String authenticateUser(String usernameText, String passwordText) throws UnsupportedEncodingException; 
	public String authenticateCompany(String companyEmailText, String adminPasswordText) throws UnsupportedEncodingException; 
	public String authenticateStaff(String companyNameText, String passwordText) throws UnsupportedEncodingException; 
	public void messageReceived(String username, String message, String message_time);
//	public void setUserKey(String value);
	public boolean isNetworkConnected();
	public boolean isUserAuthenticated();
//	public String getLastRawFriendList();
	public String getLastRawCompanyList();
	public String getLastRawStoreList();
	public void exit();
	public String signUpUser(String usernameText, String passwordText, String email);
	public String signUpCompany(String companyName, String adminPassword,String companyEmail,String companyAddress,String city,String country,String rc_number);
	public String signUpStaff(String companyName, String staffName,String staffPasswordText);
//	public String addNewFriendRequest(String friendUsername);
//	public String sendFriendsReqsResponse(String approvedFriendNames,
//			String discardedFriendNames);

	
}
