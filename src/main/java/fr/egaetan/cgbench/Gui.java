package fr.egaetan.cgbench;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;

import fr.egaetan.cgbench.api.LastBattlesApi;
import fr.egaetan.cgbench.api.LeaderboardApi;
import fr.egaetan.cgbench.api.LeaderboardLeagueApi;
import fr.egaetan.cgbench.api.TestSessionApi;
import fr.egaetan.cgbench.model.config.GameConfig;
import fr.egaetan.cgbench.model.config.MultisConfig;
import fr.egaetan.cgbench.model.leaderboard.Leaderboard;
import fr.egaetan.cgbench.model.leaderboard.SuccessLeaderboard;
import fr.egaetan.cgbench.model.leaderboard.User;
import fr.egaetan.cgbench.services.LastBattlesLoader;
import fr.egaetan.cgbench.services.LastBattlesService;
import fr.egaetan.cgbench.services.LeaderBoardLoader;
import fr.egaetan.cgbench.services.LeaderBoardLoaderService;
import fr.egaetan.cgbench.services.SearchAgentId;
import fr.egaetan.cgbench.ui.BatchRun;
import fr.egaetan.cgbench.ui.ConfPanel;
import fr.egaetan.cgbench.ui.LeaderBoardPane;
import fr.egaetan.cgbench.ui.ObservableValue;
import fr.svivien.cgbenchmark.CGBenchmark;
import fr.svivien.cgbenchmark.Constants;
import fr.svivien.cgbenchmark.model.config.AccountConfiguration;
import fr.svivien.cgbenchmark.model.config.GlobalConfiguration;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.GsonConverterFactory;
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
	private LeaderboardApi leaderboardApi;
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
	
	public Gui() {
		currentGame = new ObservableValue<>();
		currentLogin = new ObservableValue<>();
		current = new ObservableValue<>();
		userList = new ObservableValue<>();
		
		initListeners();
	}

	private void initListeners() {
		scheduler = Executors.newScheduledThreadPool(1);
		currentLogin.addPropertyChangeListener(new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
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
				scheduler.scheduleAtFixedRate(() -> this.propertyChange(evt), 60, 60, TimeUnit.SECONDS);
			}
		});
		userList.addPropertyChangeListener(new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				lastBattlesService.lastBattles();
			}
		});
	}
	
	
	private void initServices() {
		HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
		interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
		OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).readTimeout(600, TimeUnit.SECONDS).build();
		
		//OkHttpClient client = new OkHttpClient.Builder().readTimeout(600, TimeUnit.SECONDS).build();
		Retrofit retrofit = new Retrofit.Builder().client(client).baseUrl(Constants.CG_HOST).addConverterFactory(GsonConverterFactory.create()).build();
		leaderboardApi = retrofit.create(LeaderboardApi.class);
		leaderboardLeagueApi = retrofit.create(LeaderboardLeagueApi.class);
		testSessionApi = retrofit.create(TestSessionApi.class);
		LastBattlesApi testBattlesApi = retrofit.create(LastBattlesApi.class);

		leaderboardService = new LeaderBoardLoaderService(currentGame, currentLogin, leaderboardLeagueApi, testSessionApi);
		lastBattlesService = new LastBattlesService(currentGame, currentLogin, testBattlesApi);
	}
	
	private void load(String string) {
		//confPane.loadConfig(string);
	}

	public void readRessources() {
		multisConfig = readMultisList();
	}

	
	private void buildGui() {
		JFrame mainFrame = new JFrame("CGBenchmark");
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		confPane = new ConfPanel(multisConfig, new SearchAgentId(leaderboardApi), batchRunner(mainFrame), currentGame, currentLogin);
		confPane.buildConfPane();
		
		leaderboardPane = new LeaderBoardPane(userList, current, confPane.ennemiesLink(), lastBattlesService.lastBattles());
		leaderboardPane.buildPane();
		
		JSplitPane splittedConfig = new JSplitPane();
		splittedConfig.setLeftComponent(confPane.confPanel());

		JSplitPane splittedLdbrd = new JSplitPane();
		splittedLdbrd.setRightComponent(leaderboardPane.panel());
		splittedLdbrd.setLeftComponent(new JLabel("Main"));
		
		splittedConfig.setRightComponent(splittedLdbrd);

		mainFrame.getContentPane().add(splittedConfig, BorderLayout.CENTER);
		mainFrame.pack();
		mainFrame.setVisible(true);
	}

	private BatchRun batchRunner(JFrame mainFrame) {
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
				CGBenchmark bench = new CGBenchmark(config);
				new Thread(() -> bench.launch(), "Batch-Run").start();
			}
		};
		return runBatch;
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
