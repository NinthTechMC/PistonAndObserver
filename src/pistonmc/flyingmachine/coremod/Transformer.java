package pistonmc.flyingmachine.coremod;

import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

import net.minecraft.launchwrapper.IClassTransformer;

/**
 * Transformer for the coremod that hooks the BlockPistonBase class
 */
public class Transformer implements IClassTransformer {
	
	private static final String BlockPistonBaseClassName = "net.minecraft.block.BlockPistonBase";
	private static final String CanExtendMethodName = "canExtend";
	private static final String CanExtendMethodDescriptor = "(Lnet/minecraft/world/World;IIII)Z";
	private static final String OnBlockEventReceivedMethodName = "onBlockEventReceived";
	private static final String OnBlockEventReceivedDescriptor = "(Lnet/minecraft/world/World;IIIII)Z";
	// replacement is different because the original method is not static
	private static final String OnBlockEventReceivedReplacementDescriptor = "(Lnet/minecraft/block/Block;Lnet/minecraft/world/World;IIIII)Z";

	private Map<String, String> obfMap;
	/**
	 * if we are in obfuscated environment
	 */
	private boolean isObf;

	public Transformer() {
		obfMap = new HashMap<String, String>();
		obfMap.put(BlockPistonBaseClassName, "app");
		obfMap.put(CanExtendMethodName, "h");
		obfMap.put(CanExtendMethodDescriptor, "(Lahb;IIII)Z");
		obfMap.put(OnBlockEventReceivedMethodName, "a");
		obfMap.put(OnBlockEventReceivedDescriptor, "(Lahb;IIIII)Z");
		obfMap.put(OnBlockEventReceivedReplacementDescriptor, "(Laji;Lahb;IIIII)Z");
	}

	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		if (transformedName.equals(BlockPistonBaseClassName)) {
			isObf = !name.equals(transformedName);
			CoremodMain.log.info("Attempting to transform BlockPistonBase, old length = " + basicClass.length);
			ClassNode node = new ClassNode();
			ClassReader reader = new ClassReader(basicClass);
			reader.accept(node, 0);

			CoremodMain.log.info("Transforming " + name + "(" + transformedName + ")");
			transformBlockPistonBase(node);

			ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
			node.accept(writer);
			byte[] newContent = writer.toByteArray();
			CoremodMain.log.info("Transformed BlockPistonBase, new length = " + newContent.length);

			return newContent;
		}

		return basicClass;
	}

	private void transformBlockPistonBase(ClassNode classNode) {
		
		Map<String, MethodHook> hookMap = new HashMap<String, MethodHook>();
		// @formatter:off
		String replacementClass = CoremodInfo.GroupInternal + "/piston/BlockPistonHooks";
		String hookConfigMethod = "hooksEnableSlimeBlockPiston";
		
		MethodHook onBlockEventReceivedHook = new MethodHook.Builder()
			.setConfigMethod(hookConfigMethod)
			.setReplacement(
				replacementClass,
				OnBlockEventReceivedMethodName,
				getSymbol(OnBlockEventReceivedReplacementDescriptor))
			.setOpcodes(Opcodes.IRETURN, Opcodes.ALOAD,Opcodes.ALOAD,Opcodes.ILOAD,Opcodes.ILOAD,Opcodes.ILOAD,Opcodes.ILOAD,Opcodes.ILOAD)
			.build();
		hookMap.put(getSymbol(OnBlockEventReceivedMethodName)+getSymbol(OnBlockEventReceivedDescriptor), onBlockEventReceivedHook);
		
		MethodHook canExtendHook = new MethodHook.Builder()
			.setConfigMethod(hookConfigMethod)
			.setReplacement(
				replacementClass,
				CanExtendMethodName,
				getSymbol(CanExtendMethodDescriptor))
			.setOpcodes(Opcodes.IRETURN, Opcodes.ALOAD,Opcodes.ILOAD,Opcodes.ILOAD,Opcodes.ILOAD,Opcodes.ILOAD)
			.build();
		hookMap.put(getSymbol(CanExtendMethodName)+getSymbol(CanExtendMethodDescriptor), canExtendHook);
		
		CoremodMain.log.info("Registered " +hookMap.size()+" Hooks");
		// @formatter:on
		int hookedCount = 0;
		for (MethodNode method : classNode.methods) {
			String methodFullSignature = method.name + method.desc;
			MethodHook hook = hookMap.get(methodFullSignature);
			if (hook != null) {
				InsnList toInsert = hook.getInsnList();
				InsnList currentList = method.instructions;
				if (currentList.size() == 0) {
					// error case?
					currentList.add(toInsert);
				} else {
					currentList.insertBefore(currentList.getFirst(), toInsert);
				}
				CoremodMain.log.info("Hooked " + methodFullSignature);
				hookedCount++;
			}

		}
		CoremodMain.log.info("Inserted " + hookedCount + " Hooks");
	}

	private String getSymbol(String deobfName) {
		String name = isObf ? obfMap.get(deobfName) : deobfName;
		return name;
	}

}
