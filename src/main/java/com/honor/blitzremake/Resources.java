package com.honor.blitzremake;

import com.honor.blitzremake.graphics.VertexArray;

public class Resources {

	public static VertexArray projectile_mesh, decal1_mesh, decal3_mesh, weapon_mesh, generator_mesh, ground_mesh, preLevel_mesh, light_mesh, pickup_mesh, particle_mesh, electric_mesh, hp_mesh, sparks_mesh;
	public static VertexArray wall_mesh, enemy_mesh, fade_mesh, pipe_mesh, blood_particle_mesh;
	public static VertexArray bloodSplat_mesh, sparkle_mesh, debug_mesh;

	public static void loadMeshes() {
		projectile_mesh = new VertexArray(16.0f, 40.0f, 0.2f);
		decal1_mesh = new VertexArray(64.0f, 64.0f, 0.1f);
		decal3_mesh = new VertexArray(88.0f, 32.0f, 0.1f);
		weapon_mesh = new VertexArray(80.0f, 33.0f, 0.4f);
		generator_mesh = new VertexArray(100.0f, 100.0f, 0.1f);
		ground_mesh = new VertexArray(256.0f, 256.0f, 0.0f);
		preLevel_mesh = new VertexArray(863.0f, 532.0f, 0.0f);
		pickup_mesh = new VertexArray(64, 64, 0.1f);
		light_mesh = new VertexArray(400, 400, 0.3f);
		particle_mesh = new VertexArray(2, 2, 0.9f);
		blood_particle_mesh = new VertexArray(128, 128, 0.1F);

		electric_mesh = new VertexArray(40, 40, 0.95f);
		hp_mesh = new VertexArray(32, 32, 0.95f);
		sparks_mesh = new VertexArray(120, 28, 0.95f);
		wall_mesh = new VertexArray(130, 130, 0.0f);
		enemy_mesh = new VertexArray(64, 64, 0.0f);
		pipe_mesh = new VertexArray(112, 81, 0.0f);
		fade_mesh = new VertexArray(2, 2, 0.0f);

		sparkle_mesh = new VertexArray(120, 28, 0.0f);
		
		debug_mesh = new VertexArray(10, 10, 0.3f);

		float[] vertices = new float[] { 0, 128, 0, //
				0, 0, 0, //
				128, 0, 0, //
				128, 128, 0 };

		byte[] indices = new byte[] { 0, 1, 2, //
				2, 3, 0 };

		float[] textureCoordinates = new float[] { 0, 1, //
				0, 0, //
				1, 0, //
				1, 1 };

		bloodSplat_mesh = new VertexArray(vertices, indices, textureCoordinates);

	}
}
