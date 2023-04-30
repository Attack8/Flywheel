package com.jozufozu.flywheel.gl.versioned;

import java.nio.ByteBuffer;
import java.util.Optional;

import org.lwjgl.PointerBuffer;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL20C;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.system.MemoryStack;

import net.minecraft.Util;

/**
 * An instance of this class stores information about what OpenGL features are available.
 * <br>
 * Each field stores an enum variant that provides access to the most appropriate version of a feature for the current
 * system.
 */
public class GlCompat {
	private static final GLCapabilities caps;
	public static final VertexArray vertexArray;
	public static final BufferStorage bufferStorage;
	public static final boolean amd;
	public static final boolean supportsIndirect;

	static {
		caps = GL.createCapabilities();
		bufferStorage = getLatest(BufferStorage.class);
		vertexArray = getLatest(VertexArray.class);
		supportsIndirect = _decideIfWeSupportIndirect();
		amd = _decideIfWeAreAMDWindows();
	}

	private GlCompat() {
	}

	public static boolean onAMDWindows() {
		return amd;
	}

	public static boolean supportsInstancing() {
		return vertexArray != VertexArray.UNSUPPORTED;
	}

	public static boolean supportsIndirect() {
		return supportsIndirect;
	}

	private static boolean _decideIfWeSupportIndirect() {
		return caps.OpenGL46 || (
				caps.GL_ARB_compute_shader &&
				caps.GL_ARB_shader_draw_parameters &&
				caps.GL_ARB_base_instance &&
				caps.GL_ARB_multi_draw_indirect &&
				caps.GL_ARB_direct_state_access);
	}

	/**
	 * Get the most compatible version of a specific OpenGL feature by iterating over enum constants in order.
	 *
	 * @param <V>   The type of the versioning enum.
	 * @param clazz The class of the versioning enum.
	 * @return The first defined enum variant to return true.
	 */
	private static <V extends Enum<V> & GlVersioned> V getLatest(Class<V> clazz) {
		for (V it : clazz.getEnumConstants()) {
			if (it.supported(GlCompat.caps)) {
				return Optional.of(it)
						.get();
			}
		}
		throw new IllegalStateException("Invalid versioned enum, must provide at least one supported constant");
	}

	/**
	 * Modified from:
	 * <br> <a href="https://github.com/grondag/canvas/commit/820bf754092ccaf8d0c169620c2ff575722d7d96">canvas</a>
	 *
	 * <p>Identical in function to {@link GL20C#glShaderSource(int, CharSequence)} but
	 * passes a null pointer for string length to force the driver to rely on the null
	 * terminator for string length.  This is a workaround for an apparent flaw with some
	 * AMD drivers that don't receive or interpret the length correctly, resulting in
	 * an access violation when the driver tries to read past the string memory.
	 *
	 * <p>Hat tip to fewizz for the find and the fix.
	 */
	public static void safeShaderSource(int glId, CharSequence source) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			final ByteBuffer sourceBuffer = stack.UTF8(source, true);
			final PointerBuffer pointers = stack.mallocPointer(1);
			pointers.put(sourceBuffer);
			GL20C.nglShaderSource(glId, 1, pointers.address0(), 0);
		}
	}

	private static boolean _decideIfWeAreAMDWindows() {
		if (Util.getPlatform() != Util.OS.WINDOWS) {
			return false;
		}

		String vendor = GL20C.glGetString(GL20C.GL_VENDOR);

		if (vendor == null) {
			return false;
		}

		// vendor string I got was "ATI Technologies Inc."
		return vendor.contains("ATI") || vendor.contains("AMD");
	}
}

