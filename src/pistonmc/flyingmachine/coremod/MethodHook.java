package pistonmc.flyingmachine.coremod;


import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

/**
 * Util for hooking a method with another method
 */
public class MethodHook {
	public static class Builder {
		/**
		 * Method name in CoremodConfig for enabling the hook
		 */
		private String configEnableMethod;
		/**
		 * Class that contains the replacement method
		 */
		private String replacementClass;
		/**
		 * Name of the method in the replacement class
		 */
		private String replacementMethod;
		/**
		 * Descriptor of the replacement method (for arguments)
		 */
		private String replacementDescriptor;
		private int returnOpcode;
		private int[] loadOpcodes;

		public Builder setConfigMethod(String configMethod) {
			this.configEnableMethod = configMethod;
			return this;
		}

		public Builder setReplacement(String className, String methodName, String descriptor) {
			this.replacementClass = className;
			this.replacementMethod = methodName;
			this.replacementDescriptor = descriptor;
			return this;
		}

		public Builder setOpcodes(int returnOpcode, int... load) {
			this.returnOpcode = returnOpcode;
			this.loadOpcodes = load;
			return this;
		}

		public MethodHook build() {
			return new MethodHook(configEnableMethod, replacementClass, replacementMethod, replacementDescriptor, returnOpcode,
					loadOpcodes);
		}
	}

	private String configEnableMethod;
	private String replacementClass;
	private String replacementMethod;
	private String replacementDescriptor;
	private int returnOpcode;
	private int[] loadOpcodes;

	private MethodHook(String configEnableMethod, String replacementClass, String replacementMethod,
			String replacementDescriptor, int returnOpcode, int[] loadOpcodes) {
		super();
		this.configEnableMethod = configEnableMethod;
		this.replacementClass = replacementClass;
		this.replacementMethod = replacementMethod;
		this.replacementDescriptor = replacementDescriptor;
		this.returnOpcode = returnOpcode;
		this.loadOpcodes = loadOpcodes;
	}

	public InsnList getInsnList() {
		// generate insn list like this
		// invokestatic gate
		// ifeq L1
		// load
		// load ...
		// invokestatic replacement
		// ireturn
		// L1
		InsnList list = new InsnList();

		LabelNode endLabel = new LabelNode();
		MethodInsnNode invokeGateInsn = new MethodInsnNode(Opcodes.INVOKESTATIC,
				Type.getInternalName(CoremodConfig.class), configEnableMethod, "()Z", false);
		list.add(invokeGateInsn);
		JumpInsnNode ifeqInsn = new JumpInsnNode(Opcodes.IFEQ, endLabel);
		list.add(ifeqInsn);
		for (int i = 0; i < loadOpcodes.length; i++) {
			list.add(new VarInsnNode(loadOpcodes[i], i));
		}

		MethodInsnNode invokeReplacementInsn = new MethodInsnNode(Opcodes.INVOKESTATIC, replacementClass,
				replacementMethod, replacementDescriptor, false);
		list.add(invokeReplacementInsn);
		InsnNode returnInsn = new InsnNode(returnOpcode);
		list.add(returnInsn);
		list.add(endLabel);
		list.add(new FrameNode(Opcodes.F_SAME, 0, null, 0, null));

		return list;
	}

}
