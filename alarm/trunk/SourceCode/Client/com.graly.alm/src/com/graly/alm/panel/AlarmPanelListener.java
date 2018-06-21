package com.graly.alm.panel;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.log4j.Logger;

import com.graly.alm.model.AlarmPanelMessage;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.core.config.Config;

public class AlarmPanelListener implements MessageListener {
	
	private static final Logger logger = Logger.getLogger(AlarmPanelListener.class);
	
	private Connection connection;
	private Session session;
	private Topic topic;
	private MessageConsumer consumer;
	private AlarmPanel panel;
	
	public AlarmPanelListener(AlarmPanel panel) {
		this.panel = panel;
	}
	
	public void run() {
		try {
			AlarmPanelCfMod config = Config.sharedInstance(AlarmPanelCfMod.class.getName()).createConfigModule(AlarmPanelCfMod.class);
			String url = config.getUrl();
			String subject = config.getSubject(); 
			String user = config.getUser();
			String password = config.getPassword();
			if (user == null || user.trim().length() == 0) {
				user = ActiveMQConnection.DEFAULT_USER;
				password = ActiveMQConnection.DEFAULT_PASSWORD;
			}
			
			ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(user, password, url);
			connection = connectionFactory.createConnection();
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			topic = session.createTopic(subject);

			consumer = session.createConsumer(topic);
			
	        consumer.setMessageListener(this);
	        
			connection.start();
			
		} catch(JMSException e){
			this.close();
			logger.error("AlarmPanelListener run : " + e.getMessage(), e);		
		} 
	}

	public void onMessage(Message message) {
		try {
			if (message instanceof ObjectMessage) {
				ObjectMessage oMessage = (ObjectMessage)message;
				AlarmPanelMessage panelMessage = (AlarmPanelMessage)oMessage.getObject();
				if (Env.getUserRrn() == panelMessage.getUserRrn()) {
					panel.onMessage(panelMessage);
				}
			}
		} catch (Exception e) {
			logger.error("sendMessage ", e);
		}
	}

	public void close() {
		try {
			if (consumer != null){
				consumer.close();
			}
		} catch (JMSException e) {
		}
		try {
			if (session != null){
				session.close();
			}
		} catch (JMSException e) {
		}
		try {
			if (connection != null){
				connection.close();
			}
		} catch (JMSException e) {
		}
	}
}
