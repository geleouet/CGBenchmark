
package fr.egaetan.cgbench.model.tests_session;


public class League {

    private int divisionIndex;
    private int divisionCount;
    private int openingLeaguesCount;

    public int getDivisionIndex() {
        return divisionIndex;
    }

    public void setDivisionIndex(int divisionIndex) {
        this.divisionIndex = divisionIndex;
    }

    public int getDivisionCount() {
        return divisionCount;
    }

    public void setDivisionCount(int divisionCount) {
        this.divisionCount = divisionCount;
    }

    public int getOpeningLeaguesCount() {
        return openingLeaguesCount;
    }

    public void setOpeningLeaguesCount(int openingLeaguesCount) {
        this.openingLeaguesCount = openingLeaguesCount;
    }

    public String getDivisionName() {
    	switch (divisionIndex) {
    	case 5:
    		return "Legend";
    	case 4:
    		return "Gold";
    	case 3:
    		return "Silver";
    	case 2:
    		return "Bronze";
    	case 1:
    		return "Wood";
    	}
    	return "Wood";
    }

}
