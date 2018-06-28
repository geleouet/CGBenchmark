package fr.egaetan.cgbench.api.param;

import java.util.ArrayList;

@SuppressWarnings("serial")
public class LastBattlesParam extends ArrayList<String>{

	public LastBattlesParam(String handle, long last) {
		add(handle);
		if (last != -1) {
			add("" + last);
		}
		else {
			add(null);
		}
			
	}

	public LastBattlesParam(int agentId, long last) {
		add("" + agentId);
		if (last != -1) {
			add("" + last);
		}
		else {
			add(null);
		}
		
	}
}
