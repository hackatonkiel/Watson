package de.bewatec.watsontest.utils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.location.GpsStatus.Listener;
import android.os.AsyncTask;
import android.os.Environment;

import com.ibm.watson.developer_cloud.text_to_speech.v1.TextToSpeech;
import com.ibm.watson.developer_cloud.text_to_speech.v1.model.AudioFormat;
import com.ibm.watson.developer_cloud.text_to_speech.v1.model.Voice;

public class WatsonTextToSpeech {

	private TextToSpeech service = null;
	private Voice voice = Voice.EN_MICHAEL;
	private AudioFormat audioFormat = AudioFormat.WEBM_VORBIS;

	private TextToSpeechListener listener = null;





	public WatsonTextToSpeech(TextToSpeech service, TextToSpeechListener listener) {
		this.service = service;
		this.listener = listener;

	}

	public TextToSpeechListener getListener() {
		return listener;
	}



	public void setListener(TextToSpeechListener listener) {
		this.listener = listener;
	}



	
	public void textToSpeech(final String params) {
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String fileName = Environment.getExternalStorageDirectory().getAbsolutePath();
				fileName += "/testFile.webm";

				// Carefull with supported Audioformats!
				InputStream in = service.synthesize(params, voice, audioFormat).execute();

				try {
					OutputStream out = new FileOutputStream(fileName);

					byte[] buffer = new byte[1024];
					int length;
					while ((length = in.read(buffer)) > 0) {
						out.write(buffer, 0, length);
					}
					out.close();
					in.close();

					// stream.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}		
				
				
				listener.synthesizedToFile(fileName);
			}
			
			
		}).start();
		
	}

}
