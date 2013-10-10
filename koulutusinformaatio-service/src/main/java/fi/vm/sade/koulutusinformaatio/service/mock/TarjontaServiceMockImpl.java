package fi.vm.sade.koulutusinformaatio.service.mock;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import fi.vm.sade.koulutusinformaatio.domain.*;
import fi.vm.sade.koulutusinformaatio.domain.exception.KoodistoException;
import fi.vm.sade.koulutusinformaatio.domain.exception.TarjontaParseException;
import fi.vm.sade.koulutusinformaatio.service.TarjontaService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author Hannu Lyytikainen
 */
@Service
@Profile("dev")
public class TarjontaServiceMockImpl implements TarjontaService {

    private static final String PARENT_KOMO_OID = "1.2.3.4.5";

    private static final String PARENT_LOS_ID_1 = "11.22.33.44.55";
    private static final String PARENT_LOS_ID_2 = "211.22.33.44.55";

    private static final String PARENT_LOI_ID_1 = "111.222.333.444.555";
    private static final String PARENT_LOI_ID_2 = "2111.222.333.444.555";

    private static final String CHILD_LOS_ID_1 = "1111.2222.3333.4444.5555";
    private static final String CHILD_LOS_ID_2 = "21111.2222.3333.4444.5555";

    private static final String CHILD_LOI_ID_1 = "1111.2222.3333.4444.66666";
    private static final String CHILD_LOI_ID_2 = "21111.2222.3333.4444.66666";

    private static final String APPLICATION_SYSTEM_ID = "11111.22222.33333.44444.55555";

    private static final String APPLICATION_OPTION_ID_1 = "111111.222222.333333.444444.555555";
    private static final String APPLICATION_OPTION_ID_2 = "2111111.222222.333333.444444.555555";

    private static final String PROVIDER_ID = "1111111.2222222.3333333.4444444.5555555";

    @Override
    public List<ParentLOS> findParentLearningOpportunity(String oid) throws TarjontaParseException, KoodistoException {

        Provider provider = createProvider();
        ApplicationSystem as = createApplicationSystem(APPLICATION_SYSTEM_ID);

        I18nText parent1Name = createI18nText("Audiovisuaalisen viestinnän perustutkinto");
        ParentLOSRef parent1Ref = createParentLORef(PARENT_LOS_ID_1, parent1Name);
        ChildLOIRef childLOIRef1 = createChildLORef(CHILD_LOI_ID_1, CHILD_LOS_ID_1, APPLICATION_SYSTEM_ID);
        ApplicationOption ao1 = createApplicationOption(APPLICATION_OPTION_ID_1,
                createI18nText("Audiovisuaalisen viestinnän perustutkinto, pk"), as, Lists.newArrayList(childLOIRef1) ,parent1Ref);
        ChildLOI childLOI1 = createChildLOI(CHILD_LOI_ID_1, Lists.newArrayList(ao1));
        ChildLOS childLOS1 = createChildLOS(CHILD_LOS_ID_1,Lists.newArrayList(childLOI1));
        ParentLOI parentLOI1 = createParentLOI(PARENT_LOI_ID_1, Lists.newArrayList(childLOIRef1), Sets.newHashSet(ao1));
        ParentLOS parent1 = createParentLOS(PARENT_LOI_ID_1, parent1Name, Lists.newArrayList(parentLOI1), provider, Lists.newArrayList(childLOS1));

        I18nText parent2Name = createI18nText("Audiovisuaalisen viestinnän perustutkinto tutkinto 2");
        ParentLOSRef parent2Ref = createParentLORef(PARENT_LOS_ID_2, parent2Name);
        ChildLOIRef childLOIRef2 = createChildLORef(CHILD_LOI_ID_2, CHILD_LOS_ID_2, APPLICATION_SYSTEM_ID);
        ApplicationOption ao2 = createApplicationOption(
                APPLICATION_OPTION_ID_2, createI18nText("Audiovisuaalisen viestinnän perustutkinto2, pk"), as, Lists.newArrayList(childLOIRef2), parent2Ref);
        ChildLOI childLOI2 = createChildLOI(CHILD_LOI_ID_2, Lists.newArrayList(ao2));
        ChildLOS childLOS2 = createChildLOS(CHILD_LOS_ID_2,Lists.newArrayList(childLOI2));
        ParentLOI parentLOI2 = createParentLOI(PARENT_LOI_ID_2, Lists.newArrayList(childLOIRef2), Sets.newHashSet(ao2));
        ParentLOS parent2 = createParentLOS(PARENT_LOI_ID_2, parent2Name, Lists.newArrayList(parentLOI2), provider, Lists.newArrayList(childLOS2));

        return Lists.newArrayList(parent1, parent2);
    }

    @Override
    public List<String> listParentLearnignOpportunityOids() {
        return Lists.newArrayList(PARENT_KOMO_OID);
    }

    @Override
    public List<String> listParentLearnignOpportunityOids(int count, int startIndex) {
        return listParentLearnignOpportunityOids();
    }

    private ParentLOS createParentLOS(String id, I18nText name, List<ParentLOI> lois, Provider provider, List<ChildLOS> children) {
        ParentLOS parentLOS = new ParentLOS();
        parentLOS.setId(id);
        parentLOS.setName(name);
        parentLOS.setLois(lois);
        parentLOS.setProvider(provider);
        parentLOS.setStructure(createI18nText(
                "<p>Tutkinnon kaikille pakollinen tutkinnonosa on audiovisuaalinen tuotanto (30 ov). Lisäksi suoritetaan valinnaisia osia, jotka tukevat ammatillista suuntautumista.</p>  <p>Ammatillisten tutkinnon osien laajuus on yhteensä 90 opintoviikkoa. (Näyttötutkinnossa tutkinnon osilla ei ole laajuuksia.) Ammatillisessa peruskoulutuksessa (opetussuunnitelmaperusteisessa) opiskellaan lisäksi 20 ov ammattitaitoa täydentäviä ja 10 ov vapaasti valittavia tutkinnon osia.</p>"));
        parentLOS.setAccessToFurtherStudies(createI18nText("<p>Ammatillisista perustutkinnoista sekä ammatti- ja erikoisammattitutkinnoista saa yleisen jatko-opintokelpoisuuden ammattikorkeakouluihin ja yliopistoihin. Luonteva jatko-opintoväylä esim. ammatillisen perustutkinnon suorittaneilla on artenomin, muotoilijan tai konservaattorin ammattikorkeakoulututkinnot. Aalto-yliopiston Taideteollisessa korkeakoulussa tai Lapin yliopistossa voi suorittaa esimerkiksi taiteen kandidaatin tai taiteen maisterin tutkinnot. Ammatillisen opettajan pedagogiset opinnot antavat jatkokoulutusmahdollisuuden ammatillisen opettajan työtehtäviin. Tekniikan kandidaatin, diplomi-insinöörin tai arkkitehdin tutkintoja voi suorittaa teknillisissä korkeakouluissa ja yliopistoissa.</p>"));
        parentLOS.setGoals(createI18nText("<p>Tutkinnon suorittanut voi työskennellä erilaisissa audiovisuaalisen alan yrityksissä, joita ovat muun muassa kustannus-, lehti-, peli- ja ohjelmistotalot, viestintä- ja mainostoimistot, äänistudiot, valokuvaamot, elokuva-, video- ja animaatiotuotannot, radio- ja televisioyhtiöt, teatteri sekä kuttuuri- ja tapahtumatuotannot. Ammattinimikkeinä ovat kamera-assistentti, ääniassistentti, valoassistentti, fotoassistentti ja AD-assistentti.</p>   <p>Tutkinnon suorittaja tuntee audiovisuaalisen alan työprosessin työvaiheet esituotannosta jälkituotantoon. Hänellä on audiovisuaalisen alan perusvalmiudet, jonka tueksi voi hankkia erityis-  ja ammattiosaamista monilta viestinnän aloilta. Hän hallitsee tekniikkaa ja tietotekniikkaa, mutta hänellä on myös esteettistä ja kulttuurista näkemystä. Hän on viestintäkykyinen ja osaa kommunikoida äidinkielellään ja englanniksi. Hän osaa hankkia tietoa ja pysyä mukana alan muutoksissa. Hän osaa tehdä ryhmätyötä ja dokumentoida työvaiheet sekä työn tulokset.</p>"));
        parentLOS.setEducationDomain(createI18nText("Kulttuuriala"));
        parentLOS.setStydyDomain(createI18nText("Viestintä ja informaatiotieteet"));
        parentLOS.setEducationDegree("32");
        parentLOS.setCreditValue("120");
        parentLOS.setCreditUnit(createI18nText("opintoviikko"));
        parentLOS.setChildren(children);
        return parentLOS;
    }

    private ParentLOI createParentLOI(String id, List<ChildLOIRef> refs, Set<ApplicationOption> aos) {
        ParentLOI parentLOI = new ParentLOI();
        parentLOI.setId(id);
        parentLOI.setSelectingDegreeProgram(createI18nText("<p>Opintojen valinta teksti</p>"));
        parentLOI.setPrerequisite(new Code("PK", createI18nText("Peruskoulu"), createI18nText("Peruskoulu")));
        parentLOI.setChildRefs(refs);
        parentLOI.setApplicationOptions(aos);
        return parentLOI;
    }

    private ChildLOS createChildLOS(String id, List<ChildLOI> lois) {
        ChildLOS childLOS = new ChildLOS();
        childLOS.setId(id);
        childLOS.setName(createI18nText("Audiovisuaalisen viestinnän koulutusohjelma, media-assistentti",
                "Utbildningsprogrammet för audiovisuell kommunikation, medieassistent", "Audio visual communication study program, media assistant",
                "audiovisuaalinen, assistentti", "audiovisuaalinen, assistentti", "audiovisuaalinen, assistentti"));
        childLOS.setDegreeTitle(createI18nText("Audiovisuaalisen viestinnän koulutusohjelma, media-assistentti",
                "Utbildningsprogrammet för audiovisuell kommunikation, medieassistent", "Audio visual communication study program, media assistant"));
        childLOS.setQualification(createI18nText("Media-assistentti", "Medieassistent", "Media assistant"));
        childLOS.setGoals(createI18nText("<p>Audiovisuaalisen viestinnän perustutkinnon tavoitteena on antaa opiskelijalle tarvittava perusosaaminen alan assistenttitason työtehtäviin, jotka liittyvät viestintätuotteiden suunnitteluun, toteuttamiseen, valmistamiseen, tuottamiseen ja markkinointiin.</p>  <p>Audiovisuaalisilla viestintätuotteilla tarkoitetaan mm. valokuvia, elokuvia, video-, radio- ja tv- ohjelmia, multimediatuotteita, vuorovaikutteisia pelejä ja - ohjelmia, digitaalisia elokuvatallenteita, webbisivuja ja wap- palveluja. Perustutkinnon tavoitteissa on painotettu teknistuotannollisen tietotaidon lisäksi esteettisfilosofista perusnäkemystä sekä kulttuurihistorian perusteiden tuntemusta.</p>  <p>Audiovisuaalisen viestinnän alalla on useita laajoja ammatteja ja ammattiryhmiä, siksi alalla tarvitaan sekä moniosaajia että erikoistuneita tietyn tekniikan tai taidon erityisosaajia. Tämän takia alan perustutkinnon tavoitteissa korostuu alan kaikille opiskelijoille yhteinen perusosaaminen sekä laaja valinnaisten opintojen osuus, joka mahdollistaa hyvin erilaisten suuntautumisvaihtoehtojen toteuttamisen.</p>",
                "<p>Målet med grundexamen i audiovisuell kommunikation är att ge dig de nödvändiga baskunskaperna i de arbetsuppgifter på assistentnivå inom branschen som hänför sig till planering, genomförande, framställning, produktion och marknadsföring av medieprodukter. Här avses med audiovisuella medieprodukter bl.a. fotografier, filmer, video-, radio- och TV-program, multimedieprodukter, interaktiva spel och program, digitala filminspelningar, webbsidor och wap-tjänster. I målen för grundexamen betonas utöver kunnande i teknisk produktion en estetisk-filosofisk grundsyn samt kunskap i kulturhistoriens grunder.</p>  <p>Inom branschen för audiovisuell kommunikation finns flera yrken och yrkesgrupper och därför behövs både personer med kunskaper inom flera olika områden och personer som är specialiserade på en viss teknik eller färdighet. I grundexamen betonas en gemensam baskompetens för alla studerande samt en omfattande andel valfria studier som möjliggör olika inriktningsalternativ.</p>",
                "<p>Degree goal in english</p>"));
        childLOS.setLois(lois);
        return childLOS;
    }

    private ChildLOI createChildLOI(String id, List<ApplicationOption> aos) {
        ChildLOI childLOI = new ChildLOI();
        childLOI.setId(id);
        childLOI.setApplicationOptions(aos);
        Calendar cal = new GregorianCalendar();
        cal.set(Calendar.YEAR, 2014);
        cal.set(Calendar.MONTH, 8);
        cal.set(Calendar.DATE, 1);
        childLOI.setStartDate(cal.getTime());
        childLOI.setFormOfEducation(Lists.newArrayList(createI18nText("Nuorten koulutus", "Utbildning för unga", "Education and training for young")));
        childLOI.setTeachingLanguages(Lists.newArrayList(new Code("FI", createI18nText("suomi"), createI18nText("suomi"))));
        childLOI.setFormOfTeaching(Lists.newArrayList(createI18nText("Lähiopetus")));
        childLOI.setPrerequisite(new Code("PK", createI18nText("Peruskoulu"), createI18nText("Peruskoulu")));
        childLOI.setProfessionalTitles(Lists.newArrayList(createI18nText("Graafikko")));

//        childLOI.setWebLinks();
//        childLOI.setWorkingLifePlacement();
//        childLOI.setInternationalization();
//        childLOI.setCooperation();
//        childLOI.setContent();

        return childLOI;
    }

    private ChildLOIRef createChildLORef(String loiId, String losId, String asId) {
        ChildLOIRef childLOIRef = new ChildLOIRef();
        childLOIRef.setId(loiId);
        childLOIRef.setLosId(losId);
        childLOIRef.setAsIds(Lists.newArrayList(asId));
        childLOIRef.setName(createI18nText("Audiovisuaalisen viestinnän koulutusohjelma, media-assistentti",
                "Utbildningsprogrammet för audiovisuell kommunikation, medieassistent", "Audio visual communication study program, media assistant",
                "audiovisuaalinen, assistentti", "audiovisuaalinen, assistentti", "audiovisuaalinen, assistentti"));
        childLOIRef.setNameByTeachingLang("Audiovisuaalisen viestinnän koulutusohjelma, media-assistentti");
        childLOIRef.setQualification(createI18nText("Media-assistentti"));
        childLOIRef.setPrerequisite(new Code("PK", createI18nText("Peruskoulu"), createI18nText("Peruskoulu")));
        return childLOIRef;
    }

    private ApplicationSystem createApplicationSystem(String id) {
        ApplicationSystem as = new ApplicationSystem();
        as.setId(id);
        as.setName(createI18nText("Yhteishaku 2013"));
        Calendar start = new GregorianCalendar();
        start.set(Calendar.YEAR, 2013);
        start.set(Calendar.MONTH, Calendar.JUNE);
        start.set(Calendar.DATE, 1);
        Calendar end = new GregorianCalendar();
        end.set(Calendar.YEAR, 2014);
        end.set(Calendar.MONTH, Calendar.AUGUST);
        end.set(Calendar.DATE, 15);
        end.set(Calendar.HOUR_OF_DAY, 23);
        as.setApplicationDates(Lists.newArrayList(new DateRange(start.getTime(), end.getTime())));
        return as;
    }

    private ApplicationOption createApplicationOption(String id, I18nText name, ApplicationSystem as, List<ChildLOIRef> childLOIRefs, ParentLOSRef pRef) {
        ApplicationOption ao = new ApplicationOption();
        ao.setId(id);
        ao.setName(name);
        ao.setAoIdentifier("452");
        ao.setApplicationSystem(as);
        ao.setEducationDegree("32");
        ao.setChildLOIRefs(childLOIRefs);
        ao.setProvider(createProvider());
        ao.setStartingQuota(10);
        ao.setLowestAcceptedScore(5);
        ao.setLowestAcceptedAverage(7.7);
        //ao.setAttachmentDeliveryDeadline();
        //ao.setAttachmentDeliveryAddress();
        ao.setLastYearApplicantCount(10);
        ao.setSora(true);
        ao.setTeachingLanguages(Lists.newArrayList("FI", "EN"));
        ao.setParent(pRef);
        ao.setSelectionCriteria(createI18nText("<p>Valintaperustekuvaus</p>"));
        ao.setPrerequisite(new Code("PK", createI18nText("Peruskoulu"), createI18nText("Peruskoulu")));
        ao.setRequiredBaseEducations(Lists.newArrayList("6", "2", "7", "0", "1", "3"));
        //ao.setExams();
        return ao;
    }

    private ParentLOSRef createParentLORef(String id, I18nText name) {
        ParentLOSRef parentLORef = new ParentLOSRef();
        parentLORef.setId(id);
        parentLORef.setName(name);
        return parentLORef;
    }

    private Provider createProvider() {

        Provider provider = new Provider(PROVIDER_ID, createI18nText("Pohjois-Karjalan ammattiopisto Joensuu, tekniikka ja kulttuuri"));
        provider.setApplicationSystemIDs(Sets.newHashSet(APPLICATION_SYSTEM_ID));

        Address visiting = new Address();
        visiting.setStreetAddress("Peltolankatu 4");
        visiting.setPostalCode("80101");
        visiting.setPostOffice("JOENSUU");

        Address postal = new Address();
        postal.setStreetAddress("PL 101");
        postal.setPostalCode("80101");
        postal.setPostOffice("JOENSUU");
        provider.setPostalAddress(postal);
        provider.setVisitingAddress(visiting);
        provider.setWebPage("http://www.pkky.fi/amo/joensuutk");
        provider.setAthleteEducation(false);
        provider.setEmail("amo.joensuutk@pkky.fi");
        provider.setFax("013  244 2648");
        provider.setPhone("013  244 200");
        provider.setPlaceOfBusinessCode("0250201");
//        provider.setDescription();
//        provider.setHealthcare();
//        provider.setAccessibility();
//        provider.setLearningEnvironment();
//        provider.setDining();
//        provider.setLivingExpenses();
//        provider.setSocial();
//        provider.setPicture();

        return provider;

    }


    private I18nText createI18nText(String fi) {
        return createI18nText(fi, fi, fi);
    }

    private I18nText createI18nText(String fi, String sv, String en) {
        return createI18nText(fi, sv, en, fi, sv, en);
    }

    private I18nText createI18nText(String fi, String sv, String en, String short_fi, String short_sv, String short_en) {
        Map<String, String> values = Maps.newHashMap();
        values.put("fi", fi);
        values.put("sv", sv);
        values.put("en", en);
        Map<String, String> shortValues = Maps.newHashMap();
        shortValues.put("fi", short_fi);
        shortValues.put("sv", short_sv);
        shortValues.put("en", short_en);
        return new I18nText(values, shortValues);
    }

}
