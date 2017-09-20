package de.bewatec.watsontest.utils;

import android.os.AsyncTask;
import android.util.Log;

import com.ibm.watson.developer_cloud.conversation.v1.ConversationService;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageRequest;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;

import config.WatsonConfigData;

public class WatsonConversation {

	private static final String LOG_TAG = "Conversation";
	private ConversationService service = null;
	private ConversationListener listener;

	public WatsonConversation(ConversationService service) {
		this.service = service;
	}

	public ConversationListener getListener() {
		return listener;
	}

	public void setListener(ConversationListener listener) {
		this.listener = listener;
	}

	public void textToSpeech(final String params) {

		Log.d(LOG_TAG, "Message: " + params);
		new Thread(new Runnable() {
			MessageRequest newMessage;

			@Override
			public void run() {
				if (params == null) {
					newMessage = new MessageRequest.Builder().build();
				} else {
					newMessage = new MessageRequest.Builder().inputText(params).build();
				}
				MessageResponse response = service.message(WatsonConfigData.CONVERSATION_WORKSPACE_ID, newMessage)
						.execute();
				if (response.getText().size() > 0) {
					Log.d(LOG_TAG, response.getText().get(0));

					listener.getConversationResult(response.getText().get(0));
				}
			}

		}).start();

	}

}
