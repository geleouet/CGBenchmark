
package fr.egaetan.cgbench.model.tests_session;


public class Arena {

    private ArenaCodinGamer arenaCodinGamer;
    private Division division;
    private int id;
    private League league;
    private int timeToPromotion;

    public ArenaCodinGamer getArenaCodinGamer() {
        return arenaCodinGamer;
    }

    public void setArenaCodinGamer(ArenaCodinGamer arenaCodinGamer) {
        this.arenaCodinGamer = arenaCodinGamer;
    }

    public Division getDivision() {
        return division;
    }

    public void setDivision(Division division) {
        this.division = division;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public League getLeague() {
        return league;
    }

    public void setLeague(League league) {
        this.league = league;
    }

    public int getTimeToPromotion() {
        return timeToPromotion;
    }

    public void setTimeToPromotion(int timeToPromotion) {
        this.timeToPromotion = timeToPromotion;
    }

}
