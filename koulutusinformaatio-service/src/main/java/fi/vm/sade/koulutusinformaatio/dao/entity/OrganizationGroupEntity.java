package fi.vm.sade.koulutusinformaatio.dao.entity;

import java.util.List;

import org.mongodb.morphia.annotations.Embedded;

@Embedded
public class OrganizationGroupEntity {

    private String oid;
    private Integer prioriteetti;
    private List<String> usageGroups;
    private List<String> groupTypes;

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public Integer getPrioriteetti() {
        return prioriteetti;
    }

    public void setPrioriteetti(Integer prioriteetti) {
        this.prioriteetti = prioriteetti;
    }

    public List<String> getUsageGroups() {
        return usageGroups;
    }

    public void setUsageGroups(List<String> usageGroups) {
        this.usageGroups = usageGroups;
    }

    public List<String> getGroupTypes() {
        return groupTypes;
    }

    public void setGroupTypes(List<String> groupTypes) {
        this.groupTypes = groupTypes;
    }
}
