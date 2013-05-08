package fi.vm.sade.koulutusinformaatio.service.impl;

import fi.vm.sade.koulutusinformaatio.domain.I18nText;
import fi.vm.sade.koulutusinformaatio.domain.ParentLOS;
import fi.vm.sade.koulutusinformaatio.service.TarjontaService;
import fi.vm.sade.tarjonta.service.resources.dto.KomoDTO;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author Hannu Lyytikainen
 */
public class ParentLOSBuilder {

    private TarjontaService tarjontaService;

    @Autowired
    public ParentLOSBuilder(TarjontaService tarjontaService) {
        this.tarjontaService = tarjontaService;
    }

    public ParentLOS buildParentLOS(KomoDTO komoDTO) {
        ParentLOS parentLOS = new ParentLOS();
        parentLOS.setId(komoDTO.getOid());
        parentLOS.setName(new I18nText(komoDTO.getNimi()));
        List<String> childLosIds = komoDTO.getAlaModuulit();
        for (String childLosId : childLosIds) {
            

        }


        return parentLOS;
    }
}
