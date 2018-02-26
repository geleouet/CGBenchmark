
package fr.egaetan.cgbench.model.tests_session;


public class ArenaCodinGamer {

    private int arenaId;
    private int divisionId;
    private int roomIndex;
    private int codinGamerId;
    private boolean eligibleForPromotion;

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

    public int getRoomIndex() {
        return roomIndex;
    }

    public void setRoomIndex(int roomIndex) {
        this.roomIndex = roomIndex;
    }

    public int getCodinGamerId() {
        return codinGamerId;
    }

    public void setCodinGamerId(int codinGamerId) {
        this.codinGamerId = codinGamerId;
    }

    public boolean isEligibleForPromotion() {
        return eligibleForPromotion;
    }

    public void setEligibleForPromotion(boolean eligibleForPromotion) {
        this.eligibleForPromotion = eligibleForPromotion;
    }

}
