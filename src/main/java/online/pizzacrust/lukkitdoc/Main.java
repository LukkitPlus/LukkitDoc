package online.pizzacrust.lukkitdoc;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        File file = new File(args[0]);
        ClassIndexer indexer = new ClassIndexer(file);
        List<ClassNode> classNodeList = indexer.index();
        List<ClassNode> functions = new ArrayList<ClassNode>();
        List<ClassNode> libs = new ArrayList<ClassNode>();
        for (ClassNode classNode : classNodeList) {
            if (!classNode.name.equals("online/pizzacrust/lukkitplus/environment/LuaLibrary")) {
                if (!classNode.name.equals
                        ("online/pizzacrust/lukkitplus/environment/LuaLibrary$StaticLibrary")) {
                    if (classNode.interfaces.contains
                            ("online/pizzacrust/lukkitplus/environment/FunctionController")) {
                        System.out.println("Found function: " + classNode.name);
                        functions.add(classNode);
                        continue;
                    }
                    if (classNode.superName.equals("online/pizzacrust/lukkitplus/environment/LuaLibrary")) {
                        System.out.println("Found library: " + classNode.name);
                        libs.add(classNode);
                        continue;
                    }
                    if (classNode.superName.equals
                            ("online/pizzacrust/lukkitplus/environment/LuaLibrary$StaticLibrary")) {
                        System.out.println("Found static library: " + classNode.name);
                        libs.add(classNode);
                        continue;
                    }
                }
            }
        }
        StringBuilder builder = new StringBuilder();
        builder.append("# LukkitDoc\n");
        builder.append("This is an automatically generated document.\n");
        builder.append("\n");
        for (ClassNode lib : libs) {
            builder.append("## " + lib.name.replace('/', '.') + "\n");
            List<String> fields = new ArrayList<String>();
            for (MethodNode methodNode : lib.methods) {
                if (methodNode.name.equals("<init>")) {
                    fields.addAll(getFields(methodNode));
                }
            }
            builder.append("\n");
            for (String field : fields) {
                builder.append("- **Variable** - " + field + "\n");
            }
            builder.append("\n");
            List<ClassNode> cFunc = getFunctionsForLib(functions,lib);
            for (ClassNode func : cFunc) {
                builder.append("- **Function** - " + getNameFunc(func) + "\n");
            }
            builder.append("\n");
        }
        File output = new File("output.md");
        Files.write(output.toPath(), builder.toString().getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
    }
    public static String getNameFunc(ClassNode classNode) {
        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals("getName")) {
                Iterator<AbstractInsnNode> insnNodeIterator = methodNode.instructions.iterator();
                while (insnNodeIterator.hasNext()) {
                    AbstractInsnNode insnNode = insnNodeIterator.next();
                    if (insnNode instanceof LdcInsnNode) {
                        LdcInsnNode ldcInsnNode = (LdcInsnNode) insnNode;
                        return (String) ldcInsnNode.cst;
                    }
                }
            }
        }
        return null;
    }
    public static List<ClassNode> getFunctionsForLib(List<ClassNode> allFunctions, ClassNode lib) {
        List<ClassNode> functions = new ArrayList<ClassNode>();
        for (ClassNode function : allFunctions) {
            if (function.name.contains(lib.name)) {
                functions.add(function);
            }
        }
        return functions;
    }
    public static List<String> getFields(MethodNode methodNode) {
        List<String> strings = new ArrayList<String>();
        Iterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
        while (iterator.hasNext()) {
            AbstractInsnNode abstractInsnNode = iterator.next();
            if (abstractInsnNode instanceof LdcInsnNode) {
                LdcInsnNode ldcInsnNode = (LdcInsnNode) abstractInsnNode;
                strings.add((String) ldcInsnNode.cst);
            }
        }
        return strings;
    }
}
