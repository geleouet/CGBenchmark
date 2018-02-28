package fr.egaetan.cgbench.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.URI;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import fr.egaetan.cgbench.model.leaderboard.Battle;
import fr.egaetan.cgbench.model.leaderboard.Codingamer;
import fr.egaetan.cgbench.model.leaderboard.Player;
import fr.egaetan.cgbench.model.leaderboard.User;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.embed.swing.JFXPanel;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebErrorEvent;
import javafx.scene.web.WebView;

public class LastBattlesPane {

	public class LastBattleTableModel extends DefaultTableModel {

		private static final long serialVersionUID = 7448146050176669524L;

		@Override
		public int getRowCount() {
			return lastBattles == null || lastBattles.getValue() == null ? 0 : lastBattles.getValue().size();
		}
		
		@Override
		public String getColumnName(int column) {
			switch (column) {
			case 0:
			case 1:
			case 2:
			case 3:
				return ""  + column;
			case 4:
				return "replay";
			}
			return null;
		}
		
		@Override
		public Class<?> getColumnClass(int columnIndex) {
			switch (columnIndex) {
			case 0:
			case 1:
			case 2:
			case 3:
				return Player.class;
			case 4:
				return String.class;
			}
			return null;
		}
		
		@Override
		public int getColumnCount() {
			return 5;
		}
		
		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}
		
		@Override
		public Object getValueAt(int row, int column) {
			Battle battle = lastBattles.getValue().get(row);
			switch (column) {
			case 0:
			case 1:
			case 2:
			case 3:
				if (battle.getPlayers().size() <= column) {
					return null;
				}
				return battle.getPlayers().get(column);
			case 4:
				return battle.getGameId();
			}
			return null;
		}
		
	}
	
	
	ObservableValue<List<Battle>> lastBattles;
	ObservableValue<List<Battle>> lastBattles$;
	ObservableValue<List<User>> users;
	ObservableValue<User> me;
	private JScrollPane scroll;

	public LastBattlesPane(ObservableValue<List<Battle>> lastBattles$, ObservableValue<List<User>> users, ObservableValue<User> me) {
		this.lastBattles$ = lastBattles$;
		this.users = users;
		this.me = me;
		this.lastBattles = new ObservableValue<>();
		PropertyChangeListener l = new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (me.getValue() == null || lastBattles$.getValue()==null) {
					return;
				}
				lastBattles.setValue(lastBattles$.getValue().stream()
						.filter(b -> b.getPlayers().stream().anyMatch(p -> p.getPlayerAgentId() == me.getValue().getAgentId()))
						.sorted(Comparator.comparing(b -> -b.getGameId()))
						.collect(Collectors.toList()));
			}
		};
		lastBattles$.addPropertyChangeListener(l);
		me.addPropertyChangeListener(l);
	}

	public void buildPane() {
		LastBattleTableModel dm = new LastBattleTableModel();
		JTable tableLastBattle = new JTable(dm);
		
		tableLastBattle.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				int col = tableLastBattle.columnAtPoint(e.getPoint());
				int rowView = tableLastBattle.rowAtPoint(e.getPoint());
				if (col == 4 && rowView >  -1 && e.isControlDown()) {
					int row = tableLastBattle.convertRowIndexToModel(rowView);
					Battle b = lastBattles.getValue().get(row);
					
					if (Desktop.isDesktopSupported()) {
						Desktop desktop = Desktop.getDesktop();
						if (desktop.isSupported(Action.BROWSE)) {
							try {
								desktop.browse(URI.create("https://www.codingame.com/replay/"+b.getGameId()));
							}
							catch (IOException e1) {
								// Ignore exception
							}
						}
					}
					/*
					// TODO why it doesn't work ?
					JFrame frame = (JFrame) SwingUtilities.getRoot(tableLastBattle);
					JDialog diag = new JDialog(frame, "Replay", true);
					
					final JFXPanel fxPanel = new JFXPanel();
					Platform.runLater(new Runnable() {
			            private WebEngine engine;

						@Override
			            public void run() {
			                initFX();
			            }

						private void initFX() {
							createScene();
						}

						private void createScene() {
							WebView view = new WebView();
							engine = view.getEngine();
							engine.setOnError(new EventHandler<WebErrorEvent>() {
								
								@Override
								public void handle(WebErrorEvent event) {
									System.err.println(event.getMessage());
								}
							});
							engine.load("https://www.codingame.com/replay/"+b.getGameId());
							fxPanel.setScene(new Scene(view));
						}
			       });
					
					diag.getContentPane().add(fxPanel, BorderLayout.CENTER);
					diag.pack();
					diag.setSize(800, 600);
					diag.setLocationRelativeTo(tableLastBattle);
					diag.setVisible(true);*/
					
				}
			}
		});
		tableLastBattle.setRowHeight(32);
		tableLastBattle.setDefaultRenderer(String.class, new DefaultTableCellRenderer() {

			private static final long serialVersionUID = 3373574818270274695L;

			{
				Map<TextAttribute, Integer> fontAttributes = new HashMap<>();
				fontAttributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
				setFont(getFont().deriveFont(fontAttributes));
			}
			
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				return this;
			}
		});
		tableLastBattle.setDefaultRenderer(Player.class, new DefaultTableCellRenderer() {
			
			private static final long serialVersionUID = -8943876377201045641L;
			
			boolean isMe = false;
			boolean isEmpty = false;
			
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				
				if (isEmpty) {
					Dimension s = getSize();
					g.setColor(new Color(50,50,50,100));
					g.fillRect(0, 0, s.width, s.height);
				}
				else if (!isMe) {
					Dimension s = getSize();
					g.setColor(new Color(50,50,50,25));
					g.fillRect(0, 0, s.width, s.height);
				}
			}
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				super.getTableCellRendererComponent(table, "", isSelected, hasFocus, row, column);
				isMe = false;
				Player p = (Player) value;
				if (users == null ||  users.getValue() == null || p == null) {
					setText("");
					setIcon(null);
					setToolTipText(null);
					isEmpty = true;
					return this;
				}
				
				isEmpty = false;
				setBorder(BorderFactory.createEmptyBorder());
				int bdsize = 3;
				if (p.getPosition() == 0) {
					if (column < 3) {
						Player pl = (Player) table.getValueAt(row, column+1);
						if (column == 0) {
							if (pl != null && pl.getPosition()== p.getPosition()) {
								setBorder(BorderFactory.createMatteBorder(bdsize, bdsize, bdsize, 0, Color.red));
							}
						}
						else {
							if (pl != null && pl.getPosition()== p.getPosition()) {
								setBorder(BorderFactory.createMatteBorder(bdsize, 0, bdsize, 0, Color.red));
							}
							else {
								setBorder(BorderFactory.createMatteBorder(bdsize, 0, bdsize, bdsize, Color.red));
							}
						}
					}
				}
				if (p.getPosition() == 1) {
					if (column < 3) {
						if (column >= 1) {
							Player pl = (Player) table.getValueAt(row, column+1);
							Player pv = (Player) table.getValueAt(row, column-1);
							
							if (pl != null && pl.getPosition()== p.getPosition() && pv != null && pv.getPosition()== p.getPosition()) {
								setBorder(BorderFactory.createMatteBorder(bdsize, 0, bdsize, 0, Color.yellow));
							}
							else if (pl != null && pl.getPosition()== p.getPosition() && pv != null && pv.getPosition() != p.getPosition()) {
								setBorder(BorderFactory.createMatteBorder(bdsize, bdsize, bdsize, 0, Color.yellow));
							}
							else if (pl != null && pl.getPosition()!= p.getPosition() && pv != null && pv.getPosition() == p.getPosition()) {
								setBorder(BorderFactory.createMatteBorder(bdsize, 0, bdsize, bdsize, Color.yellow));
							}
							else if (pl == null && pv != null && pv.getPosition() == p.getPosition()) {
								setBorder(BorderFactory.createMatteBorder(bdsize, 0, bdsize, bdsize, Color.yellow));
							}
						}
					}
				}
				
				Optional<User> findFirst = users.getValue().stream().filter(u -> u.getAgentId() == p.getPlayerAgentId() || (u.getCodingamer() != null && p.getUserId() == u.getCodingamer().getUserId())).findFirst();
				
				if (findFirst.isPresent()) {
					User user = findFirst.get();
					if (user.getCodingamer() != null && user.getCodingamer().getUserId() == me.getValue().getCodingamer().getUserId()) {
						isMe = true;
					}
					final String avatarId = avatar(user);
					if (avatarId.startsWith("_boss")) {
						setFont(getFont().deriveFont(Font.ITALIC));
					}
					ImageIcon iconForBattle = avatarIcon(avatarId);
					setIcon(iconForBattle);
					
					int p$ = -1;
					for (int i = 0; i < users.getValue().size(); i++) {
						if (users.getValue().get(i)!=null && users.getValue().get(i).getPseudo() != null && users.getValue().get(i).getPseudo().equals(user.getPseudo())) {
							p$ = i;
							break;
						}
					}
					setText(user.getPseudo() /*+ (p$ != -1 ? (" #" + (p$+1)) : "")*/);
					
					setToolTipText("<html><img src=\"file:./avatars/avatar"+ avatarId + ".png\" height=\"50\" width=\"50\">"+(p$ != -1 ? (" #" + (p$+1)) : "")+ " "+ user.getPseudo() +" </html>");
					
				}
				return this;
			}
			
			Map<String, ImageIcon> cache = new HashMap<>();
			private ImageIcon avatarIcon(final String avatarId) {
				return cache.computeIfAbsent(avatarId, av -> {
					final ImageIcon icon = new ImageIcon("./avatars/avatar" + av + ".png");
					Image scaledInstance = icon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
					ImageIcon iconForBattle = new ImageIcon(scaledInstance);
					return iconForBattle;
				});
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
		});
		
		scroll = new JScrollPane(tableLastBattle);
		lastBattles.addPropertyChangeListener(new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				dm.fireTableDataChanged();
			}
		});
	}

	public JComponent panel() {
		return scroll;
	}

}
