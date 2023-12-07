package utils;

import java.awt.event.KeyEvent;
import java.io.Serializable;
import java.util.Random;

/**
 * @author POO2016
 * @version 07-Nov-2021
 * 
 * Cardinal directions
 *
 */
public enum Direction implements Serializable {
	
	LEFT(new Vector2D(-1,0)), 
	UP(new Vector2D(0,-1)), 
	RIGHT(new Vector2D(1,0)), 
	DOWN(new Vector2D(0,1));

	private Vector2D vector;

	Direction(Vector2D vector) {
		this.vector = vector;
	}
	
	public Vector2D asVector() {
		return vector;
	}

	public Direction Random() {
		double value = Math.random();
        if(value< 0.25)
			return Direction.UP;
		else if(value < 0.50 && value>=0.25)
			return Direction.DOWN;
		else if(value< 0.75 && value>=0.50)
			return Direction.LEFT;
		else if(value < 1 && value>=0.75)
			return Direction.RIGHT;
		else
			return null;
    }
	
	public static Direction directionFor(int keyCode) {
		switch(keyCode){
			case KeyEvent.VK_DOWN:
				return DOWN;
			case KeyEvent.VK_UP:
				return UP;
			case KeyEvent.VK_LEFT:
				return LEFT;
			case KeyEvent.VK_RIGHT:
				return RIGHT;
		}

		throw new IllegalArgumentException();
	}

	public static boolean isDirection(int lastKeyPressed) {		
		return lastKeyPressed >= KeyEvent.VK_LEFT && lastKeyPressed <= KeyEvent.VK_DOWN;
	}

	public Direction opposite() {
		switch (this) {
			case UP: return DOWN;
			case DOWN: return UP;
			case LEFT: return RIGHT;
			default: return LEFT;
		}
	}
	
	public static Direction forVector(Vector2D v) {
		for (Direction d : values())
			if (v.equals(d.asVector()))
				return d;
		throw new IllegalArgumentException();	
	}
}
