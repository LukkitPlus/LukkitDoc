package online.pizzacrust.lukkitdoc;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.File;
import java.util.Iterator;
import java.util.List;

public class PlayerTypeProfiler {

    public static void main(String[] args) throws Exception {
        File file = new File(args[0]);
        ClassIndexer indexer = new ClassIndexer(file);
        List<ClassNode> nodes = indexer.index();
        for (ClassNode node : nodes) {
            if (node.name.equals("online/pizzacrust/lukkitplus/api/PlayerType")) {
                for (MethodNode methodNode : node.methods) {
                    if (methodNode.name.equals("<init>")) {
                        Iterator<AbstractInsnNode> insnNodeIterator = methodNode.instructions
                                .iterator();
                        while (insnNodeIterator.hasNext()) {
                            AbstractInsnNode abstractInsnNode = insnNodeIterator.next();
                            if (abstractInsnNode instanceof MethodInsnNode) {
                                MethodInsnNode methodInsnNode = (MethodInsnNode) abstractInsnNode;
                                System.out.println(methodInsnNode.owner + "." + methodInsnNode
                                        .name + methodInsnNode.desc);
                            }
                        }
                    }
                }
            }
        }
    }

}
