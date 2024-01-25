package hdtx.androidsdk.data;

public class FBInfo {
    private String fbAppId;
    private String fbName;
    private String fbToken;

    public FBInfo(String fbAppId, String fbName, String fbToken) {
        this.fbAppId = fbAppId;
        this.fbName = fbName;
        this.fbToken = fbToken;
    }

    public String getFbAppId() {
        return fbAppId;
    }

    public void setFbAppId(String fbAppId) {
        this.fbAppId = fbAppId;
    }

    public String getFbName() {
        return fbName;
    }

    public void setFbName(String fbName) {
        this.fbName = fbName;
    }

    public String getFbToken() {
        return fbToken;
    }

    public void setFbToken(String fbToken) {
        this.fbToken = fbToken;
    }
}
