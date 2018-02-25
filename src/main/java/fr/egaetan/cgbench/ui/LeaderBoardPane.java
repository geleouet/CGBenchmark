package fr.egaetan.cgbench.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import fr.egaetan.cgbench.api.LeaderboardApi;
import fr.egaetan.cgbench.api.LeaderboardParam;
import fr.egaetan.cgbench.model.config.GameConfig;
import fr.egaetan.cgbench.model.leaderboard.SuccessLeaderboard;
import fr.egaetan.cgbench.model.leaderboard.User;
import fr.egaetan.cgbench.ui.ConfPanel.EnnemiesLink;
import retrofit2.Call;
import retrofit2.Response;

public class LeaderBoardPane {

	private LeaderboardApi leaderboardApi;
	private JScrollPane scroll;
	private List<User> users;
	private BoardModel model;
	private EnnemiesLink ennemiesLink;

	private ExecutorService bgThread = Executors.newCachedThreadPool(new ThreadFactory() {

		AtomicInteger counter = new AtomicInteger(0);

		@Override
		public Thread newThread(Runnable r) {
			Thread t = new Thread(r, "LeaderboardBg-thread-" + counter.incrementAndGet());
			t.setDaemon(true);
			return t;
		}
	});

	public LeaderBoardPane(ObservableValue<GameConfig> config, EnnemiesLink ennemiesLink, LeaderboardApi leaderboardApi) {
		this.ennemiesLink = ennemiesLink;
		this.leaderboardApi = leaderboardApi;
		this.model = new BoardModel();
		config.addPropertyChangeListener(evt -> bgThread.execute(() -> majLeaderboard(config.getValue())));
	}

	void majLeaderboard(GameConfig config) {
		if (config == null) {
			users = new ArrayList<>();
			model.fireTableDataChanged();
			return;
		}
		Call<SuccessLeaderboard> load = this.leaderboardApi.load(new LeaderboardParam(config.getName()));
		try {
			Response<SuccessLeaderboard> execute = load.execute();
			users = execute.body().getSuccess().getUsers();
			model.fireTableDataChanged();

			if (users.size() > 0) {
				preloadAvatars();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	void preloadAvatars() throws MalformedURLException, IOException {
		File avatarsDir = new File("./avatars");
		if (!avatarsDir.exists()) {
			avatarsDir.mkdirs();
		}
		for (int user_i = 0; user_i < users.size(); user_i++) {
			User userl = users.get(user_i);
			if (userl.getCodingamer().getAvatar() == null) {
				final File file = new File("./avatars/avatarNull.png");
				if (!file.exists()) {
					URL url = new URL("https://static.codingame.com/common/images/img_general_avatar.e40ca637.png"); // navigation_avatar
					Files.copy(url.openStream(), file.toPath());
					https: // static.codingame.com/common/images/img_general_avatar.e40ca637.png
					for (int j = 0; j < users.size(); j++) {
						if (users.get(j).getCodingamer().getAvatar() == null) {
							model.fireTableCellUpdated(user_i, 1);
						}
					}
					continue;
				}
				
			}
			final File file = new File("./avatars/avatar" + userl.getCodingamer().getAvatar() + ".png");
			if (file.exists()) {
				continue;
			}
			URL url = new URL("https://static.codingame.com/servlet/fileservlet?id=" + userl.getCodingamer().getAvatar() + "&format=navigation_avatar"); // navigation_avatar
			try {
				Files.copy(url.openStream(), file.toPath());
			} catch (FileNotFoundException e) {
				URL urlSave = new URL("https://static.codingame.com/common/images/img_general_avatar.e40ca637.png"); // navigation_avatar
				Files.copy(urlSave.openStream(), file.toPath());
				
			}
			model.fireTableCellUpdated(user_i, 1);
		}
	}

	public JComponent panel() {
		return scroll;
	}

	public void buildPane() {
		JTable board = new JTable(model);
		board.setAutoCreateRowSorter(true);
		DefaultTableCellRenderer pyjama = new DefaultTableCellRenderer() {

			private static final long serialVersionUID = -40468109924609093L;
			private final Color BG_COLOR = new Color(0xeeeefe);

			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				// label.setToolTipText("<html><img src=\"" +
				// Main.class.getResource("tooltip.gif")
				// + "\"> Tooltip ");
				final Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				c.setBackground(row % 2 == 0 ? BG_COLOR : Color.WHITE);
				if (isSelected) {
					c.setBackground(row % 2 == 0 ? BG_COLOR.darker() : Color.LIGHT_GRAY);
				}
				((JComponent) c).setToolTipText(null);
				
				if (table.convertColumnIndexToModel(column) == 1) {
					final String avatarId = users.get(table.convertRowIndexToModel(row)).getCodingamer().getAvatar();
					final ImageIcon icon = new ImageIcon("./avatars/avatar" + (avatarId != null ? avatarId : "Null") + ".png");
					Image scaledInstance = icon.getImage().getScaledInstance(16, 16, Image.SCALE_FAST);
					((JLabel) c).setIcon(new ImageIcon(scaledInstance));
					((JLabel) c).setToolTipText("<html><img src=\"file:./avatars/avatar"+ (avatarId != null ? avatarId : "Null") + ".png\" height=\"50\" width=\"50\"></html>");
					
				} else {
					((JLabel) c).setIcon(null);
				}
				return c;
			}
		};
		board.setDefaultRenderer(Object.class, pyjama);
		board.setDefaultRenderer(Integer.class, pyjama);
		board.setDefaultRenderer(Double.class, pyjama);
		
		board.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		board.getColumnModel().getColumn(0).setPreferredWidth(25);
		board.getColumnModel().getColumn(1).setPreferredWidth(150);
		board.getColumnModel().getColumn(2).setPreferredWidth(50);
		board.getColumnModel().getColumn(2).setPreferredWidth(80);
	    
		board.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					int row = board.convertRowIndexToModel(board.rowAtPoint(e.getPoint()));
					User user = users.get(row);
					ennemiesLink.add(user.getCodingamer().getPseudo(), user.getAgentId());
				}
			}
		});
		scroll = new JScrollPane(board);

	}

	class BoardModel extends AbstractTableModel {

		private static final long serialVersionUID = 3267787251883655719L;

		@Override
		public String getColumnName(int column) {
			switch (column) {
			case 0:
				return "Rank";
			case 1:
				return "Name";
			case 2:
				return "Score";
			case 3:
				return "AgentId";

			default:
				return "";
			}
		}

		@Override
		public Class<?> getColumnClass(int column) {
			switch (column) {
			case 0:
				return Integer.class;
			case 1:
				return String.class;
			case 2:
				return Double.class;
			case 3:
				return Integer.class;

			default:
				return Object.class;
			}
		}

		@Override
		public int getRowCount() {
			return users == null ? 0 : users.size();
		}

		@Override
		public int getColumnCount() {
			return 4;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			User user = users.get(rowIndex);
			switch (columnIndex) {
			case 0:
				return user.getRank();
			case 1:
				return user.getCodingamer().getPseudo();
			case 2:
				return user.getScore();
			case 3:
				return user.getAgentId();
			}
			return null;
		}

	}

}
