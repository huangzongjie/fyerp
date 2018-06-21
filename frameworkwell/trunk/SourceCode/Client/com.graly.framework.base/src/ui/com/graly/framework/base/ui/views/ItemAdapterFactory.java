package com.graly.framework.base.ui.views;

import com.graly.framework.base.ui.util.ClassKeyedMap;

public class ItemAdapterFactory {

    ClassKeyedMap registry = new ClassKeyedMap();

    public ItemAdapter getAdapter(Class<?> klass) {
        return (ItemAdapter)registry.get(klass);
    }

    public void registerAdapter(Class<?> klass, ItemAdapter adapter) {
        registry.put(klass, adapter);
    }

    public void dispose() {
        registry.clear();
    }
}
