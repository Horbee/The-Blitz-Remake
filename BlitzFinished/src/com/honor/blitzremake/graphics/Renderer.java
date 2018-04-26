package com.honor.blitzremake.graphics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.honor.blitzremake.entity.Entity;
import com.honor.blitzremake.entity.TexturedModel;

public class Renderer {

	private Map<TexturedModel, List<Entity>> entities = new HashMap<TexturedModel, List<Entity>>();
	private List<Light> lights = new ArrayList<Light>();
	
	private EntityRenderer entityRenderer;
	private LightRenderer lightRenderer;
	
	public Renderer() {
		entityRenderer = new EntityRenderer();
		lightRenderer = new LightRenderer();
	}
	
	public void render() {
		
		entityRenderer.render(entities);
		lightRenderer.render(lights);
		
		lights.clear();
		entities.clear();
	}

	public void addEntity(Entity e) {
		TexturedModel model = e.getModel();
		List<Entity> batch = entities.get(model);
		if (batch != null) {
			batch.add(e);
		} else {
			List<Entity> newBatch = new ArrayList<Entity>();
			newBatch.add(e);
			entities.put(model, newBatch);
		}
	}
	
	public void addLight(Light l) {
		lights.add(l);
	}

}
