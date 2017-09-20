package de.bewatec.watsontest;

import java.io.IOException;
import java.io.InputStream;

import android.media.AudioRecord;


public class AudioStreamer extends InputStream {

	private static final String LOG_TAG = "WATSON";

	private AudioRecord mAudioRecorder;


	private boolean recording;


	public AudioStreamer(AudioRecord recorder) {

		mAudioRecorder = recorder;//

	}

	public void startRecording() {
		if (!recording) {
			mAudioRecorder.startRecording();
			recording = true;
		}
	}

	public void stopRecording(){
		if(recording){
			mAudioRecorder.stop();
			recording = false;
		}
		
	}
	
	@Override
	public int read(byte[] buffer) throws IOException {
		return mAudioRecorder.read(buffer, 0, buffer.length);
	}

	@Override
	public int read(byte[] buffer, int byteOffset, int byteCount) throws IOException {
		return mAudioRecorder.read(buffer, byteOffset, byteCount);
	}

	@Override
	public int read() throws IOException {
		byte[] audiobuffer = new byte[1];
		mAudioRecorder.read(audiobuffer, 0, 1);
		return audiobuffer[0];
	}
	
	
	

}
