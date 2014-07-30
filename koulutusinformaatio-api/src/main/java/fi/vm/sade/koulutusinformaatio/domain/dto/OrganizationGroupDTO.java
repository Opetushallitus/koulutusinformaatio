package fi.vm.sade.koulutusinformaatio.domain.dto;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.List;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class OrganizationGroupDTO {

    private String oid;
    private List<String> usageGroups;
    private List<String> groupTypes;

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
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
