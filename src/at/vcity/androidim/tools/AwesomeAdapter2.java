package at.vcity.androidim.tools;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import at.vcity.androidim.R;
import at.vcity.androidim.services.IMService;
import at.vcity.androidim.types.MessageInfo;
/**
 * AwesomeAdapter2 is a Custom class to implement custom row in ListView
 * 
 * @author Adil Soomro
 *
 */
public class AwesomeAdapter2 extends  ArrayAdapter<Message> {
    private final Context mContext;
	private ArrayList<Message> mMessages;
	//private TextView mUsername_field;  //it should be implemented for incoming messages
	//private TextView mMessage_time;
	//private String mUsername;
	//private String mMsg_time;



	public AwesomeAdapter2(Context context, int ResourceId, ArrayList<Message> messages) {
		super(context, ResourceId,messages);
		this.mContext = context;
		this.mMessages = messages;
		
	}
	@Override
	public int getCount() {
		return mMessages.size();
	}
	@Override
	public Message getItem(int position) {		
		return mMessages.get(position);
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Message message = (Message) this.getItem(position);
		String fontType = "MAJALLA.TTF";
		CustomizedTypface customizedTypface = new CustomizedTypface(mContext, fontType);
		ViewHolder holder =null; 
		LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

		if(convertView == null)
		{
			
			convertView = mInflater.inflate(R.layout.sms_row, null);
			
			holder = new ViewHolder();
			
			holder.message = (TextView) convertView.findViewById(R.id.message_text);
		//	holder.username_field = (TextView) convertView.findViewById(R.id.message_username);
		//	holder.message_time = (TextView) convertView.findViewById(R.id.message_time);
			customizedTypface.setTypeface(holder.message, mContext, fontType); 
			customizedTypface.setTypeface(holder.username_field, mContext, fontType);
			customizedTypface.setTypeface(holder.message_time, mContext, fontType);
			convertView.setTag(holder);
		}
		else
			holder = (ViewHolder) convertView.getTag();
		
		holder.message.setText(message.getMessage());
		holder.message_time.setText(MessageInfo.SENDTIME);

		LayoutParams lprowView = (LayoutParams) holder.message.getLayoutParams();
		LayoutParams lpsender = (LayoutParams) holder.username_field.getLayoutParams();
		LayoutParams lptime = (LayoutParams) holder.message_time.getLayoutParams();
		//check if it is a status message then remove background, and change text color.
		if(message.isStatusMessage())
		{
			holder.message.setBackgroundDrawable(null);
			lprowView.gravity = Gravity.LEFT;
	//		holder.message.setTextColor(R.color.textFieldColor);
		}
		else
		{		
			//Check whether message is mine to show green background and align to right
			if(message.isMine())
			{
				holder.message.setBackgroundResource(R.drawable.speech_bubble_green);
				lprowView.gravity = Gravity.RIGHT;
				holder.username_field.setText(" ");
			}
			//If not mine then it is from sender to show orange background and align to left
			else
			{
				holder.message.setBackgroundResource(R.drawable.speech_bubble_orange);
				lprowView.gravity = Gravity.LEFT;
				holder.username_field.setText(MessageInfo.USERID);

			}
			holder.message.setLayoutParams(lprowView);
			holder.username_field.setLayoutParams(lprowView);
			holder.message_time.setLayoutParams(lprowView);
			customizedTypface.setTypeface(holder.message, mContext, fontType); 
			customizedTypface.setTypeface(holder.username_field, mContext, fontType); 
			customizedTypface.setTypeface(holder.message_time, mContext, fontType); 
}
		return convertView;
	}
	
	private  class ViewHolder
	{
		TextView message;
		TextView  username_field;
		TextView message_time;
		
	}

	@Override
	public long getItemId(int position) {
		//Unimplemented, because we aren't using Sqlite.
		return position;
	}

}
