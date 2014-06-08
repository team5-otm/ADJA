package at.vcity.androidim.tools;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;

public class CustomizedTypface {
	 Context Context;
	 String fontType = " ";
	 TextView textView;
	public CustomizedTypface(Context mContext, String mFontType) {
		
		this.Context = mContext;
		this.fontType = mFontType;
		
	}
	public void setTypeface(TextView convertView, Context mContext, String mFontType){
		this.Context = mContext;
		this.fontType = mFontType;
		this.textView = convertView; 
		Typeface font = Typeface.createFromAsset(mContext.getResources().getAssets(), mFontType); 
		convertView.setTypeface(font);
		
	}

}
