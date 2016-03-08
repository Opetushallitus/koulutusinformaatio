package fi.vm.sade.koulutusinformaatio.converter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import fi.vm.sade.koulutusinformaatio.domain.HigherEducationLOSRef;
import fi.vm.sade.koulutusinformaatio.domain.I18nText;
import fi.vm.sade.koulutusinformaatio.domain.dto.HigherEducationLOSRefDTO;

/**
 * 
 * @author Markus
 */
public class HigherEducationLOSRefToDTO {
    
    private HigherEducationLOSRefToDTO() {
    }

    public static List<HigherEducationLOSRefDTO> convert(final Set<HigherEducationLOSRef> refs, final String lang) {
        List<HigherEducationLOSRefDTO> higherEdus = new ArrayList<HigherEducationLOSRefDTO>();
        if (refs != null) {
            for (HigherEducationLOSRef ref : refs) {
                HigherEducationLOSRefDTO higherEdu = convert(ref, lang);
                higherEdus.add(higherEdu);
            }
        }
        Collections.sort(higherEdus);
        return higherEdus;
    }

    public static HigherEducationLOSRefDTO convert(final HigherEducationLOSRef ref, final String lang) {
        HigherEducationLOSRefDTO higherEdu = new HigherEducationLOSRefDTO();
        higherEdu.setId(ref.getId());
        higherEdu.setPrerequisite(CodeToDTO.convert(ref.getPrerequisite(), lang));
        higherEdu.setQualifications(ConverterUtil.getTextsByLanguageUseFallbackLang(ref.getQualifications(), lang));
        higherEdu.setName(ConverterUtil.getTextByLanguageUseFallbackLang(ref.getName(), lang));
        higherEdu.setProvider(ConverterUtil.getTextByLanguageUseFallbackLang(ref.getProvider(), lang));
        if (ref.isAdultVocational()) {
            higherEdu.setFieldOfExpertise(ConverterUtil.getTextByLanguageUseFallbackLang(ref.getFieldOfExpertise(), lang));
            String eduKind = ConverterUtil.getTextByLanguageUseFallbackLang(ref.getEducationKind(), lang);
            higherEdu.setName(String.format("%s%s",  higherEdu.getName(), (eduKind != null && !eduKind.isEmpty()) ?  String.format(", %s", eduKind.toLowerCase()) : ""));
        }
        
        return higherEdu;
    }

    public static List<I18nText> convert(final List<HigherEducationLOSRef> refs) {
        if (refs != null) {
            return Lists.transform(refs, new Function<HigherEducationLOSRef, I18nText>() {
                @Override
                public I18nText apply(HigherEducationLOSRef higerEdLOSRef) {
                    return (higerEdLOSRef != null) ? higerEdLOSRef.getName() : null;
                }
            });
        } else {
            return null;
        }
    }

}
