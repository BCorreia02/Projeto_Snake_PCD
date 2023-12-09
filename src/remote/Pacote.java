package remote;

import java.io.Serializable;
import java.util.LinkedList;

import environment.BoardPosition;
import environment.Cell;
import game.GameElement;
import game.Goal;
import game.Obstacle;

public class Pacote implements Serializable{


	public enum ObjectType {
        SNAKE,
        OBSTACLE,
        GOAL
    }	

	private ObjectType objectType;
	private LinkedList<Cell> cells;
	private int size;
	private boolean isHuman;
	private int id;
	private Goal goal;
	private BoardPosition goalPos;
	private int GoalValue;
	private LinkedList<Obstacle> obstacles;
	private LinkedList<BoardPosition> path;

	
	public Pacote(int id,LinkedList<Cell> cells, int size,boolean isHuman,LinkedList<BoardPosition> path, ObjectType objectType) {
		this.id=id;
		this.cells=cells;
		this.size=size;
		this.isHuman=isHuman;
		this.path=path;
		this.objectType = objectType;
	}
	
	
	
	public Pacote(Goal goal, BoardPosition goalPos, int GoalValue, ObjectType objectType) {
		this.goal = goal;
		this.goalPos = goalPos;
		this.GoalValue = GoalValue;
	}



	public Pacote(LinkedList<Obstacle> obstacles, ObjectType objectType) {
		this.obstacles = obstacles;
	}



	public int getId() {
		return id;
	}
	
	public LinkedList<Cell> getCells() {
		return cells;
	}
	
	
	public int getStrength() {
		return size;
	}
	

	public Boolean getisHuman() {
		return isHuman;
	}

	public LinkedList<BoardPosition> getPath(){
		return path;
	}
	
	public ObjectType getObjectType() {
        return objectType;
    }

	public LinkedList<Obstacle> getObstacles(){
		return obstacles;
	}

	public Goal getGoal(){
		return goal;
	}

	public BoardPosition getGoalPosition(){
		return goalPos;
	}
}
