
package fr.egaetan.cgbench.model.tests_session;

import java.util.List;

public class Success {

    private Puzzle puzzle;
    private String testType;
    private CurrentQuestion currentQuestion;
    private boolean direct;
    private List<Question_> questions = null;
    private int testSessionId;
    private String testSessionHandle;
    private boolean needAccount;
    private boolean shareable;
    private int userId;

    public Puzzle getPuzzle() {
        return puzzle;
    }

    public void setPuzzle(Puzzle puzzle) {
        this.puzzle = puzzle;
    }

    public String getTestType() {
        return testType;
    }

    public void setTestType(String testType) {
        this.testType = testType;
    }

    public CurrentQuestion getCurrentQuestion() {
        return currentQuestion;
    }

    public void setCurrentQuestion(CurrentQuestion currentQuestion) {
        this.currentQuestion = currentQuestion;
    }

    public boolean isDirect() {
        return direct;
    }

    public void setDirect(boolean direct) {
        this.direct = direct;
    }

    public List<Question_> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question_> questions) {
        this.questions = questions;
    }

    public int getTestSessionId() {
        return testSessionId;
    }

    public void setTestSessionId(int testSessionId) {
        this.testSessionId = testSessionId;
    }

    public String getTestSessionHandle() {
        return testSessionHandle;
    }

    public void setTestSessionHandle(String testSessionHandle) {
        this.testSessionHandle = testSessionHandle;
    }

    public boolean isNeedAccount() {
        return needAccount;
    }

    public void setNeedAccount(boolean needAccount) {
        this.needAccount = needAccount;
    }

    public boolean isShareable() {
        return shareable;
    }

    public void setShareable(boolean shareable) {
        this.shareable = shareable;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

}
