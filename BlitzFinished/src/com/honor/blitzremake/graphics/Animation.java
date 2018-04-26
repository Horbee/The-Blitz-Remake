package com.honor.blitzremake.graphics;

import java.util.ArrayList;
import java.util.List;


public class Animation {
	
	public List<Texture> textures = new ArrayList<Texture>();
	
	private int frame = 0;
	private int rate;
	private int lenght = -1;
	private int time = 0;
	
	public boolean oncePlayed = false;
	public boolean oncePlayedReverse = false;

	public Animation(Texture[] textures, int rate) {
		for (int i = 0; i < textures.length; i++) {
			this.textures.add(textures[i]);
		}
		
		this.lenght = textures.length;
		this.rate = rate;
	}

	public Animation(Texture[] textures) {
		for (int i = 0; i < textures.length; i++) {
			this.textures.add(textures[i]);
		}
		
		this.lenght = textures.length;
		this.rate = 5;
	}

	public void update() {
		time++;
		if (time % rate == 0) {
			if (frame >= lenght - 1) {
				oncePlayed = true;
				frame = 0;
			} else frame++;
		}
	}

	public void playOnce() {
		time++;
		if (time % rate == 0) {
			if (frame >= lenght - 1) {
				oncePlayed = true;
				oncePlayedReverse = false;
			} else frame++;
		}
	}

	/*public void playReverseOnce() {
		time++;
		if (time % rate == 0) {
			if (frame >= lenght - 1) {
				oncePlayedReverse = true;
				oncePlayed = false;
			} else frame++;
			sprite = sheet.getSprites()[4 - frame];
			if (oncePlayedReverse) frame = 0;
		}
		// System.out.println(sprite + ", Frame: " + frame);
	}*/

	public Texture getTexture() {
		return textures.get(frame);
	}

	public void setFrameRate(int frames) {
		rate = frames;
	}
	
	public int getFrame() {
		return frame;
	}
	
	public void resetFrame() {
		frame = 0;
	}

	public void setFrame(int index) {
		this.frame = index;
	}
}
