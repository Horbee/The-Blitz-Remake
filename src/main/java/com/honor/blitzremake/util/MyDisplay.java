package com.honor.blitzremake.util;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import com.honor.blitzremake.Game;

public class MyDisplay {

	public static void set() {	
		try {
			Display.setDisplayMode(new DisplayMode(Game.WIDTH, Game.HEIGHT));

			/*DisplayMode[] modes = Display.getAvailableDisplayModes();
			for (int i = 0; i < modes.length; i++) {
				DisplayMode current = modes[i];
				System.out.println(current.getWidth() + "x" + current.getHeight() + "x" + current.getBitsPerPixel() + " " + current.getFrequency() + "Hz");
			}*/

			//Display.setFullscreen(true);
			Display.setTitle(Game.title);
			//ContextAttribs context = new ContextAttribs(2, 1);
			//Display.create(new PixelFormat(), context.withProfileCore(true));
			Display.create();
			System.out.println("Display initialized with the Resolution: " + Game.WIDTH + "x" + Game.HEIGHT);
		} catch (LWJGLException e) {
			System.err.println("Display coludn't be initialized!");
			e.printStackTrace();
			System.exit(1);
		}
	}

	public static void setDisplayMode(int width, int height, boolean fullscreen) {

		// if already set, return
		if ((Display.getDisplayMode().getWidth() == width) && (Display.getDisplayMode().getHeight() == height) && (Display.isFullscreen() == fullscreen)) {
			return;
		}

		DisplayMode targetDisplayMode = null;

		try {
			if (fullscreen) {
				DisplayMode[] modes = Display.getAvailableDisplayModes();
				int freq = 0;

				for (int i = 0; i < modes.length; i++) {
					DisplayMode current = modes[i];

					if ((current.getWidth() == width) && (current.getHeight() == height)) {
						if ((targetDisplayMode == null) || (current.getFrequency() >= freq)) {
							if ((targetDisplayMode == null) || (current.getBitsPerPixel() > targetDisplayMode.getBitsPerPixel())) {
								targetDisplayMode = current;
								freq = targetDisplayMode.getFrequency();
							}
						}

						// if we've found a match for bpp and frequence against the
						// original display mode then it's probably best to go for this one
						// since it's most likely compatible with the monitor
						if ((current.getBitsPerPixel() == Display.getDesktopDisplayMode().getBitsPerPixel()) && (current.getFrequency() == Display.getDesktopDisplayMode().getFrequency())) {
							targetDisplayMode = current;
							break;
						}
					}
				}
			} else {
				targetDisplayMode = new DisplayMode(width, height);
			}
			
			if (targetDisplayMode == null) {
	            System.out.println("Failed to find value mode: "+width+"x"+height+" fs="+fullscreen);
	            return;
	        }
	 
	        Display.setDisplayMode(targetDisplayMode);
	        Display.setFullscreen(fullscreen);
	        Display.setTitle(Game.title);
			Display.create();
			System.out.println("Display initialized with the Resolution: " + width + "x" + height);
		} catch (LWJGLException e) {
			System.out.println("Unable to setup mode " + width + "x" + height + " fullscreen=" + fullscreen + e);
		}

	}

}
