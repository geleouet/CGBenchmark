
package fr.egaetan.cgbench.model.tests_session;


public class CurrentQuestion {

    private boolean hasActivityInCurrentDivision;
    private Arena arena;
    private boolean hasAgent;
    private Question question;
    private Answer answer;
    private int lastSubmissionId;

    public boolean isHasActivityInCurrentDivision() {
        return hasActivityInCurrentDivision;
    }

    public void setHasActivityInCurrentDivision(boolean hasActivityInCurrentDivision) {
        this.hasActivityInCurrentDivision = hasActivityInCurrentDivision;
    }

    public Arena getArena() {
        return arena;
    }

    public void setArena(Arena arena) {
        this.arena = arena;
    }

    public boolean isHasAgent() {
        return hasAgent;
    }

    public void setHasAgent(boolean hasAgent) {
        this.hasAgent = hasAgent;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public Answer getAnswer() {
        return answer;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
    }

    public int getLastSubmissionId() {
        return lastSubmissionId;
    }

    public void setLastSubmissionId(int lastSubmissionId) {
        this.lastSubmissionId = lastSubmissionId;
    }

}
