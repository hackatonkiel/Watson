package de.bewatec.watsontest;

import java.io.IOException;

import android.app.Activity;
import android.app.Fragment;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import de.bewatec.camtest.R;
import de.bewatec.watsontest.bot.James;

public class MainActivity extends Activity{

	private static final String LOG_TAG = "WatsonTest";

	private James jamesBot;
	
	private Button conversationButton;
	
	private Button streamingButton;


	private MediaPlayer mPlayer;
	private boolean streaming;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		jamesBot = new James(this);
		jamesBot.init();
		
		
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
		}

		conversationButton = (Button) findViewById(R.id.Conversation);
		conversationButton.setVisibility(View.GONE);
		conversationButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
					//conversationButton.setText("Start Streaming Audio to Watson");
					//String[] data = null;
					//Initialize with empty message
					//conversation.execute(data);
				

			}
		});

		streamingButton = (Button) findViewById(R.id.Streaming);
		streamingButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!streaming) {
					streamingButton.setText("StartConversation");
					conversationButton.setVisibility(View.VISIBLE);
					jamesBot.startConversation();
					
					streaming = true;
				} else {
					streamingButton.setText("StopConversation");
					
					jamesBot.stopConversation();

					
					conversationButton.setVisibility(View.GONE);
					
					streaming = false;
				}
			}
		});
	}


	
	
	
	
	private void startPlaying(String file) {
		mPlayer = new MediaPlayer();
		try {

			mPlayer.setDataSource(file);
			mPlayer.prepare();
			mPlayer.start();
		} catch (IOException e) {
			Log.e(LOG_TAG, "prepare() failed");
		}
	}

	private void stopPlaying() {
		mPlayer.release();
		mPlayer = null;
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}


	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
			
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container, false);
			return rootView;
		}
	}


	
}
