package fr.egaetan.cgbench.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import fr.egaetan.cgbench.model.leaderboard.Battle;
import fr.egaetan.cgbench.model.leaderboard.Codingamer;
import fr.egaetan.cgbench.model.leaderboard.User;
import fr.egaetan.cgbench.ui.ConfPanel.EnnemiesLink;

public class LeaderBoardPane {

	final static Color BG_COLOR = new Color(0xeeeefe);
	final static Color ME_COLOR = new Color(0xf9d140);

	final static Color BOSS_COLOR = new Color(0xe5829e);

	private JScrollPane scroll;
	private BoardModel model;
	EnnemiesLink ennemiesLink;
	List<User> users;
	final ObservableValue<User> currentUser;
	final ObservableValue<List<Battle>> lastBattles;

	private ExecutorService bgThread = Executors.newCachedThreadPool(new ThreadFactory() {

		AtomicInteger counter = new AtomicInteger(0);

		@Override
		public Thread newThread(Runnable r) {
			Thread t = new Thread(r, "LeaderboardBg-thread-" + counter.incrementAndGet());
			t.setDaemon(true);
			return t;
		}
	});

	
	public LeaderBoardPane(ObservableValue<List<User>> usersList,  ObservableValue<User> currentUser, EnnemiesLink ennemiesLink, ObservableValue<List<Battle>> lastBattles) {
		this.ennemiesLink = ennemiesLink;
		this.currentUser = currentUser;
		this.lastBattles = lastBattles;
		this.model = new BoardModel();
		lastBattles.addPropertyChangeListener(l -> SwingUtilities.invokeLater(() -> model.fireTableDataChanged()));
		usersList.addPropertyChangeListener(e -> bgThread.execute(() -> majRows(usersList.getValue())));
	}


	private void majRows(List<User> v) {
		users = v;
		SwingUtilities.invokeLater(() -> model.fireTableDataChanged());
		if (users.size() > 0) {
			preloadAvatars();
		}
	}

	void preloadAvatars() {
		File avatarsDir = new File("./avatars");
		if (!avatarsDir.exists()) {
			avatarsDir.mkdirs();
		}
		ExecutorService avatarLoader = Executors.newFixedThreadPool(8, new ThreadFactory() {

			AtomicInteger counter = new AtomicInteger(0);

			@Override
			public Thread newThread(Runnable r) {
				Thread t = new Thread(r, "LoadAvatars-thread-" + counter.incrementAndGet());
				t.setDaemon(true);
				return t;
			}
		});
		
		for (int user_i = 0; user_i < users.size(); user_i++) {
			int user_i$ = user_i;
			avatarLoader.execute(() -> {
				try {
					User userl = users.get(user_i$);
					if (userl.getCodingamer() == null) {
						copyRessourcesAvatar("./avatars/avatar_boss_wood.png", "images/league_boss_wood.png");
						copyRessourcesAvatar("./avatars/avatar_boss_bronze.png", "images/league_boss_bronze.png");
						copyRessourcesAvatar("./avatars/avatar_boss_silver.png", "images/league_boss_silver.png");
						copyRessourcesAvatar("./avatars/avatar_boss_gold.png", "images/league_boss_gold.png");
						SwingUtilities.invokeLater(() -> model.fireTableCellUpdated(user_i$, 1));
						return;
					}
					if (userl.getCodingamer().getAvatar() == null) {
						final File file = new File("./avatars/avatarNull.png");
						if (!file.exists()) {
							copyRessourcesAvatar("./avatars/avatarNull.png", "images/avatarNull.png");
							for (int j = 0; j < users.size(); j++) {
								if (users.get(j).getCodingamer().getAvatar() == null) {
									SwingUtilities.invokeLater(() -> model.fireTableCellUpdated(user_i$, 1)); 
								}
							}
							return;
						}
						
					}
					final File file = new File("./avatars/avatar" + userl.getCodingamer().getAvatar() + ".png");
					if (file.exists()) {
						return;
					}
					URL url = new URL("https://static.codingame.com/servlet/fileservlet?id=" + userl.getCodingamer().getAvatar() + "&format=navigation_avatar"); // navigation_avatar
					try {
						Files.copy(url.openStream(), file.toPath());
					} catch (FileNotFoundException e) {
						URL urlSave = new URL("https://static.codingame.com/common/images/img_general_avatar.e40ca637.png"); // navigation_avatar
						Files.copy(urlSave.openStream(), file.toPath());
						
					}
					SwingUtilities.invokeLater(() -> model.fireTableCellUpdated(user_i$, 1)); 
				}
				catch (IOException e) {
					// hide exception
				}
			});
			
		}
		avatarLoader.shutdown();
	}

	private void copyRessourcesAvatar(String dest, String src) throws IOException {
		final File file = new File(dest);
		URL resource = getClass().getClassLoader().getResource("./"+src);
		if (!file.exists()) {
			Files.copy(resource.openStream(), file.toPath());
		}
	}

	public JComponent panel() {
		JPanel res = new JPanel(new GridBagLayout());
		
		JScrollBar r = new JScrollBar() {

			private static final long serialVersionUID = 6262119679761475240L;
			
			final Color brighter = ME_COLOR.brighter();
			
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				int p = -1;
				if (users == null || currentUser.value == null) {
					return;
				}
				for (int i = 0; i < users.size(); i++) {
					if (users.get(i)!=null && users.get(i).getPseudo() != null && users.get(i).getPseudo().equals(currentUser.value.getPseudo())) {
						p = i;
						break;
					}
				}
				if (p != -1) {
					int size = users.size();
					Dimension s = getSize();
					g.setColor(ME_COLOR);
					g.fillRect(1, s.height*p/size - 4, s.width-2, 8);
					g.setColor(brighter);
					g.fillRect(2, s.height*p/size - 3, s.width-4, 6);
					
				}
			}
			
		};
		r.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				int p = -1;
				if (users == null || currentUser.value == null) {
					return;
				}
				for (int i = 0; i < users.size(); i++) {
					if (users.get(i)!=null && users.get(i).getPseudo() != null && users.get(i).getPseudo().equals(currentUser.value.getPseudo())) {
						p = i;
						break;
					}
				}
				if (p != -1) {
					int size = users.size();
					int y = e.getY();
					Dimension s = r.getSize();
					
					if (y > s.height*p/size - 4 && y < s.height*p/size + 4) {
						r.getModel().setValue(Math.max(r.getMinimum(), Math.min(r.getMaximum()-r.getModel().getExtent(), (r.getMaximum()-r.getMinimum())*p/size - r.getModel().getExtent()/2)));
					}
				}
			}
		});
		scroll.setVerticalScrollBar(r);
		res.add(scroll, new GridBagConstraints(1, 0, 1, 1, 100., 1., GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0, 0));
		return res;
	}

	public void buildPane() {
		JTable board = new JTable(model);
		board.setAutoCreateRowSorter(true);
		
		hideColumns(board);
		
		DefaultTableCellRenderer pyjama = new DefaultTableCellRenderer() {

			private static final long serialVersionUID = -40468109924609093L;
			
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				Object value$ = value;
				if (value != null && table.convertColumnIndexToModel(column) == 4) {
					value$ = value + "%";
					
				}
				
				final Component c = super.getTableCellRendererComponent(table, value$, isSelected, hasFocus, row, column);
				c.setBackground(row % 2 == 1 ? BG_COLOR : Color.WHITE);
				c.setFont(c.getFont().deriveFont(Font.PLAIN));
				
				User user = users.get(table.convertRowIndexToModel(row));
				
				if (user.getPseudo() != null && currentUser.value != null && user.getPseudo().equals(currentUser.value.getPseudo())) {
					c.setFont(c.getFont().deriveFont(Font.BOLD));
					if (isSelected) {
						c.setBackground(ME_COLOR.darker());
					}
					else {
						c.setBackground(ME_COLOR);
					}
				}
				else if (user.getCodingamer() == null) {
					c.setFont(c.getFont().deriveFont(Font.BOLD));
					if (isSelected) {
						c.setBackground(BOSS_COLOR.darker());
					}
					else {
						c.setBackground(BOSS_COLOR);
					}
				}
				else if (isSelected) {
					c.setBackground(row % 2 == 1 ? BG_COLOR.darker() : Color.LIGHT_GRAY);
				}
				if (value != null && table.convertColumnIndexToModel(column) == 4) {
					Integer va = (Integer) value;
					((JLabel) c).setBackground(new Color((int) (255-(va)/100.*255),(int) (va/100.*240), (int) Math.max(30, Math.min(255, (128-Math.abs(va-50.)/50.*255)))).brighter());
				}
				((JComponent) c).setToolTipText(null);
				
				if (table.convertColumnIndexToModel(column) == 1) {
					final String avatarId = avatar(user);
					if (avatarId.startsWith("_boss")) {
						c.setFont(c.getFont().deriveFont(Font.ITALIC));
					}
					final ImageIcon icon = new ImageIcon("./avatars/avatar" + avatarId + ".png");
					Image scaledInstance = icon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
					((JLabel) c).setIcon(new ImageIcon(scaledInstance));
					((JLabel) c).setToolTipText("<html><img src=\"file:./avatars/avatar"+ avatarId + ".png\" height=\"50\" width=\"50\"></html>");
					
				} else {
					((JLabel) c).setIcon(null);
				}
				return c;
			}

			private String avatar(User user) {
				Codingamer codingamer = user.getCodingamer();
				if (codingamer == null) {
					if (user.getLeague().getDivisionIndex() >= 4) {
						return "_boss_gold";
					}
					if (user.getLeague().getDivisionIndex() == 3) {
						return "_boss_silver";
					}
					if (user.getLeague().getDivisionIndex() == 2) {
						return "_boss_bronze";
					}
					if (user.getLeague().getDivisionIndex() <= 1) {
						return "_boss_wood";
					}
					return "Null";
					
				}
				String avatarId = codingamer.getAvatar();
				return (avatarId != null ? avatarId : "Null");
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
			
			@SuppressWarnings("serial")
			@Override
			public void mouseReleased(MouseEvent e) {
				if (ennemiesLink != null && e.isPopupTrigger()) {
					int row = board.convertRowIndexToModel(board.rowAtPoint(e.getPoint()));
					User user = users.get(row);
					JPopupMenu pop = new JPopupMenu();
					pop.add(new JMenuItem(new AbstractAction("Add to configuration") {
						
						@Override
						public void actionPerformed(ActionEvent ev) {
							ennemiesLink.add(user.getPseudo(), user.getAgentId());
						}
					}));
					pop.show(board, e.getX(), e.getY());
				}
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					int row = board.convertRowIndexToModel(board.rowAtPoint(e.getPoint()));
					User user = users.get(row);
					if (ennemiesLink != null) {
						ennemiesLink.add(user.getPseudo(), user.getAgentId());
					}
				}
			}
		});
		scroll = new JScrollPane(board);
	}


	@SuppressWarnings("unused")
	protected void hideColumns(JTable board) { /* Nothing here */}

	class BoardModel extends AbstractTableModel {

		private static final long serialVersionUID = 3267787251883655719L;

		@Override
		public String getColumnName(int column) {
			switch (column) {
			case 0:
				return "#";
			case 1:
				return "Name";
			case 2:
				return "Score";
			case 3:
				return "AgentId";
			case 4:
				return "WinRate";
			case 5:
				return "W / D / L";
			case 6:
				return "%95";
			case 7:
				return "Total";

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
			case 4:
				return Integer.class;
			case 5:
				return String.class;
			case 6:
				return String.class;
			case 7:
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
		public boolean isCellEditable(int row, int column) {
			return false;
		}
		
		@Override
		public int getColumnCount() {
			return 8;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			User user = users.get(rowIndex);
			
			switch (columnIndex) {
			case 0:
				return user.getRank();
			case 1:
				return user.getPseudo() != null ? user.getPseudo() : "";
			case 2:
				return user.getScore();
			case 3:
				return user.getAgentId();
			case 4: {
				User userId = currentUser.getValue();
				if (userId == null || userId.getAgentId() == user.getAgentId()) {
					return null;
				}
				List<Battle> value = lastBattles.getValue();
				if (value== null || value.size() == 0) {
					return null;
				}
				List<Battle> matchs = matches(user, value);
				if (matchs.size() == 0) {
					return null;
				}
				return (int) Math.round((matchs.stream().filter(m -> m.position(userId) < m.position(user)).count()*100.) / matchs.size());
			}
			case 5: {
				User userId = currentUser.getValue();
				if (userId == null || userId.getAgentId() == user.getAgentId()) {
					return null;
				}
				List<Battle> value = lastBattles.getValue();
				if (value== null || value.size() == 0) {
					return null;
				}
				List<Battle> matchs = matches(user, value);
				if (matchs.size() == 0) {
					return null;
				}
				return ((int) ((matchs.stream().filter(m -> m.position(userId) < m.position(user)).count()) )) +" / " + 
				(int) ((matchs.stream().filter(m -> m.position(userId) == m.position(user)).count()) ) + " / " +
				(int) ((matchs.stream().filter(m -> m.position(userId) > m.position(user)).count()) );
			}
			case 6: {
				User userId = currentUser.getValue();
				if (userId == null || userId.getAgentId() == user.getAgentId()) {
					return null;
				}
				List<Battle> value = lastBattles.getValue();
				if (value== null || value.size() == 0) {
					return null;
				}
				List<Battle> matchs = matches(user, value);
				if (matchs.size() == 0) {
					return null;
				}
				double w = (int) ((matchs.stream().filter(m -> m.position(userId) < m.position(user)).count()) );
				double n = matchs.size();
				
				double m = w/n;
				
				double sigmamin = Math.sqrt(w/(n+1) - (w/(n+1))*(w/(n+1)));
				double sigmamax = Math.sqrt((w+1)/(n+1) - ((w+1)/(n+1))*((w+1)/(n+1)));

				double min = Math.max(0, m - 1.96*sigmamin / Math.sqrt(n+1));
				double max = Math.min(1, m + 1.96*sigmamax / Math.sqrt(n+1));
				
				int mini = (int) Math.round(100*min);
				int maxi = (int) Math.round(100*max);
				return "[" + mini+"%, "+maxi+"%]";
			}
			case 7:
			{
				User userId = currentUser.getValue();
				if (userId == null || userId.getAgentId() == user.getAgentId()) {
					return null;
				}
				List<Battle> value = lastBattles.getValue();
				if (value== null || value.size() == 0) {
					return null;
				}
				List<Battle> matchs = matches(user, value);
				if (matchs.size() == 0) {
					return null;
				}
				return matchs.size();
			}
			
			}
			return null;
		}

		private List<Battle> matches(User user, List<Battle> value) {
			User userId = currentUser.getValue();
			if (userId == null) {
				return Collections.emptyList();
			}
			return value.stream()
					.filter(b -> b.getPlayers().stream().anyMatch(p -> p.getPlayerAgentId() == userId.getAgentId()))
					.filter(b -> b.getPlayers().stream().anyMatch(p -> p.getPlayerAgentId() == user.getAgentId()))
					.collect(Collectors.toList());
		}

	}

}
