package org.singledog.global.session;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

public class WrappedSessionMeta implements Serializable {
    @JsonIgnore
    private static final long serialVersionUID = 1389311435501928243L;
    private long creationTime = System.currentTimeMillis();
    private String id;
    private long lastAccessTime = creationTime;
    private int maxInactiveInterval;
    private boolean isNew = true;

    public long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getLastAccessTime() {
        return lastAccessTime;
    }

    public void setLastAccessTime(long lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
    }

    public int getMaxInactiveInterval() {
        return maxInactiveInterval;
    }

    public void setMaxInactiveInterval(int maxInactiveInterval) {
        this.maxInactiveInterval = maxInactiveInterval;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean isNew) {
        this.isNew = isNew;
    }
}

