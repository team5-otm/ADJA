package at.vcity.androidim;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

public class SplashScreen extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        setContentView(R.layout.splash_screen);
        Thread splashThread = new Thread(){
			public void run() {
				 try{
						sleep(1000);
						finish();
						Intent second = new Intent(SplashScreen.this, FrontPage.class);												
						startActivity(second);	
				 }catch (InterruptedException e) {
					 	e.printStackTrace();
				 }
			}
		};
		splashThread.start();

	}
	
}
