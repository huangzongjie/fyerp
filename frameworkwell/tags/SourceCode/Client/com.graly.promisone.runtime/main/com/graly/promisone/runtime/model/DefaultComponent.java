package com.graly.promisone.runtime.model;

public class DefaultComponent implements Component, Adaptable {
	
	public void activate(ComponentContext context) throws Exception {
    }

    public void deactivate(ComponentContext context) throws Exception {
    }
    
    public void registerExtension(Extension extension) throws Exception {
        Object[] contribs = extension.getContributions();
        if (contribs == null) {
            return;
        }
        for (Object contrib : contribs) {
            registerContribution(contrib, extension.getExtensionPoint(), extension.getComponent());
        }
    }
    
    public void unregisterExtension(Extension extension) throws Exception {
        Object[] contribs = extension.getContributions();
        if (contribs == null) {
            return;
        }
        for (Object contrib : contribs) {
            unregisterContribution(contrib, extension.getExtensionPoint(), extension.getComponent());
        }
    }
    
    public void registerContribution(Object contribution, String extensionPoint,
            ComponentInstance contributor) throws Exception {
    }

    public void unregisterContribution(Object contribution, String extensionPoint,
            ComponentInstance contributor) throws Exception {
    }

    public <T> T getAdapter(Class<T> adapter) {
        return adapter.cast(this);
    }
}
