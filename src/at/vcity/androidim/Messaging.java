package at.vcity.androidim;


import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import at.vcity.androidim.interfaces.IAppManager;
import at.vcity.androidim.services.IMService;
import at.vcity.androidim.tools.AwesomeAdapter;
import at.vcity.androidim.tools.AwesomeAdapter2;
import at.vcity.androidim.tools.CompanyController;
import at.vcity.androidim.tools.FriendController;
import at.vcity.androidim.tools.LocalStorageHandler;
import at.vcity.androidim.tools.Message;
import at.vcity.androidim.types.CompanyInfo;
import at.vcity.androidim.types.FriendInfo;
import at.vcity.androidim.types.MessageInfo;


public class Messaging extends ListActivity {

	private static final int MESSAGE_CANNOT_BE_SENT = 0;
	static String sender;
	private ArrayList<Message> messages;
	private AwesomeAdapter adapter;
	private EditText text;
	//private EditText messageHistoryText;
	private Button sendMessageButton;
	private IAppManager imService;
	//private FriendInfo friend = new FriendInfo();
	private CompanyInfo company = new CompanyInfo(); 
	private LocalStorageHandler localstoragehandler; 
	private Cursor dbCursor;
	private int mResourceId;
	
	private ServiceConnection mConnection = new ServiceConnection() {
      
		
		
		public void onServiceConnected(ComponentName className, IBinder service) {          
            imService = ((IMService.IMBinder)service).getService();
        }
        public void onServiceDisconnected(ComponentName className) {
        	imService = null;
            Toast.makeText(Messaging.this, R.string.local_service_stopped,
                    Toast.LENGTH_SHORT).show();
        }
    };
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);	   
		
		setContentView(R.layout.message); //messaging_screen);
				
	//	messageHistoryText = (EditText) findViewById(R.id.messageHistory);
		
		text = (EditText) findViewById(R.id.text);
		
		text.requestFocus();			
		
		sendMessageButton = (Button) findViewById(R.id.sendMessageButton);
		
		Bundle extras = this.getIntent().getExtras();
		
		
		friend.userName = extras.getString(FriendInfo.USERNAME);
		friend.ip = extras.getString(FriendInfo.IP);
		friend.port = extras.getString(FriendInfo.PORT);
		String msg = extras.getString(MessageInfo.MESSAGETEXT);
		String msg_time = extras.getString(MessageInfo.SENDTIME); 
		sender = friend.userName;
		
		this.setTitle(sender);
		messages = new ArrayList<Message>();
		
		adapter = new AwesomeAdapter(this, messages);
		setListAdapter(adapter);
		//	EditText friendUserName = (EditText) findViewById(R.id.friendUserName);
	//	friendUserName.setText(friend.userName);
		
		
		localstoragehandler = new LocalStorageHandler(this);
		dbCursor = localstoragehandler.get(friend.userName, IMService.USERNAME );
		
		if (dbCursor.getCount() > 0){
		int noOfScorer = 0;
		dbCursor.moveToFirst();
		    while ((!dbCursor.isAfterLast())&&noOfScorer<dbCursor.getCount()) 
		    {
		        noOfScorer++;

	//			this.appendToMessageHistory(dbCursor.getString(2) , dbCursor.getString(3));
		        dbCursor.moveToNext();
		    }
		}
		localstoragehandler.close();
		
		if (msg != null) 
		{
			addNewMessage(new Message(msg,false));
	//		this.appendToMessageHistory(friend.userName , msg);
			((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).cancel((friend.userName+msg).hashCode());
		}
		
		sendMessageButton.setOnClickListener(new OnClickListener(){
			CharSequence message;
			Handler handler = new Handler();		
			public void onClick(View arg0) {
		//		final String msg_time_to_db = UTCdateFormat(); //UTC time to send to database
				//final String msg_time_to_display = getCurrentTimeStamp();// formatted time to display on phone, its in am/pm format
				message = text.getText();
				if (message.length()>0) 
				{		
	//				appendToMessageHistory(imService.getUsername(), message.toString());
					addNewMessage(new Message(message.toString(), true));
			//		localstoragehandler.insert(imService.getUsername(), friend.userName, message.toString(), msg_time_to_display);
								
					text.setText("");
					Thread thread = new Thread(){					
						public void run() {
							try {
								if (imService.sendMessage(imService.getUsername(), friend.userName, message.toString()) == null)
								{
									
									handler.post(new Runnable(){	

										public void run() {
											
									        Toast.makeText(getApplicationContext(),R.string.message_cannot_be_sent, Toast.LENGTH_LONG).show();

											
											//showDialog(MESSAGE_CANNOT_BE_SENT);										
										}
										
									});
								}
							} catch (UnsupportedEncodingException e) {
								Toast.makeText(getApplicationContext(),R.string.message_cannot_be_sent, Toast.LENGTH_LONG).show();

								e.printStackTrace();
							}
						}						
					};
					thread.start();
										
				}
				
			}});
		
		text.setOnKeyListener(new OnKeyListener(){
			public boolean onKey(View v, int keyCode, KeyEvent event) 
			{
				if (keyCode == 66){
					sendMessageButton.performClick();
					return true;
				}
				return false;
			}
			
			
		});
				
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		int message = -1;
		switch (id)
		{
		case MESSAGE_CANNOT_BE_SENT:
			message = R.string.message_cannot_be_sent;
		break;
		}
		
		if (message == -1)
		{
			return null;
		}
		else
		{
			return new AlertDialog.Builder(Messaging.this)       
			.setMessage(message)
			.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					/* User clicked OK so do some stuff */
				}
			})        
			.create();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(messageReceiver);
		unbindService(mConnection);
		
	//	FriendController.setActiveFriend(null);
		CompanyController.setActiveCompany(null);
	}

	@Override
	protected void onResume() 
	{		
		super.onResume();
		bindService(new Intent(Messaging.this, IMService.class), mConnection , Context.BIND_AUTO_CREATE);
				
		IntentFilter i = new IntentFilter();
		i.addAction(IMService.TAKE_MESSAGE);
		
		registerReceiver(messageReceiver, i);
		
	//	FriendController.setActiveFriend(friend.userName);		
		CompanyController.setActiveCompany(company.companyName);		
		
	}
	
	public class  MessageReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) 
		{		
			Bundle extra = intent.getExtras();
			String username = extra.getString(MessageInfo.USERID);			
			String message = extra.getString(MessageInfo.MESSAGETEXT);
 		//	String message_time = extra.getString(MessageInfo.SENDTIME);
 		//	String convertd_msg_time;
			if (username != null && message != null)
			{
				if (friend.userName.equals(username)) {
		//			appendToMessageHistory(username, message);
					addNewMessage(new Message(message, false));
				//	localstoragehandler.insert(username,imService.getUsername(), message, convertd_msg_time);
					
				}
				else {
					if (message.length() > 15) {
						message = message.substring(0, 15);
					}
					Toast.makeText(Messaging.this,  username + " says '"+
													message + "'",
													Toast.LENGTH_SHORT).show();		
				}
			}			
		}
		
	};
	private MessageReceiver messageReceiver = new MessageReceiver();
	
// public  void appendToMessageHistory(String username, String message) {
//		if (username != null && message != null) {
//			messageHistoryText.append(username + ":\n");								
//			messageHistoryText.append(message + "\n");
//		}
//}
 public static String getCurrentTimeStamp() {
	    SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//dd/MM/yyyy
	    Date now = new Date();
	    String strDate = sdfDate.format(now);
	    return strDate;
	}	
 public String UTCdateFormat(){
		Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
		//send this to database
		System.out.println("UTC FORMAT " + cal.getInstance().getTime());
	return  "cal.getInstance().getTime()";
	}
	
	public String LocalDateFormat(){
		SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
	return 	sdf.format(Calendar.getInstance().getTime());
	}
	
	@SuppressWarnings("null")
	public String UTCtoLocalTime(String incoming_message_time) throws ParseException{
		SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
		DateFormat datef = null;
		Date dd;
			dd = datef.parse(incoming_message_time);
 return sdf.format(dd);  // for later implementation, check the system time zone and adjust the time accordingly
	//convert UTC time from server to local time zone
	}
	
	public void addNewMessage(Message m)
	{
		messages.add(m);
		adapter.notifyDataSetChanged();
		getListView().setSelection(messages.size()-1);
	}
	@Override
	protected void onDestroy() {
	    super.onDestroy();
	    if (localstoragehandler != null) {
	    	localstoragehandler.close();
	    }
	    if (dbCursor != null) {
	    	dbCursor.close();
	    }
	}
	

}
