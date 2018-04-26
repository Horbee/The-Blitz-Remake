package com.honor.blitzremake.input;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Controller;
import org.lwjgl.input.Controllers;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class Input {

	public static Controller controller;

	public static void create() {
		/*try {
			Controllers.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}

		controller = Controllers.getController(0);

		for (int i = 0; i < controller.getAxisCount(); i++) {
			System.out.println(controller.getAxisName(i));
			controller.setDeadZone(i, 0.3f);
		}

		for (int i = 0; i < controller.getButtonCount(); i++) {
			System.out.println(controller.getButtonName(i));
		}*/

	}

	public static boolean up, down, left, right;

	public static void pollInput() {
		up = false;
		down = false;
		left = false;
		right = false;
		
		if (Keyboard.isKeyDown(Keyboard.KEY_W) || Keyboard.isKeyDown(Keyboard.KEY_UP))
			up = true;
		else if (Keyboard.isKeyDown(Keyboard.KEY_S) || Keyboard.isKeyDown(Keyboard.KEY_DOWN))
			down = true;

		if (Keyboard.isKeyDown(Keyboard.KEY_A) || Keyboard.isKeyDown(Keyboard.KEY_LEFT))
			left = true;
		else if (Keyboard.isKeyDown(Keyboard.KEY_D) || Keyboard.isKeyDown(Keyboard.KEY_RIGHT))
			right = true;
		
		/*
		if (Keyboard.isKeyDown(Keyboard.KEY_W) || Keyboard.isKeyDown(Keyboard.KEY_UP) || controller.getAxisValue(0) < 0)
			up = true;
		else if (Keyboard.isKeyDown(Keyboard.KEY_S) || Keyboard.isKeyDown(Keyboard.KEY_DOWN) || controller.getAxisValue(0) > 0)
			down = true;

		if (Keyboard.isKeyDown(Keyboard.KEY_A) || Keyboard.isKeyDown(Keyboard.KEY_LEFT) || controller.getAxisValue(1) < 0)
			left = true;
		else if (Keyboard.isKeyDown(Keyboard.KEY_D) || Keyboard.isKeyDown(Keyboard.KEY_RIGHT) || controller.getAxisValue(1) > 0)
			right = true;

		Controllers.clearEvents();
		controller.poll();
		float x = ((float) Mouse.getX()) + controller.getAxisValue(3) * 10;
		float y = ((float) Mouse.getY()) - controller.getAxisValue(2) * 10;
		Mouse.setCursorPosition((int) x, (int) y);*/
		
	}

}
