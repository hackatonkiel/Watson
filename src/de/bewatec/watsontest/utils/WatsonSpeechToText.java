package de.bewatec.watsontest.utils;

import java.io.InputStream;

import okhttp3.WebSocket;

import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.RecognizeOptions;
import com.ibm.watson.developer_cloud.speech_to_text.v1.websocket.RecognizeCallback;


public class WatsonSpeechToText  {

	private SpeechToText service = null;
	private RecognizeOptions options;
	
	private SpeechToTextListener listener;
	private InputStream stream;
	
	
	public WatsonSpeechToText(SpeechToText service, RecognizeOptions options){
		this.service = service;
		
		this.options = options;
		
	}
	

	
	public void startSpeechToText(final RecognizeCallback listener,final InputStream stream){
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				service.recognizeUsingWebSocket(stream, options, listener);				
			}
		}).start();
		
		
	}
	
	

	
	
	

}
