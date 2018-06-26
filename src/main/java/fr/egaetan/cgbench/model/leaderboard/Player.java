package fr.egaetan.cgbench.model.leaderboard;

public class Player {
	int playerAgentId;
	int position;
	int userId;
	String nickname;
	String avatar;

	public int getPlayerAgentId() {
		return playerAgentId;
	}

	public void setPlayerAgentId(int playerAgentId) {
		this.playerAgentId = playerAgentId;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
}