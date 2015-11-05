package fi.vm.sade.koulutusinformaatio.converter;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.google.common.collect.Lists;

import fi.vm.sade.koulutusinformaatio.domain.Code;
import fi.vm.sade.koulutusinformaatio.domain.KoulutusLOS;
import fi.vm.sade.koulutusinformaatio.domain.TutkintoLOS;
import fi.vm.sade.koulutusinformaatio.domain.dto.TutkintoLOSDTO;
import fi.vm.sade.koulutusinformaatio.domain.exception.KIConversionException;

public class TutkintoLOSToDTOTest {

    @Test(expected = KIConversionException.class)
    public void testThrowsExceptionWhenPrerequisiteDoesNotMatchChildren() {
        TutkintoLOS tutkinto = new TutkintoLOS();
        KoulutusLOS child = new KoulutusLOS();
        Code code = new Code();
        code.setValue("YO");
        child.setKoulutusPrerequisite(code);
        tutkinto.setChildEducations(Lists.newArrayList(child));
        TutkintoLOSToDTO.convert(tutkinto, "fi", "fi", "fi", "PK");
    }

    @Test
    public void testConvertsChildLOS() {
        TutkintoLOS tutkinto = new TutkintoLOS();
        KoulutusLOS child = new KoulutusLOS();
        Code code = new Code();
        code.setValue("PK");
        child.setKoulutusPrerequisite(code);
        tutkinto.setChildEducations(Lists.newArrayList(child));
        TutkintoLOSDTO dto = TutkintoLOSToDTO.convert(tutkinto, "fi", "fi", "fi", "PK");
        assertEquals(1, dto.getChildren().size());
    }
}
