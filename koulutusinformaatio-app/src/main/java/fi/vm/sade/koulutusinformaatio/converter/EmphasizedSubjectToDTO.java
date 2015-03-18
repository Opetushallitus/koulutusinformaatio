package fi.vm.sade.koulutusinformaatio.converter;

import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import fi.vm.sade.koulutusinformaatio.domain.EmphasizedSubject;
import fi.vm.sade.koulutusinformaatio.domain.dto.EmphasizedSubjectDTO;

/**
 * @author Hannu Lyytikainen
 */
public final class EmphasizedSubjectToDTO {

    private EmphasizedSubjectToDTO() {
    }

    public static EmphasizedSubjectDTO convert(EmphasizedSubject emphasizedSubject, String lang) {
        if (emphasizedSubject != null) {
            EmphasizedSubjectDTO dto = new EmphasizedSubjectDTO();
            dto.setValue(emphasizedSubject.getValue());
            dto.setSubject(ConverterUtil.getTextByLanguage(emphasizedSubject.getSubject(), lang));
            return dto;
        }
        return null;
    }

    public static List<EmphasizedSubjectDTO> convertAll(final List<EmphasizedSubject> emphasizedSubjects, final String lang) {
        if (emphasizedSubjects != null) {
            return Lists.transform(emphasizedSubjects, new Function<EmphasizedSubject, EmphasizedSubjectDTO>() {
                @Override
                public EmphasizedSubjectDTO apply(EmphasizedSubject input) {
                    return convert(input, lang);
                }
            });
        }
        return null;
    }
}