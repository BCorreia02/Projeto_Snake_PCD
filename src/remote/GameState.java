package remote;

import java.io.Serializable;
import java.util.List;

public class GameState implements Serializable{
private List<Pacote> pacotes;
	
	public GameState (List<Pacote> pacotes) {
		this.pacotes=pacotes;
	}
	
	public List<Pacote> getPacotes(){
		return pacotes;
	}

}
