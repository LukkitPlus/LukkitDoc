package online.pizzacrust.lukkitdoc;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import sun.misc.IOUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassIndexer {

    private final File file;

    public ClassIndexer(File file) {
        this.file = file;
    }

    public List<ClassNode> index() throws Exception {
        ArrayList<ClassNode> nodes = new ArrayList<ClassNode>();
        JarFile jarFile = new JarFile(this.file);
        Enumeration<JarEntry> enumeration = jarFile.entries();
        while (enumeration.hasMoreElements()) {
            JarEntry jarEntry = enumeration.nextElement();
            if (jarEntry.getName().endsWith(".class")) {
                byte[] classBytes = IOUtils.readFully(jarFile.getInputStream(jarEntry), -1, false);
                ClassReader classReader = new ClassReader(classBytes);
                ClassNode classNode = new ClassNode();
                classReader.accept(classNode, 0);
                nodes.add(classNode);
            }
        }
        return nodes;
    }

}
