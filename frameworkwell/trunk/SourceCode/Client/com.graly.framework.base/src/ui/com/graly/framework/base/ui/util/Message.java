package com.graly.framework.base.ui.util;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Locale;
import org.apache.log4j.Logger;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADMessage;
import com.graly.framework.runtime.Framework;

public class Message {
	
	private final static Logger logger = Logger.getLogger(Message.class);

	private static final Message instance = new Message();
	private static final Map<String, String> bundle = new HashMap<String, String>();
	
	private Message() {
    }

    public static Message getInstance() {
    	return instance;
    }
    
	public static String getString(String key)
	{
		try {
			String language = Locale.getDefault().getLanguage();
			String message = bundle.get(key + "_" + language);
			if (message == null){
				message = bundle.get(key);
			}
			return (message==null) ? "" : message;
		} catch (Exception e) {
			logger.warn("Message : Can not find " + key + " message");
			return '!' + key + '!';
		}
	}
	
	public static void load(){
		synchronized (bundle) {
			try {
				ADManager entityManager = Framework.getService(ADManager.class);
				List<ADMessage> messages = entityManager.getMessage();
				for (ADMessage msg : messages){
					if (msg.getLanguage() != null && !"".equals(msg.getLanguage().trim())){
						bundle.put(msg.getKey() + "_" + msg.getLanguage(), msg.getMessage());
					} else {
						bundle.put(msg.getKey(), msg.getMessage());
					}
				}
	        } catch (Exception e) {
				logger.error("Message : Load error ", e);
	        }
		}
	}
}