package remote;

import java.io.Serializable;
import java.util.HashMap;

import environment.BoardPosition;
import environment.Cell;

public class Pacotev1 implements Serializable{
    
    private HashMap<BoardPosition, Cell> map;

    public Pacotev1(HashMap<BoardPosition, Cell> map){
        this.map = map;
    }

}
