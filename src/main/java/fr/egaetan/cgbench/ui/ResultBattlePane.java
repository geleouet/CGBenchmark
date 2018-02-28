package fr.egaetan.cgbench.ui;

import java.util.List;

import javax.swing.JTable;

import fr.egaetan.cgbench.model.leaderboard.Battle;
import fr.egaetan.cgbench.model.leaderboard.User;

public class ResultBattlePane extends LeaderBoardPane {

	public ResultBattlePane(ObservableValue<List<User>> usersList, ObservableValue<User> currentUser, ObservableValue<List<Battle>> lastBattles) {
		super(usersList, currentUser, null, lastBattles);
	}

	@Override
	protected void hideColumns(JTable board) {

		board.getColumnModel().removeColumn(board.getColumnModel().getColumn(2));
		board.getColumnModel().removeColumn(board.getColumnModel().getColumn(0));

	}

}
