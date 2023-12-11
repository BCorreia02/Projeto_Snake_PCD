package remote;

import java.io.Serializable;
import java.util.List;

public class GameState implements Serializable {
	private List<Pacotev1> pacotes;

	public GameState(List<Pacotev1> pacotes) {
		this.pacotes = pacotes;
	}

	public List<Pacotev1> getPacotes() {
		return pacotes;
	}

}
