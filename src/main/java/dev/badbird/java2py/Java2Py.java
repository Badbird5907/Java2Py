package dev.badbird.java2py;

import com.sun.org.apache.bcel.internal.classfile.AccessFlags;
import org.objectweb.asm.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.util.*;

public class Java2Py {
    private static final Map<String, String> TYPES_MAP;

    static {
        TYPES_MAP = new HashMap<>();
        TYPES_MAP.put("Ljava/lang/String", "str");
        TYPES_MAP.put("Ljava/lang/Integer", "i32");
        TYPES_MAP.put("Ljava/lang/Long", "i64");
        TYPES_MAP.put("Ljava/lang/Float", "f32");
        TYPES_MAP.put("Ljava/lang/Double", "f64");
        TYPES_MAP.put("Ljava/lang/Boolean", "bool");
        TYPES_MAP.put("Ljava/lang/Byte", "u8");
        TYPES_MAP.put("Ljava/lang/Short", "i16");
        TYPES_MAP.put("Ljava/lang/Character", "char");

        // primitive types
        TYPES_MAP.put("int", "i32");
        TYPES_MAP.put("long", "i64");
        TYPES_MAP.put("float", "f32");
        TYPES_MAP.put("double", "f64");
        TYPES_MAP.put("boolean", "bool");
        TYPES_MAP.put("byte", "u8");
        TYPES_MAP.put("short", "i16");
        TYPES_MAP.put("char", "char");

        // bytecode types
        TYPES_MAP.put("I", "i32");
        TYPES_MAP.put("J", "i64");
        TYPES_MAP.put("F", "f32");
        TYPES_MAP.put("D", "f64");
        TYPES_MAP.put("Z", "bool");
        TYPES_MAP.put("B", "u8");
        TYPES_MAP.put("S", "i16");
        TYPES_MAP.put("C", "char");

    }

    private ClassReader classReader;

    private List<String> result = new ArrayList<>();

    public Java2Py(byte[] classBytes) {
        classReader = new ClassReader(classBytes);

        classReader.accept(new ClassVisitor(Opcodes.ASM8) {
            @Override
            public FieldVisitor visitField(int access, String name, String desc,
                                           String signature, Object cst) {
                System.out.println("access: " + access + " name: " + name + " desc: " + desc + " signature: " + signature + " cst: " + cst);
                boolean staticField = Modifier.isStatic(access);
                boolean finalField = Modifier.isFinal(access);

                if (!staticField) {
                    // TODO this is not a static field, so cst is null, and we need to get the value

                }

                String varName = (finalField ? "__" + name + "__" : name);

                String line = varName + (cst != null ? " = " + cst : "") + " # " + typeToPy(desc) + " | " + name;
                result.add(line);
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
        System.out.println("--------------------");
        System.out.println(String.join("\n", result));

    }

    public String typeToPy(String type) {
        return TYPES_MAP.getOrDefault(type, type);
    }

    public static void main(String[] args) throws IOException {
        File file = new File("Test.class");
        byte[] classBytes = Files.readAllBytes(file.toPath());
        Java2Py java2Py = new Java2Py(classBytes);
    }
}
