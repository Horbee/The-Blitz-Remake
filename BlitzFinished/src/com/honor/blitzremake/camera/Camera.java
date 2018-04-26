package com.honor.blitzremake.camera;

import com.honor.blitzremake.math.Matrix4f;
import com.honor.blitzremake.math.Vector3f;

public class Camera {
	
	public static Vector3f position = new Vector3f();
	
	public static void move(int x, int y) {
		position.x = x;
		position.y = y;
		
	}
	
	public static Matrix4f getViewMatrix() {
		return Matrix4f.translate(new Vector3f(position.x, position.y, 0.0f));
	}

}
