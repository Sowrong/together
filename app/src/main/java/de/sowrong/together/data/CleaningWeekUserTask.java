package de.sowrong.together.data;

public class CleaningWeekUserTask implements Comparable<CleaningWeekUserTask> {
    private String userId;
    private String dutyId;
    private boolean finished;

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

    @Override
    public int compareTo(CleaningWeekUserTask other) {
        User ownUser = Users.getInstance().getUserById(this.getUserId());
        User otherUser = Users.getInstance().getUserById(other.getUserId());

        if (ownUser != null && otherUser != null) {
            return ownUser.getName().compareTo(otherUser.getName());
        } else {
            return 0;
        }
    }
}
