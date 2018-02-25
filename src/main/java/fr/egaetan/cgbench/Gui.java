package fr.egaetan.cgbench;

import java.awt.BorderLayout;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
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

import fr.egaetan.cgbench.api.LeaderboardApi;
import fr.egaetan.cgbench.model.config.MultisConfig;
import fr.egaetan.cgbench.services.SearchAgentId;
import fr.egaetan.cgbench.ui.BatchRun;
import fr.egaetan.cgbench.ui.ConfPanel;
import fr.egaetan.cgbench.ui.LeaderBoardPane;
import fr.svivien.cgbenchmark.CGBenchmark;
import fr.svivien.cgbenchmark.Constants;
import fr.svivien.cgbenchmark.model.config.GlobalConfiguration;
import okhttp3.OkHttpClient;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;

public class Gui {

    private static final Log LOG = LogFactory.getLog(Gui.class);
    ConfPanel confPane;
	MultisConfig multisConfig;
	private LeaderboardApi leaderboardApi;
	private LeaderBoardPane leaderboardPane;

	
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
	

	private void initServices() {
		OkHttpClient client = new OkHttpClient.Builder().readTimeout(600, TimeUnit.SECONDS).build();
		Retrofit retrofit = new Retrofit.Builder().client(client).baseUrl(Constants.CG_HOST).addConverterFactory(GsonConverterFactory.create()).build();
		leaderboardApi = retrofit.create(LeaderboardApi.class);
		 
		
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
		
		BatchRun runBatch = new BatchRun() {
			
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
		confPane = new ConfPanel(multisConfig, new SearchAgentId(leaderboardApi), runBatch);
		confPane.buildConfPane();
		
		leaderboardPane = new LeaderBoardPane(confPane.gameConfig(), confPane.ennemiesLink(), leaderboardApi);
		leaderboardPane.buildPane();
		
		JSplitPane splittedConfig = new JSplitPane();
		splittedConfig.setLeftComponent(confPane.confPanel());

		JSplitPane splittedLdbrd = new JSplitPane();
		splittedLdbrd.setRightComponent(leaderboardPane.panel());
		splittedLdbrd.setLeftComponent(new JLabel("Main"));
		
		splittedConfig.setRightComponent(splittedLdbrd);

		mainFrame.getContentPane().add(splittedConfig, BorderLayout.CENTER);
		mainFrame.pack();
		mainFrame.setVisible(true);;
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
        UIManager.setLookAndFeel(
            UIManager.getSystemLookAndFeelClassName());
    } 
    catch (UnsupportedLookAndFeelException e) {
       // handle exception
    }
    catch (ClassNotFoundException e) {
       // handle exception
    }
    catch (InstantiationException e) {
       // handle exception
    }
    catch (IllegalAccessException e) {
       // handle exception
    }
		
	}
}
