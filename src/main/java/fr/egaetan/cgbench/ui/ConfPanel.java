package fr.egaetan.cgbench.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;

import fr.egaetan.cgbench.Gui;
import fr.egaetan.cgbench.model.config.GameConfig;
import fr.egaetan.cgbench.model.config.MultisConfig;
import fr.egaetan.cgbench.model.config.PlayerPosition;
import fr.egaetan.cgbench.model.config.SeedParamConfig;
import fr.egaetan.cgbench.services.SearchAgentId;
import fr.svivien.cgbenchmark.SessionLogIn;
import fr.svivien.cgbenchmark.model.config.AccountConfiguration;
import fr.svivien.cgbenchmark.model.config.CodeConfiguration;
import fr.svivien.cgbenchmark.model.config.EnemyConfiguration;
import fr.svivien.cgbenchmark.model.config.GlobalConfiguration;

public class ConfPanel {

	private static final Log LOG = LogFactory.getLog(Gui.class);
	final MultisConfig multisConfig;

	public JPanel configPanel;

	private JTextField accountName;
	private JTextField email;
	private JPasswordField password;
	private JComboBox<GameConfig> multiName;
	private JFormattedTextField cooldown;
	private JComboBox<PlayerPosition> playerPosition;
	private JComboBox<Integer> minEnnemies;
	private JComboBox<Integer> maxEnnemies;
	private JCheckBox randomSeed;

	private int nbPlayer;

	Runnable updateEnnemies[] = new Runnable[50];

	List<String> seedsOrigine = new ArrayList<>();
	Runnable updateSeeds;

	ObservableValue<GameConfig> currentGame;
	ObservableValue<AccountConfiguration> currentLogin;

	private List<SeedParamConfig> seedsConfig;
	private SearchAgentId searchAgentId;
	private List<CodeConfiguration> code_configs = new ArrayList<>();
	private JTabbedPane codesTabbed;
	private BatchRun batchRun;

	public ConfPanel(MultisConfig multisConfig, SearchAgentId searchAgentId, BatchRun batchRun, ObservableValue<GameConfig> currentGame, ObservableValue<AccountConfiguration> currentLogin) {
		this.multisConfig = multisConfig;
		this.searchAgentId = searchAgentId;
		this.batchRun = batchRun;
		this.currentGame = currentGame;
		this.currentLogin = currentLogin;
		code_configs.add(new CodeConfiguration());
	}

	public JPanel confPanel() {
		return configPanel;
	}

	@SuppressWarnings("serial")
	public JScrollPane ennemiesPanel(int index) {
		JPanel ennemiesPane = new JPanel(new GridBagLayout());

		updateEnnemies[index] = () -> {
			SwingUtilities.invokeLater(() -> {
				ennemiesPane.removeAll();
				List<EnemyConfiguration> ennemies = code_configs.get(index).getEnemies();

				for (int i = 0; i < ennemies.size(); i++) {
					int i$ = i;
					ennemiesPane.add(new JLabel("Name:"), new GridBagConstraints(0, i * 3, 1, 1, 1., 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(01, 05, 0, 0), 0, 0));
					JTextField ennemyName = new JTextField(ennemies.get(i$).getName());
					ennemyName.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {
							ennemies.get(i$).setName(ennemyName.getText());
						}
					});
					ennemiesPane.add(ennemyName, new GridBagConstraints(1, i * 3, 1, 1, 10., 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(01, 05, 0, 0), 0, 0));

					ennemiesPane.add(new JLabel("AgentId:"), new GridBagConstraints(0, i * 3 + 1, 1, 1, 1., 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(01, 05, 10, 0), 0, 0));
					JTextField ennemyAgentId = new JTextField(ennemies.get(i$).getAgentId().toString());
					ennemyAgentId.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {
							ennemies.get(i$).setAgentId(toInt(ennemyAgentId));
						}
					});
					ennemiesPane.add(ennemyAgentId, new GridBagConstraints(1, i * 3 + 1, 1, 1, 10., 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(01, 05, 10, 0), 0, 0));

					int tabIndex$ = index;
					JButton rf = new JButton(new AbstractAction("X") {

						@Override
						public void actionPerformed(ActionEvent e) {
							ennemies.remove(i$);
							updateEnnemies[tabIndex$].run();
						}
					});
					ennemiesPane.add(rf, new GridBagConstraints(2, i * 3, 1, 1, 0., 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(01, 0, 0, 0), 0, 0));
					JButton search = new JButton(new AbstractAction("?") {

						@Override
						public void actionPerformed(ActionEvent e) {
							JOptionPane pane = new JOptionPane("Searching for last agentId");
							JDialog dialog = pane.createDialog(ennemiesPane, "Please wait...");
							new Thread(() -> {
								String agentId = searchAgentId.searchFor(((GameConfig) multiName.getSelectedItem()).getName(), ennemyName.getText());
								ennemyAgentId.setText(agentId);
								dialog.setVisible(false);
								dialog.dispose();
							}, "LoadLeaderBoard").start();
							dialog.setVisible(true);

						}
					});
					ennemiesPane.add(search, new GridBagConstraints(2, i * 3 + 1, 1, 1, 0., 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(01, 0, 10, 0), 0, 0));
					ennemiesPane.add(new JSeparator(SwingConstants.HORIZONTAL), new GridBagConstraints(0, i * 3 + 2, 3, 1, 1., 1, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(01, 05, 0, 05), 0, 0));

				}
				ennemiesPane.revalidate();
				ennemiesPane.repaint();

			});
		};

		return new JScrollPane(ennemiesPane);
	}

	@SuppressWarnings("serial")
	public JScrollPane seedPanel() {
		JPanel seedsPane = new JPanel(new GridBagLayout());

		updateSeeds = () -> {

			SwingUtilities.invokeLater(() -> {

				if (seedsConfig == null) {
					GameConfig gameConfig = (GameConfig) multiName.getSelectedItem();
					currentGame.setValue(gameConfig);
					seedsConfig = gameConfig.getSeeds();
				}

				seedsPane.removeAll();
				for (int i = 0; i < seedsOrigine.size(); i++) {
					int i$ = i;
					if (seedsConfig == null) {
						JTextField tf = new JTextField(seedsOrigine.get(i));
						tf.addActionListener(new ActionListener() {

							@Override
							public void actionPerformed(ActionEvent e) {
								seedsOrigine.set(i$, tf.getText());
							}
						});
						JButton rf = new JButton(new AbstractAction("X") {

							@Override
							public void actionPerformed(ActionEvent e) {
								seedsOrigine.remove(i$);
								updateSeeds.run();
							}
						});
						seedsPane.add(tf, new GridBagConstraints(0, i, 1, 1, 10., 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(01, 05, 0, 0), 0, 0));
						seedsPane.add(rf, new GridBagConstraints(1, i, 1, 1, 0., 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(01, 0, 0, 01), 0, 0));
					} else {
						String[] split = seedsOrigine.get(i$).split("\n");
						Map<String, String> seedsParam = new HashMap<>();
						for (String s : split) {
							String[] split_equals = s.split("=");
							if (split_equals.length == 2) {
								seedsParam.put(split_equals[0].trim(), split_equals[1].trim());
							}
						}

						for (int j = 0; j < seedsConfig.size(); j++) {
							SeedParamConfig sconfig = seedsConfig.get(j);
							String name = sconfig.getName();
							seedsPane.add(new JLabel(name), new GridBagConstraints(0, i * (seedsConfig.size() + 1) + j, 1, 1, 1., 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(01, 01, 0, 0), 0, 0));
							JTextField tf = new JTextField(seedsParam.getOrDefault(name, ""));
							tf.addActionListener(new ActionListener() {

								@Override
								public void actionPerformed(ActionEvent e) {
									seedsParam.put(name, tf.getText());
									StringBuilder sb = new StringBuilder();
									for (int k = 0; k < seedsConfig.size(); k++) {
										SeedParamConfig config_k = seedsConfig.get(k);
										if (k != 0) {
											sb.append("\n");
										}
										sb.append(config_k.getName() + "=" + seedsParam.getOrDefault(config_k.getName(), ""));
									}
									seedsOrigine.set(i$, sb.toString());
								}
							});
							seedsPane.add(tf, new GridBagConstraints(1, i * (seedsConfig.size() + 1) + j, 1, 1, 100., 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(01, 01, 0, 0), 0, 0));
						}
						JButton rf = new JButton(new AbstractAction("X") {

							@Override
							public void actionPerformed(ActionEvent e) {
								seedsOrigine.remove(i$);
								updateSeeds.run();
							}
						});
						seedsPane.add(rf, new GridBagConstraints(2, i * (seedsConfig.size() + 1), 1, 1, 1., 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(01, 01, 0, 0), 0, 0));
						seedsPane.add(new JSeparator(SwingConstants.HORIZONTAL), new GridBagConstraints(0, i * (seedsConfig.size() + 1) + seedsConfig.size(), 3, 1, 1., 1, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(01, 05, 0, 05), 0, 0));

					}
				}
				seedsPane.revalidate();
				seedsPane.repaint();
				// configPanel.repaint();
			});
		};

		return new JScrollPane(seedsPane);
	}

	@SuppressWarnings("serial")
	public void buildConfPane() {
		JPanel configPane = new JPanel(new GridBagLayout());

		JPanel accountConfig = new JPanel(new GridBagLayout());
		accountConfig.add(new JLabel("Name:"), new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(05, 05, 0, 05), 0, 0));
		accountName = new JTextField(10);
		accountConfig.add(accountName, new GridBagConstraints(1, 0, 1, 1, 10., 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(05, 05, 0, 05), 0, 0));
		accountConfig.add(new JLabel("Email:"), new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(05, 05, 0, 05), 0, 0));
		email = new JTextField(30);
		accountConfig.add(email, new GridBagConstraints(1, 1, 1, 1, 10., 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(05, 05, 0, 05), 0, 0));

		accountConfig.add(new JLabel("Password:"), new GridBagConstraints(0, 2, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(05, 05, 0, 05), 0, 0));
		password = new JPasswordField(10);
		accountConfig.add(password, new GridBagConstraints(1, 2, 1, 1, 10., 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(05, 05, 0, 05), 0, 0));

		accountConfig.add(new JLabel("Multi:"), new GridBagConstraints(0, 3, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(05, 05, 0, 05), 0, 0));
		multiName = new JComboBox<>(multisConfig.getMultiList().toArray(new GameConfig[0]));
		multiName.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				GameConfig gameConfig = (GameConfig) multiName.getSelectedItem();
				currentGame.setValue(gameConfig);
				seedsConfig = gameConfig.getSeeds();
				updateSeeds.run();
			}
		});
		multiName.setRenderer(new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				final Component label = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				setText(((GameConfig) value).getName());
				return label;
			}
		});
		accountConfig.add(multiName, new GridBagConstraints(1, 3, 1, 1, 10., 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(05, 05, 0, 05), 0, 0));

		
		
		accountConfig.add(new JButton(new AbstractAction("Login") {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				new Thread(() -> {
					AccountConfiguration accountConfiguration = accountConfiguration();
					new SessionLogIn().retrieveAccountCookieAndSession(accountConfiguration, currentGame.value.getName());
					currentLogin.setValue(accountConfiguration);
				}, "Login").start();
				
			}
		}), new GridBagConstraints(0, 4, 2, 1, 10., 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(05, 05, 0, 05), 0, 0));

		TitledBorder border = new TitledBorder("Account");
		border.setTitleJustification(TitledBorder.LEFT);
		border.setTitlePosition(TitledBorder.TOP);
		accountConfig.setBorder(border);

		JPanel globalConfig = new JPanel(new GridBagLayout());

		nbPlayer = 4;
		
		cooldown = new JFormattedTextField(new DecimalFormat());
		cooldown.setInputVerifier(new InputVerifier() {

			@Override
			public boolean verify(JComponent input) {
				try {
					String text = ((JFormattedTextField) input).getText();
					int parseInt = Integer.parseInt(text);
					final boolean res = parseInt >= 30;
					if (res) {
						cooldown.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
					} else {
						cooldown.setBorder(BorderFactory.createLineBorder(Color.red, 1));
					}
					return res;
				} catch (NumberFormatException e) {
					// Ignore parse exception
					return false;
				}
			}
		});
		globalConfig.add(new JLabel("Cooldown:"), new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(05, 05, 0, 05), 0, 0));
		globalConfig.add(cooldown, new GridBagConstraints(1, 1, 1, 1, 10., 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(05, 05, 0, 05), 0, 0));

		playerPosition = new JComboBox<PlayerPosition>(PlayerPosition.forPlayersGame(nbPlayer));
		globalConfig.add(new JLabel("Player position:"), new GridBagConstraints(0, 2, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(05, 05, 0, 05), 0, 0));
		globalConfig.add(playerPosition, new GridBagConstraints(1, 2, 1, 1, 10., 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(05, 05, 0, 05), 0, 0));

		minEnnemies = new JComboBox<Integer>(new Integer[] { 1, 2, 3, 4 });
		globalConfig.add(new JLabel("Min. Ennemies:"), new GridBagConstraints(0, 3, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(05, 05, 0, 05), 0, 0));
		globalConfig.add(minEnnemies, new GridBagConstraints(1, 3, 1, 1, 10., 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(05, 05, 0, 05), 0, 0));

		maxEnnemies = new JComboBox<Integer>(new Integer[] { 1, 2, 3, 4 });
		globalConfig.add(new JLabel("Max. Ennemies:"), new GridBagConstraints(0, 4, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(05, 05, 0, 05), 0, 0));
		globalConfig.add(maxEnnemies, new GridBagConstraints(1, 4, 1, 1, 10., 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(05, 05, 0, 05), 0, 0));

		randomSeed = new JCheckBox();
		globalConfig.add(new JLabel("Random Seed:"), new GridBagConstraints(0, 5, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(05, 05, 0, 05), 0, 0));
		globalConfig.add(randomSeed, new GridBagConstraints(1, 5, 1, 1, 10., 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(05, 05, 0, 05), 0, 0));

		JPanel seedList = new JPanel(new GridBagLayout());
		seedList.setBorder(BorderFactory.createTitledBorder("Seeds"));
		final JScrollPane seedPanel = seedPanel();
		seedPanel.setPreferredSize(seedPanel.getViewport().getView().getPreferredSize());

		seedList.add(seedPanel, new GridBagConstraints(0, 0, 1, 1, 1, 100, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 90));
		JButton addSeed = new JButton(new AbstractAction("Add") {

			@Override
			public void actionPerformed(ActionEvent e) {
				seedsOrigine.add("");
				updateSeeds.run();
			}
		});
		seedList.add(addSeed, new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));

		globalConfig.add(seedList, new GridBagConstraints(0, 6, 2, 1, 1, 100, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(05, 05, 0, 05), 0, 0));

		randomSeed.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				seedList.setVisible(!randomSeed.isSelected());
			}
		});

		TitledBorder globalOorder = new TitledBorder("Global");
		globalOorder.setTitleJustification(TitledBorder.LEFT);
		globalOorder.setTitlePosition(TitledBorder.TOP);
		globalConfig.setBorder(globalOorder);

		codesTabbed = new JTabbedPane();
		updateCodesConfig(codesTabbed);
		
		JPanel runPane = new JPanel(new GridBagLayout());
		JButton runButton = new JButton(new AbstractAction("Run") {

			@Override
			public void actionPerformed(ActionEvent e) {
				batchRun();
			}
		});
		runPane.add(new JLabel(""), new GridBagConstraints(0, 0, 1, 1, 1, 10, GridBagConstraints.SOUTH, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		runPane.add(runButton, new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.SOUTH, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));

		configPane.add(accountConfig, new GridBagConstraints(0, 0, 1, 1, 1., 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(05, 05, 0, 05), 0, 0));
		configPane.add(globalConfig, new GridBagConstraints(0, 1, 1, 1, 1., 10, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(05, 05, 0, 05), 0, 0));
		configPane.add(codesTabbed, new GridBagConstraints(0, 2, 1, 1, 1., 10, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(05, 05, 0, 05), 0, 0));

		JPanel loadSavePane = new JPanel(new GridLayout(1, 2));
		loadSavePane.add(new JButton(new AbstractAction("Load") {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc = new JFileChooser();
				jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int showOpenDialog = jfc.showOpenDialog(loadSavePane);
				if (showOpenDialog == JFileChooser.APPROVE_OPTION) {
					loadConfig(jfc.getSelectedFile().getAbsolutePath());
				}
			}
		}));
		loadSavePane.add(new JButton(new AbstractAction("Save") {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc = new JFileChooser();
				jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int showOpenDialog = jfc.showSaveDialog(loadSavePane);
				if (showOpenDialog == JFileChooser.APPROVE_OPTION) {
					saveConfig(jfc.getSelectedFile().getAbsolutePath());
				}
			}
		}));
		configPane.add(loadSavePane, new GridBagConstraints(0, 4, 1, 1, 10., 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(15, 05, 10, 05), 0, 0));

		configPane.add(runPane, new GridBagConstraints(0, 5, 1, 1, 10., 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(15, 05, 10, 05), 0, 0));

		configPanel = new JPanel(new GridBagLayout());
		configPanel.add(configPane, new GridBagConstraints(0, 1, 1, 1, 1., 1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(05, 05, 0, 05), 0, 0));
	}

	protected void batchRun() {
		batchRun.launch(config());
	}

	@SuppressWarnings("serial")
	void updateCodesConfig(JTabbedPane codesTabbed) {
		codesTabbed.removeAll();
		for (int code_i = 0; code_i < code_configs.size(); code_i++) {

			JPanel codeConfig = new JPanel(new GridBagLayout());

			int code_tab$ = code_i;
			codeConfig.add(new JButton(new AbstractAction("Delete code config") {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					code_configs.remove(code_tab$);
					updateCodesConfig(codesTabbed);
				}
			}), new GridBagConstraints(0, 0, 2, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(05, 05, 0, 05), 0, 0));
			
			JTextField path = new JTextField();
			codeConfig.add(new JLabel("Path:"), new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(05, 05, 0, 05), 0, 0));
			JPanel codePath = new JPanel(new GridBagLayout());
			codePath.add(path, new GridBagConstraints(0, 0, 1, 1, 10., 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(05, 05, 0, 05), 0, 0));
			JButton openDirectory = new JButton(new AbstractAction("..") {

				@Override
				public void actionPerformed(ActionEvent e) {
					JFileChooser jfc = new JFileChooser();
					jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
					int showOpenDialog = jfc.showOpenDialog(codePath);
					if (showOpenDialog == JFileChooser.APPROVE_OPTION) {
						path.setText(jfc.getSelectedFile().getAbsolutePath());
					}
				}
			});
			codePath.add(path, new GridBagConstraints(0, 0, 1, 1, 10., 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
			codePath.add(openDirectory, new GridBagConstraints(1, 0, 1, 1, 0., 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
			codeConfig.add(codePath, new GridBagConstraints(1, 1, 1, 1, 10., 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(05, 05, 0, 05), 0, 0));

			JTextField language = new JTextField();
			codeConfig.add(new JLabel("Language:"), new GridBagConstraints(0, 2, 1, 1, 0., 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(05, 05, 0, 05), 0, 0));
			codeConfig.add(language, new GridBagConstraints(1, 2, 1, 1, 0., 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(05, 05, 0, 05), 0, 0));

			JFormattedTextField nbReplays = new JFormattedTextField(new DecimalFormat());
			nbReplays.setInputVerifier(new InputVerifier() {

				@Override
				public boolean verify(JComponent input) {
					try {
						String text = ((JFormattedTextField) input).getText();
						int parseInt = Integer.parseInt(text);
						final boolean res = parseInt > 0 && parseInt < 100;
						if (res) {
							nbReplays.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
						} else {
							nbReplays.setBorder(BorderFactory.createLineBorder(Color.red, 1));
						}
						return res;
					} catch (NumberFormatException e) {
						// Ignore parse exception
						return false;
					}
				}
			});
			codeConfig.add(new JLabel("Nb. replays:"), new GridBagConstraints(0, 3, 1, 1, 0., 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(05, 05, 0, 05), 0, 0));
			codeConfig.add(nbReplays, new GridBagConstraints(1, 3, 1, 1, 0., 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(05, 05, 0, 05), 0, 0));
			codeConfig.setBorder(BorderFactory.createTitledBorder("Code"));

			JPanel ennemiesConfig = new JPanel(new GridBagLayout());
			final JScrollPane ennemiesScroll = ennemiesPanel(code_i);
			ennemiesScroll.setPreferredSize(ennemiesScroll.getViewport().getView().getPreferredSize());
			ennemiesConfig.add(ennemiesScroll, new GridBagConstraints(0, 0, 1, 1, 1., 100, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(05, 05, 0, 05), 0, 100));
			int tabIndex$ = code_i;
			JButton addEnnemy = new JButton(new AbstractAction("Add") {

				@Override
				public void actionPerformed(ActionEvent e) {
					List<EnemyConfiguration> ennemies = code_configs.get(tabIndex$).getEnemies();
					ennemies.add(new EnemyConfiguration(0, ""));
					updateEnnemies[tabIndex$].run();
				}
			});
			ennemiesConfig.add(addEnnemy, new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));

			ennemiesConfig.setBorder(BorderFactory.createTitledBorder("Enemies"));

			codeConfig.add(ennemiesConfig, new GridBagConstraints(0, 4, 2, 1, 0., 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(01, 01, 0, 01), 0, 0));
			
			CodeConfiguration codeConfiguration = code_configs.get(code_i);
			
			path.setText(codeConfiguration.getSourcePath());
			language.setText(codeConfiguration.getLanguage());
			nbReplays.setValue(codeConfiguration.getNbReplays());
			updateEnnemies[code_i].run();

			path.getDocument().addDocumentListener(new DocumentListener() {
				
				@Override
				public void removeUpdate(DocumentEvent e) {
					codeConfiguration.setSourcePath(path.getText());					
				}
				
				@Override
				public void insertUpdate(DocumentEvent e) {
					codeConfiguration.setSourcePath(path.getText());				
				}
				
				@Override
				public void changedUpdate(DocumentEvent e) {
					codeConfiguration.setSourcePath(path.getText());				
				}
			});
			language.getDocument().addDocumentListener(new DocumentListener() {
				
				@Override
				public void removeUpdate(DocumentEvent e) {
					codeConfiguration.setLanguage(language.getText());					
				}
				
				@Override
				public void insertUpdate(DocumentEvent e) {
					codeConfiguration.setLanguage(language.getText());					
				}
				
				@Override
				public void changedUpdate(DocumentEvent e) {
					codeConfiguration.setLanguage(language.getText());					
				}
			});
			nbReplays.getDocument().addDocumentListener(new DocumentListener() {
				
				private Integer parse() {
					try {
						return Integer.parseInt(nbReplays.getText());
					}
					catch (NumberFormatException e) {
						return codeConfiguration.getNbReplays();
					}
				}
				
				@Override
				public void removeUpdate(DocumentEvent e) {
					codeConfiguration.setNbReplays(parse());					
				}

				
				@Override
				public void insertUpdate(DocumentEvent e) {
					codeConfiguration.setNbReplays(parse());					
				}
				
				@Override
				public void changedUpdate(DocumentEvent e) {
					codeConfiguration.setNbReplays(parse());					
				}
			});
			path.addActionListener(e -> codeConfiguration.setSourcePath(path.getText()));
			language.addActionListener(e -> codeConfiguration.setLanguage(language.getText()));
			nbReplays.addActionListener(e -> codeConfiguration.setNbReplays(Integer.parseInt(nbReplays.getText())));
			
			codesTabbed.add("" + code_i, codeConfig);
		}

		codesTabbed.add("+", new JLabel(""));
		codesTabbed.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (codesTabbed.getSelectedIndex() == codesTabbed.getTabCount() - 1 && codesTabbed.getTabCount() > 1) {
					code_configs.add(new CodeConfiguration());
					updateCodesConfig(codesTabbed);
				}
			}
		});
	}

	public void loadConfig(GlobalConfiguration config) {
		accountName.setText(config.getAccountConfigurationList().get(0).getAccountName());
		email.setText(config.getAccountConfigurationList().get(0).getAccountLogin());
		password.setText(config.getAccountConfigurationList().get(0).getAccountPassword());

		GameConfig gameConfig = multisConfig.getMultiList().stream().filter(m -> m.getName().equals(config.getMultiName())).findFirst().get();
		multiName.setSelectedItem(gameConfig);
		cooldown.setValue(config.getRequestCooldown());
		playerPosition.setSelectedItem(Arrays.stream(PlayerPosition.forPlayersGame(nbPlayer)).filter(pp -> pp.code() == config.getPlayerPosition()).findFirst().get());
		minEnnemies.setSelectedItem(config.getMinEnemiesNumber());
		maxEnnemies.setSelectedItem(config.getMaxEnemiesNumber());
		randomSeed.setSelected(config.getRandomSeed());

		code_configs = config.getCodeConfigurationList();

		updateCodesConfig(codesTabbed);
		
		for (int i = 0; i < code_configs.size(); i++) {
			updateEnnemies[i].run();
		}

		seedsOrigine = config.getSeedList();
		seedsConfig = gameConfig.getSeeds();

		updateSeeds.run();

	}

	public void saveConfig(String fileName) {
		try {
			config().writeConfig(fileName);
		} catch (IOException e) {
			LOG.error("Unable to save", e);
			JOptionPane.showMessageDialog(configPanel, "Unable to save config", "Save", JOptionPane.ERROR_MESSAGE);
		}
	}

	GlobalConfiguration config() {
		GlobalConfiguration config = new GlobalConfiguration();
		config.setAccountConfigurationList(new ArrayList<>());
		config.setCodeConfigurationList(code_configs);
		config.setSeedList(new ArrayList<>());

		config.getAccountConfigurationList().add(accountConfiguration());

		config.setMaxEnemiesNumber((Integer) maxEnnemies.getSelectedItem());
		config.setMinEnemiesNumber((Integer) minEnnemies.getSelectedItem());
		config.setMultiName(currentGame.getValue().getName());
		config.setPlayerPosition(((PlayerPosition) playerPosition.getSelectedItem()).code());
		config.setRandomSeed(randomSeed.isSelected());
		config.setRequestCooldown(Integer.parseInt(cooldown.getText()));

		for (String seed : seedsOrigine) {
			config.getSeedList().add(seed);
		}
		return config;
	}

	AccountConfiguration accountConfiguration() {
		AccountConfiguration accountConfiguration = new AccountConfiguration();
		accountConfiguration.setAccountName(accountName.getText());
		accountConfiguration.setAccountLogin(email.getText());
		accountConfiguration.setAccountPassword(new String(password.getPassword()));
		return accountConfiguration;
	}

	public GlobalConfiguration readConfig(String filePath) {
		try {
			LOG.info("Loading configuration file : " + filePath);
			Gson gson = new Gson();
			FileInputStream configFileInputStream = new FileInputStream(filePath);
			JsonReader reader = new JsonReader(new InputStreamReader(configFileInputStream, "UTF-8"));
			return gson.fromJson(reader, GlobalConfiguration.class);
		} catch (JsonIOException | JsonSyntaxException | FileNotFoundException | UnsupportedEncodingException e) {
			LOG.error("Unable to read config", e);
		}
		return new GlobalConfiguration();
	}

	public void loadConfig(String filePath) {
		loadConfig(readConfig(filePath));
	}

	int toInt(JTextField ennemyAgentId) {
		try {
			return Integer.parseInt(ennemyAgentId.getText());
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	public ObservableValue<GameConfig> gameConfig() {
		return currentGame;
	}

	public static interface EnnemiesLink {
		public void add(String name, int i);
	}

	public EnnemiesLink ennemiesLink() {
		return new EnnemiesLink() {

			@Override
			public void add(String name, int agentid) {
				List<EnemyConfiguration> ennemies = code_configs.get(codesTabbed.getSelectedIndex()).getEnemies();
				ennemies.add(new EnemyConfiguration(agentid, name));
				updateEnnemies[codesTabbed.getSelectedIndex()].run();
			}
		};
	}

	public ObservableValue<AccountConfiguration> currentLogin() {
		return currentLogin;
	}

}
