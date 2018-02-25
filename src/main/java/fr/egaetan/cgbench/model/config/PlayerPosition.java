package fr.egaetan.cgbench.model.config;

public class PlayerPosition {

	public static PlayerPosition[] forPlayersGame(int nb) {
		PlayerPosition[] res = new PlayerPosition[nb + 2];
		res[0] = new PlayerPosition(-2, "Random");
		res[1] = new PlayerPosition(-1, "Every");
		for (int i = 0; i < nb; i ++) {
			res[i+2] = new PlayerPosition(i, "Position " + i);
		}
		return res;
	}
	
	final int code;
	final String name;
	
	public PlayerPosition(int code, String name) {
		super();
		this.code = code;
		this.name = name;
	}
	
	@Override
	public String toString() {
		return name;
	}

	public int code() {
		return code;
	}
	
}
