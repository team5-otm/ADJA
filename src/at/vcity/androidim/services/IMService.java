/* 
 * Copyright (C) 2007 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package at.vcity.androidim.services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.EditText;
import at.vcity.androidim.UserSignIn;
import at.vcity.androidim.Messaging;
import at.vcity.androidim.R;
import at.vcity.androidim.communication.SocketOperator;
import at.vcity.androidim.interfaces.IAppManager;
import at.vcity.androidim.interfaces.ISocketOperator;
import at.vcity.androidim.interfaces.IUpdateData;
import at.vcity.androidim.tools.CompanyController;
import at.vcity.androidim.tools.FriendController;
import at.vcity.androidim.tools.LocalStorageHandler;
import at.vcity.androidim.tools.MessageController;
import at.vcity.androidim.tools.UserController;
import at.vcity.androidim.tools.XMLHandler;
import at.vcity.androidim.types.CompanyInfo;
import at.vcity.androidim.types.FriendInfo;
import at.vcity.androidim.types.MessageInfo;
import at.vcity.androidim.types.StaffInfo;


/**
 * This is an example of implementing an application service that runs locally
 * in the same process as the application.  The {@link LocalServiceController}
 * and {@link LocalServiceBinding} classes show how to interact with the
 * service.
 *
 * <p>Notice the use of the {@link NotificationManager} when interesting things
 * happen in the service.  This is generally how background services should
 * interact with the user, rather than doing something more disruptive such as
 * calling startActivity().
 */
public class IMService extends Service implements IAppManager, IUpdateData {
//	private NotificationManager mNM;
	
	public static String USERNAME;
	public static final String TAKE_MESSAGE = "Take_Message";
	public static final String FRIEND_LIST_UPDATED = "Take Friend List";
	public static final String COMPANY_LIST_UPDATED = "Take Company List";
	public static final String STAFF_LIST_UPDATED = "Take Staff List";
	public static final String MESSAGE_LIST_UPDATED = "Take Message List";
	public ConnectivityManager conManager = null; 
	private final int UPDATE_TIME_PERIOD = 15000;
//	private static final INT LISTENING_PORT_NO = 8956;
//	private String rawFriendList = new String();
	private String rawCompanyList = new String();
	private String rawUserList = new String();
	private String rawMessageList = new String();
	private String rawStoreList = new String();
	private String rawStaffList = new String();

	ISocketOperator socketOperator = new SocketOperator(this);

	private final IBinder mBinder = new IMBinder();
	private String username;
	private String password;
	private String message_time;
	private boolean authenticatedUser = false;
	private boolean authenticatedCompany = false;
	private boolean authenticatedStaff = false;
	 // timer to take the updated data from server
	private Timer timer;
	

	private LocalStorageHandler localstoragehandler; 
	
	private NotificationManager mNM;

	public class IMBinder extends Binder {
		public IAppManager getService() {
			return IMService.this;
		}
		
	}
	   
    @Override
    public void onCreate() 
    {   	
         mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

         localstoragehandler = new LocalStorageHandler(this);
        // Display a notification about us starting.  We put an icon in the status bar.
     //   showNotification();
    	conManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
    	new LocalStorageHandler(this);
    	
    	// Timer is used to take the friendList info every UPDATE_TIME_PERIOD;
		timer = new Timer();   
		
		Thread thread = new Thread()
		{
			@Override
			public void run() {			
				
				//socketOperator.startListening(LISTENING_PORT_NO);
				Random random = new Random();
				int tryCount = 0;
				while (socketOperator.startListening(10000 + random.nextInt(20000))  == 0 )
				{		
					tryCount++; 
					if (tryCount > 10)
					{
						// if it can't listen a port after trying 10 times, give up...
						break;
					}
					
				}
			}
		};		
		thread.start();
    
    }

/*
    @Override
    public void onDestroy() {
        // Cancel the persistent notification.
        mNM.cancel(R.string.local_service_started);

        // Tell the user we stopped.
        Toast.makeText(this, R.string.local_service_stopped, Toast.LENGTH_SHORT).show();
    }
*/	

	@Override
	public IBinder onBind(Intent intent) 
	{
		return mBinder;
	}




	/**
	 * Show a notification while this service is running.
	 * @param msg 
	 **/
    private void showNotification(String username, String msg) 
	{       
        // Set the icon, scrolling text and TIMESTAMP
    	String title = "AndroidIM: You got a new Message! (" + username + ")";
 				
    	String text = username + ": " + 
     				((msg.length() < 5) ? msg : msg.substring(0, 5)+ "...");
    	
    	//NotificationCompat.Builder notification = new NotificationCompat.Builder(R.drawable.stat_sample, title,System.currentTimeMillis());
    	NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
    	.setSmallIcon(R.drawable.stat_sample)
    	.setContentTitle(title)
    	.setContentText(text); 
    	
    	

        Intent i = new Intent(this, Messaging.class);
        i.putExtra(CompanyInfo.COMPANY_NAME, username);
        i.putExtra(MessageInfo.MESSAGETEXT, msg);	
        
        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                i, 0);

        // Set the info for the views that show in the notification panel.
        // msg.length()>15 ? MSG : msg.substring(0, 15);
        mBuilder.setContentIntent(contentIntent); 
        
        mBuilder.setContentText("New message from " + username + ": " + msg);
        
        //TODO: it can be improved, for instance message coming from same user may be concatenated 
        // next version
        
        // Send the notification.
        // We use a layout id because it is a unique number.  We use it later to cancel.
        mNM.notify((username+msg).hashCode(), mBuilder.build());
    }
	 

	public String getUsername() {
		return this.username;
	}

	
	public String getMessage_time() {
		return message_time;
	}

	public void setMessage_time(String message_time) {
		this.message_time = message_time;
	}

	public String sendMessage(String  username, String  tousername, String message) throws UnsupportedEncodingException 
	{			
		String params = "username="+ URLEncoder.encode(this.username,"UTF-8") +
						"&password="+ URLEncoder.encode(this.password,"UTF-8") +
						"&to=" + URLEncoder.encode(tousername,"UTF-8") +
						"&message="+ URLEncoder.encode(message,"UTF-8") +
						"&action="  + URLEncoder.encode("sendMessage","UTF-8")+
						"&";		
		Log.i("PARAMS", params);
		return socketOperator.sendHttpRequest(params);		
	}

	
//	private String getFriendList() throws UnsupportedEncodingException 	{		
//		// after authentication, server replies with friendList xml
//		
//		 rawFriendList = socketOperator.sendHttpRequest(getAuthenticateUserParams(username, password));
//		 if (rawFriendList != null) {
//			 this.parseFriendInfo(rawFriendList);
//		 }
//		 return rawFriendList;
//	}
//	
	private String getMessageList() throws UnsupportedEncodingException 	{		
		// after authentication, server replies with friendList xml
		
		 rawMessageList = socketOperator.sendHttpRequest(getAuthenticateUserParams(username, password));
		 if (rawMessageList != null) {
			 this.parseMessageInfo(rawMessageList);
		 }
		 return rawMessageList;
	}
	
	private String getCompanyList() throws UnsupportedEncodingException 	{		
		// after authentication, server replies with friendList xml
		
		 rawCompanyList = socketOperator.sendHttpRequest(getAuthenticateUserParams(username, password));
		 if (rawCompanyList != null) {
			 this.parseCompanyInfo(rawCompanyList);
		 }
		 return rawCompanyList;
	}
	
	private String getStaffList() throws UnsupportedEncodingException  {
		// TODO Auto-generated method stub
		rawStaffList = socketOperator.sendHttpRequest(getAuthenticateCompanyParams(username, password));
		if (rawStaffList != null){
			this.parseStaffInfo(rawStaffList);
		}
		
		return rawStaffList;
	}

	
	private String getStoreList() throws UnsupportedEncodingException 	{		
		// after authentication, server replies with friendList xml
		
		rawStoreList = socketOperator.sendHttpRequest(getAuthenticateUserParams(username, password));
		 if (rawStoreList != null) {
			 this.parseCompanyInfo(rawStoreList);
		 }
		 return rawStoreList;
	}
	

	/**
	 * authenticateUser: it authenticates the user and if succesful
	 * it returns the friend list or if authentication is failed 
	 * it returns the "0" in string type
	 * @throws UnsupportedEncodingException 
	 * */
	public String authenticateUser(String usernameText, String passwordText) throws UnsupportedEncodingException 
	{
		this.username = usernameText;
		this.password = passwordText;	
		
		this.authenticatedUser = false;
		
		String result = this.getCompanyList(); //socketOperator.sendHttpRequest(getAuthenticateUserParams(username, password));
		if (result != null && !result.equals(UserSignIn.AUTHENTICATION_FAILED)) 
		{			
			// if user is authenticated then return string from server is not equal to AUTHENTICATION_FAILED
			this.authenticatedUser = true;
			rawCompanyList = result;
			USERNAME = this.username;
			Intent i = new Intent(COMPANY_LIST_UPDATED);					
			i.putExtra(CompanyInfo.COMPANY_LIST, rawCompanyList);
			sendBroadcast(i);
			
			timer.schedule(new TimerTask()
			{			
				public void run() 
				{
					try {					
						//rawFriendList = IMService.this.getFriendList();
						// sending friend list 
						Intent i = new Intent(COMPANY_LIST_UPDATED);
						Intent i2 = new Intent(MESSAGE_LIST_UPDATED);
						String tmp = IMService.this.getCompanyList();
						String tmp2 = IMService.this.getMessageList();
						if (tmp != null) {
							i.putExtra(CompanyInfo.COMPANY_LIST, tmp);
							sendBroadcast(i);	
							Log.i("company list broadcast sent ", "");
						
						if (tmp2 != null) {
							i2.putExtra(MessageInfo.MESSAGE_LIST, tmp2);
							sendBroadcast(i2);	
							Log.i("company list broadcast sent ", "");
						}
						}
						else {
							Log.i("company list returned null", "");
						}
					}
					catch (Exception e) {
						e.printStackTrace();
					}					
				}			
			}, UPDATE_TIME_PERIOD, UPDATE_TIME_PERIOD);
		}
		
		return result;		
	}

	public String authenticateCompany(String usernameText, String passwordText) throws UnsupportedEncodingException 
	{
		this.username = usernameText;
		this.password = passwordText;	
		
		this.authenticatedCompany = false;
		
		String result = this.getStaffList(); //socketOperator.sendHttpRequest(getAuthenticateUserParams(username, password));
		if (result != null && !result.equals(UserSignIn.AUTHENTICATION_FAILED)) 
		{			
			// if user is authenticated then return string from server is not equal to AUTHENTICATION_FAILED
			this.authenticatedCompany = true;
			rawStaffList = result;
			USERNAME = this.username;
			Intent i = new Intent(STAFF_LIST_UPDATED);					
			i.putExtra(StaffInfo.STAFF_LIST, rawStaffList);
			sendBroadcast(i);
			
			timer.schedule(new TimerTask()
			{			
				public void run() 
				{
					try {					
						//rawFriendList = IMService.this.getFriendList();
						// sending friend list 
						Intent i = new Intent(STAFF_LIST_UPDATED);
				//		Intent i2 = new Intent(MESSAGE_LIST_UPDATED);
						String tmp = IMService.this.getStaffList();
				//		String tmp2 = IMService.this.getMessageList();
						if (tmp != null) {
							i.putExtra(FriendInfo.FRIEND_LIST, tmp);
							sendBroadcast(i);	
							Log.i("staff list broadcast sent ", "");
						
//						if (tmp2 != null) {
//							i2.putExtra(MessageInfo.MESSAGE_LIST, tmp2);
//							sendBroadcast(i2);	
//							Log.i("friend list broadcast sent ", "");
//						}
						}
						else {
							Log.i("friend list returned null", "");
						}
					}
					catch (Exception e) {
						e.printStackTrace();
					}					
				}			
			}, UPDATE_TIME_PERIOD, UPDATE_TIME_PERIOD);
		}
		
		return result;		
	}

	
	public String authenticateStaff(String usernameText, String passwordText) throws UnsupportedEncodingException 
	{
		this.username = usernameText;
		this.password = passwordText;	
		
		this.authenticatedUser = false;
		
		String result = this.getUserList(); //socketOperator.sendHttpRequest(getAuthenticateUserParams(username, password));
		if (result != null && !result.equals(UserSignIn.AUTHENTICATION_FAILED)) 
		{			
			// if user is authenticated then return string from server is not equal to AUTHENTICATION_FAILED
			this.authenticatedUser = true;
			rawCompanyList = result;
			USERNAME = this.username;
			Intent i = new Intent(FRIEND_LIST_UPDATED);					
			i.putExtra(FriendInfo.FRIEND_LIST, rawCompanyList);
			sendBroadcast(i);
			
			timer.schedule(new TimerTask()
			{			
				public void run() 
				{
					try {					
						//rawFriendList = IMService.this.getFriendList();
						// sending friend list 
						Intent i = new Intent(FRIEND_LIST_UPDATED);
						Intent i2 = new Intent(MESSAGE_LIST_UPDATED);
						String tmp = IMService.this.getFriendList();
						String tmp2 = IMService.this.getMessageList();
						if (tmp != null) {
							i.putExtra(FriendInfo.FRIEND_LIST, tmp);
							sendBroadcast(i);	
							Log.i("friend list broadcast sent ", "");
						
						if (tmp2 != null) {
							i2.putExtra(MessageInfo.MESSAGE_LIST, tmp2);
							sendBroadcast(i2);	
							Log.i("friend list broadcast sent ", "");
						}
						}
						else {
							Log.i("friend list returned null", "");
						}
					}
					catch (Exception e) {
						e.printStackTrace();
					}					
				}			
			}, UPDATE_TIME_PERIOD, UPDATE_TIME_PERIOD);
		}
		
		return result;		
	}

public void messageReceived(String username, String message, String message_time) 
	{				
		
		//FriendInfo friend = FriendController.getFriendInfo(username);
		MessageInfo msg = MessageController.checkMessage(username);
		if ( msg != null)
		{			
			Intent i = new Intent(TAKE_MESSAGE);
		
			i.putExtra(MessageInfo.USERID, msg.userid);			
			i.putExtra(MessageInfo.MESSAGETEXT, msg.messagetext);
			i.putExtra(MessageInfo.SENDTIME, msg.sendtime);
			sendBroadcast(i);
			String activeCompany = CompanyController.getActiveCompany();
			String activeUser = UserController.getActiveUser();
			if (activeFriend == null || activeFriend.equals(username) == false) 
			{
			//	localstoragehandler.insert(username,this.getUsername(), message.toString(), this.getMessage_time());
				showNotification(username, message);
			}
			
			Log.i("TAKE_MESSAGE broadcast sent by im service", "");
		}	
		
	}  
	
	private String getAuthenticateUserParams(String usernameText, String passwordText) throws UnsupportedEncodingException 
	{			
		String params = "username=" + URLEncoder.encode(usernameText,"UTF-8") +
						"&password="+ URLEncoder.encode(passwordText,"UTF-8") +
						"&action="  + URLEncoder.encode("authenticateUser","UTF-8")+
						"&port="    + URLEncoder.encode(Integer.toString(socketOperator.getListeningPort()),"UTF-8") +
						"&";		
		
		return params;		
	}

	private String getAuthenticateStaffParams(String staffnameText, String passwordText) throws UnsupportedEncodingException 
	{			
		String params = "username=" + URLEncoder.encode(staffnameText,"UTF-8") +
						"&password="+ URLEncoder.encode(passwordText,"UTF-8") +
						"&action="  + URLEncoder.encode("authenticateStaff","UTF-8")+
						"&port="    + URLEncoder.encode(Integer.toString(socketOperator.getListeningPort()),"UTF-8") +
						"&";		
		
		return params;		
	}
	private String getAuthenticateCompanyParams(String companynameText, String passwordText) throws UnsupportedEncodingException 
	{			
		String params = "username=" + URLEncoder.encode(companynameText,"UTF-8") +
						"&password="+ URLEncoder.encode(passwordText,"UTF-8") +
						"&action="  + URLEncoder.encode("authenticateCompany","UTF-8")+
						"&port="    + URLEncoder.encode(Integer.toString(socketOperator.getListeningPort()),"UTF-8") +
						"&";		
		
		return params;		
	}
	
	public void setUserKey(String value) 
	{		
	}

	public boolean isNetworkConnected() {
		return conManager.getActiveNetworkInfo().isConnected();
	}
	
	public boolean isUserAuthenticated(){
		return authenticatedUser;
	}
	
	public boolean isStaffAuthenticated(){
		return authenticatedStaff;
	}
	
	public boolean isCompanyAuthenticated(){
		return authenticatedCompany;
	}
	
//	public String getLastRawFriendList() {		
//		return this.rawFriendList;
//	}

	public String getLastRawCompanyList() {		
		return this.rawCompanyList;
	}

	public String getLastRawUserList() {		
		return this.rawUserList;
	}
	@Override
	public String getLastRawStoreList() {
		// TODO Auto-generated method stub
		return this.rawStoreList;
	}

	@Override
	public void onDestroy() {
		Log.i("IMService is being destroyed", "...");
		super.onDestroy();
	}
	
	public void exit() 
	{
		timer.cancel();
		socketOperator.exit(); 
		socketOperator = null;
		this.stopSelf();
	}
	
	public String signUpUser(String usernameText, String passwordText,
			String emailText) 
	{
		String params = "username=" + usernameText +
						"&password=" + passwordText +
						"&action=" + "signUpUser"+
						"&email=" + emailText+
						"&";
		
		String result = socketOperator.sendHttpRequest(params);		
		
		return result;
	}	

	
	public String signUpCompany(String companyName, String adminPassword,
			String companyEmail,String companyAddress,String city,String country,String rc_number) 
	{
		String params = "company_name=" + companyName +
						"&admin_password=" + adminPassword +
						"&action=" + "companySignUp"+
						"&company_email=" + companyEmail +
						"&company_address=" + companyAddress +
						"&city=" + city +
						"&country=" + country +
						"&rc_number=" + rc_number +
						"&";
		
		String result = socketOperator.sendHttpRequest(params);		
		
		return result;
	}
	
	@Override
	public String signUpStaff(String companyName, String staffName,
			String staffPasswordText) {
		// TODO Auto-generated method stub
		return null;
	}

//	public String addNewFriendRequest(String friendUsername) 
//	{
//		String params = "username=" + this.username +
//		"&password=" + this.password +
//		"&action=" + "addNewFriend" +
//		"&friendUserName=" + friendUsername +
//		"&";
//
//		String result = socketOperator.sendHttpRequest(params);		
//		
//		return result;
//	}

//	public String sendFriendsReqsResponse(String approvedFriendNames,
//			String discardedFriendNames) 
//	{
//		String params = "username=" + this.username +
//		"&password=" + this.password +
//		"&action=" + "responseOfFriendReqs"+
//		"&approvedFriends=" + approvedFriendNames +
//		"&discardedFriends=" +discardedFriendNames +
//		"&";
//
//		String result = socketOperator.sendHttpRequest(params);		
//		
//		return result;
//		
//	} 
	
//	private void parseFriendInfo(String xml)
//	{			
//		try 
//		{
//			SAXParser sp = SAXParserFactory.newInstance().newSAXParser();
//			sp.parse(new ByteArrayInputStream(xml.getBytes()), new XMLHandler(IMService.this));		
//		} 
//		catch (ParserConfigurationException e) {			
//			e.printStackTrace();
//		}
//		catch (SAXException e) {			
//			e.printStackTrace();
//		} 
//		catch (IOException e) {			
//			e.printStackTrace();
	
//		}	
//	}
	private void parseCompanyInfo(String xml)
	{			
		try 
		{
			SAXParser sp = SAXParserFactory.newInstance().newSAXParser();
			sp.parse(new ByteArrayInputStream(xml.getBytes()), new XMLHandler(IMService.this));		
		} 
		catch (ParserConfigurationException e) {			
			e.printStackTrace();
		}
		catch (SAXException e) {			
			e.printStackTrace();
		} 
		catch (IOException e) {			
			e.printStackTrace();
		}	
	}
	
	private void parseStaffInfo(String xml) {
		// TODO Auto-generated method stub
		try 
		{
			SAXParser sp = SAXParserFactory.newInstance().newSAXParser();
			sp.parse(new ByteArrayInputStream(xml.getBytes()), new XMLHandler(IMService.this));		
		} 
		catch (ParserConfigurationException e) {			
			e.printStackTrace();
		}
		catch (SAXException e) {			
			e.printStackTrace();
		} 
		catch (IOException e) {			
			e.printStackTrace();
		}	
	}

	private void parseMessageInfo(String xml)
	{			
		try 
		{
			SAXParser sp = SAXParserFactory.newInstance().newSAXParser();
			sp.parse(new ByteArrayInputStream(xml.getBytes()), new XMLHandler(IMService.this));		
		} 
		catch (ParserConfigurationException e) {			
			e.printStackTrace();
		}
		catch (SAXException e) {			
			e.printStackTrace();
		} 
		catch (IOException e) {			
			e.printStackTrace();
		}	
	}
	
	

//	public void updateData(MessageInfo[] messages,FriendInfo[] friends,
//			FriendInfo[] unApprovedFriends, String userKey) 
//	{
//		this.setUserKey(userKey);
//		//FriendController.	
//		MessageController.setMessagesInfo(messages);
//		//Log.i("MESSAGEIMSERVICE","messages.length="+messages.length);
//		
//		int i = 0;
//		while (i < messages.length){
//			messageReceived(messages[i].userid,messages[i].messagetext, messages[i].sendtime);
//			//appManager.messageReceived(messages[i].userid,messages[i].messagetext);
//			i++;
//		}
//		
//		
//		FriendController.setFriendsInfo(friends);
//		FriendController.setUnapprovedFriendsInfo(unApprovedFriends);
//		
//	}
	public void updateData(MessageInfo[] messages,CompanyInfo[] companys,
		String userKey) 
	{
		this.setUserKey(userKey);
		//FriendController.	
		MessageController.setMessagesInfo(messages);
		//Log.i("MESSAGEIMSERVICE","messages.length="+messages.length);
		
		int i = 0;
		while (i < messages.length){
			messageReceived(messages[i].userid,messages[i].messagetext, messages[i].sendtime);
			//appManager.messageReceived(messages[i].userid,messages[i].messagetext);
			i++;
		}
		
		
		CompanyController.setCompanysInfo(companys);
		
	}

	
	
	
	

	
	
	
	
}