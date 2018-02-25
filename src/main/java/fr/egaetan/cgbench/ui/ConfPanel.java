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
import java.io.FileOutputStream;
import java.io.FileWriter;
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
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;

import fr.egaetan.cgbench.Gui;
import fr.egaetan.cgbench.model.config.GameConfig;
import fr.egaetan.cgbench.model.config.MultisConfig;
import fr.egaetan.cgbench.model.config.PlayerPosition;
import fr.egaetan.cgbench.model.config.SeedParamConfig;
import fr.egaetan.cgbench.services.SearchAgentId;
import fr.svivien.cgbenchmark.model.config.AccountConfiguration;
import fr.svivien.cgbenchmark.model.config.CodeConfiguration;
import fr.svivien.cgbenchmark.model.config.EnemyConfiguration;
import fr.svivien.cgbenchmark.model.config.GlobalConfiguration;

public class ConfPanel {

    private static final Log LOG = LogFactory.getLog(Gui.class);
    final MultisConfig multisConfig;

    private JPanel configPanel;
    
	private JTextField accountName;
	private JTextField email;
	private JPasswordField password;
	private JComboBox<GameConfig> multiName;
	private JFormattedTextField cooldown;
	private JComboBox<PlayerPosition> playerPosition;
	private JComboBox<Integer> minEnnemies;
	private JComboBox<Integer> maxEnnemies;
	private JCheckBox randomSeed;
	private JTextField path;
	private JTextField language;
	private JFormattedTextField nbReplays;

	private int nbPlayer;
	
	List<EnemyConfiguration> ennemies = new ArrayList<>();
	Runnable updateEnnemies;
	
	List<String> seedsOrigine = new ArrayList<>();
	Runnable updateSeeds;

	ObservableValue<GameConfig> currentGame = new ObservableValue<>();
	
	private List<SeedParamConfig> seedsConfig;
	private SearchAgentId searchAgentId;
	
	public ConfPanel(MultisConfig multisConfig, SearchAgentId searchAgentId) {
		this.multisConfig = multisConfig;
		this.searchAgentId = searchAgentId;
	}

	public JPanel confPanel() {
		return configPanel;
	}
	
	@SuppressWarnings("serial")
	public JScrollPane ennemiesPanel() {
		JPanel ennemiesPane = new JPanel(new GridBagLayout());
		
		updateEnnemies = () -> {
			SwingUtilities.invokeLater(() -> {
				ennemiesPane.removeAll();
				
				for (int i = 0; i < ennemies.size(); i++) {
					int i$ = i;
					ennemiesPane.add(new JLabel("Name:"), new GridBagConstraints(0, i*3, 1, 1, 1., 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(01, 05, 0, 0), 0, 0));
					JTextField ennemyName = new JTextField(ennemies.get(i$).getName());
					ennemyName.addActionListener(new ActionListener() {
						
						@Override
						public void actionPerformed(ActionEvent e) {
							ennemies.get(i$).setName(ennemyName.getText());
						}
					});
					ennemiesPane.add(ennemyName, new GridBagConstraints(1, i*3, 1, 1, 10., 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(01, 05, 0, 0), 0, 0));

					ennemiesPane.add(new JLabel("AgentId:"), new GridBagConstraints(0, i*3+1, 1, 1, 1., 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(01, 05, 10, 0), 0, 0));
					JTextField ennemyAgentId = new JTextField(ennemies.get(i$).getAgentId().toString());
					ennemyAgentId.addActionListener(new ActionListener() {
						
						@Override
						public void actionPerformed(ActionEvent e) {
							ennemies.get(i$).setAgentId(toInt(ennemyAgentId));
						}
					});
					ennemiesPane.add(ennemyAgentId, new GridBagConstraints(1, i*3+1, 1, 1, 10., 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(01, 05, 10, 0), 0, 0));
					
					JButton rf = new JButton(new AbstractAction("X") {
						
						@Override
						public void actionPerformed(ActionEvent e) {
							ennemies.remove(i$);
							updateEnnemies.run();
						}
					});
					ennemiesPane.add(rf, new GridBagConstraints(2, i*3, 1, 1, 0., 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(01, 0, 0, 0), 0, 0));
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
					ennemiesPane.add(search, new GridBagConstraints(2, i*3+1, 1, 1, 0., 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(01, 0, 10, 0), 0, 0));
					ennemiesPane.add(new JSeparator(SwingConstants.HORIZONTAL), new GridBagConstraints(0, i*3 + 2, 3, 1, 1., 1, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(01, 05, 0, 05), 0, 0));
					
				}
				ennemiesPane.revalidate();
				ennemiesPane.repaint();
				
			});
		};
		
		return new JScrollPane(ennemiesPane);
	}
	
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
					}
					else {
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
							seedsPane.add(new JLabel(name), new GridBagConstraints(0, i*(seedsConfig.size()+1) + j, 1, 1, 1., 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(01, 01, 0, 0), 0, 0));
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
										sb.append(config_k.getName()+"="+seedsParam.getOrDefault(config_k.getName(), ""));
									}
									seedsOrigine.set(i$, sb.toString());
								}
							});
							seedsPane.add(tf, new GridBagConstraints(1, i*(seedsConfig.size()+1) + j, 1, 1, 100., 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(01, 01, 0, 0), 0, 0));
						}
						JButton rf = new JButton(new AbstractAction("X") {
							
							@Override
							public void actionPerformed(ActionEvent e) {
								seedsOrigine.remove(i$);
								updateSeeds.run();
							}
						});
						seedsPane.add(rf, new GridBagConstraints(2, i*(seedsConfig.size()+1), 1, 1, 1., 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(01, 01, 0, 0), 0, 0));
						seedsPane.add(new JSeparator(SwingConstants.HORIZONTAL), new GridBagConstraints(0, i*(seedsConfig.size()+1) + seedsConfig.size(), 3, 1, 1., 1, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(01, 05, 0, 05), 0, 0));
						
					}
				}
				seedsPane.revalidate();
				seedsPane.repaint();
//				configPanel.repaint();
			});
		};

		return new JScrollPane(seedsPane);
	}
	
	@SuppressWarnings("serial")
	public void buildConfPane() {
		JPanel configPane = new JPanel(new GridBagLayout());
		
	    
		JPanel accountConfig = new JPanel(new GridBagLayout());
		accountConfig.add(new JLabel("Name:"), new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(05,05,0,05), 0, 0));
		accountName = new JTextField(10);
		accountConfig.add(accountName, new GridBagConstraints(1, 0, 1, 1, 10., 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(05,05,0,05), 0, 0));
		accountConfig.add(new JLabel("Email:"), new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(05,05,0,05), 0, 0));
		email = new JTextField(30);
		accountConfig.add(email, new GridBagConstraints(1, 1, 1, 1, 10., 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(05,05,0,05), 0, 0));
		
		accountConfig.add(new JLabel("Password:"), new GridBagConstraints(0, 2, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(05,05,0,05), 0, 0));
		password = new JPasswordField(10);
		accountConfig.add(password, new GridBagConstraints(1, 2, 1, 1, 10., 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(05,05,0,05), 0, 0));
		
	//		JButton login = new JButton("Login");
	//		accountConfig.add(login, new GridBagConstraints(0, 3, 2, 1, 10., 1, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(05,05,0,05), 0, 0));
	//		
	
		TitledBorder border = new TitledBorder("Account");
		border.setTitleJustification(TitledBorder.LEFT);
		border.setTitlePosition(TitledBorder.TOP);
		accountConfig.setBorder(border);
		
	
		JPanel globalConfig = new JPanel(new GridBagLayout());
	
		nbPlayer = 4;
		globalConfig.add(new JLabel("Multi:"), new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(05,05,0,05), 0, 0));
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
		globalConfig.add(multiName, new GridBagConstraints(1, 0, 1, 1, 10., 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(05,05,0,05), 0, 0));
	
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
					}
					else {
						cooldown.setBorder(BorderFactory.createLineBorder(Color.red, 1));
					}
					return res;
				} catch (NumberFormatException e) {
					// Ignore parse exception
					return false;
				}
			}
		});
		globalConfig.add(new JLabel("Cooldown:"), new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(05,05,0,05), 0, 0));
		globalConfig.add(cooldown, new GridBagConstraints(1, 1, 1, 1, 10., 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(05,05,0,05), 0, 0));
	
		playerPosition = new JComboBox<PlayerPosition>(PlayerPosition.forPlayersGame(nbPlayer));
		globalConfig.add(new JLabel("Player position:"), new GridBagConstraints(0, 2, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(05,05,0,05), 0, 0));
		globalConfig.add(playerPosition, new GridBagConstraints(1, 2, 1, 1, 10., 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(05,05,0,05), 0, 0));
	
		minEnnemies = new JComboBox<Integer>(new Integer[] {1, 2, 3, 4});
		globalConfig.add(new JLabel("Min. Ennemies:"), new GridBagConstraints(0, 3, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(05,05,0,05), 0, 0));
		globalConfig.add(minEnnemies, new GridBagConstraints(1, 3, 1, 1, 10., 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(05,05,0,05), 0, 0));
	
		maxEnnemies = new JComboBox<Integer>(new Integer[] {1, 2, 3, 4});
		globalConfig.add(new JLabel("Max. Ennemies:"), new GridBagConstraints(0, 4, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(05,05,0,05), 0, 0));
		globalConfig.add(maxEnnemies, new GridBagConstraints(1, 4, 1, 1, 10., 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(05,05,0,05), 0, 0));
		
		randomSeed = new JCheckBox();
		globalConfig.add(new JLabel("Random Seed:"), new GridBagConstraints(0, 5, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(05,05,0,05), 0, 0));
		globalConfig.add(randomSeed, new GridBagConstraints(1, 5, 1, 1, 10., 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(05,05,0,05), 0, 0));
		
		
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
		
		
		JPanel codeConfig = new JPanel(new GridBagLayout());
		
		path = new JTextField();
		codeConfig.add(new JLabel("Path:"), new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(05,05,0,05), 0, 0));
		JPanel codePath = new JPanel(new GridBagLayout());
		codePath.add(path, new GridBagConstraints(0, 0, 1, 1, 10., 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(05,05,0,05), 0, 0));
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
		codePath.add(path, new GridBagConstraints(0, 0, 1, 1, 10., 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0, 0));
		codePath.add(openDirectory, new GridBagConstraints(1, 0, 1, 1, 0., 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0, 0));
		codeConfig.add(codePath, new GridBagConstraints(1, 0, 1, 1, 10., 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(05,05,0,05), 0, 0));
		
		language = new JTextField();
		codeConfig.add(new JLabel("Language:"), new GridBagConstraints(0, 1, 1, 1, 0., 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(05,05,0,05), 0, 0));
		codeConfig.add(language, new GridBagConstraints(1, 1, 1, 1, 0., 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(05,05,0,05), 0, 0));
	
		nbReplays = new JFormattedTextField(new DecimalFormat());
		nbReplays.setInputVerifier(new InputVerifier() {
			
			@Override
			public boolean verify(JComponent input) {
				try {
					String text = ((JFormattedTextField) input).getText();
					int parseInt = Integer.parseInt(text);
					final boolean res = parseInt > 0 && parseInt < 100;
					if (res) {
						nbReplays.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
					}
					else {
						nbReplays.setBorder(BorderFactory.createLineBorder(Color.red, 1));
					}
					return res;
				} catch (NumberFormatException e) {
					// Ignore parse exception
					return false;
				}
			}
		});
		codeConfig.add(new JLabel("Nb. replays:"), new GridBagConstraints(0, 2, 1, 1, 0., 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(05,05,0,05), 0, 0));
		codeConfig.add(nbReplays, new GridBagConstraints(1, 2, 1, 1, 0., 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(05,05,0,05), 0, 0));
		codeConfig.setBorder(BorderFactory.createTitledBorder("Code"));
		
		JPanel ennemiesConfig = new JPanel(new GridBagLayout());
		final JScrollPane ennemiesScroll = ennemiesPanel();
		ennemiesScroll.setPreferredSize(ennemiesScroll.getViewport().getView().getPreferredSize());
		ennemiesConfig.add(ennemiesScroll, new GridBagConstraints(0, 0, 1, 1, 1., 100, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(05,05,0,05), 0, 100));
		JButton addEnnemy = new JButton(new AbstractAction("Add") {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ennemies.add(new EnemyConfiguration(0, ""));
				updateEnnemies.run();
			}
		});
		ennemiesConfig.add(addEnnemy, new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		
		ennemiesConfig.setBorder(BorderFactory.createTitledBorder("Enemies"));
		
		JPanel runPane = new JPanel(new GridBagLayout());
		JButton runButton = new JButton(new AbstractAction("Run") {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		runPane.add(new JLabel(""), new GridBagConstraints(0, 0, 1, 1, 1, 10, GridBagConstraints.SOUTH, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		runPane.add(runButton, new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.SOUTH, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		
		
		configPane.add(accountConfig, new GridBagConstraints(0, 0, 1, 1, 1., 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(05,05,0,05), 0, 0));
		configPane.add(globalConfig, new GridBagConstraints(0, 1, 1, 1, 1., 10, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(05,05,0,05), 0, 0));
		configPane.add(codeConfig, new GridBagConstraints(0, 2, 1, 1, 1., 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(05,05,0,05), 0, 0));
		configPane.add(ennemiesConfig, new GridBagConstraints(0, 3, 1, 1, 1., 10, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(05,05,0,05), 0, 0));
		
		
		JPanel loadSavePane = new JPanel(new GridLayout(1, 2));
		loadSavePane.add(new JButton(new AbstractAction("Load") {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc = new JFileChooser();
				jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int showOpenDialog = jfc.showOpenDialog(codePath);
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
				int showOpenDialog = jfc.showSaveDialog(codePath);
				if (showOpenDialog == JFileChooser.APPROVE_OPTION) {
					saveConfig(jfc.getSelectedFile().getAbsolutePath());
				}
			}
		}));
		configPane.add(loadSavePane, new GridBagConstraints(0, 4, 1, 1, 10., 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(15,05,10,05), 0, 0));

		configPane.add(runPane, new GridBagConstraints(0, 5, 1, 1, 10., 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(15,05,10,05), 0, 0));
		
		
		
		configPanel = new JPanel(new GridBagLayout());
		configPanel.add(configPane, new GridBagConstraints(0, 1, 1, 1, 1., 1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(05,05,0,05), 0, 0));
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
		
		path.setText(config.getCodeConfigurationList().get(0).getSourcePath());
		language.setText(config.getCodeConfigurationList().get(0).getLanguage());
		nbReplays.setValue(config.getCodeConfigurationList().get(0).getNbReplays());
		
		seedsOrigine = config.getSeedList();
		seedsConfig = gameConfig.getSeeds();
		
		updateSeeds.run();
		
		ennemies = config.getCodeConfigurationList().get(0).getEnemies();
		updateEnnemies.run();
	}
	public void saveConfig(String fileName) {
		GlobalConfiguration config =  new GlobalConfiguration();
		config.setAccountConfigurationList(new ArrayList<>());
		config.setCodeConfigurationList(new ArrayList<>());
		config.setSeedList(new ArrayList<>());
		
		AccountConfiguration accountConfiguration = new AccountConfiguration();
		accountConfiguration.setAccountName(accountName.getText());
		accountConfiguration.setAccountLogin(email.getText());
		accountConfiguration.setAccountPassword(new String(password.getPassword()));
		config.getAccountConfigurationList().add(accountConfiguration);
		
		config.setMaxEnemiesNumber((Integer) maxEnnemies.getSelectedItem());
		config.setMinEnemiesNumber((Integer) minEnnemies.getSelectedItem());
		config.setMultiName(currentGame.getValue().getName());
		config.setPlayerPosition(((PlayerPosition) playerPosition.getSelectedItem()).code());
		config.setRandomSeed(randomSeed.isSelected());
		config.setRequestCooldown(Integer.parseInt(cooldown.getText()));

		for (String seed : seedsOrigine) {
			config.getSeedList().add(seed);
		}
		
		CodeConfiguration codeConfiguration = new CodeConfiguration();
		codeConfiguration.setLanguage(language.getText());
		codeConfiguration.setNbReplays(Integer.parseInt(nbReplays.getText()));
		codeConfiguration.setSourcePath(path.getText());
		codeConfiguration.setEnemies(ennemies);

		config.getCodeConfigurationList().add(codeConfiguration);

		String json = new GsonBuilder().create().toJson(config);
		try (FileWriter fw = new FileWriter(fileName)) {
			fw.write(json);
			fw.flush();
		} catch (IOException e) {
			LOG.error("Unable to save", e);
			JOptionPane.showMessageDialog(configPanel, "Unable to save config", "Save", JOptionPane.ERROR_MESSAGE);
		}
	}

	public GlobalConfiguration readConfig(String filePath)  {
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
				ennemies.add(new EnemyConfiguration(agentid, name));
				updateEnnemies.run();
			}
		};
	}

}
