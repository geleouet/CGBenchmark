package fr.egaetan.cgbench.api.param;

import java.util.ArrayList;

@SuppressWarnings("serial")
public class GameParam extends ArrayList<String> {

	public GameParam(long gameId, int agentId) {
		add("" + gameId);
		add("" + agentId);
	}
}
