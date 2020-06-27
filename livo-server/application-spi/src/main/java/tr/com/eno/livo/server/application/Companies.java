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

public class Companies implements Serializable, Comparable<Companies> {

    private final String appName;
    private final String companyId;
    private final int createUser;
    private final long createDate;

    @ConstructorProperties({"appName", "companyId", "createUser", "createDate"})
    public Companies(String appName, String companyId, int createUser, long createDate) {

        this.appName = appName;
        this.companyId = companyId;
        this.createUser = createUser;
        this.createDate = createDate;
        
    }

    public String getAppName() {
        return appName;
    }

    public String getCompanyId() {
        return companyId;
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

        if (obj instanceof Companies) {

            Companies otherApplication = (Companies) obj;

            return Objects.equals(this.appName, otherApplication.appName) && Objects.equals(this.companyId, otherApplication.companyId);

        } else {

            return false;
        }
    }

    @Override
    public int hashCode() {

        return Objects.hash(this.appName, this.companyId);
    }

    @Override
    public int compareTo(Companies o) {

        if (o == null) {
            throw new NullPointerException();
        }

        if (this.appName == null) {
            throw new NullPointerException();
        }

        if (this.companyId == null) {
            throw new NullPointerException();
        }

        int domainComparison = this.appName.compareTo(o.appName);
        int nameComparison = this.companyId.compareTo(o.companyId);
        
        return domainComparison == 0 ? nameComparison : domainComparison;
    }
}
