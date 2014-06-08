package at.vcity.androidim;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import at.vcity.androidim.tools.SimpleGestureFilter;
import at.vcity.androidim.tools.SimpleGestureFilter.SimpleGestureListener;

public class FrontPage extends Activity implements SimpleGestureListener{
	private SimpleGestureFilter detector; 
	private Button logIn;
	private Button signUp;
	private ImageView adjaLogo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.front_page);
		logIn= (Button)findViewById(R.id.logIn);
		signUp = (Button) findViewById(R.id.signUp);
		adjaLogo =(ImageView)findViewById(R.id.adjalogo);
        detector = new SimpleGestureFilter(this,this);

	logIn.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent logInPage = new Intent(FrontPage.this, UserSignIn.class);
			startActivity(logInPage);
		}
	});
	
	
	signUp.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent signUpPage = new Intent(FrontPage.this, UserSignUp.class);
			startActivity(signUpPage);
		}
	} );
	
	
	}
	 @Override 
	    public boolean dispatchTouchEvent(MotionEvent me){ 
	      this.detector.onTouchEvent(me);
	     return super.dispatchTouchEvent(me); 
	    }
	 @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.front_page, menu);
		return true;
	}
	@Override
	public void onSwipe(int direction) {
		// TODO Auto-generated method stub
		  String str = "";
		  Resources res = getResources();
		  Drawable first_page = res.getDrawable(R.drawable.greenstar);	  
			Drawable second_page = res.getDrawable(R.drawable.redstar);	  
			Drawable third_page = res.getDrawable(R.drawable.speech_bubble_green);	  
			View background = findViewById(R.id.page_background);
		  background.setBackgroundDrawable(first_page);
			  if(direction==SimpleGestureFilter.SWIPE_RIGHT){
				  str = "Swipe Right";

				  if(background.getBackground()==first_page){
					  //first page, change to second
					  background.setBackgroundDrawable(second_page);
				  }else if(background.getBackground()==second_page){
					 // second page, change to third					 
					  background.setBackgroundDrawable(third_page);
				  }else if(background.getBackground()==third_page){
					  //third page, change to first
					  background.setBackgroundDrawable(first_page);
				  }
				  
			  }else if(direction==SimpleGestureFilter.SWIPE_LEFT){
				  str = "Swipe Left";
				  if(background.getBackground()==first_page){
					  //first page, change to third
					  background.setBackgroundDrawable(third_page);
				  }else if(background.getBackground()==third_page){
					 // third page, change to second
					  background.setBackgroundDrawable(second_page);
				  }else if(background.getBackground()==second_page){
					  //second page, change to first
					  background.setBackgroundDrawable(first_page);
					  }
				  
			  }
			   Toast.makeText(this, str, Toast.LENGTH_SHORT).show();

			  

	}
	@Override
	public void onDoubleTap() {
		// TODO Auto-generated method stub
		Toast.makeText(this, "Double Tap", Toast.LENGTH_SHORT).show();
	}
}
