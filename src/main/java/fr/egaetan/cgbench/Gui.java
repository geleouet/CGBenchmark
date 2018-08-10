package fr.egaetan.cgbench;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;

import fr.egaetan.cgbench.analyser.locm.AnalyseReplays;
import fr.egaetan.cgbench.api.GameApi;
import fr.egaetan.cgbench.api.LastBattlesAgentApi;
import fr.egaetan.cgbench.api.LastBattlesApi;
import fr.egaetan.cgbench.api.LeaderboardApi;
import fr.egaetan.cgbench.api.LeaderboardChallengeApi;
import fr.egaetan.cgbench.api.LeaderboardLeagueApi;
import fr.egaetan.cgbench.api.LeaderboardMultiApi;
import fr.egaetan.cgbench.api.TestSessionApi;
import fr.egaetan.cgbench.api.param.LastBattlesParam;
import fr.egaetan.cgbench.model.config.GameConfig;
import fr.egaetan.cgbench.model.config.MultisConfig;
import fr.egaetan.cgbench.model.leaderboard.Battle;
import fr.egaetan.cgbench.model.leaderboard.Game;
import fr.egaetan.cgbench.model.leaderboard.Leaderboard;
import fr.egaetan.cgbench.model.leaderboard.Player;
import fr.egaetan.cgbench.model.leaderboard.SuccessLastBattles;
import fr.egaetan.cgbench.model.leaderboard.SuccessLeaderboard;
import fr.egaetan.cgbench.model.leaderboard.User;
import fr.egaetan.cgbench.services.LastBattlesLoader;
import fr.egaetan.cgbench.services.LastBattlesLoaderService;
import fr.egaetan.cgbench.services.LastBattlesService;
import fr.egaetan.cgbench.services.LeaderBoardLoader;
import fr.egaetan.cgbench.services.LeaderBoardLoaderService;
import fr.egaetan.cgbench.services.SearchAgentId;
import fr.egaetan.cgbench.ui.ActionOnUser;
import fr.egaetan.cgbench.ui.BatchRun;
import fr.egaetan.cgbench.ui.ConfPanel;
import fr.egaetan.cgbench.ui.DockableTabbedPane;
import fr.egaetan.cgbench.ui.LastBattlesPane;
import fr.egaetan.cgbench.ui.LeaderBoardPane;
import fr.egaetan.cgbench.ui.ObservableValue;
import fr.svivien.cgbenchmark.CGBenchmark;
import fr.svivien.cgbenchmark.Constants;
import fr.svivien.cgbenchmark.model.config.AccountConfiguration;
import fr.svivien.cgbenchmark.model.config.GlobalConfiguration;
import fr.svivien.cgbenchmark.model.request.play.PlayResponse;
import fr.svivien.cgbenchmark.model.test.TestInput;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;

public class Gui {

    private static final Log LOG = LogFactory.getLog(Gui.class);


	public static void main(String[] args) throws InvocationTargetException, InterruptedException {
		setLAF();

		Gui gui = new Gui();
		gui.initServices();
		
		SwingUtilities.invokeAndWait(() -> {
			gui.readRessources();
			gui.buildGui();
			
			LOG.debug("GUI builded");
		});
		
		gui.load("./config.json");
	}
	
	ConfPanel confPane;
	MultisConfig multisConfig;
	private LeaderboardChallengeApi leaderboardChallengeApi;
	private LeaderboardMultiApi leaderboardMultiApi;
	LeaderboardLeagueApi leaderboardLeagueApi;
	private LeaderBoardPane leaderboardPane;
	TestSessionApi testSessionApi;
	final ObservableValue<GameConfig> currentGame;
	final ObservableValue<AccountConfiguration> currentLogin;
	LeaderBoardLoader leaderboardService;
	LastBattlesLoader lastBattlesService;
	ObservableValue<User> current;
	ObservableValue<List<User>> userList;
	ScheduledExecutorService scheduler;
	private LastBattlesPane lastBattlesPane;
	JFrame mainFrame;
	LastBattlesAgentApi lastBattlesAgentApi;
	private LeaderboardApi leaderboardApi;
	GameApi loadGameApi;
	
	AnalyseReplays analyser;

	public Gui() {
		currentGame = new ObservableValue<>();
		currentLogin = new ObservableValue<>();
		current = new ObservableValue<>();
		userList = new ObservableValue<>();
		
		analyser = new AnalyseReplays();
		initListeners();
	}

	private void initListeners() {
		scheduler = Executors.newScheduledThreadPool(1, new ThreadFactory() {

			@Override
			public Thread newThread(Runnable r) {
				return new Thread(r, "RefreshScheduler");
			}
		});
		Runnable scheduledLeaderBoard = () -> {
			AccountConfiguration accountConfiguration = currentLogin.getValue();
			if (accountConfiguration == null) {
				userList.setValue(Collections.emptyList());
				return;
			}
			if (accountConfiguration.getAccountIde() == null) {
				userList.setValue(Collections.emptyList());
				return;
			}
			SuccessLeaderboard body = leaderboardService.load();
			current.setValue(accountConfiguration.getUser());
			Leaderboard success = body.getSuccess();
			userList.setValue(success.getUsers());
		};
		currentLogin.addPropertyChangeListener(new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				scheduledLeaderBoard.run();
			}
		});
		userList.addPropertyChangeListener(new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				lastBattlesService.lastBattles();
			}
		});
		scheduler.scheduleAtFixedRate(scheduledLeaderBoard, 10, 30, TimeUnit.SECONDS);
	}
	
	
	private void initServices() {
//		HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
//		interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//		OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).readTimeout(600, TimeUnit.SECONDS).build();
		OkHttpClient client = new OkHttpClient.Builder().readTimeout(600, TimeUnit.SECONDS).build();
		
		GsonConverterFactory converter = GsonConverterFactory.create();
		Retrofit retrofit = new Retrofit.Builder().client(client).baseUrl(Constants.CG_HOST).addConverterFactory(converter).build();
		leaderboardChallengeApi = retrofit.create(LeaderboardChallengeApi.class);
		leaderboardMultiApi = retrofit.create(LeaderboardMultiApi.class);
		leaderboardLeagueApi = retrofit.create(LeaderboardLeagueApi.class);
		testSessionApi = retrofit.create(TestSessionApi.class);
		loadGameApi = retrofit.create(GameApi.class);
		lastBattlesAgentApi = retrofit.create(LastBattlesAgentApi.class);
		LastBattlesApi testBattlesApi = retrofit.create(LastBattlesApi.class);
		leaderboardApi = p -> {
			if (currentGame.getValue() != null) {
				if (currentGame.getValue().isChallenge() == null) {
					Call<SuccessLeaderboard> multi = leaderboardMultiApi.load(p);
					try {
						Response<SuccessLeaderboard> execute = multi.execute();
						if (execute.isSuccess() && execute.body() != null && execute.body().getSuccess() != null) {
							currentGame.getValue().setChallenge(false);
						}
						else if (execute.isSuccess() && execute.body() != null && execute.body().getSuccess() == null) {
							currentGame.getValue().setChallenge(true);
						} 
						
					}
					catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (currentGame.getValue().isChallenge() != null && !currentGame.getValue().isChallenge()) {
					return leaderboardMultiApi.load(p);
				}
				return leaderboardChallengeApi.load(p);
			}
			return leaderboardMultiApi.load(p);
		};
		
		leaderboardService = new LeaderBoardLoaderService(currentGame, currentLogin, leaderboardLeagueApi, leaderboardApi, testSessionApi);
		lastBattlesService = new LastBattlesService(currentGame, currentLogin, testBattlesApi) {
			@Override
			protected String comment() {
				return " " + currentLogin.getValue().getAccountName();
			}
		};
	}
	
	private void load(@SuppressWarnings("unused") String string) {
		//confPane.loadConfig(string);
	}

	public void readRessources() {
		multisConfig = readMultisList();
	}
	
	private void buildGui() {
		mainFrame = new JFrame("CGBenchmark");
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		confPane = new ConfPanel(multisConfig, new SearchAgentId(leaderboardApi), batchRunner(), currentGame, currentLogin);
		confPane.buildConfPane();
		
		JPanel arena = buildArenaPane();
		
		JSplitPane splittedConfig = new JSplitPane();
		splittedConfig.setLeftComponent(confPane.confPanel());

		JSplitPane splittedLdbrd = new JSplitPane();
		splittedLdbrd.setRightComponent(arena);
		splittedLdbrd.setLeftComponent(new JButton(new AbstractAction("Main") {

			private static final long serialVersionUID = 3441614445062883587L;
			@Override
			public void actionPerformed(ActionEvent e) {
				analyser.analyseReplays(current.getValue(), lastBattlesAgentApi, loadGameApi, currentLogin.getValue());
			}
		}));
		
		splittedConfig.setRightComponent(splittedLdbrd);

		mainFrame.getContentPane().add(splittedConfig, BorderLayout.CENTER);
		mainFrame.pack();
		mainFrame.setVisible(true);
	}
	
	private void analyse(User user) {
		analyser.analyseReplays(user, lastBattlesAgentApi, loadGameApi, currentLogin.getValue());
	}

	private void createSubLastBattles(User user) {
		AtomicBoolean endRequests = new AtomicBoolean(false);
		new SwingWorker<Void, Void>() {

			private LastBattlesPane subLastBattlesPane;

			@Override
			protected Void doInBackground()  {
				
				ObservableValue<User> other = new ObservableValue<>();
				other.setValue(user);
				LastBattlesLoaderService lastBattlesLoaderService = new LastBattlesLoaderService(lastBattlesAgentApi::load) {
					@Override
					protected Call<SuccessLastBattles> loadGames(long lastGameId) {
						return this.testBattlesApi.load(new LastBattlesParam(user.getAgentId(), lastGameId));
					}
					
					@Override
					protected String comment() {
						return " " + user.getPseudo();
					}
				};
				ObservableValue<List<Battle>> lastBattles2 = lastBattlesLoaderService.lastBattles();
				
				subLastBattlesPane = new LastBattlesPane(lastBattles2, userList, other);
				subLastBattlesPane.buildPane();
				subLastBattlesPane.process();
				
				
				scheduler.scheduleAtFixedRate(() -> {
					if (endRequests.get()) {
						throw new RuntimeException("Close connection");
					}
					lastBattlesLoaderService.lastBattles();
				}, 10, 10, TimeUnit.SECONDS); 
				
				return null;
			}
			
			@Override
			protected void done() {
				JComponent panel = subLastBattlesPane.panel();
				JDialog diag = new JDialog(mainFrame, user.getCodingamer() != null ? user.getCodingamer().getPseudo() : user.getPseudo(), ModalityType.MODELESS);
				diag.getContentPane().add(panel, BorderLayout.CENTER);
				diag.pack();
				diag.addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosed(WindowEvent e) {
						endRequests.set(true);
					}
				});
				diag.setVisible(true);
			}
		}.execute();
		
	}
	

	private void createSubLeaderboard(User user) {
		ObservableValue<User> other = new ObservableValue<>();
		other.setValue(user);
		
		LastBattlesLoaderService lastBattlesLoaderService = new LastBattlesLoaderService(lastBattlesAgentApi::load) {
			@Override
			protected Call<SuccessLastBattles> loadGames(long lastGameId) {
				return this.testBattlesApi.load(new LastBattlesParam(user.getAgentId(), lastGameId));
			}
			
			@Override
			protected String comment() {
				return " " + user.getPseudo();
			}
		};
		LeaderBoardPane leaderBoardPane = new LeaderBoardPane(userList, other, confPane.ennemiesLink(), leaderboardActions(),  
				lastBattlesLoaderService.lastBattles());
		leaderBoardPane.buildPane();
		
		AtomicBoolean endRequests = new AtomicBoolean(false);
		scheduler.scheduleAtFixedRate(() -> {
			if (endRequests.get()) {
				throw new RuntimeException("Close connection");
			}
			lastBattlesLoaderService.lastBattles();
		}, 1, 10, TimeUnit.SECONDS); 
		
		JDialog diag = new JDialog(mainFrame, user.getCodingamer() != null ? user.getCodingamer().getPseudo() : user.getPseudo(), ModalityType.MODELESS);
		diag.getContentPane().add(leaderBoardPane.panel(), BorderLayout.CENTER);
		diag.pack();
		diag.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				endRequests.set(true);
			}
		});
		diag.setVisible(true);
	}

	private JPanel buildArenaPane() {
		ObservableValue<List<Battle>> lastBattles = lastBattlesService.lastBattles();
		leaderboardPane = new LeaderBoardPane(userList, current, confPane.ennemiesLink(),
				leaderboardActions(),
				lastBattles);
		leaderboardPane.buildPane();
		
		lastBattlesPane = new LastBattlesPane(lastBattles, userList, current);
		lastBattlesPane.buildPane();
		
		JPanel arena = new JPanel(new GridBagLayout());
		JLabel arenaClassement = new JLabel("#?");
		arenaClassement.setFont(arenaClassement.getFont().deriveFont(arenaClassement.getFont().getSize() * 2.5f));
		JLabel progressClassement = new JLabel("") {

			private static final long serialVersionUID = 258371179814040775L;

			@Override
			protected void paintComponent(Graphics g) {
				Double value = lastBattlesService.progress().getValue();
				if (value == null || value >= 1.) {
					// Nothing
				}
				else {
					Dimension s = getSize();
					g.setColor(new Color((int) Math.min(150, 100+value*100),(int) (90 + value * 100),(int) Math.min(255, 105 + value * 250)));
					g.fillRect(0, 0, (int) (s.width*value), s.height);
				}
				super.paintComponent(g);
			}
		};
		progressClassement.setHorizontalAlignment(SwingConstants.CENTER);
		
		progressClassement.setFont(progressClassement.getFont().deriveFont(progressClassement.getFont().getSize() * 1.5f));
		lastBattlesService.progress().addPropertyChangeListener(new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				Double value = lastBattlesService.progress().getValue();
				if (value == null || value >= 1.) {
					progressClassement.setText("");
				}
				else {
					progressClassement.setText("" + Math.round(value * 100)+"%");
					progressClassement.setBorder(BorderFactory.createLineBorder(Color.darkGray, 1));
				}
			}
		});
		userList.addPropertyChangeListener(new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				List<User> users = userList.getValue();
				int p = -1;
				if (users == null || currentLogin.getValue() == null) {
					return;
				}
				for (int i = 0; i < users.size(); i++) {
					if (users.get(i)!=null && users.get(i).getPseudo() != null && users.get(i).getPseudo().equals(currentLogin.getValue().getAccountName())) {
						p = i;
						break;
					}
				}		
				arenaClassement.setText("#" + (p+1)/* + " - "+ (users.get(p).getLeague()!= null ? users.get(p).getLeague().getDivisionName() : "")*/);
				if (users.get(p).getLeague()!= null) {
					ImageIcon icon = new ImageIcon(getClass().getResource("/images/league_"+users.get(p).getLeague().getDivisionName().toLowerCase()+".png"));
					arenaClassement.setIcon(new ImageIcon(icon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH)));
				}
				else {
					arenaClassement.setIcon(null);
				}
				
			}
		});
		arena.add(arenaClassement, new GridBagConstraints(0, 0, 1, 1, 1, 0., GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(02, 10, 02, 10), 0, 0));
		arena.add(progressClassement, new GridBagConstraints(1, 0, 1, 1, 1, 0., GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(02, 10, 02, 10), 0, 0));
		
		JTabbedPane arenaTabs = new DockableTabbedPane();
		arenaTabs.add("Leaderboard",leaderboardPane.panel());
		arenaTabs.add("LastBattles", lastBattlesPane.panel());
		arena.add(arenaTabs, new GridBagConstraints(0, 1, 2, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		return arena;
	}

	private List<ActionOnUser> leaderboardActions() {
		return Arrays.asList(
				new ActionOnUser(this::createSubLeaderboard, "Show statistics..."),
				new ActionOnUser(this::createSubLastBattles, "Show last battles..."), new ActionOnUser(this::analyse, "Analyse...")
				);
	}

	
	
	private BatchRun batchRunner() {
		BatchRun runBatch = new BatchRun() {
			private final Log LOG = LogFactory.getLog(BatchRun.class);

			@Override
			public void launch(GlobalConfiguration config) {
				try {
					config.checkConfiguration();
				} catch (IllegalArgumentException e) {
					LOG.error("Illegal config", e);
					JOptionPane.showMessageDialog(mainFrame, e.getMessage(), "Illegal configuration", JOptionPane.ERROR_MESSAGE);
					return;
				}
				List<ObservableValue<List<Battle>>> lastBattles2 = new ArrayList<>();
				ObservableValue<User> me$ = new ObservableValue<>();
				User userBatch = new User();
				userBatch.setAgentId(-1);
				userBatch.setCodingamer(current.getValue().getCodingamer());
				me$.setValue(userBatch);
			
				
				DockableTabbedPane resultsTabs = new DockableTabbedPane();
				
				JDialog diag = new JDialog(mainFrame, "Batch...", ModalityType.MODELESS);
				diag.getContentPane().add(resultsTabs, BorderLayout.CENTER);
				diag.pack();
				diag.addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosed(WindowEvent e) {
					}
				});
				diag.setVisible(true);
				
				
				CGBenchmark bench = new CGBenchmark(config, (t, r) -> {
					int indexOfTab = resultsTabs.indexOfTab(t.getCodeName());
					if (indexOfTab != -1) {
						Gui.this.consumeMatch(t, r, lastBattles2.get(indexOfTab));	
					}
					else {
						ObservableValue<List<Battle>> newLastBattles = new ObservableValue<>();
						newLastBattles.setValue(new ArrayList<>());
						lastBattles2.add(newLastBattles);
						LastBattlesPane subLastBattlesPane = new LastBattlesPane(newLastBattles, userList, me$);
						subLastBattlesPane.buildPane();
						subLastBattlesPane.process();
						ObservableValue<List<User>> userList$ = new ObservableValue<>();
						userList$.setValue(Collections.emptyList());
						newLastBattles.addPropertyChangeListener(e -> {
							checku(newLastBattles, userList$);
						});
						
						LeaderBoardPane subLeaderboard = new LeaderBoardPane(userList$, me$, confPane.ennemiesLink(), leaderboardActions(),  
								newLastBattles);
						subLeaderboard.buildPane();
						JPanel tab = new JPanel(new FlowLayout());
						tab.add(subLastBattlesPane.panel());
						tab.add(subLeaderboard.panel());
						resultsTabs.add(t.getCodeName(), tab);
						Gui.this.consumeMatch(t, r, newLastBattles);	
					}
				});
				
				new Thread(() -> bench.launch(), "Batch-Run").start();
			}

		};
		return runBatch;
	}

	private void checku(ObservableValue<List<Battle>> newLastBattles, ObservableValue<List<User>> userList$) {
		List<User> collect = userList.getValue().stream().filter(u -> newLastBattles.getValue().stream().anyMatch(b -> b.getPlayers().stream().anyMatch(
				p -> (u.getCodingamer() != null 
				&& p.getUserId() == u.getCodingamer().getUserId()) || (p.getPlayerAgentId() == u.getAgentId())))).collect(Collectors.toList());
		userList$.setValue(collect);
		userList$.fire();
	}
	
	void consumeMatch(TestInput test, PlayResponse response, ObservableValue<List<Battle>> lastBattles2) {
		System.out.println("---");
		if (response.success != null) {
			Game game = new Game();
			game.setGameId(response.success.gameId);
			game.readRanks(response.success.ranks);
			game.readScores(response.success.scores);
			game.setAgents(test.agents());
			Battle b = new Battle();
			b.setGameId(response.success.gameId);
			b.setDone(true);
			
			List<Player> players = new ArrayList<>();
			for (int i = 0; i < test.agents().size(); i++) {
				Player p = new Player();
				p.setPlayerAgentId(test.getPlayers().get(i).getAgentId());
				p.setNickname(test.getPlayers().get(i).getName());
				p.setPosition(response.success.ranks.get(i));
				if (test.getPlayers().get(i).getAgentId() == -1) {
					p.setUserId(current.getValue().getCodingamer().getUserId());
					p.setNickname(test.getCodeName());
				}
				players.add(p);
			}
			players.sort(Comparator.comparing(p -> p.getPosition()));
			b.setPlayers(players);
			b.setGame(game);
			
			lastBattles2.getValue().add(b);
			lastBattles2.fire();
		}
	}

	private static MultisConfig readMultisList() {
		try {
			Gson gson = new Gson();
			ClassLoader classLoader = Gui.class.getClassLoader();
			File file = new File(classLoader.getResource("multis.json").getFile());
			FileInputStream configFileInputStream = new FileInputStream(file);
			JsonReader reader = new JsonReader(new InputStreamReader(configFileInputStream, "UTF-8"));
			return  gson.fromJson(reader, MultisConfig.class);
		} catch (JsonIOException | JsonSyntaxException | FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}	
		return null;
	}

	private static void setLAF() {
		try {
			// Set System L&F
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (UnsupportedLookAndFeelException e) {
			// handle exception
		} catch (ClassNotFoundException e) {
			// handle exception
		} catch (InstantiationException e) {
			// handle exception
		} catch (IllegalAccessException e) {
			// handle exception
		}

	}
}
