package fr.egaetan.cgbench.model.leaderboard;

import java.util.List;
import java.util.Optional;

public class Leaderboard {
	List<User> users;

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public Optional<User> findByName(String name) {
		return users.stream().filter(u -> u.matchName(name)).findAny();
	}

}