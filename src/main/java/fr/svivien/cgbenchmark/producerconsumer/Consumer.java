package fr.svivien.cgbenchmark.producerconsumer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.svivien.cgbenchmark.Constants;
import fr.svivien.cgbenchmark.api.CGPlayApi;
import fr.svivien.cgbenchmark.model.request.play.PlayRequest;
import fr.svivien.cgbenchmark.model.request.play.PlayResponse;
import fr.svivien.cgbenchmark.model.request.play.PlayResponse.Frame;
import fr.svivien.cgbenchmark.model.test.ResultWrapper;
import fr.svivien.cgbenchmark.model.test.TestInput;
import fr.svivien.cgbenchmark.model.test.TestOutput;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;

/**
 * Consumes tests in the broker, runs them against CG API and stores the results in synchronized collection
 */
public class Consumer implements Runnable {

    private static final Log LOG = LogFactory.getLog(Consumer.class);

    private String name;
    private Broker broker;
    private OkHttpClient client;
    private Retrofit retrofit;
    private CGPlayApi cgPlayApi;
    private ResultWrapper resultWrapper;
    private String cookie;
    private String ide;
    private int cooldown;
    private long globalStartTime = 0;
    private long totalTestNumber = 0;
    private long totalPauseDuration = 0;
    private final PlayResultListener listener;
    private static final String outputFormat = "[ %10s ]%s";

    private AtomicBoolean pause;

    public Consumer(String name, Broker broker, String cookie, String ide, int cooldown, AtomicBoolean pause, PlayResultListener listener) {
        this.cookie = cookie;
        this.ide = ide;
        this.name = name;
        this.broker = broker;
        this.pause = pause;
        this.client = new OkHttpClient.Builder().readTimeout(600, TimeUnit.SECONDS).build();
        this.retrofit = new Retrofit.Builder().client(client).baseUrl(Constants.CG_HOST).addConverterFactory(GsonConverterFactory.create()).build();
        this.cgPlayApi = retrofit.create(CGPlayApi.class);
        this.cooldown = cooldown;
        this.listener = listener;
    }

    @Override
    public void run() {
        try {
            globalStartTime = System.currentTimeMillis();
            while (true) {
                // Retrieves next test in the broker
                TestInput test = broker.getNextTest();
                long tryStart = System.currentTimeMillis();

                // No more tests in the broker
                if (test == null) break;

                for (int tries = 0; tries < 20; tries++) { /** Arbitrary value .. */
                    tryStart = System.currentTimeMillis();
                    TestOutput result = testCode(cgPlayApi, test);
                    LOG.info(String.format(outputFormat, this.name, result.getResultString()));
                    if (!result.isError()) {
                        totalTestNumber++;
                        resultWrapper.addTestResult(result);
                        break;
                    } else {
                        // Error occurred, waiting before retrying again
                        Thread.sleep(tries < 10 ? 20000 : 40000); /** More arbitrary values .. */
                    }
                }

                if (broker.getTestSize() > 0) {
                    shouldPause();

                    // The cooldown is applied on the start-time of each test, and not on the end-time of previous test
                    Thread.sleep(Math.max(100, cooldown * 1000 - (System.currentTimeMillis() - tryStart)));

                    shouldPause();
                }
            }
            LOG.info("Consumer " + this.name + " finished its job.");
        } catch (InterruptedException ex) {
            LOG.fatal("Consumer " + name + " has encountered an issue.", ex);
        }
    }

    public void resetDurationStats() {
        totalTestNumber = 0;
        totalPauseDuration = 0;
    }

    public double getMeanTestDuration() {
        if (totalTestNumber == 0) return -1;
        return ((double) ((System.currentTimeMillis() - globalStartTime) - totalPauseDuration) / (double) totalTestNumber);
    }

    private void shouldPause() {
        if (pause.get()) {

            long pauseStart = System.currentTimeMillis();

            LOG.info(String.format(outputFormat, this.name, " -- PAUSED --"));
            while (pause.get()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    LOG.fatal("Consumer " + name + " has encountered an issue while resuming from pause", ex);
                }
            }

            totalPauseDuration += (System.currentTimeMillis() - pauseStart);
        }
    }

    private TestOutput testCode(CGPlayApi cgPlayApi, TestInput test) {
        PlayRequest request = new PlayRequest(test.getCode(), test.getLang(), ide, test.getSeed(), test.getPlayers());
        Call<PlayResponse> call = cgPlayApi.play(request, Constants.CG_HOST + "/ide/" + ide, cookie);
        try {
            PlayResponse playResponse = call.execute().body();
            listener.consume(test, playResponse);
            TestOutput testOutput = new TestOutput(test, playResponse);
			return testOutput;
        } catch (IOException | RuntimeException e) {
        	e.printStackTrace();
            TestOutput to = new TestOutput(test, null);
            return to;
        }
    }


    ////     DUMMY for test purpose
    //    private TestOutput testCode(CGPlayApi cgPlayApi, TestInput test) {
    //        PlayResponse resp = new PlayResponse();
    //        resp.success = resp.new PlayResponseSuccess();
    //        resp.success.gameId = (long) (297629806 + Math.random() * 702370193);
    //        resp.success.frames = new ArrayList<>();
    //        resp.success.scores = new ArrayList<>();
    //        for (int i = 0; i < test.getPlayers().size(); i++) {
    //            resp.success.scores.add((int) (Math.random() * 10));
    //        }
    //        if (saveLogs) dumpLogForPlay(test, resp);
    //        return new TestOutput(test, resp);
    //    }

    public void setResultWrapper(ResultWrapper resultWrapper) {
        this.resultWrapper = resultWrapper;
    }
}
