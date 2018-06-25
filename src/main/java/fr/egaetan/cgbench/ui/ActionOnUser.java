package fr.egaetan.cgbench.ui;

import java.util.function.Consumer;

import fr.egaetan.cgbench.model.leaderboard.User;

public class ActionOnUser implements Consumer<User>{

	final Consumer<User> action;
	final String name;
	
	public ActionOnUser(Consumer<User> action, String name) {
		super();
		this.action = action;
		this.name = name;
	}

	@Override
	public void accept(User t) {
		action.accept(t);
	}

	
	
	
}
