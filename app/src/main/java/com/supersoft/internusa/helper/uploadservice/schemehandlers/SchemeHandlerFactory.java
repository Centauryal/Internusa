package com.supersoft.internusa.helper.uploadservice.schemehandlers;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Factory which instantiates the proper scheme handler based on the scheme passed.
 * @author gotev
 */
public class SchemeHandlerFactory {

    private static class LazyHolder {
        private static final SchemeHandlerFactory INSTANCE = new SchemeHandlerFactory();
    }

    public static SchemeHandlerFactory getInstance() {
        return LazyHolder.INSTANCE;
    }

    private LinkedHashMap<String, Class<? extends SchemeHandler>> handlers = new LinkedHashMap<>();

    private SchemeHandlerFactory() {
        handlers.put("file://", FileSchemeHandler.class);
        handlers.put("content://", ContentSchemeHandler.class);
    }

    public SchemeHandler get(String path)
            throws NoSuchMethodException, IllegalAccessException,
                   InvocationTargetException, InstantiationException {
        for (Map.Entry<String, Class<? extends SchemeHandler>> handler : handlers.entrySet()) {
            //Log.e("SHF", "PATH > " + path + " >key > "+ handler.getKey());
            if (path.startsWith(handler.getKey())) {
                //.getConstructor(String.class).newInstance(path);
                return handler.getValue().getConstructor(String.class).newInstance(path);
            }
        }

        throw new UnsupportedOperationException("No handlers for " + path);
    }

    public boolean isSupported(String path) {
        for (String scheme : handlers.keySet()) {
            if (path.startsWith(scheme))
                return true;
        }

        return false;
    }
}
