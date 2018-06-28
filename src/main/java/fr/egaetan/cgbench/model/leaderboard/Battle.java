package fr.egaetan.cgbench.model.leaderboard;

import java.util.ArrayList;
import java.util.List;

public class Battle {
	List<Player> players = new ArrayList<>(2);
	long gameId;
	boolean done;
	Game game;

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Battle) {
			Battle other = (Battle) obj;
			return other.gameId == gameId;
		}
		return super.equals(obj);
	}
	
	@Override
	public int hashCode() {
		return Long.hashCode(gameId);
	}
	
	public List<Player> getPlayers() {
		return players;
	}

	public void setPlayers(List<Player> players) {
		this.players = players;
	}

	public long getGameId() {
		return gameId;
	}

	public void setGameId(long gameId) {
		this.gameId = gameId;
	}

	public boolean isDone() {
		return done;
	}

	public void setDone(boolean done) {
		this.done = done;
	}

	public Result resultFor(User user) {
		return resultFor(user.agentId);
	}

	public Result resultFor(int agentId) {
		if (players.size() == 2) {
			if (players.get(0).getPosition() == players.get(1).getPosition()) {
				return Result.DRAW;
			}
			if (players.get(0).playerAgentId == agentId && players.get(0).getPosition() == 0) {
				return Result.WIN;
			}
			else if (players.get(1).playerAgentId == agentId && players.get(1).getPosition() == 0) {
				return Result.WIN;
			}
			return Result.LOSS;
		}
		else if (players.size() == 3) {
			if (players.get(0).playerAgentId == agentId && players.get(0).getPosition() == 0) {
				return Result.WIN;
			}
			if (players.get(1).playerAgentId == agentId && players.get(1).getPosition() == 0) {
				return Result.WIN;
			}
			if (players.get(2).playerAgentId == agentId && players.get(2).getPosition() == 0) {
				return Result.WIN;
			}
			
			return Result.LOSS;
		}
		else if (players.size() == 4) {
			if (players.get(0).playerAgentId == agentId && players.get(0).getPosition() == 0) {
				return Result.WIN;
			}
			if (players.get(1).playerAgentId == agentId && players.get(1).getPosition() == 0) {
				return Result.WIN;
			}
			if (players.get(2).playerAgentId == agentId && players.get(2).getPosition() == 0) {
				return Result.WIN;
			}
			if (players.get(3).playerAgentId == agentId && players.get(3).getPosition() == 0) {
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

	public int position(User userId) {
		return players.stream()
				.filter(p-> p .getPlayerAgentId() == userId.agentId)
				.map(p->p.position).findFirst().orElse(99);
	}

}