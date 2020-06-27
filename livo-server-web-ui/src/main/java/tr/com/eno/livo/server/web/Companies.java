package tr.com.eno.livo.server.web;

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

    private final Integer userId;
    private final Integer companyId;
    private final int createUser;
    private final long createDate;

    @ConstructorProperties({"userId", "companyId", "createUser", "createDate"})
    public Companies(Integer userId, Integer companyId, int createUser, long createDate) {

        this.userId = userId;
        this.companyId = companyId;
        this.createUser = createUser;
        this.createDate = createDate;
        
    }

    public Integer getUserId() {
        return userId;
    }

    public Integer getCompanyId() {
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

            return Objects.equals(this.userId, otherApplication.userId) && Objects.equals(this.companyId, otherApplication.companyId);

        } else {

            return false;
        }
    }

    @Override
    public int hashCode() {

        return Objects.hash(this.userId, this.companyId);
    }

    @Override
    public int compareTo(Companies o) {

        if (o == null) {
            throw new NullPointerException();
        }

        if (this.userId == null) {
            throw new NullPointerException();
        }

        if (this.companyId == null) {
            throw new NullPointerException();
        }

        int userComparison = this.userId.compareTo(o.userId);
        int nameComparison = this.companyId.compareTo(o.companyId);
        
        return userComparison == 0 ? nameComparison : userComparison;
    }
}
