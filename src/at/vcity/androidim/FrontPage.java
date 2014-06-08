package at.vcity.androidim;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

public class FrontPage extends Activity {
	//private SimpleGestureFilter detector; 
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
//        detector = new SimpleGestureFilter(this,this);

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
	 //     this.detector.onTouchEvent(me);
	     return super.dispatchTouchEvent(me); 
	    }
	 @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.front_page, menu);
		return true;
	}
	}
