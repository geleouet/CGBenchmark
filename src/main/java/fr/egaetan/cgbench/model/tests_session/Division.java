
package fr.egaetan.cgbench.model.tests_session;


public class Division {

    private int arenaId;
    private int divisionId;
    private int arenabossId;
    private long creationTime;
    private long lastPromotionTime;
    private int promotionInterval;
    private int divisionIndex;
    private boolean sameRules;
    private boolean resetCode;
    private Arenaboss arenaboss;

    public int getArenaId() {
        return arenaId;
    }

    public void setArenaId(int arenaId) {
        this.arenaId = arenaId;
    }

    public int getDivisionId() {
        return divisionId;
    }

    public void setDivisionId(int divisionId) {
        this.divisionId = divisionId;
    }

    public int getArenabossId() {
        return arenabossId;
    }

    public void setArenabossId(int arenabossId) {
        this.arenabossId = arenabossId;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    public long getLastPromotionTime() {
        return lastPromotionTime;
    }

    public void setLastPromotionTime(long lastPromotionTime) {
        this.lastPromotionTime = lastPromotionTime;
    }

    public int getPromotionInterval() {
        return promotionInterval;
    }

    public void setPromotionInterval(int promotionInterval) {
        this.promotionInterval = promotionInterval;
    }

    public int getDivisionIndex() {
        return divisionIndex;
    }

    public void setDivisionIndex(int divisionIndex) {
        this.divisionIndex = divisionIndex;
    }

    public boolean isSameRules() {
        return sameRules;
    }

    public void setSameRules(boolean sameRules) {
        this.sameRules = sameRules;
    }

    public boolean isResetCode() {
        return resetCode;
    }

    public void setResetCode(boolean resetCode) {
        this.resetCode = resetCode;
    }

    public Arenaboss getArenaboss() {
        return arenaboss;
    }

    public void setArenaboss(Arenaboss arenaboss) {
        this.arenaboss = arenaboss;
    }

}
