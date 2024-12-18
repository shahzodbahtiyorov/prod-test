package org.finance.data.model.register;

public class JwtToken {
    private boolean status;
    private boolean created;
    private String refresh;
    private String access;

    public JwtToken(boolean status, boolean created, String refresh, String access) {
        this.status = status;
        this.created = created;
        this.refresh = refresh;
        this.access = access;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public boolean isCreated() {
        return created;
    }

    public void setCreated(boolean created) {
        this.created = created;
    }

    public String getRefresh() {
        return refresh;
    }

    public void setRefresh(String refresh) {
        this.refresh = refresh;
    }

    public String getAccess() {
        return access;
    }

    public void setAccess(String access) {
        this.access = access;
    }
}
