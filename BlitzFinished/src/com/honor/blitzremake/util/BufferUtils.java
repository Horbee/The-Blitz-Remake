package com.honor.blitzremake.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class BufferUtils {
	
	public static ByteBuffer createByteBuffer(byte[] data) {
		ByteBuffer result = ByteBuffer.allocateDirect(data.length).order(ByteOrder.nativeOrder());
		result.put(data).flip();
		return result;
	}

	public static FloatBuffer createFloatBuffer(float[] data) {
		FloatBuffer result = ByteBuffer.allocateDirect(data.length << 2).order(ByteOrder.nativeOrder()).asFloatBuffer();
		result.put(data).flip();
		return result;
	}
	
	public static IntBuffer createIntBuffer(int[] data) {
		IntBuffer result = ByteBuffer.allocateDirect(data.length << 2).order(ByteOrder.nativeOrder()).asIntBuffer();
		result.put(data).flip();
		return result;
	}

	
}
