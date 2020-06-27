package tr.com.eno.livo.server.application;

import java.beans.ConstructorProperties;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import tr.com.eno.livo.server.users.User;
import tr.com.eno.livo.server.users.UserGroup;

public class IconsSplashes implements Serializable, Comparable<IconsSplashes> {

    private final String appName;
    private final String imgId;
    private final String platform;
    private final String fileName;
    private final int createUser;
    private final long createDate;

    @ConstructorProperties({"appName", "imgId", "platform", "fileName", "createUser", "createDate"})
    public IconsSplashes(String appName, String imgId, String platform, String fileName, int createUser, long createDate) {

        this.appName = appName;
        this.imgId = imgId;
        this.platform = platform;
        this.fileName = fileName;
        this.createUser = createUser;
        this.createDate = createDate;
        
    }

    public String getAppName() {
        return appName;
    }

    public String getImgId() {
        return imgId;
    }

    public String getPlatform() {
        return platform;
    }

    public String getFileName() {
        return fileName;
    }

    public int getCreateUser() {
        return createUser;
    }

    public long getCreateDate() {
        return createDate;
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == null) {
            throw new NullPointerException();
        }

        if (obj instanceof IconsSplashes) {

            IconsSplashes otherApplication = (IconsSplashes) obj;

            return Objects.equals(this.appName, otherApplication.appName) && Objects.equals(this.imgId, otherApplication.imgId);

        } else {

            return false;
        }
    }

    @Override
    public int hashCode() {

        return Objects.hash(this.appName, this.imgId);
    }

    @Override
    public int compareTo(IconsSplashes o) {

        if (o == null) {
            throw new NullPointerException();
        }

        if (this.appName == null) {
            throw new NullPointerException();
        }

        if (this.imgId == null) {
            throw new NullPointerException();
        }

        int appNameComparison = this.appName.compareTo(o.appName);
        int nameComparison = this.imgId.compareTo(o.imgId);
        
        return appNameComparison == 0 ? nameComparison : appNameComparison;
    }
}
