package fi.vm.sade.koulutusinformaatio.service.mock;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import fi.vm.sade.koulutusinformaatio.domain.*;
import fi.vm.sade.koulutusinformaatio.domain.exception.KoodistoException;
import fi.vm.sade.koulutusinformaatio.domain.exception.TarjontaParseException;
import fi.vm.sade.koulutusinformaatio.service.TarjontaService;
import fi.vm.sade.tarjonta.service.resources.dto.OidRDTO;
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
    private static final String PARENT_LOS_ID = "11.22.33.44.55";
    private static final String PARENT_LOI_ID = "111.222.333.444.555";
    private static final String CHILD_LO_ID = "1111.2222.3333.4444.5555";
    private static final String CHILD_LOI_ID = "1111.2222.3333.4444.66666";
    private static final String APPLICATION_SYSTEM_ID = "11111.22222.33333.44444.55555";
    private static final String APPLICATION_OPTION_ID = "111111.222222.333333.444444.555555";
    private static final String PROVIDER_ID = "1111111.2222222.3333333.4444444.5555555";


    @Override
    public List<ParentLOS> findParentLearningOpportunity(String oid) throws TarjontaParseException, KoodistoException {
        return Lists.newArrayList(createParentLOS());
    }

    @Override
    public List<OidRDTO> listParentLearnignOpportunityOids() {
        OidRDTO oid = new OidRDTO();
        oid.setOid(PARENT_KOMO_OID);
        return Lists.newArrayList(oid);
    }

    @Override
    public List<OidRDTO> listParentLearnignOpportunityOids(int count, int startIndex) {
        return listParentLearnignOpportunityOids();
    }

    private ParentLOS createParentLOS() {
        ParentLOS parentLOS = new ParentLOS();
        parentLOS.setId(PARENT_LOS_ID);
        parentLOS.setName(createI18nText("Audiovisuaalisen viestinnän perustutkinto"));
        parentLOS.setLois(Lists.newArrayList(createParentLOI()));
        parentLOS.setProvider(createProvider());
        parentLOS.setStructureDiagram(createI18nText(
                "<p>Tutkinnon kaikille pakollinen tutkinnonosa on audiovisuaalinen tuotanto (30 ov). Lisäksi suoritetaan valinnaisia osia, jotka tukevat ammatillista suuntautumista.</p>  <p>Ammatillisten tutkinnon osien laajuus on yhteensä 90 opintoviikkoa. (Näyttötutkinnossa tutkinnon osilla ei ole laajuuksia.) Ammatillisessa peruskoulutuksessa (opetussuunnitelmaperusteisessa) opiskellaan lisäksi 20 ov ammattitaitoa täydentäviä ja 10 ov vapaasti valittavia tutkinnon osia.</p>"));
        parentLOS.setAccessToFurtherStudies(createI18nText("<p>Ammatillisista perustutkinnoista sekä ammatti- ja erikoisammattitutkinnoista saa yleisen jatko-opintokelpoisuuden ammattikorkeakouluihin ja yliopistoihin. Luonteva jatko-opintoväylä esim. ammatillisen perustutkinnon suorittaneilla on artenomin, muotoilijan tai konservaattorin ammattikorkeakoulututkinnot. Aalto-yliopiston Taideteollisessa korkeakoulussa tai Lapin yliopistossa voi suorittaa esimerkiksi taiteen kandidaatin tai taiteen maisterin tutkinnot. Ammatillisen opettajan pedagogiset opinnot antavat jatkokoulutusmahdollisuuden ammatillisen opettajan työtehtäviin. Tekniikan kandidaatin, diplomi-insinöörin tai arkkitehdin tutkintoja voi suorittaa teknillisissä korkeakouluissa ja yliopistoissa.</p>"));
        parentLOS.setDegreeProgramSelection(createI18nText("<p>Koulutusohjelman valinna kuvaus</p>"));
        parentLOS.setGoals(createI18nText("<p>Tutkinnon suorittanut voi työskennellä erilaisissa audiovisuaalisen alan yrityksissä, joita ovat muun muassa kustannus-, lehti-, peli- ja ohjelmistotalot, viestintä- ja mainostoimistot, äänistudiot, valokuvaamot, elokuva-, video- ja animaatiotuotannot, radio- ja televisioyhtiöt, teatteri sekä kuttuuri- ja tapahtumatuotannot. Ammattinimikkeinä ovat kamera-assistentti, ääniassistentti, valoassistentti, fotoassistentti ja AD-assistentti.</p>   <p>Tutkinnon suorittaja tuntee audiovisuaalisen alan työprosessin työvaiheet esituotannosta jälkituotantoon. Hänellä on audiovisuaalisen alan perusvalmiudet, jonka tueksi voi hankkia erityis-  ja ammattiosaamista monilta viestinnän aloilta. Hän hallitsee tekniikkaa ja tietotekniikkaa, mutta hänellä on myös esteettistä ja kulttuurista näkemystä. Hän on viestintäkykyinen ja osaa kommunikoida äidinkielellään ja englanniksi. Hän osaa hankkia tietoa ja pysyä mukana alan muutoksissa. Hän osaa tehdä ryhmätyötä ja dokumentoida työvaiheet sekä työn tulokset.</p>"));
        parentLOS.setEducationDomain(createI18nText("Kulttuuriala"));
        parentLOS.setStydyDomain(createI18nText("Viestintä ja informaatiotieteet"));
        parentLOS.setEducationDegree("32");
        parentLOS.setCreditValue("120");
        parentLOS.setCreditUnit(createI18nText("opintoviikko"));
        parentLOS.setChildren(Lists.newArrayList(createChildLOS()));
        return parentLOS;
    }

    private ParentLOI createParentLOI() {
        ParentLOI parentLOI = new ParentLOI();
        parentLOI.setId(PARENT_LOI_ID);
        parentLOI.setSelectingEducation(createI18nText("<p>Opintojen valinta teksti</p>"));
        parentLOI.setPrerequisite(new Code("PK", createI18nText("Peruskoulu"), createI18nText("Peruskoulu")));
        parentLOI.setChildRefs(Lists.newArrayList(createChildLORef()));
        parentLOI.setApplicationOptions(Sets.newHashSet(createApplicationOption()));
        return parentLOI;
    }

    private ChildLOS createChildLOS() {
        ChildLOS childLOS = new ChildLOS();
        childLOS.setId(CHILD_LO_ID);
        childLOS.setName(createI18nText("Audiovisuaalisen viestinnän koulutusohjelma, media-assistentti",
                "Utbildningsprogrammet för audiovisuell kommunikation, medieassistent", "Audio visual communication study program, media assistant",
                "audiovisuaalinen, assistentti", "audiovisuaalinen, assistentti", "audiovisuaalinen, assistentti"));
        childLOS.setDegreeTitle(createI18nText("Audiovisuaalisen viestinnän koulutusohjelma, media-assistentti",
                "Utbildningsprogrammet för audiovisuell kommunikation, medieassistent", "Audio visual communication study program, media assistant"));
        childLOS.setQualification(createI18nText("Media-assistentti", "Medieassistent", "Media assistant"));
        childLOS.setDegreeGoal(createI18nText("<p>Audiovisuaalisen viestinnän perustutkinnon tavoitteena on antaa opiskelijalle tarvittava perusosaaminen alan assistenttitason työtehtäviin, jotka liittyvät viestintätuotteiden suunnitteluun, toteuttamiseen, valmistamiseen, tuottamiseen ja markkinointiin.</p>  <p>Audiovisuaalisilla viestintätuotteilla tarkoitetaan mm. valokuvia, elokuvia, video-, radio- ja tv- ohjelmia, multimediatuotteita, vuorovaikutteisia pelejä ja - ohjelmia, digitaalisia elokuvatallenteita, webbisivuja ja wap- palveluja. Perustutkinnon tavoitteissa on painotettu teknistuotannollisen tietotaidon lisäksi esteettisfilosofista perusnäkemystä sekä kulttuurihistorian perusteiden tuntemusta.</p>  <p>Audiovisuaalisen viestinnän alalla on useita laajoja ammatteja ja ammattiryhmiä, siksi alalla tarvitaan sekä moniosaajia että erikoistuneita tietyn tekniikan tai taidon erityisosaajia. Tämän takia alan perustutkinnon tavoitteissa korostuu alan kaikille opiskelijoille yhteinen perusosaaminen sekä laaja valinnaisten opintojen osuus, joka mahdollistaa hyvin erilaisten suuntautumisvaihtoehtojen toteuttamisen.</p>",
                "<p>Målet med grundexamen i audiovisuell kommunikation är att ge dig de nödvändiga baskunskaperna i de arbetsuppgifter på assistentnivå inom branschen som hänför sig till planering, genomförande, framställning, produktion och marknadsföring av medieprodukter. Här avses med audiovisuella medieprodukter bl.a. fotografier, filmer, video-, radio- och TV-program, multimedieprodukter, interaktiva spel och program, digitala filminspelningar, webbsidor och wap-tjänster. I målen för grundexamen betonas utöver kunnande i teknisk produktion en estetisk-filosofisk grundsyn samt kunskap i kulturhistoriens grunder.</p>  <p>Inom branschen för audiovisuell kommunikation finns flera yrken och yrkesgrupper och därför behövs både personer med kunskaper inom flera olika områden och personer som är specialiserade på en viss teknik eller färdighet. I grundexamen betonas en gemensam baskompetens för alla studerande samt en omfattande andel valfria studier som möjliggör olika inriktningsalternativ.</p>",
                "<p>Degree goal in english</p>"));
        childLOS.setLois(Lists.newArrayList(createChildLOI()));
        return childLOS;
    }

    private ChildLOI createChildLOI() {
        ChildLOI childLOI = new ChildLOI();
        childLOI.setId(CHILD_LOI_ID);
        childLOI.setApplicationOptions(Lists.newArrayList(createApplicationOption()));
        childLOI.setApplicationSystemIds(Lists.newArrayList(APPLICATION_SYSTEM_ID));
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

    private ChildLOIRef createChildLORef() {
        ChildLOIRef childLOIRef = new ChildLOIRef();
        childLOIRef.setId(CHILD_LO_ID);
        childLOIRef.setLosId(CHILD_LO_ID);
        childLOIRef.setAsIds(Lists.newArrayList(APPLICATION_SYSTEM_ID));
        childLOIRef.setName(createI18nText("Audiovisuaalisen viestinnän koulutusohjelma, media-assistentti",
                "Utbildningsprogrammet för audiovisuell kommunikation, medieassistent", "Audio visual communication study program, media assistant",
                "audiovisuaalinen, assistentti", "audiovisuaalinen, assistentti", "audiovisuaalinen, assistentti"));
        childLOIRef.setNameByTeachingLang("Audiovisuaalisen viestinnän koulutusohjelma, media-assistentti");
        childLOIRef.setQualification(createI18nText("Media-assistentti"));
        childLOIRef.setPrerequisite(new Code("PK", createI18nText("Peruskoulu"), createI18nText("Peruskoulu")));
        return childLOIRef;
    }

    private ApplicationSystem createApplicationSystem() {
        ApplicationSystem as = new ApplicationSystem();
        as.setId(APPLICATION_SYSTEM_ID);
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

    private ApplicationOption createApplicationOption() {
        ApplicationOption ao = new ApplicationOption();
        ao.setId(APPLICATION_OPTION_ID);
        ao.setName(createI18nText("Audiovisuaalisen viestinnän perustutkinto, pk"));
        ao.setAoIdentifier("452");
        ao.setApplicationSystem(createApplicationSystem());
        ao.setEducationDegree("32");
        ao.setChildLOIRefs(Lists.newArrayList(createChildLORef()));
        ao.setProvider(createProvider());
        ao.setStartingQuota(10);
        ao.setLowestAcceptedScore(5);
        ao.setLowestAcceptedAverage(7.7);
        //ao.setAttachmentDeliveryDeadline();
        //ao.setAttachmentDeliveryAddress();
        ao.setLastYearApplicantCount(10);
        ao.setSora(true);
        ao.setTeachingLanguages(Lists.newArrayList("FI", "EN"));
        ao.setParent(createParentLORef());
        ao.setSelectionCriteria(createI18nText("<p>Valintaperustekuvaus</p>"));
        ao.setPrerequisite(new Code("PK", createI18nText("Peruskoulu"), createI18nText("Peruskoulu")));
        ao.setRequiredBaseEducations(Lists.newArrayList("6", "2", "7", "0", "1", "3"));
        //ao.setExams();
        return ao;
    }

    private ParentLOSRef createParentLORef() {
        ParentLOSRef parentLORef = new ParentLOSRef();
        parentLORef.setId(PARENT_LOS_ID);
        parentLORef.setName(createI18nText("Audiovisuaalisen viestinnän perustutkinto"));
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
