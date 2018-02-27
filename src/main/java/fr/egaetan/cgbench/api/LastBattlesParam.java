package fr.egaetan.cgbench.api;

import java.util.ArrayList;

@SuppressWarnings("serial")
public class LastBattlesParam extends ArrayList<String>{

	public LastBattlesParam(String handle, int last) {
		add(handle);
		if (last != -1) {
			add("" + last);
		}
		else {
			add(null);
		}
			
	}
}
