package tr.com.eno.livo.server.users;

import java.beans.ConstructorProperties;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import tr.com.eno.livo.server.users.User;
import tr.com.eno.livo.server.users.UserGroup;

public class GroupCompanies implements Serializable, Comparable<GroupCompanies> {

    private final String groupName;
    private final Integer companyId;
    private final int createUser;
    private final long createDate;

    @ConstructorProperties({"groupName", "companyId", "createUser", "createDate"})
    public GroupCompanies(String groupName, Integer companyId, int createUser, long createDate) {

        this.groupName = groupName;
        this.companyId = companyId;
        this.createUser = createUser;
        this.createDate = createDate;
        
    }

    public String getGroupName() {
        return groupName;
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

        if (obj instanceof GroupCompanies) {

            GroupCompanies otherApplication = (GroupCompanies) obj;

            return Objects.equals(this.groupName, otherApplication.groupName) && Objects.equals(this.companyId, otherApplication.companyId);

        } else {

            return false;
        }
    }

    @Override
    public int hashCode() {

        return Objects.hash(this.groupName, this.companyId);
    }

    @Override
    public int compareTo(GroupCompanies o) {

        if (o == null) {
            throw new NullPointerException();
        }

        if (this.groupName == null) {
            throw new NullPointerException();
        }

        if (this.companyId == null) {
            throw new NullPointerException();
        }

        int groupNameComparison = this.groupName.compareTo(o.groupName);
        int nameComparison = this.companyId.compareTo(o.companyId);
        
        return groupNameComparison == 0 ? nameComparison : groupNameComparison;
    }
}
