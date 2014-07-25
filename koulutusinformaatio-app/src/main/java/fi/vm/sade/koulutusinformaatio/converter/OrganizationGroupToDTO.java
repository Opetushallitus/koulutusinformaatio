package fi.vm.sade.koulutusinformaatio.converter;

import fi.vm.sade.koulutusinformaatio.domain.OrganizationGroup;
import fi.vm.sade.koulutusinformaatio.domain.dto.OrganizationGroupDTO;

import java.util.ArrayList;
import java.util.List;

public class OrganizationGroupToDTO {

    private OrganizationGroupToDTO() {
    }

    public static OrganizationGroupDTO convert(OrganizationGroup organizationGroup) {
        OrganizationGroupDTO dto = new OrganizationGroupDTO();
        dto.setOid(organizationGroup.getOid());
        dto.setUsageGroups(organizationGroup.getUsageGroups());
        dto.setGroupTypes(organizationGroup.getGroupTypes());
        return dto;
    }

    public static List<OrganizationGroupDTO> convertAll(List<OrganizationGroup> organizationGroups) {
        List<OrganizationGroupDTO> dtos = new ArrayList<OrganizationGroupDTO>();
        if (organizationGroups != null) {
            for (OrganizationGroup group : organizationGroups) {
                dtos.add(convert(group));
            }
        }
        return dtos;
    }
}
