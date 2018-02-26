
package fr.egaetan.cgbench.model.tests_session;

import java.util.List;

public class Puzzle {

    private String forumPostId;
    private String chatRoom;
    private String title;
    private long previewBinaryId;
    private int id;
    private String leaderboardPublicId;
    private List<Object> hints = null;
    private String level;
    private String handle;
    private String detailsPageUrl;

    public String getForumPostId() {
        return forumPostId;
    }

    public void setForumPostId(String forumPostId) {
        this.forumPostId = forumPostId;
    }

    public String getChatRoom() {
        return chatRoom;
    }

    public void setChatRoom(String chatRoom) {
        this.chatRoom = chatRoom;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getPreviewBinaryId() {
        return previewBinaryId;
    }

    public void setPreviewBinaryId(long previewBinaryId) {
        this.previewBinaryId = previewBinaryId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLeaderboardPublicId() {
        return leaderboardPublicId;
    }

    public void setLeaderboardPublicId(String leaderboardPublicId) {
        this.leaderboardPublicId = leaderboardPublicId;
    }

    public List<Object> getHints() {
        return hints;
    }

    public void setHints(List<Object> hints) {
        this.hints = hints;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getHandle() {
        return handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public String getDetailsPageUrl() {
        return detailsPageUrl;
    }

    public void setDetailsPageUrl(String detailsPageUrl) {
        this.detailsPageUrl = detailsPageUrl;
    }

}
