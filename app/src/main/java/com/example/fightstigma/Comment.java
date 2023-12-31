package com.example.fightstigma;

public class Comment {
    private String commentID, comment, userID;
    private Object timeCommented;

    public Comment() {
    }

    public Comment(String commentID, String comment, String userID, Object timeCommented) {
        this.commentID=commentID;
        this.comment = comment;
        this.userID = userID;
        this.timeCommented = timeCommented;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public Object getTimeCommented() {
        return timeCommented;
    }

    public void setTimeCommented(String timeCommented) {
        this.timeCommented = timeCommented;
    }

    public String getCommentID() {
        return commentID;
    }

    public void setCommentID(String commentID) {
        this.commentID = commentID;
    }

}
