package de.sowrong.together.data;

public class CleaningWeekUserTask {
    private String entryId;
    private String userId;
    private String dutyId;
    private boolean finished;

    public CleaningWeekUserTask(String entryId, String userId, String dutyId, boolean finished) {
        this.entryId = entryId;
        this.userId = userId;
        this.dutyId = dutyId;
        this.finished = finished;
    }

    public String getEntryId() {
        return entryId;
    }

    public void setEntryId(String entryId) {
        this.entryId = entryId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDutyId() {
        return dutyId;
    }

    public void setDutyId(String dutyId) {
        this.dutyId = dutyId;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }
}
