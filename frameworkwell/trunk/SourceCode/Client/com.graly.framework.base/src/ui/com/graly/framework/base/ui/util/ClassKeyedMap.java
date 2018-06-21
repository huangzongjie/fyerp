package com.graly.framework.base.ui.util;

import java.util.HashMap;

public class ClassKeyedMap extends HashMap<Class, Object> {

    private static final long serialVersionUID = -1L;

    private final static Object NULL = new Object();

    public Object get(Class klass) {
        Object value = findValue(klass);
        return value != NULL ? value : null;
    }

    private final Object findValue(Class klass) {
        Object value = super.get(klass);
        if (value != null) {
            return value;
        }
        // query interfaces first
        Class[] itfs = klass.getInterfaces();
        for (Class itf : itfs) {
            value = findValue(itf);
            if (value != null && value != NULL) {
                put(klass, value);
                return value;
            }
        }
        // query superclass
        Class sc = klass.getSuperclass();
        if (sc != null) {
            value = findValue(sc);
            // value is allways not null (at least NULL)
            super.put(klass, value);
            return value;
        }
        // nothing found
        super.put(klass, NULL);
        return NULL;
    }
}