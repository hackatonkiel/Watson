package de.bewatec.watsontest.bot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.database.CursorJoiner.Result;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder.AudioSource;
import android.util.Log;

import com.ibm.watson.developer_cloud.conversation.v1.ConversationService;
import com.ibm.watson.developer_cloud.http.HttpMediaType;
import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.RecognizeOptions;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeakerLabel;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechAlternative;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechResults;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechTimestamp;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.Transcript;
import com.ibm.watson.developer_cloud.speech_to_text.v1.websocket.RecognizeCallback;
import com.ibm.watson.developer_cloud.text_to_speech.v1.TextToSpeech;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.ToneAnalyzer;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneAnalysis;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneOptions;

import config.WatsonConfigData;
import de.bewatec.watsontest.AudioStreamer;
import de.bewatec.watsontest.utils.ConversationListener;
import de.bewatec.watsontest.utils.TextToSpeechListener;
import de.bewatec.watsontest.utils.WatsonConversation;
import de.bewatec.watsontest.utils.WatsonSpeechToText;
import de.bewatec.watsontest.utils.WatsonTextToSpeech;

public class James implements TextToSpeechListener, ConversationListener, RecognizeCallback {

	private JamesStates currentState;

	private AudioRecord audioRecorder;
	private AudioStreamer micStreamer;

	private WatsonSpeechToText speechToText;
	private WatsonConversation conversation;
	private WatsonTextToSpeech textToSpeech;
	private SpeechToText sttService;

	private ToneAnalyzer toneAnalyzerService;

	private boolean initialized = false;

	private MediaPlayer mPlayer;

	private int bufferSize;
	private static final String LOG_TAG = "James";

	private boolean hasCurrenSpeaker = false;
	private SpeakerLabel currentSpeaker = null;
	private List<SpeakerLabel> speakerLabels = null;
	private Map<Integer, StringBuilder> speakerToWord;
	private List<SpeechTimestamp> allTimestamps = null;
	private int[] labelValues = null;

	private AudioManager audioManager;
	
	private Context context;
	
	public James(Context app) {
		this.context = app;
		this.bufferSize = AudioRecord.getMinBufferSize(WatsonConfigData.sampleRate, WatsonConfigData.channelConfig,
				WatsonConfigData.audioFormat) * 2;
		Log.d(LOG_TAG, "BufferSize " + bufferSize);
	}

	public void init() {
		audioRecorder = new AudioRecord(AudioSource.MIC, WatsonConfigData.sampleRate, WatsonConfigData.channelConfig,
				WatsonConfigData.audioFormat, this.bufferSize);
		micStreamer = new AudioStreamer(audioRecorder);
		micStreamer.startRecording();

		this.sttService = new SpeechToText(WatsonConfigData.API_USER, WatsonConfigData.API_PW);
		this.sttService.setEndPoint(WatsonConfigData.API_URL);

		RecognizeOptions options = new RecognizeOptions.Builder()
				.timestamps(true)
				.speakerLabels(true)
				// .continuous(true)
				.interimResults(true)
				.contentType(
						HttpMediaType.AUDIO_PCM + ";rate=" + WatsonConfigData.sampleRate + ";channels="
								+ WatsonConfigData.channelCount).build();

		speechToText = new WatsonSpeechToText(this.sttService, options);

		ConversationService conversationService = new ConversationService(ConversationService.VERSION_DATE_2017_04_21,
				WatsonConfigData.CONVERSATION_USER, WatsonConfigData.CONVERSATION_PW);
		conversationService.setEndPoint(WatsonConfigData.CONVERSATION_API_URL);

		conversation = new WatsonConversation(conversationService);

		conversation.setListener(this);

		TextToSpeech textToSpeechService = new TextToSpeech();

		textToSpeechService = new TextToSpeech(WatsonConfigData.SPEECH_TO_TEXT_USER, WatsonConfigData.SPEECH_TO_TEXT_PW);
		textToSpeechService.setEndPoint(WatsonConfigData.SPEECH_TO_TEXT_API_URL);

		textToSpeech = new WatsonTextToSpeech(textToSpeechService, this);

		this.toneAnalyzerService = new ToneAnalyzer(ToneAnalyzer.VERSION_DATE_2016_05_19);
		this.toneAnalyzerService.setUsernameAndPassword("b2581d44-9650-4d62-8e62-2ac17e3a377b", "YgEZXR404fsH");
		this.toneAnalyzerService.setEndPoint("https://gateway-fra.watsonplatform.net/tone-analyzer/api");

		this.audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		this.audioManager.setMicrophoneMute(true);
		
		
		this.speakerLabels = new ArrayList<SpeakerLabel>();
		this.speakerToWord = new HashMap<Integer, StringBuilder>();
		this.allTimestamps = new ArrayList<SpeechTimestamp>();
		this.labelValues = new int[10]; // max 10 Labels

		initialized = true;
	}

	public void startConversation() {
		this.currentState = JamesStates.LISTENING;
		// conversation.execute("");
		Log.d(LOG_TAG,"now Listening");
		this.audioManager.setMicrophoneMute(false);
		speechToText.startSpeechToText(James.this, micStreamer);
	}

	public void stopConversation() {
		this.currentState = null;
		stopPlaying();
	}

	@Override
	public void onConnected() {
		// TODO Auto-generated method stub
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
	}

	@Override
	public void onError(Exception arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onInactivityTimeout(RuntimeException arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onListening() {
		// TODO Auto-generated method stub
	}

	private String extractItems(SpeechResults speech) {
		Transcript finalResult = null;
		SpeechAlternative alternativeWithMaxConf;
		List<SpeechTimestamp> timeStamps;

		if (speech != null) {
			if (speech.getResults() != null) {
				for (Transcript result : speech.getResults()) {
					if (result.isFinal()) {
						finalResult = result;
						break;
					}
				}

				if (finalResult != null) {

					alternativeWithMaxConf = JamesUtils.getAlternativesWithMaxConfidence(finalResult);

					return alternativeWithMaxConf.getTranscript();

					// Log.d(LOG_TAG, "Final transcript: " + alternativeWithMaxConf.getTranscript());

					/*
					 * printAllSpeakers(); if (alternativeWithMaxConf != null) { timeStamps =
					 * alternativeWithMaxConf.getTimestamps(); saveTimestamps(timeStamps); sortWordToLabelByTimeStamp(); }
					 */
				}
			}

			// if(speech.getSpeakerLabels() != null){
			// Log.d(LOG_TAG,"Labels?: "+speech.toString());
			// }
			// addSpeakerLabels(speech.getSpeakerLabels());// If labels available they will be saved

		}
		return null;
	}

	private synchronized void saveTimestamps(List<SpeechTimestamp> timestamps) {
		if (timestamps != null) {
			for (SpeechTimestamp speechTimestamp : timestamps) {
				Log.d(LOG_TAG, "Timestamp: " + speechTimestamp.getWord());
				this.allTimestamps.add(speechTimestamp);
			}
		}
	}

	/**
	 * Checks if the speakerlabels are already existing and adds the ones that are new
	 * 
	 * @param speakerLabels
	 */
	private synchronized void addSpeakerLabels(List<SpeakerLabel> speakerLabelList) {

		if (speakerLabelList != null && this.speakerToWord != null && this.speakerLabels != null) {

			for (SpeakerLabel speakerLabel : speakerLabelList) {
				for (int counter = 0; counter < this.labelValues.length; counter++) {

					if (!containsSpeakerLabel(speakerLabel.getSpeaker())) {
						this.labelValues[counter] = speakerLabel.getSpeaker();
						this.speakerToWord.put(speakerLabel.getSpeaker(), new StringBuilder(100));
						this.speakerLabels.add(speakerLabel);
					}

				}
			}

		}
	}

	private boolean containsSpeakerLabel(int speaker) {

		for (int i = 0; i < this.labelValues.length; i++) {
			if (this.labelValues[i] == speaker) {
				return true;
			}
		}
		return false;

	}

	private synchronized void sortWordToLabelByTimeStamp() {
		Log.d(LOG_TAG, "sortWordToLabelByTimestamp");
		if (this.allTimestamps != null) {

			for (SpeechTimestamp timestamp : this.allTimestamps) {

				if (timestamp != null && this.speakerToWord != null && this.speakerLabels != null) {

					for (SpeakerLabel speaker : this.speakerLabels) {
						Log.d(LOG_TAG, "try to add a word");
						if (speaker.getFrom() == timestamp.getStartTime() && speaker.getTo() == timestamp.getEndTime()) {
							StringBuilder builder = this.speakerToWord.get(speaker.getSpeaker());
							builder.insert(builder.length(), timestamp.getWord());// append word to StringBuilder for the label

							Log.d(LOG_TAG, "word should be added");
						}
					}
				} else {
					Log.d(LOG_TAG, "timeStamp || speakerToWord -> NULL");
				}
			}
		}

	}

	private synchronized void printAllSpeakers() {

		if (this.speakerLabels != null && this.speakerToWord != null) {
			Log.d(LOG_TAG, speakerLabels.size() + " Speakers are in list");
			for (SpeakerLabel label : this.speakerLabels) {
				Log.d(LOG_TAG, "Speaker: " + label.getSpeaker() + " :"
						+ this.speakerToWord.get(label.getSpeaker()).toString());
			}
		}
	}

	// get speech to text results
	@Override
	public void onTranscription(SpeechResults speech) {
		 String resultString = extractItems(speech);
		if(resultString != null)
		 Log.d(LOG_TAG, "Speech: "+resultString);
		
		if (this.currentState == JamesStates.LISTENING) {

			final SpeechResults finalSpeech = speech;
			new Thread(new Runnable() {

				@Override
				public void run() {

					String transcript = extractItems(finalSpeech);

					if (transcript != null) {
						// Call the service and get the tone
						ToneOptions tonOptions = new ToneOptions.Builder().build();
						ToneAnalysis tone = toneAnalyzerService.getTone(transcript, tonOptions).execute();

						Log.d(LOG_TAG, "SpeechResult: " + transcript );
						conversation.textToSpeech(transcript);
					}
					/*
					 * if (speakerToWord != null) { if (currentSpeaker != null) { Log.d(LOG_TAG, "CurrentSpeaker:" + " (" +
					 * currentSpeaker.getSpeaker() + ") " + speakerToWord.get(currentSpeaker).toString()); } }
					 */

				}

			}).start();
		}

	}

	@Override
	public void onTranscriptionComplete() {
		// TODO Auto-generated method stub

	}

	// Conversation results from watson
	@Override
	public void getConversationResult(String answer) {
		this.audioManager.setMicrophoneMute(true);
		this.currentState = JamesStates.TALKING;
		Log.d(LOG_TAG,"now talking");
		Log.d(LOG_TAG, "Conversation answ. :" + answer);

		textToSpeech.textToSpeech(answer);
	}

	// File where audio from texttospeech is saved
	@Override
	public void synthesizedToFile(String fileName) {
		startPlaying(fileName);
	}

	private void startPlaying(String file) {
		mPlayer = new MediaPlayer();
		mPlayer.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				audioManager.setMicrophoneMute(false);
				Log.d(LOG_TAG,"now Listening");
				currentState = JamesStates.LISTENING;
			}
		});
		try {

			mPlayer.setDataSource(file);
			mPlayer.prepare();
			mPlayer.start();
		} catch (IOException e) {
			Log.e(LOG_TAG, "prepare() failed");
		}
	}

	private void stopPlaying() {
		if (mPlayer != null) {
			mPlayer.release();
			mPlayer = null;
		}
	}

	public enum JamesStates {

		// Do not listen, just say hello to the user
		GREETING(1),

		// Stop talking and just listen
		LISTENING(2),

		// Stop listening and just talk
		TALKING(3);

		int number;

		JamesStates(int number) {
			this.number = number;
		}
	}

}
