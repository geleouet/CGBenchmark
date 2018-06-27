package fr.svivien.cgbenchmark;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;

import fr.svivien.cgbenchmark.model.config.AccountConfiguration;
import fr.svivien.cgbenchmark.model.config.CodeConfiguration;
import fr.svivien.cgbenchmark.model.config.EnemyConfiguration;
import fr.svivien.cgbenchmark.model.config.GlobalConfiguration;
import fr.svivien.cgbenchmark.model.test.ResultWrapper;
import fr.svivien.cgbenchmark.model.test.TestInput;
import fr.svivien.cgbenchmark.producerconsumer.Broker;
import fr.svivien.cgbenchmark.producerconsumer.Consumer;

public class CGBenchmark {

    private static final Log LOG = LogFactory.getLog(CGBenchmark.class);

    private GlobalConfiguration globalConfiguration = null;
    private List<Consumer> accountConsumerList = new ArrayList<>();
    private Broker testBroker = new Broker();
    private Random rnd = new Random();
    private EnemyConfiguration me = new EnemyConfiguration(-1, "[ME]");
    private AtomicBoolean pause = new AtomicBoolean(false);
    SessionLogIn loginSession = new SessionLogIn();

    public CGBenchmark(String cfgFilePath) {
        this(readConfigurationFile(cfgFilePath));
    }

    public CGBenchmark(GlobalConfiguration globalConfiguration) {
    	this.globalConfiguration = globalConfiguration;
    	
    	createAccountConsumers();
	}
    
	private void createAccountConsumers() {
		LOG.info("Registering " + globalConfiguration.getAccountConfigurationList().size() + " account(s)");
        for (AccountConfiguration accountCfg : globalConfiguration.getAccountConfigurationList()) {
            try {
                loginSession.retrieveAccountCookieAndSession(accountCfg, globalConfiguration.getMultiName());
            } catch (IllegalStateException e) {
                LOG.fatal("Error while retrieving account cookie and session", e);
                System.exit(1);
            }
            accountConsumerList.add(new Consumer(accountCfg.getAccountName(), testBroker, accountCfg.getAccountCookie(), accountCfg.getAccountIde(), globalConfiguration.getRequestCooldown(), pause));
            LOG.info("Account " + accountCfg.getAccountName() + " successfully registered");
        }
	}

	static GlobalConfiguration readConfigurationFile(String cfgFilePath) {
		try {
			GlobalConfiguration globalConfiguration = parseConfigurationFile(cfgFilePath);
            globalConfiguration.checkConfiguration();
            return globalConfiguration;
        } catch (UnsupportedEncodingException | FileNotFoundException | JsonIOException | JsonSyntaxException e) {
            LOG.fatal("Failed to parse configuration file", e);
            System.exit(1);
        } catch (IllegalArgumentException e) {
            LOG.fatal("Configuration is invalid", e);
            System.exit(1);
        }
		return null;
	}

    public void launch() {
        // Launching tests
        for (CodeConfiguration codeCfg : globalConfiguration.getCodeConfigurationList()) {

            ExecutorService threadPool = Executors.newFixedThreadPool(accountConsumerList.size(), new ThreadFactory() {
				
            	AtomicInteger counter = new AtomicInteger(0);
            	
				@Override
				public Thread newThread(Runnable r) {
					return new Thread(r, "BenchmarkConsumer-"+counter.incrementAndGet());
				}
			});

            Path p = Paths.get(codeCfg.getSourcePath());
            String codeName = p.getFileName().toString();

            // Brand new resultWrapper for this test
            ResultWrapper resultWrapper = new ResultWrapper(codeCfg);

            try {
                createTests(codeCfg);

                String logStr = "Launching " + testBroker.getTestSize() + " tests " + codeName + " against";
                for (EnemyConfiguration ec : codeCfg.getEnemies()) {
                    logStr += " " + ec.getName() + "_" + ec.getAgentId();
                }
                LOG.info(logStr);

                // Adding consumers in the thread-pool and wiring fresh new resultWrapper
                for (Consumer consumer : accountConsumerList) {
                    consumer.setResultWrapper(resultWrapper);
                    threadPool.execute(consumer);
                }

                // Unleash the executor
                threadPool.shutdown();
                threadPool.awaitTermination(5, TimeUnit.DAYS);

                LOG.info("Final results :" + resultWrapper.getWinrateDetails());

                // Complete the report with all the results and final winrate
                resultWrapper.finishReport();

                // Write report to external file
                File reportsDir = new File("./reports");
        		if (!reportsDir.exists()) {
        			reportsDir.mkdirs();
        		}
                
                String reportFileName = "./reports/" + codeName;
                for (EnemyConfiguration ec : codeCfg.getEnemies()) {
                    codeName += "-" + ec.getName() + "_" + ec.getAgentId();
                }
                reportFileName += "-" + resultWrapper.getShortFilenameWinrate() + ".txt";
                
                
                LOG.info("Writing final report to : " + reportFileName);
                try (PrintWriter out = new PrintWriter(reportFileName)) {
                    out.println(resultWrapper.getReportBuilder().toString());
                } catch (Exception e) {
                    LOG.warn("An error has occurred when writing final report", e);
                }
            } catch (IOException e) {
                LOG.error("An error has occurred while reading source for " + codeCfg.getSourcePath(), e);
            } catch (InterruptedException e) {
                LOG.error("An error has occurred within the broker/executor", e);
            }
        }

        LOG.info("No more tests. Ending.");
    }

    private void createTests(CodeConfiguration codeCfg) throws IOException, InterruptedException {
        String codeContent = new String(Files.readAllBytes(Paths.get(codeCfg.getSourcePath())));

        // Filling the broker with all the tests
        for (int replay = 0; replay < codeCfg.getNbReplays(); replay++) {
            rnd.setSeed(28731L); /** More arbitrary values ... */

            if (globalConfiguration.getRandomSeed()) {
                List<EnemyConfiguration> selectedPlayers = getRandomEnemies(codeCfg);
                int myStartingPosition = globalConfiguration.isSingleRandomStartPosition() ? rnd.nextInt(selectedPlayers.size() + 1) : globalConfiguration.getPlayerPosition();
                addTestFixedPosition(selectedPlayers, replay, null, codeContent, codeCfg.getLanguage(), myStartingPosition);
            } else {
                for (int testNumber = 0; testNumber < globalConfiguration.getSeedList().size(); testNumber++) {
                    List<EnemyConfiguration> selectedPlayers = getRandomEnemies(codeCfg);
                    String seed = SeedCleaner.cleanSeed(globalConfiguration.getSeedList().get(testNumber), globalConfiguration.getMultiName(), selectedPlayers.size() + 1);
                    if (globalConfiguration.isEveryPositionConfiguration()) {
                        addTestAllPermutations(selectedPlayers, testNumber, seed, codeContent, codeCfg.getLanguage());
                    } else {
                        int myStartingPosition = globalConfiguration.isSingleRandomStartPosition() ? rnd.nextInt(selectedPlayers.size() + 1) : globalConfiguration.getPlayerPosition();
                        addTestFixedPosition(selectedPlayers, testNumber, seed, codeContent, codeCfg.getLanguage(), myStartingPosition);
                    }
                }
            }
        }
    }

    private void addTestAllPermutations(List<EnemyConfiguration> selectedPlayers, int seedNumber, String seed, String codeContent, String lang) throws InterruptedException {
        List<EnemyConfiguration> players = selectedPlayers.stream().collect(Collectors.toList());
        players.add(me);
        List<List<EnemyConfiguration>> permutations = generatePermutations(players);
        for (List<EnemyConfiguration> permutation : permutations) {
            testBroker.queue.put(new TestInput(seedNumber, seed, codeContent, lang, permutation));
        }
    }

    private void addTestFixedPosition(List<EnemyConfiguration> selectedPlayers, int seedNumber, String seed, String codeContent, String lang, int myStartingPosition) throws InterruptedException {
        List<EnemyConfiguration> players = selectedPlayers.stream().collect(Collectors.toList());
        players.add(myStartingPosition, me);
        testBroker.queue.put(new TestInput(seedNumber, seed, codeContent, lang, players));
    }

    private List<List<EnemyConfiguration>> generatePermutations(List<EnemyConfiguration> original) {
        if (original.size() == 0) {
            List<List<EnemyConfiguration>> result = new ArrayList<>();
            result.add(new ArrayList<>());
            return result;
        }
        EnemyConfiguration firstElement = original.remove(0);
        List<List<EnemyConfiguration>> returnValue = new ArrayList<>();
        List<List<EnemyConfiguration>> permutations = generatePermutations(original);
        for (List<EnemyConfiguration> smallerPermuted : permutations) {
            for (int index = 0; index <= smallerPermuted.size(); index++) {
                List<EnemyConfiguration> temp = new ArrayList<>(smallerPermuted);
                temp.add(index, firstElement);
                returnValue.add(temp);
            }
        }
        return returnValue;
    }

    private List<EnemyConfiguration> getRandomEnemies(CodeConfiguration codeCfg) {
        List<EnemyConfiguration> selectedPlayers = codeCfg.getEnemies().stream().collect(Collectors.toList());
        Collections.shuffle(selectedPlayers, rnd);
        if (globalConfiguration.getEnemiesNumberDelta() > 0) {
            return selectedPlayers.subList(0, globalConfiguration.getMinEnemiesNumber() + rnd.nextInt(globalConfiguration.getEnemiesNumberDelta() + 1));
        } else {
            return selectedPlayers.subList(0, globalConfiguration.getMinEnemiesNumber());
        }
    }

    private static GlobalConfiguration parseConfigurationFile(String cfgFilePath) throws UnsupportedEncodingException, FileNotFoundException {
        LOG.info("Loading configuration file : " + cfgFilePath);
        Gson gson = new Gson();
        FileInputStream configFileInputStream = new FileInputStream(cfgFilePath);
        JsonReader reader = new JsonReader(new InputStreamReader(configFileInputStream, "UTF-8"));
        return gson.fromJson(reader, GlobalConfiguration.class);
    }

	public void pause() {
		this.pause.set(true);
	}

	public void resume() {
		this.pause.set(false);
	}

}
