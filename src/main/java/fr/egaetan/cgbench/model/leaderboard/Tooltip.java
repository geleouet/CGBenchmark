package fr.egaetan.cgbench.model.leaderboard;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Tooltip {
	int turn;
	String text;
	int event;
	String origin;

	public Tooltip() {
	}

	public Tooltip(String s) {
		// {"turn":84,"text":"$0: timeout!","event":0}
		JsonObject jsonObject = new JsonParser().parse(s).getAsJsonObject();
		turn = jsonObject.get("turn").getAsJsonPrimitive().getAsInt();
		event = jsonObject.get("event").getAsJsonPrimitive().getAsInt();
		text = jsonObject.get("text").getAsJsonPrimitive().getAsString();
		this.origin = s;
	}

	public int getTurn() {
		return turn;
	}

	public void setTurn(int turn) {
		this.turn = turn;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getEvent() {
		return event;
	}

	public void setEvent(int event) {
		this.event = event;
	}

}