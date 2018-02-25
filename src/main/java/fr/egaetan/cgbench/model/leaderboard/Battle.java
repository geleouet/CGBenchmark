package fr.egaetan.cgbench.model.leaderboard;

import java.util.ArrayList;
import java.util.List;

public class Battle {
	List<Player> players = new ArrayList<>(2);
	int gameId;
	boolean done;
	Game game;

	public List<Player> getPlayers() {
		return players;
	}

	public void setPlayers(List<Player> players) {
		this.players = players;
	}

	public int getGameId() {
		return gameId;
	}

	public void setGameId(int gameId) {
		this.gameId = gameId;
	}

	public boolean isDone() {
		return done;
	}

	public void setDone(boolean done) {
		this.done = done;
	}

	public Result resultFor(User user) {
		if (players.size() == 2) {
			if (players.get(0).getPosition() == players.get(1).getPosition()) {
				return Result.DRAW;
			}
			if (players.get(0).playerAgentId == user.agentId && players.get(0).getPosition() == 0) {
				return Result.WIN;
			}
			else if (players.get(1).playerAgentId == user.agentId && players.get(1).getPosition() == 0) {
				return Result.WIN;
			}
			return Result.LOSS;
		}
		return Result.ERROR;
	}

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

}