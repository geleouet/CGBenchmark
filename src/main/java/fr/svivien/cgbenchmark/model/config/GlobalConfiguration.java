package fr.svivien.cgbenchmark.model.config;

import java.util.List;

public class GlobalConfiguration {

    private List<AccountConfiguration> accountConfigurationList;
    private List<String> seedList;
    private Integer requestCooldown;
    private Boolean randomSeed;
    private Integer playerPosition;
    private Integer minEnemiesNumber;
    private Integer maxEnemiesNumber;
    private List<CodeConfiguration> codeConfigurationList;
    private String multiName;

    public String getMultiName() {
        return multiName;
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
}
