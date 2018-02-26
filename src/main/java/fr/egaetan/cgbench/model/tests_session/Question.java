
package fr.egaetan.cgbench.model.tests_session;

import java.util.List;

public class Question {

    private int nbPlayersMin;
    private int nbPlayersMax;
    private String viewer;
    private List<String> languages = null;
    private String stubGenerator;
    private int id;
    private int initialId;
    private String type;
    private String statement;
    private int duration;
    private int userId;
    private int index;
    private String title;

    public int getNbPlayersMin() {
        return nbPlayersMin;
    }

    public void setNbPlayersMin(int nbPlayersMin) {
        this.nbPlayersMin = nbPlayersMin;
    }

    public int getNbPlayersMax() {
        return nbPlayersMax;
    }

    public void setNbPlayersMax(int nbPlayersMax) {
        this.nbPlayersMax = nbPlayersMax;
    }

    public String getViewer() {
        return viewer;
    }

    public void setViewer(String viewer) {
        this.viewer = viewer;
    }

    public List<String> getLanguages() {
        return languages;
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

    public String getStubGenerator() {
        return stubGenerator;
    }

    public void setStubGenerator(String stubGenerator) {
        this.stubGenerator = stubGenerator;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getInitialId() {
        return initialId;
    }

    public void setInitialId(int initialId) {
        this.initialId = initialId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
