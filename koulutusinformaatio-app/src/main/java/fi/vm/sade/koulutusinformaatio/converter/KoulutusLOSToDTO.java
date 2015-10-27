package fi.vm.sade.koulutusinformaatio.converter;

import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

import fi.vm.sade.koulutusinformaatio.domain.ApplicationOption;
import fi.vm.sade.koulutusinformaatio.domain.ApplicationSystem;
import fi.vm.sade.koulutusinformaatio.domain.KoulutusLOS;
import fi.vm.sade.koulutusinformaatio.domain.TutkintoLOS;
import fi.vm.sade.koulutusinformaatio.domain.dto.ApplicationSystemDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.ChildLOIRefDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.KoulutusLOSDTO;
import fi.vm.sade.koulutusinformaatio.domain.dto.ParentLOSRefDTO;

public class KoulutusLOSToDTO {

    public KoulutusLOSToDTO() {

    }

    public static KoulutusLOSDTO convert(final KoulutusLOS los, final String lang, final String uiLang) {
        KoulutusLOSDTO dto = new KoulutusLOSDTO();

        dto.setId(los.getId());

        String descriptionLang = HigherEducationLOSToDTO.getDescriptionLang(lang, los.getAvailableTranslationLanguages());
        descriptionLang = (descriptionLang) == null ? lang : descriptionLang;
        dto.setTranslationLanguage(descriptionLang);
        dto.setName(ConverterUtil.getTextByLanguageUseFallbackLang(los.getName(), uiLang));
        dto.setEducationDegree(los.getEducationDegree());
        dto.setEducationDegreeName(ConverterUtil.getTextByLanguageUseFallbackLang(los.getEducationDegreeLang(), uiLang));
        dto.setDegreeTitle(ConverterUtil.getTextByLanguageUseFallbackLang(los.getDegreeTitle(), uiLang));
        dto.setDegreeTitles(ConverterUtil.getTextsByLanguageUseFallbackLang(los.getDegreeTitles(), uiLang));
        dto.setQualifications(ConverterUtil.getTextsByLanguageUseFallbackLang(los.getQualifications(), uiLang));
        dto.setGoals(ConverterUtil.getTextByLanguage(los.getGoals(), descriptionLang));
        dto.setStructure(ConverterUtil.getTextByLanguage(los.getStructure(), descriptionLang));
        dto.setAccessToFurtherStudies(ConverterUtil.getTextByLanguage(los.getAccessToFurtherStudies(), descriptionLang));

        dto.setTargetGroup(ConverterUtil.getTextByLanguage(los.getTargetGroup(), descriptionLang));
        dto.setSubjectsAndCourses(ConverterUtil.getTextByLanguage(los.getSubjectsAndCourses(), descriptionLang));
        dto.setDiplomas(ConverterUtil.getTextsByLanguage(los.getDiplomas(), lang));
        dto.setLanguageSelection(LanguageSelectionToDTO.convertAll(los.getLanguageSelection(), lang));

        dto.setProvider(ProviderToDTO.convert(los.getProvider(), uiLang, "fi", uiLang));
        dto.setAvailableTranslationLanguages(CodeToDTO.convertAll(los.getAvailableTranslationLanguages(), uiLang));
        dto.setCreditValue(los.getCreditValue());

        dto.setCreditUnit(ConverterUtil.getTextByLanguageUseFallbackLang(los.getCreditUnit(), uiLang));

        dto.setPrerequisites(CodeToDTO.convertAll(los.getPrerequisites(), uiLang));
        dto.setFormOfTeaching(ConverterUtil.getTextsByLanguageUseFallbackLang(los.getFormOfTeaching(), uiLang));
        dto.setTeachingTimes(ConverterUtil.getTextsByLanguageUseFallbackLang(los.getTeachingTimes(), uiLang));
        dto.setTeachingPlaces(ConverterUtil.getTextsByLanguageUseFallbackLang(los.getTeachingPlaces(), uiLang));
        dto.setTeachingLanguages(CodeToName.convertAll(los.getTeachingLanguages(), uiLang));
        if (los.getStartDate() != null) {
            dto.setStartDate(los.getStartDate());
        } else {
            dto.setStartYear(los.getStartYear());
            dto.setStartSeason(ConverterUtil.getTextByLanguageUseFallbackLang(los.getStartSeason(), uiLang));
        }
        dto.setStartDates(los.getStartDates());
        dto.setInternationalization(ConverterUtil.getTextByLanguage(los.getInternationalization(), descriptionLang));
        dto.setCooperation(ConverterUtil.getTextByLanguage(los.getCooperation(), descriptionLang));
        dto.setContent(ConverterUtil.getTextByLanguage(los.getContent(), descriptionLang));
        dto.setWorkingLifePlacement(ConverterUtil.getTextByLanguageUseFallbackLang(los.getWorkingLifePlacement(), descriptionLang));
        dto.setContactPersons(ContactPersonToDTO.convertAll(los.getContactPersons()));
        dto.setPlannedDuration(los.getPlannedDuration());
        dto.setPlannedDuration(los.getPlannedDuration());
        dto.setPlannedDurationUnit(ConverterUtil.getTextByLanguageUseFallbackLang(los.getPlannedDurationUnit(), uiLang));
        dto.setEducationDomain(ConverterUtil.getTextByLanguageUseFallbackLang(los.getEducationDomain(), uiLang));
        dto.setLinkToCurriculum(los.getLinkToCurriculum());

        // as based approach for UI

        if (los.getApplicationOptions() != null) {
            SetMultimap<ApplicationSystem, ApplicationOption> aoByAs = HashMultimap.create();
            for (ApplicationOption ao : los.getApplicationOptions()) {
                aoByAs.put(ao.getApplicationSystem(), ao);
            }

            for (ApplicationSystem as : aoByAs.keySet()) {
                ApplicationSystemDTO asDTO = ApplicationSystemToDTO.convert(as, uiLang);
                asDTO.setStatus(as.getStatus());
                for (ApplicationOption ao : aoByAs.get(as)) {
                    asDTO.getApplicationOptions().add(ApplicationOptionToDTO.convertHigherEducation(ao, lang, uiLang, "fi"));
                }
                dto.getApplicationSystems().add(asDTO);
            }
        }

        dto.setStatus(los.getStatus());
        if (los.getEducationCode() != null) {
            dto.setEducationCode(ConverterUtil.getTextByLanguageUseFallbackLang(los.getEducationCode().getName(), uiLang));
            dto.setKoulutuskoodi(los.getEducationCode().getUri());
        }
        if (los.getThemes() != null) {
            dto.setThemes(CodeToDTO.convertCodesDistinct(los.getThemes(), uiLang));
        }
        if (los.getTopics() != null) {
            dto.setTopics(CodeToDTO.convertAll(los.getTopics(), uiLang));
        }
        dto.setEducationType(los.getEducationType());

        if (!los.getSiblings().isEmpty()) {
            Set<ChildLOIRefDTO> siblings = new HashSet<ChildLOIRefDTO>();
            for (KoulutusLOS sibling : los.getSiblings()) {

                if (los.getKoulutusPrerequisite() != null
                        && !TutkintoLOSToDTO.isSamePrerequisite(los.getKoulutusPrerequisite().getValue(), sibling)) {
                    continue;
                }

                ChildLOIRefDTO siblingDto = new ChildLOIRefDTO();
                siblingDto.setId(sibling.getId());
                siblingDto.setName(ConverterUtil.getTextByLanguageUseFallbackLang(sibling.getName(), lang));
                siblingDto.setActive(los.getId().equals(sibling.getId()));
                siblingDto.setType(sibling.getType());
                siblings.add(siblingDto);
            }
            dto.setSiblings(siblings);
        }

        if (los.getTutkinto() != null) {
            ParentLOSRefDTO parentLos = new ParentLOSRefDTO();
            TutkintoLOS tutkintoLOS = los.getTutkinto();

            parentLos.setId(tutkintoLOS.getId());
            parentLos.setName(ConverterUtil.getTextByLanguageUseFallbackLang(tutkintoLOS.getName(), lang));
            parentLos.setType(tutkintoLOS.getType());
            dto.setParentLos(parentLos);
        }

        KoulutusLOS opintokokonaisuus = los.getOpintokokonaisuus();
        if (opintokokonaisuus != null) {
            ParentLOSRefDTO parentLos = new ParentLOSRefDTO();
            parentLos.setId(opintokokonaisuus.getId());
            parentLos.setName(ConverterUtil.getTextByLanguageUseFallbackLang(opintokokonaisuus.getName(), lang));
            parentLos.setType(opintokokonaisuus.getType());
            dto.setParentLos(parentLos);
        }

        if (!los.getOpintojaksos().isEmpty()) {
            Set<ChildLOIRefDTO> opintojaksoDtos = new HashSet<ChildLOIRefDTO>();
            for (KoulutusLOS koulutusLOS : los.getOpintojaksos()) {
                ChildLOIRefDTO opintojaksoDto = new ChildLOIRefDTO();
                opintojaksoDto.setId(koulutusLOS.getId());
                opintojaksoDto.setName(ConverterUtil.getTextByLanguageUseFallbackLang(koulutusLOS.getName(), lang));
                opintojaksoDto.setActive(los.getId().equals(koulutusLOS.getId()));
                opintojaksoDto.setType(koulutusLOS.getType());
                opintojaksoDtos.add(opintojaksoDto);
            }
            dto.setSiblings(opintojaksoDtos);
        }

        dto.setEndDate(los.getEndDate());

        dto.setOpettaja(los.getOpettaja());
        dto.setSubjects(los.getSubjects());
        dto.setVastaavaKorkeakoulu(ConverterUtil.getTextByLanguageUseFallbackLang(los.getVastaavaKorkeakoulu(), lang));

        dto.setKoulutusPrerequisite(CodeToDTO.convert(los.getKoulutusPrerequisite(), lang));

        dto.setOpinnonTyyppiUri(los.getOpinnonTyyppiUri());
        dto.setMaksullisuus(ConverterUtil.getTextByLanguageUseFallbackLang(los.getMaksullisuus(), lang));
        dto.setEdeltavatOpinnot(ConverterUtil.getTextByLanguageUseFallbackLang(los.getEdeltavatOpinnot(), lang));
        dto.setArviointi(ConverterUtil.getTextByLanguageUseFallbackLang(los.getArviointi(), lang));
        dto.setOpetuksenAikaJaPaikka(ConverterUtil.getTextByLanguageUseFallbackLang(los.getOpetuksenAikaJaPaikka(), lang));
        dto.setLisatietoja(ConverterUtil.getTextByLanguageUseFallbackLang(los.getLisatietoja(), lang));
        dto.setCompetence(ConverterUtil.getTextByLanguageUseFallbackLang(los.getCompetence(), lang));

        dto.setCharge(los.getHinta());

        return dto;
    }

}
