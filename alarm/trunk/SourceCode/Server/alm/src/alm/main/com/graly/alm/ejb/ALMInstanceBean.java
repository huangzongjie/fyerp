package com.graly.alm.ejb;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.jboss.annotation.ejb.ResourceAdapter;


import com.graly.alm.client.ALMManager;
import com.graly.alm.model.AlarmMessage;
import com.graly.alm.util.AlarmAction;

import com.graly.framework.core.exception.ClientException;

@SuppressWarnings( { "serial", "unused" })
@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),//目的地类型
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "GLORY/ALARM/MESSAGE"),//目的地
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge")//消息确认
})
@ResourceAdapter("activemq-ra.rar")
public class ALMInstanceBean implements MessageListener {

	private static final Logger logger = Logger.getLogger(ALMInstanceBean.class);
	
    @PersistenceContext
	private EntityManager em;

    @EJB
    private ALMManager almManager;
    
	public void onMessage(Message message) {
		List<AlarmAction> alarmLists=new ArrayList<AlarmAction>();
		try {
			if (message instanceof ObjectMessage) {
				ObjectMessage oMessage = (ObjectMessage) message;
				AlarmMessage instance = (AlarmMessage) oMessage.getObject();
				
				almManager.triggerAlarm(instance);
			}
		} catch (Exception e) {
			logger.error("sendMessage ", e);
		}
	}

}
