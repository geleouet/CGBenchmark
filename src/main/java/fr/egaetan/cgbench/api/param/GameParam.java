package fr.egaetan.cgbench.api.param;

import java.util.ArrayList;

@SuppressWarnings("serial")
public class GameParam extends ArrayList<String> {

	public GameParam(int gameId, int agentId) {
		add("" + gameId);
		add("" + agentId);
	}
}
