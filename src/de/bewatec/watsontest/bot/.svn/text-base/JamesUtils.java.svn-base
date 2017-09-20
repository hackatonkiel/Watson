package de.bewatec.watsontest.bot;

import java.util.List;

import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechAlternative;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.Transcript;

public class JamesUtils {

	private static final String LOG_TAG = "James";



	public synchronized static SpeechAlternative getAlternativesWithMaxConfidence(Transcript finalResult) {
		SpeechAlternative altWithMaxConfidence = null;
		if (finalResult != null) {

			List<SpeechAlternative> alternatives = finalResult.getAlternatives();

			if (alternatives != null) {
				double maxConfidence = 0;
				for (SpeechAlternative alternative : alternatives) {
					if (alternative.getConfidence() > maxConfidence) {
						altWithMaxConfidence = alternative;
						maxConfidence = alternative.getConfidence();
					}
				}
			}
		}

		return altWithMaxConfidence;
	}

	/*

	
	public void saveLables(Transcript finalResult) {
		if (finalResult != null) {

		}

	}

	private void sortWordToLabelByTimeStamp(SpeechTimestamp timeStamp) {
		if (timeStamp != null && this.speakerToWord != null) {
			for (SpeakerLabel speaker : this.speakerToWord.keySet()) {
				if (speaker.getFrom() == timeStamp.getStartTime() && speaker.getTo() == timeStamp.getEndTime()) {
					this.speakerToWord.get(speaker).append(timeStamp.getWord());// append word to StringBuilder for the label
					break;
				}
			}
		} else {
			Log.d(LOG_TAG, "timeStamp || speakerToWord -> NULL");
		}
	}



	/**
	 * Gets first result and gets the alternative with max confidence then calls "sortWortToLabelbyTimeStamp" to sort
	 * words to the right label
	 * 
	 * @param speech
	 */
	/*
	private void sortWordsToSpeakers(SpeechResults speech) {


		Transcript transcript = getFinalResult(speech); // get first with final result

		if (transcript != null) {
			List<SpeechAlternative> alternatives = transcript.getAlternatives();

			SpeechAlternative altWithMaxConfidence = null;
			double maxConfidence = 0;
			for (SpeechAlternative alternative : alternatives) {
				if (alternative.getConfidence() > maxConfidence) {
					altWithMaxConfidence = alternative;
					maxConfidence = alternative.getConfidence();
				}

			}
			if (altWithMaxConfidence != null) {
				if (altWithMaxConfidence != null) {
					List<SpeechTimestamp> timeStamps = altWithMaxConfidence.getTimestamps();
					for (SpeechTimestamp timeStamp : timeStamps) {
						sortWordToLabelByTimeStamp(timeStamp); // add words to the right label
					}
				}
			} else {
				Log.d(LOG_TAG, "altWithMaxConfidence -> NULL");
			}
		} else {
			Log.d(LOG_TAG, "transcript -> NULL");
		}

	}

	private void setCurrentSpeaker() {
		if (this.speakerToWord != null) {
			for (SpeakerLabel label : this.speakerToWord.keySet()) {

				if (this.speakerToWord.get(label).toString().contains("Hey James")) {
					this.hasCurrenSpeaker = true;
					this.currentSpeaker = label;
					new Timer().schedule(new ResetCurrenSpeaker(), 10000);// Reset current speaker after 10 Sec
				}
			}
		}
	}

	private void printAllSpeakers() {

		for (SpeakerLabel label : this.speakerToWord.keySet()) {
			Log.d(LOG_TAG, "Speaker: " + label.getSpeaker() + " :" + this.speakerToWord.get(label).toString());
		}
	}

	private class ResetCurrenSpeaker extends TimerTask {

		@Override
		public void run() {

			hasCurrenSpeaker = false;
			currentSpeaker = null;

		}

	}
*/
}
