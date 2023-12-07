package remote;

import java.io.Serializable;
import java.util.LinkedList;

import environment.BoardPosition;
import environment.Cell;

public class Pacote implements Serializable{


	private LinkedList<Cell> cells;
	private int size;
	private boolean isHuman;
	private int id;
	
	public Pacote(int id,LinkedList<Cell> cells,int size,boolean isHuman) {
		this.id=id;
		this.cells=cells;
		this.size=size;
		this.isHuman=isHuman;
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
	

	
}
