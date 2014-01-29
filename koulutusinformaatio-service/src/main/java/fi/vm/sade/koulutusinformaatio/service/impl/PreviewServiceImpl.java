package fi.vm.sade.koulutusinformaatio.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fi.vm.sade.koulutusinformaatio.domain.UniversityAppliedScienceLOS;
import fi.vm.sade.koulutusinformaatio.domain.exception.KoodistoException;
import fi.vm.sade.koulutusinformaatio.domain.exception.ResourceNotFoundException;
import fi.vm.sade.koulutusinformaatio.domain.exception.TarjontaParseException;
import fi.vm.sade.koulutusinformaatio.service.PreviewService;
import fi.vm.sade.koulutusinformaatio.service.TarjontaService;

@Service
public class PreviewServiceImpl implements PreviewService {
	
	private TarjontaService tarjontaService;
	
	@Autowired
	public PreviewServiceImpl (TarjontaService tarjontaService) {
		this.tarjontaService = tarjontaService;
	}

	@Override
	public UniversityAppliedScienceLOS previewHigherEducationLearningOpportunity(
			String oid) throws ResourceNotFoundException {
		try {
			return this.tarjontaService.findHigherEducationLearningOpportunity(oid);
		} catch (TarjontaParseException e) {
			e.printStackTrace();
			throw new ResourceNotFoundException("Resource: " + oid + " not found");
		} catch (KoodistoException e) {
			e.printStackTrace();
			throw new ResourceNotFoundException("Resource: " + oid + " not found");
		}
	}

}
