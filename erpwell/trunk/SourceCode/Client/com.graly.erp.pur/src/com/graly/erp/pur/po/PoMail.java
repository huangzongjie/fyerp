package com.graly.erp.pur.po;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport; //import javax.mail.Authenticator;
//import javax.mail.MessagingException;
//import javax.mail.PasswordAuthentication;
//import javax.mail.Session;
//import javax.mail.Transport;
//import javax.mail.internet.InternetAddress;
//import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart; //import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeMessage.RecipientType;

import org.apache.commons.io.IOUtils;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADUserRefList;
import com.graly.framework.runtime.Framework;

import com.graly.framework.base.ui.util.Env;
import freemarker.template.Template;

public class PoMail {
	
	private Properties mailProperty;//mail��Ϣ����
	private FreeMarkerConfigurer freemarkerconfiguration; 
	private final String dzorderftl = "pdftemplete/dzorder.ftl";
	
	private static String tmpdir = PoMail.class.getResource("/").getPath() + "tmpdir";
	{
		tmpdir = tmpdir.replaceAll("^/", "");
	}

	public InputStream generationPdfDzOrder(Map<String ,Object> params) throws Exception{
		final Template template = freemarkerconfiguration.getConfiguration().getTemplate(dzorderftl);
		String htmlText = FreeMarkerTemplateUtils.processTemplateIntoString(template, params); 
		String tmpFileName = UUID.randomUUID().toString(); //��������ļ���
		File dir = new File(tmpdir);
		if(!dir.exists())
			dir.mkdirs();
		String htmlFileName =  tmpdir + "/" + tmpFileName + ".html" ; 
		String pdfFileName = tmpdir + "/" + tmpFileName + ".pdf" ;
		File htmlFile = new File(htmlFileName);   //html�ļ�
		File pdfFile = new  File(pdfFileName); //pdf�ļ�
		IOUtils.write(htmlText, new FileOutputStream(htmlFile)); //������д��html�ļ�
		String command = getCommand(htmlFileName , pdfFileName);
		Runtime.getRuntime().exec(command);
//		TimeUnit.SECONDS.sleep(0); 
		return new FileInputStream(pdfFile);
	}
	
	public String getCommand(String htmlName , String pdfName){
//		return "C:\\wkhtmltopdf-0.8.3.exe "+ htmlName + " " + pdfName+" "+"--toc-l1-font-size 14";
		return "C:\\erppdf\\wkhtmltopdf\\bin\\wkhtmltopdf.exe "+ htmlName+ " " + pdfName;//ǰ������ҵİ�װ·���������Լ��İ�װ·�����ϼ���
	}
	
	//http�ļ�����
	public  boolean httpDownload(String httpUrl, String saveFile) {
		// ���������ļ�
		int bytesum = 0;
		int byteread = 0;

		URL url = null;
		try {
			url = new URL(httpUrl);
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
			return false;
		}
		try {
			URLConnection conn = url.openConnection();
			InputStream inStream = conn.getInputStream();
			FileOutputStream fs = new FileOutputStream(saveFile);

			byte[] buffer = new byte[1204];
			while ((byteread = inStream.read(buffer)) != -1) {
				bytesum += byteread;
				fs.write(buffer, 0, byteread);
			}
			inStream.close();
			fs.close();
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	//����mail����
	public void initProperties() throws Exception{
		mailProperty = new Properties();
		mailProperty.put("mail.smtp.auth", "true");
		mailProperty.put("mail.smtp.host", "smtp.canature.com");
		mailProperty.setProperty("mail.transport.protocol", "smtp");
		ADManager adManager = Framework.getService(ADManager.class);
		String emailAddress = Env.getUser().getEmail();
		if(emailAddress==null || "".equals(emailAddress)){
			mailProperty =null;
			return;
		}
		StringBuffer sf = new StringBuffer();
		sf.append(" referenceName = 'Email' ");
		sf.append(" and key= '");
		sf.append(emailAddress);
		sf.append("'");
		List<ADUserRefList> userRefList = adManager.getEntityList(Env.getOrgRrn(), ADUserRefList.class,Integer.MAX_VALUE,sf.toString(),null);
		ADUserRefList userRef =null ;
		if(userRefList!=null && userRefList.size() >0 ){
			userRef =userRefList.get(0);
		}

		mailProperty.put("mail.user", userRef.getKey());//�û���
		mailProperty.put("mail.password",userRef.getValue());//����
		mailProperty.put("mail.signature", userRef.getDescription());//����ǩ��
		
		//����
		userRef=null;
		sf = new StringBuffer();
		sf.append(" referenceName = 'Email' ");
		sf.append(" and key= 'content'");
		
		userRefList = adManager.getEntityList(Env.getOrgRrn(), ADUserRefList.class,Integer.MAX_VALUE,sf.toString(),null);
		if(userRefList!=null && userRefList.size() >0 ){
			userRef =userRefList.get(0);
			mailProperty.put("mail.content",userRef.getDescription());
		}
		
		
	}
	//����PDF�ļ�
	public void savePdfFile(String httpUrl,String fileName )throws Exception{
		String savrFile = "C:\\"+fileName;
//		httpDownload(httpUrl, savrFile);
		String command = getCommand(httpUrl, savrFile);
		Process p= Runtime.getRuntime().exec(command);
		TimeUnit.SECONDS.sleep(2); 
	}
	
	//�����ʼ�
	public  void sendMail(String toEmailAddress,String fileName) throws Exception {
			initProperties();
			// ������Ȩ��Ϣ�����ڽ���SMTP���������֤
			Authenticator authenticator = new Authenticator() {
				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					// �û���������
					String userName = mailProperty.getProperty("mail.user");
					String password = mailProperty.getProperty("mail.password");
					return new PasswordAuthentication(userName, password);
				}
			};
			Session mailSession = Session.getInstance(mailProperty, authenticator);
			mailSession.setDebug(true);
			MimeMessage message = new MimeMessage(mailSession);
			InternetAddress form = new InternetAddress(mailProperty.getProperty("mail.user"));
			message.setFrom(form);
			// �����ռ���
			InternetAddress to = new InternetAddress(toEmailAddress);
			message.setRecipient(RecipientType.TO, to);

			message.setSubject("���ܻ����ɹ�����");

			MimeMultipart msgMultipart = new MimeMultipart("mixed");
			MimeBodyPart body = new MimeBodyPart();
			body.setFileName(fileName);
			File file = new File("C:\\"+fileName);
			if(file.length()<30000){
				throw new Exception("HTMLתPDFʧ�������µ�������ʼ�,����ʼ���û�з��ͳ�ȥ");
			}
			DataSource ds1 = new FileDataSource(file);
			
			DataHandler dh1 = new DataHandler(ds1);
			body.setDataHandler(dh1);

			
			MimeBodyPart body2 = new MimeBodyPart();
			
			StringBuffer sf = new StringBuffer();
			sf.append("<p>");
			sf.append(mailProperty.getProperty("mail.content"));
			sf.append("<br/>");
			sf.append("<br/>");
			sf.append("<br/>");
			sf.append("<br/>");
			sf.append(mailProperty.getProperty("mail.signature"));
			sf.append("</p>");
			String content =sf.toString(); 
			if(content!=null && !"".equals(content)){
				content = content.replace("\r\n", "<br/>");
			}
			body2.setContent(content, "text/html;charset=gb2312");
			
			
			msgMultipart.addBodyPart(body);
			msgMultipart.addBodyPart(body2);
			
			message.setContent(msgMultipart);
			// �����ʼ�
			Transport.send(message);
			Runtime.getRuntime().exec("taskkill /f /t /im wkhtmltopdf.exe");

	}

}
