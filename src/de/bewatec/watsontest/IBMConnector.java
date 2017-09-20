package de.bewatec.watsontest;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.ibm.watson.developer_cloud.conversation.v1.ConversationService;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageRequest;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;
import com.ibm.watson.developer_cloud.http.HttpMediaType;
import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.RecognizeOptions;
import com.ibm.watson.developer_cloud.speech_to_text.v1.websocket.BaseRecognizeCallback;
import com.ibm.watson.developer_cloud.text_to_speech.v1.TextToSpeech;
import com.ibm.watson.developer_cloud.text_to_speech.v1.model.AudioFormat;
import com.ibm.watson.developer_cloud.text_to_speech.v1.model.Voice;

import config.WatsonConfigData;

public class IBMConnector {

	private static SpeechToText speechToTextService = null;
	private static ConversationService conversationService = null;
	private static TextToSpeech textToSpeechService = null;
	
	private static RecognizeOptions options = null;

	private String fileName;
	
	public IBMConnector() {
		
		speechToTextService = new SpeechToText(WatsonConfigData.API_USER, WatsonConfigData.API_PW);
		speechToTextService.setEndPoint(WatsonConfigData.API_URL);

		options = new RecognizeOptions.Builder()
				.interimResults(true)
				.continuous(true)
				.contentType(
						HttpMediaType.AUDIO_PCM + ";rate=" + WatsonConfigData.sampleRate + ";channels="
								+ WatsonConfigData.channelCount).build();

		
			
		
		
	}
	
	


	
	
	
	

}
