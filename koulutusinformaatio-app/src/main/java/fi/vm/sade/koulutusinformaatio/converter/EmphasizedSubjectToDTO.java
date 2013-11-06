package fi.vm.sade.koulutusinformaatio.converter;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import fi.vm.sade.koulutusinformaatio.domain.EmphasizedSubject;
import fi.vm.sade.koulutusinformaatio.domain.dto.EmphasizedSubjectDTO;

import java.util.List;

/**
 * @author Hannu Lyytikainen
 */
public class EmphasizedSubjectToDTO {

    public static EmphasizedSubjectDTO convert(EmphasizedSubject emphasizedSubject, String lang) {
        EmphasizedSubjectDTO dto = new EmphasizedSubjectDTO();
        dto.setValue(emphasizedSubject.getValue());
        dto.setSubject(ConverterUtil.getTextByLanguage(emphasizedSubject.getSubject(), lang));
        return dto;
    }

    public static List<EmphasizedSubjectDTO> convertAll(final List<EmphasizedSubject> emphasizedSubjects, final String lang) {
        return Lists.transform(emphasizedSubjects, new Function<EmphasizedSubject, EmphasizedSubjectDTO>() {
            @Override
            public EmphasizedSubjectDTO apply(EmphasizedSubject input) {
                return convert(input, lang);
            }
        });
    }
}