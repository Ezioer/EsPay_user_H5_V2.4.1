package hdtx.androidsdk.data;

public class FBUser {
    private String id;
    private String name;
    private String birthday;
    private String gender;
    private String profile_pic;

    public FBUser(String id, String name, String birthday, String gender, String profile_pic) {
        this.id = id;
        this.name = name;
        this.birthday = birthday;
        this.gender = gender;
        this.profile_pic = profile_pic;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getProfile_pic() {
        return profile_pic;
    }

    public void setProfile_pic(String profile_pic) {
        this.profile_pic = profile_pic;
    }
}
