package com.graly.promisone.runtime.osgi;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;

import com.graly.promisone.runtime.AbstractRuntimeService;
import com.graly.promisone.runtime.model.RuntimeContext;

public class OSGiRuntimeService extends AbstractRuntimeService implements
		FrameworkListener {
	
	public static final String PROP_CONFIG_DIR = "CONFIG_DIR";
	
	private final BundleContext bundleContext;
	private final Map<Bundle, RuntimeContext> contexts;
	
	public OSGiRuntimeService(BundleContext context) {
        super(new OSGiRuntimeContext(context.getBundle()));
        bundleContext = context;
        contexts = new HashMap<Bundle, RuntimeContext>();
        workingDir = bundleContext.getDataFile("/");
    }
	
    public BundleContext getBundleContext() {
        return bundleContext;
    }
    
    public synchronized RuntimeContext createContext(Bundle bundle) throws Exception {
        RuntimeContext ctx = contexts.get(bundle);
        if (ctx == null) {
            ctx = new OSGiRuntimeContext(bundle);
            contexts.put(bundle, ctx);
            loadComponents(bundle, ctx);
        }
        return ctx;
    }
    
    public synchronized void destroyContext(Bundle bundle) {
        RuntimeContext ctx = contexts.remove(bundle);
        if (ctx != null) {
            ctx.destroy();
        }
    }
    
    public synchronized RuntimeContext getContext(Bundle bundle) {
        return contexts.get(bundle);
    }
    
    @Override
    protected void doStart() throws Exception {
        bundleContext.addFrameworkListener(this);
        loadConfig(); // load configuration if any
        loadComponents(bundleContext.getBundle(), context);
    }

    @Override
    protected void doStop() throws Exception {
        bundleContext.removeFrameworkListener(this);
        super.doStop();
        context.destroy();
    }
    
    protected void loadComponents(Bundle bundle, RuntimeContext ctx) throws Exception {
        String list = getComponentsList(bundle);
        if (list == null) {
            return;
        }
        StringTokenizer tok = new StringTokenizer(list, ", \t\n\r\f");
        while (tok.hasMoreTokens()) {
            String desc = tok.nextToken();
            URL url = bundle.getEntry(desc);
            if (url != null) {
                try {
                    ctx.deploy(url);
                } catch (Exception e) {
                    throw e;
                }
            } else {
            }
        }
    }
    
    public static String getComponentsList(Bundle bundle) {
        return (String) bundle.getHeaders().get("PromisOne-Component");
    }
    
    protected void loadConfig() throws Exception {
        String configDir = bundleContext.getProperty(PROP_CONFIG_DIR);
        if (configDir == null) {
            return;
        }

        if (configDir.contains(":/")) { 
            URL url = new URL(configDir);
            loadProperties(url);
            return;
        }

        File dir = new File(configDir);
        if (dir.isDirectory()) {
            for (String name : dir.list()) {
                if (name.endsWith("-config.xml") || name.endsWith("-bundle.xml")) {
                    //TODO
                } else if (name.endsWith(".config")
                        || name.endsWith(".ini")
                        || name.endsWith(".properties")) {
                    File file = new File(dir, name);
                    loadProperties(file);
                }
            }
        } else { // a file - load it
            File file = new File(configDir);
            loadProperties(file);
        }
    }
    
    public void loadProperties(File file) throws IOException {
        InputStream in = new BufferedInputStream(new FileInputStream(file));
        try {
            loadProperties(in);
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }

    public void loadProperties(URL url) throws IOException {
        InputStream in = url.openStream();
        try {
            loadProperties(in);
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }

    public void loadProperties(InputStream in) throws IOException {
        Properties props = new Properties();
        props.load(in);
        for (Map.Entry<Object, Object> prop : props.entrySet()) {
            properties.put(prop.getKey().toString(), prop.getValue().toString());
        }
    }
    
	@Override
	public void frameworkEvent(FrameworkEvent event) {
        if (event.getType() == FrameworkEvent.STARTED) {

        }
	}
	
	
}
