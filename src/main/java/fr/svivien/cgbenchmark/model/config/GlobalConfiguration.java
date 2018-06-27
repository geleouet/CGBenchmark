package fr.svivien.cgbenchmark.model.config;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gson.GsonBuilder;

import fr.egaetan.cgbench.Gui;
import fr.egaetan.cgbench.ui.ConfPanel;

public class GlobalConfiguration {

	private static final Log LOG = LogFactory.getLog(Gui.class);
	
    private List<AccountConfiguration> accountConfigurationList;
    private List<String> seedList;
    private Integer requestCooldown;
    private Boolean randomSeed;
    private Integer playerPosition;
    private Integer minEnemiesNumber;
    private Integer maxEnemiesNumber;
    private List<CodeConfiguration> codeConfigurationList;
    private String multiName;
    private Boolean isContest = false;
    private boolean saveLogs;

    public void setSaveLogs(boolean saveLogs) {
        this.saveLogs = saveLogs;
    }

    public boolean isSaveLogs() {
        return saveLogs;
    }

    public String getMultiName() {
        return multiName;
    }

    public Boolean isContest() {
        return isContest;
    }

    public List<AccountConfiguration> getAccountConfigurationList() {
        return accountConfigurationList;
    }

    public List<String> getSeedList() {
        return seedList;
    }

    public void setAccountConfigurationList(List<AccountConfiguration> accountConfigurationList) {
		this.accountConfigurationList = accountConfigurationList;
	}

	public void setSeedList(List<String> seedList) {
		this.seedList = seedList;
	}

	public void setRequestCooldown(Integer requestCooldown) {
		this.requestCooldown = requestCooldown;
	}

	public void setRandomSeed(Boolean randomSeed) {
		this.randomSeed = randomSeed;
	}

	public void setPlayerPosition(Integer playerPosition) {
		this.playerPosition = playerPosition;
	}

	public void setMinEnemiesNumber(Integer minEnemiesNumber) {
		this.minEnemiesNumber = minEnemiesNumber;
	}

	public void setMaxEnemiesNumber(Integer maxEnemiesNumber) {
		this.maxEnemiesNumber = maxEnemiesNumber;
	}

	public void setCodeConfigurationList(List<CodeConfiguration> codeConfigurationList) {
		this.codeConfigurationList = codeConfigurationList;
	}

	public void setMultiName(String multiName) {
		this.multiName = multiName;
	}

	public Integer getRequestCooldown() {
        return requestCooldown;
    }

    public Boolean getRandomSeed() {
        return randomSeed;
    }

    public List<CodeConfiguration> getCodeConfigurationList() {
        return codeConfigurationList;
    }

    public Integer getPlayerPosition() {
        return playerPosition;
    }

    public boolean isEveryPositionConfiguration() {
        return playerPosition == -1;
    }

    public boolean isSingleRandomStartPosition() {
        return playerPosition == -2;
    }

    public Integer getMinEnemiesNumber() {
        return minEnemiesNumber;
    }

    public Integer getMaxEnemiesNumber() {
        return maxEnemiesNumber;
    }

    public Integer getEnemiesNumberDelta() {
        return maxEnemiesNumber - minEnemiesNumber;
    }

	public void writeConfig(String fileName) throws IOException {
		String json = new GsonBuilder().create().toJson(this);
		try (FileWriter fw = new FileWriter(fileName)) {
			fw.write(json);
			fw.flush();
		} 
	}

	public void checkConfiguration() throws IllegalArgumentException {
	    // Checks if every code file exists
	    for (CodeConfiguration codeCfg : getCodeConfigurationList()) {
	        if (codeCfg.getSourcePath() == null || !Files.isReadable(Paths.get(codeCfg.getSourcePath()))) {
	            throw new IllegalArgumentException("Cannot read " + codeCfg.getSourcePath());
	        }
	    }
	
	    // Checks write permission for final reports
	    if (!Files.isWritable(Paths.get(""))) {
	        throw new IllegalArgumentException("Cannot write in current directory");
	    }
	
	    // Checks account number
	    if (getAccountConfigurationList().isEmpty()) {
	        throw new IllegalArgumentException("You must provide at least one valid account");
	    }
	
	    // Checks that no account field is missing
	    for (AccountConfiguration accountCfg : getAccountConfigurationList()) {
	        if (accountCfg.getAccountName() == null) {
	            throw new IllegalArgumentException("You must provide account name");
	        }
	        if (accountCfg.getAccountLogin() == null || accountCfg.getAccountPassword() == null) {
	            throw new IllegalArgumentException("You must provide account login/pwd");
	        }
	    }
	
	    // Checks that there are seeds to test
	    if (!getRandomSeed() && getSeedList().isEmpty()) {
	        throw new IllegalArgumentException("You must provide some seeds or enable randomSeed");
	    }
	
	    // Checks that there is a fixed seed list when playing with every starting position configuration
	    if (getRandomSeed() && isEveryPositionConfiguration()) {
	        throw new IllegalArgumentException("Playing each seed with swapped positions requires fixed seed list");
	    }
	
	    // Checks player position
	    if (getPlayerPosition() == null || getPlayerPosition() < -2 || getPlayerPosition() > 3) {
	        throw new IllegalArgumentException("You must provide a valid player position (-1, 0 or 1)");
	    }
	}

	public void setIsContest(Boolean isContest) {
		this.isContest = isContest;
	}
}
