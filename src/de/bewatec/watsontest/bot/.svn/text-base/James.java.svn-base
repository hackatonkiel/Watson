package de.bewatec.watsontest.bot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.database.CursorJoiner.Result;
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
	

	public James() {
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

		
		this.speakerLabels = new ArrayList<SpeakerLabel>();
		this.speakerToWord = new HashMap<Integer,StringBuilder>();
		this.allTimestamps = new ArrayList<SpeechTimestamp>();
		this.labelValues = new int[10]; // max 10 Labels
		
		initialized = true;
	}

	public void startConversation() {
		this.currentState = JamesStates.LISTENING;
		// conversation.execute("");
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

	private synchronized void extractItems(SpeechResults speech) {
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
					Log.d(LOG_TAG, "Final transcript: " + alternativeWithMaxConf.getTranscript());
					printAllSpeakers();
					if (alternativeWithMaxConf != null) {
						timeStamps = alternativeWithMaxConf.getTimestamps();
						saveTimestamps(timeStamps);
						sortWordToLabelByTimeStamp();
						}
				}
			}

			addSpeakerLabels(speech.getSpeakerLabels());// If labels available they will be saved
		}
	}

	private synchronized void saveTimestamps(List<SpeechTimestamp> timestamps) {
		if (timestamps != null) {
			for (SpeechTimestamp speechTimestamp : timestamps) {
				Log.d(LOG_TAG,"Timestamp: "+ speechTimestamp.getWord());
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
				for(int counter = 0; counter < this.labelValues.length; counter ++){
					if(this.labelValues[counter] == speakerLabel.getSpeaker()){
						continue;
					}
					this.labelValues[counter] = speakerLabel.getSpeaker();
					this.speakerToWord.put(speakerLabel.getSpeaker(), new StringBuilder(100));
					this.speakerLabels.add(speakerLabel);
				}
			}
			
		}
	}

	private synchronized void sortWordToLabelByTimeStamp() {

		if (this.allTimestamps != null) {

			for (SpeechTimestamp timestamp : this.allTimestamps) {
				if (timestamp != null && this.speakerToWord != null && this.speakerLabels != null) {
					for (SpeakerLabel speaker : this.speakerLabels) {
						if (speaker.getFrom() == timestamp.getStartTime() && speaker.getTo() == timestamp.getEndTime()) {
							
							this.speakerToWord.get(speaker.getSpeaker()).append(timestamp.getWord());// append word to StringBuilder for the label
							break;
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
			Log.d(LOG_TAG,speakerLabels.size()+ " Speakers are in list");
			for (SpeakerLabel label : this.speakerLabels) {
				Log.d(LOG_TAG, "Speaker: " + label.getSpeaker() + " :"
						+ this.speakerToWord.get(label.getSpeaker()).toString() );
			}
		}
	}

	// get speech to text results
	@Override
	public void onTranscription(SpeechResults speech) {
		// String resultString = speech.toString();
		// Log.d(LOG_TAG, resultString);

		final SpeechResults finalSpeech = speech;
		new Thread(new Runnable() {

			@Override
			public void run() {

				extractItems(finalSpeech);

				if (speakerToWord != null) {
					

					if (currentSpeaker != null) {

						Log.d(LOG_TAG,
								"CurrentSpeaker:" + " (" + currentSpeaker.getSpeaker() + ") "
										+ speakerToWord.get(currentSpeaker).toString());
					}
				}

			}

		}).start();

		/*
		 * sortWordsToSpeakers(speech);
		 * 
		 * if (!hasCurrenSpeaker) { setCurrentSpeaker(); }
		 * 
		 * printAllSpeakers();
		 * 
		 * if (this.speakerToWord != null && this.currentSpeaker != null) {
		 * 
		 * Log.d(LOG_TAG, "CurrentSpeaker:"+" ("+this.currentSpeaker.getSpeaker() + ") " +
		 * this.speakerToWord.get(this.currentSpeaker).toString());
		 * 
		 * }
		 */
		// String result = speech.getResults().get(0).getAlternatives().get(0).getTranscript();
		// List<SpeakerLabel> labels = speech.getSpeakerLabels();
		// speech.getResults().get(0).getKeywordsResult().get("James").get(0).getConfidence();

		// speech.getResults().get(0).getAlternatives().get(0).getTimestamps().get(0);

		// Log.d(LOG_TAG, result);

		// conversation.textToSpeech(result);

	}

	@Override
	public void onTranscriptionComplete() {
		// TODO Auto-generated method stub

	}

	// Conversation results from watson
	@Override
	public void getConversationResult(String answer) {

		Log.d(LOG_TAG, "Conversation answ. :" + answer);

		// textToSpeech.textToSpeech(answer);
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
