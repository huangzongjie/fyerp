package com.graly.alm.helper;

import java.io.Serializable;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.log4j.Logger;


public class AlarmHelper{

	private static final Logger logger = Logger.getLogger(AlarmHelper.class);

	private static String user = ActiveMQConnection.DEFAULT_USER;
    private static String password = ActiveMQConnection.DEFAULT_PASSWORD;
    private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;

    public static String TARGET_USER = "TargetUser";
       
    public static void sendObjectMessage(String subject, Serializable object) {
    	sendObjectMessage(subject, object, "");
    }
    
	public static void sendObjectMessage(String subject, Serializable object, String targetUser) {		
		Connection connection = null;
		try {
			ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(user, password, url);
			connection = connectionFactory.createConnection();
	        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
	        Topic topic = session.createTopic(subject);
	        MessageProducer publisher = session.createProducer(topic);        
	        publisher.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
			connection.start();	    
			
	        Message message = session.createObjectMessage(object);
	        if (targetUser != null && targetUser.trim().length() > 0) {
	        	message.setStringProperty(TARGET_USER, targetUser);
	        }
	        publisher.send(message);
	        publisher.close();
	        
	        connection.stop();
	        
		} catch (Exception e) {
			logger.error("sendMessage ", e);
			
		} finally {
            try {    
            	if (connection != null) {
            		connection.close();
            	}
            } catch (Exception ignore) {
            }
        }
	}
	
}
