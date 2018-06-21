package com.graly.framework.base.ui.forms.field;

/**
 * ImageField 用于实现将本地的图像文件转换成字节型数组
 * 并负责将存储在byte[]数组中的图像数据转换成图像显示
 */

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.log4j.Logger;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.framework.base.ui.util.Message;
public class ImageField extends AbstractField {
	private static final Logger logger = Logger.getLogger(ImageField.class);
	private static String[] filterExt = { "*.jpg", "*.gif", "*.png"};
	
	protected int mStyle = SWT.BORDER | SWT.READ_ONLY;
	private static final String NO_IMAGE = "No Image(500 × 250)";
	private String INIT_IMAGEURL = "C:\\Documents and Settings\\Administrator\\My Documents\\My Pictures";
	private Composite imageComposite;
	private Label imageLabel;
	private Button upLoad;
	private String tempUrl;
	private Image loadedImage;
	private int IMAGEBYTE = 1024 * 1024;
	private double IMAGESCALE_WIDTH = 500D;
	private double IMAGESCALE_HEIGHT = 250D;
	
	public ImageField(String id) {
		super(id);
	}
	
	public ImageField(String id, String imageUrl) {
		this(id);
		this.value = imageUrl;
	}
	
	public ImageField(String id, String imageUrl, Image loadedImage) {
		this(id);
		this.value = imageUrl;
		this.loadedImage = loadedImage;
	}
	
	@Override
	public void createContent(Composite composite, FormToolkit toolkit) {
		int i = 0;
//		String labelStr = getLabel();
//        if (labelStr != null) {
//        	mControls = new Control[2];
//        	Label label = toolkit.createLabel(composite, labelStr);
//            mControls[0] = label;
//            i = 1;
//        } else {
        	mControls = new Control[1];
//        }
        imageComposite = toolkit.createComposite(composite, SWT.BORDER);
        createImageContent(imageComposite, toolkit);

        toolkit.adapt(imageComposite);
        toolkit.paintBordersFor(imageComposite);

        mControls[i] = imageComposite;
	}
	
	private void createImageContent(Composite parent, FormToolkit toolkit) {
		GridData gd = new GridData(500, 270);
        parent.setLayoutData(gd);
        parent.setLayout(new GridLayout(1, false));
        
        //create Label to upLoad image
        imageLabel = toolkit.createLabel(parent, "", SWT.NULL);
        imageLabel.setLayoutData(new GridData(GridData.FILL_BOTH));
        
        byte[] imageBytes = (byte[])getValue();
        if(imageBytes != null && imageBytes.length > 0) {
			InputStream inputStream = new ByteArrayInputStream(imageBytes);
			imageLabel.setImage(new Image(null, inputStream));
			imageLabel.setText(null);
		}
        
        //create Button to open Dialog and select a image
        Composite comp = toolkit.createComposite(parent, SWT.NULL);
        comp.setLayout(new GridLayout(1, false));
        GridData g = new GridData();
        g.horizontalAlignment = GridData.CENTER;
        comp.setLayoutData(g);
        
        upLoad = toolkit.createButton(comp, label, SWT.FLAT | SWT.PUSH);
        decorateButton(upLoad);
        upLoad.addSelectionListener(getSelectionListener());
	}

	@Override
	public void refresh() {
		byte[] imageBytes = (byte[])getValue();
		if(loadedImage != null && !loadedImage.isDisposed()) {
			loadedImage.dispose();
		}

		if(imageBytes != null && imageBytes.length > 0) {	        
			imageLabel.setText("");
			InputStream inputStream = new ByteArrayInputStream(imageBytes);			
			
			loadedImage = new Image(null, resetImageData(inputStream));
			imageLabel.setImage(loadedImage);
		} else {
			imageLabel.setText(NO_IMAGE);
		}
	}
	
	@Override
	public void enableChanged(boolean enabled) {
		imageComposite.setEnabled(enabled);
		super.enableChanged(enabled);
    }
	
	private SelectionListener getSelectionListener() {
		return new SelectionListener() {
			public void widgetSelected(SelectionEvent event) {
				FileDialog fileDialog = new FileDialog(event.widget.getDisplay().getActiveShell(), SWT.OPEN);
				fileDialog.setFilterExtensions(filterExt);
				if(tempUrl == null || tempUrl.length() == 0 ) {
					fileDialog.setFilterPath(INIT_IMAGEURL);
				} else {
					fileDialog.setFilterPath(tempUrl);
				}

				tempUrl = fileDialog.open();
				if(tempUrl != null && tempUrl.length() >= 4){
					String suffix = tempUrl.substring(tempUrl.length() - 4);
//					if(tempUrl.endsWith(".jpg") || tempUrl.endsWith(".jif") || tempUrl.endsWith("png")) {
					if(suffix.equalsIgnoreCase(".jpg") || tempUrl.equalsIgnoreCase(".jif") || tempUrl.equalsIgnoreCase("png")) {
						File imgFile = new File(tempUrl);
						long imgFileSize= imgFile.length();
						if(IMAGEBYTE < imgFileSize) {
							imageLabel.setImage(null);
							imageLabel.setText(Message.getString("common.image_tooLarge"));
							return;
						}			            
						byte[] byteValue = new byte[(int)imgFileSize];						
						try {			
							InputStream is = new BufferedInputStream(new FileInputStream(imgFile));
							int len = is.read(byteValue);
							if(len != imgFileSize) {
								return;
							}
						} catch(Exception e) {
							logger.error(e.getMessage() + e);
						}
						setValue(byteValue);
						refresh();
					} else {
						tempUrl = null;
						imageLabel.setImage(null);
						imageLabel.setText(Message.getString("common.imageformat_incorrect"));
					}
				}
			}
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		};
	}
	
	protected ImageData resetImageData(InputStream inputStream) {
		ImageData id = new ImageData(inputStream);
		int width = id.width;
        int height = id.height;
        if(width > IMAGESCALE_WIDTH && height > IMAGESCALE_HEIGHT) {
        	double ratioW = (double)(IMAGESCALE_WIDTH / width);
        	double ratioH = (double)(IMAGESCALE_HEIGHT / height);
        	if(ratioW < ratioH) {
        		width = (int)IMAGESCALE_WIDTH;
        		height = (int)(ratioW * height);
        	} else {
        		height = (int)IMAGESCALE_HEIGHT;
        		width = (int)(ratioH * width);
        	}
        } else {
        	double thumbRatio = 0D;
        	if(width > IMAGESCALE_WIDTH) {
        		thumbRatio = (double)(IMAGESCALE_WIDTH / width);
        		width = (int)IMAGESCALE_WIDTH;
        		height = (int)(thumbRatio * height);
        	}
        	if(height > IMAGESCALE_HEIGHT) {
        		thumbRatio = (double)(IMAGESCALE_HEIGHT / height);
        		height = (int)IMAGESCALE_HEIGHT;
        		width = (int)(thumbRatio * width);
        	}        	
        }
        ImageData thbData=id.scaledTo(width, height);
        return thbData;
	}
	
	public byte[] getImageByBytes() {
		return (byte[])getValue();
	}
	
	public void decorateButton(Button button) {
		button.setFont(JFaceResources.getDialogFont());
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		int widthHint = 88;  //IDialogConstants.BUTTON_WIDTH
		Point minSize = button.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
		data.widthHint = Math.max(widthHint, minSize.x);
		button.setLayoutData(data);
	}
}