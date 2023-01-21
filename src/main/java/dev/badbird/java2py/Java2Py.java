package dev.badbird.java2py;

import org.objectweb.asm.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Java2Py {
    private static final Map<String, String> TYPES_MAP;

    static {
        TYPES_MAP = new HashMap<>();
        TYPES_MAP.put("java.lang.String", "str");
        TYPES_MAP.put("java.lang.Integer", "i32");
        TYPES_MAP.put("java.lang.Long", "i64");
        TYPES_MAP.put("java.lang.Float", "f32");
        TYPES_MAP.put("java.lang.Double", "f64");
        TYPES_MAP.put("java.lang.Boolean", "bool");
        TYPES_MAP.put("java.lang.Byte", "u8");
        TYPES_MAP.put("java.lang.Short", "i16");
        TYPES_MAP.put("java.lang.Character", "char");

        // primitive types
        TYPES_MAP.put("int", "i32");
        TYPES_MAP.put("long", "i64");
        TYPES_MAP.put("float", "f32");
        TYPES_MAP.put("double", "f64");
        TYPES_MAP.put("boolean", "bool");
        TYPES_MAP.put("byte", "u8");
        TYPES_MAP.put("short", "i16");
        TYPES_MAP.put("char", "char");
    }

    private ClassReader classReader;

    public Java2Py(byte[] classBytes) {
        classReader = new ClassReader(classBytes);

        classReader.accept(new ClassVisitor(Opcodes.ASM7) {
            @Override
            public FieldVisitor visitField(int access, String name, String desc,
                                           String signature, Object cst) {
                System.out.println("access: " + access + " name: " + name + " desc: " + desc + " signature: " + signature + " cst: " + cst);
                return super.visitField(access, name, desc, signature, cst);
            }

            @Override
            public MethodVisitor visitMethod(int access, String name, String desc,
                                             String signature, String[] exceptions) {
                MethodVisitor mv = super.visitMethod(
                        access, name, desc, signature, exceptions);
                return new MethodVisitor(Opcodes.ASM5, mv) {
                    @Override
                    public void visitLdcInsn(Object cst) {
                        //System.out.println("cst: " + cst + " access: " + access + " name: " + name + " desc: " + desc + " signature: " + signature + " exceptions: " + Arrays.toString(exceptions));
                        super.visitLdcInsn(cst);
                    }
                };
            }
        }, 0);
    }

    public static void main(String[] args) throws IOException {
        File file = new File("Test.class");
        byte[] classBytes = Files.readAllBytes(file.toPath());
        Java2Py java2Py = new Java2Py(classBytes);
    }
}
