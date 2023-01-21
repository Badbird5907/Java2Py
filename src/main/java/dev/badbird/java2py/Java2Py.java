package dev.badbird.java2py;

import java.util.HashMap;
import java.util.Map;

public class Java2Py {
    private static final Map<String, String> TYPES_MAP;
    static {
        TYPES_MAP = new HashMap<>();
        TYPES_MAP.put("java.lang.String", "str");
        TYPES_MAP.put("java.lang.Integer", "i32");
    }
    public Java2Py(byte[] classBytes) {

    }
}
