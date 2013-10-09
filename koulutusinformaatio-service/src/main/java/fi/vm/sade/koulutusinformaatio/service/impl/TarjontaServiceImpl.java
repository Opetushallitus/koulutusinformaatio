/*
 * Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software:  Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * European Union Public Licence for more details.
 */

package fi.vm.sade.koulutusinformaatio.service.impl;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.*;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import fi.vm.sade.koulutusinformaatio.converter.KoulutusinformaatioObjectBuilder;
import fi.vm.sade.koulutusinformaatio.domain.*;
import fi.vm.sade.koulutusinformaatio.domain.exception.KoodistoException;
import fi.vm.sade.koulutusinformaatio.domain.exception.TarjontaParseException;
import fi.vm.sade.koulutusinformaatio.service.KoodistoService;
import fi.vm.sade.koulutusinformaatio.service.ProviderService;
import fi.vm.sade.koulutusinformaatio.service.TarjontaService;
import fi.vm.sade.tarjonta.service.resources.dto.*;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import java.util.*;

/**
 * @author Hannu Lyytikainen
 */
@Service
@Profile("default")
public class TarjontaServiceImpl implements TarjontaService {

    private static final Logger LOG = LoggerFactory.getLogger(TarjontaServiceImpl.class);

    private static final String MODULE_TYPE_PARENT = "TUTKINTO";
    private static final String MODULE_TYPE_CHILD = "TUTKINTO_OHJELMA";
    private static final String STATE_PUBLISHED = "JULKAISTU";
    private static final String BASE_EDUCATION_KOODISTO_URI = "pohjakoulutustoinenaste";

    private WebResource komoResource;
    private WebResource komotoResource;
    private WebResource hakuResource;
    private WebResource hakukohdeResource;
    private ConversionService conversionService;
    private KoodistoService koodistoService;
    private ProviderService providerService;

    @Autowired
    public TarjontaServiceImpl(@Value("${tarjonta.api.rest.url}") final String tarjontaApiUrl,
                               ConversionService conversionService, KoodistoService koodistoService, ProviderService providerService) {
        this.koodistoService = koodistoService;
        this.providerService = providerService;
        ClientConfig cc = new DefaultClientConfig();
        cc.getClasses().add(JacksonJsonProvider.class);
        Client clientWithJacksonSerializer = Client.create(cc);
        komoResource = clientWithJacksonSerializer.resource(tarjontaApiUrl + "komo");
        komotoResource = clientWithJacksonSerializer.resource(tarjontaApiUrl + "komoto");
        hakuResource = clientWithJacksonSerializer.resource(tarjontaApiUrl + "haku");
        hakukohdeResource = clientWithJacksonSerializer.resource(tarjontaApiUrl + "hakukohde");
        this.conversionService = conversionService;
    }

    public TarjontaServiceImpl() {
    }

    public KomoDTO getKomo(String oid) {
        return komoResource
                .path(oid)
                .accept(getMediaType())
                .get(new GenericType<KomoDTO>() {
                });
    }

    public List<OidRDTO> getKomotosByKomo(String oid, int count, int startIndex) {
        return komoResource
                .path(oid)
                .path("komoto")
                .queryParam("count", String.valueOf(count))
                .queryParam("startIndex", String.valueOf(startIndex))
                .accept(getMediaType())
                .get(new GenericType<List<OidRDTO>>() {
                });
    }

    public KomotoDTO getKomoto(String oid) {
        return komotoResource
                .path(oid)
                .accept(getMediaType())
                .get(new GenericType<KomotoDTO>() {
                });
    }

    public List<OidRDTO> getHakukohdesByKomoto(String oid) {
        return komotoResource
                .path(oid)
                .path("hakukohde")
                .accept(getMediaType())
                .get(new GenericType<List<OidRDTO>>() {
                });
    }

    public KomoDTO getKomoByKomoto(String oid) {
        return komotoResource
                .path(oid)
                .path("komo")
                .accept(getMediaType())
                .get(new GenericType<KomoDTO>() {});

    }

    public HakukohdeDTO getHakukohde(String oid) {
        return hakukohdeResource
                .path(oid)
                .accept(getMediaType())
                .get(new GenericType<HakukohdeDTO>() {
                });
    }

    public HakuDTO getHakuByHakukohde(String oid) {
        return hakukohdeResource
                .path(oid)
                .path("haku")
                .accept(getMediaType())
                .get(new GenericType<HakuDTO>() {});
    }

    public List<OidRDTO> getKomotosByHakukohde(String oid) {
        return hakukohdeResource
                .path(oid)
                .path("komoto")
                .accept(getMediaType())
                .get(new GenericType<List<OidRDTO>>() {});
    }

    public HakuDTO getHaku(String oid) {
        return hakuResource
                .path(oid)
                .accept(getMediaType())
                .get(new GenericType<HakuDTO>() {
                });
    }

    public List<String> listParentLearnignOpportunityOids() {
        return listParentLearnignOpportunityOids(Integer.MAX_VALUE, 0);
    }

    @Override
    public List<String> listParentLearnignOpportunityOids(int count, int startIndex) {
        List<OidRDTO> oids = komoResource
                .queryParam("count", String.valueOf(count))
                .queryParam("startIndex", String.valueOf(startIndex))
                .accept(getMediaType())
                .get(new GenericType<List<OidRDTO>>() {
                });
        return Lists.transform(oids, new Function<OidRDTO, String>() {
            @Override
            public String apply(OidRDTO input) {
                return conversionService.convert(input, String.class);
            }
        });
    }

    private String getMediaType() {
        return MediaType.APPLICATION_JSON + ";charset=UTF-8";
    }




    // oid of the parent komo object that is the pase for all the ParentLOS objects constructed by this builder
    private String oid;

    // Parent komo KomoDTO object that corresponds to the oid
    private KomoDTO parentKomo;

    // List of ParentLOS objects that are returned when the build() method is invoked
    private List<ParentLOS> parentLOSs;

    // A helper data structure that groups parent komoto KomotoDTO objects by their provider
    ArrayListMultimap<String, KomotoDTO> parentKomotosByProviderId;

    // A helper data structure that groups ChildLO objects by their ParentLOS id
    ArrayListMultimap<String, ChildLOS> childLOSsByParentLOSId;


    @Override
    public List<ParentLOS> findParentLearningOpportunity(String oid) throws TarjontaParseException, KoodistoException {

        this.oid = oid;
        parentKomotosByProviderId = ArrayListMultimap.create();
        childLOSsByParentLOSId = ArrayListMultimap.create();
        parentLOSs = Lists.newArrayList();

        try {

            resolveParentLOSs();
            resolveChildLOSs();
            reassemble();
            filter();

        } catch (KoodistoException e) {
            throw new TarjontaParseException("An error occurred while building parent LOS " + oid + " with koodisto: " + e.getMessage());
        } catch (WebApplicationException e) {
            throw new TarjontaParseException("An error occurred while building parent LOS " + oid
                    + " accessing remote resource: HTTP response code: "
                    + e.getResponse().getStatus() + ",  error message: " + e.getMessage());
        }


        return parentLOSs;
    }

    public void resolveParentLOSs() throws TarjontaParseException, KoodistoException, WebApplicationException {
        LOG.debug(Joiner.on(" ").join("Resolving parent LOSs for komo oid: ", oid));
        parentKomo = getKomo(oid);
        validateParentKomo(parentKomo);
        List<OidRDTO> parentKomotoOids = getKomotosByKomo(parentKomo.getOid(), Integer.MAX_VALUE, 0);
        if (parentKomotoOids == null || parentKomotoOids.size() == 0) {
            throw new TarjontaParseException("No instances found in parent LOS " + parentKomo.getOid());
        }
        parentKomotosByProviderId = ArrayListMultimap.create();
        for (OidRDTO parentKomotoOid : parentKomotoOids) {
            KomotoDTO parentKomoto = getKomoto(parentKomotoOid.getOid());
            parentKomotosByProviderId.put(parentKomoto.getTarjoajaOid(), parentKomoto);
        }

        for (String key : parentKomotosByProviderId.keySet()) {
            parentLOSs.add(createParentLOS(parentKomo, key, parentKomotosByProviderId.get(key)));
        }
    }

    public void resolveChildLOSs() throws TarjontaParseException, KoodistoException, WebApplicationException {
        List<String> childKomoIds = parentKomo.getAlaModuulit();

        for (String childKomoId : childKomoIds) {
            KomoDTO childKomo = getKomo(childKomoId);

            // A helper data structure that groups child komoto KomotoDTO objects by their provider and komo (ChildLOS id = komo oid + provider oid)
            ArrayListMultimap<String, KomotoDTO> childKomotosByChildLOSId = ArrayListMultimap.create();

            try {
                validateChildKomo(childKomo);
            } catch (TarjontaParseException e) {
                LOG.debug("Invalid child komo " + childKomo.getOid() + ": " + e.getMessage());
                continue;
            }

            List<OidRDTO> childKomotoOids = getKomotosByKomo(childKomoId, Integer.MAX_VALUE, 0);

            for (OidRDTO childKomotoOid : childKomotoOids) {
                KomotoDTO childKomoto = getKomoto(childKomotoOid.getOid());
                childKomotosByChildLOSId.put(getLOSId(childKomoId, childKomoto.getTarjoajaOid()), childKomoto);
            }

            for (String childLOSId : childKomotosByChildLOSId.keySet()) {
                childLOSsByParentLOSId.put(getLOSId(parentKomo.getOid(), resolveProviderId(childLOSId)),
                        createChildLOS(childKomo, childLOSId, childKomotosByChildLOSId.get(childLOSId)));
            }
        }
    }

    public void reassemble() throws TarjontaParseException, KoodistoException, WebApplicationException {
        for (ParentLOS parentLOS : parentLOSs) {

            HashMultimap<String, ApplicationOption> applicationOptionsByParentLOIId = HashMultimap.create();

            // add children to parent los
            // filter out children without lois
            List<ChildLOS> children = Lists.newArrayList(
                    Collections2.filter(childLOSsByParentLOSId.get(parentLOS.getId()), new Predicate<ChildLOS>() {
                        @Override
                        public boolean apply(fi.vm.sade.koulutusinformaatio.domain.ChildLOS input) {
                            return isChildLOSValid(input);
                        }
                    })
            );

            for (ChildLOS childLOS : children) {

                // set parent ref
                childLOS.setParent(new ParentLOSRef(parentLOS.getId(), parentLOS.getName()));

                for (ChildLOI childLOI : childLOS.getLois()) {

                    // add info to ao
                    for (ApplicationOption ao : childLOI.getApplicationOptions()) {
                        ao.setProvider(parentLOS.getProvider());
                        ao.setParent(new ParentLOSRef(parentLOS.getId(), parentLOS.getName()));
                        ao.setEducationDegree(parentLOS.getEducationDegree());
                        parentLOS.getProvider().getApplicationSystemIDs().add(ao.getApplicationSystem().getId());
                    }

                    // save application options to be added to parent loi
                    applicationOptionsByParentLOIId.putAll(childLOI.getParentLOIId(), childLOI.getApplicationOptions());

                    // add related child refs to child
                    childLOI.setRelated(new ArrayList<ChildLOIRef>());
                    for (ChildLOI ref : childLOS.getLois()) {
                        if (!childLOI.getId().equals(ref.getId()) &&
                                childLOI.getPrerequisite().getValue().equals(ref.getPrerequisite().getValue())) {
                            ChildLOIRef cRef = KoulutusinformaatioObjectBuilder.buildChildLOIRef(ref);
                            if (cRef != null) {
                                childLOI.getRelated().add(cRef);
                            }
                        }
                    }
                }
            }

            for (ParentLOI parentLOI : parentLOS.getLois()) {
                parentLOI.setApplicationOptions(applicationOptionsByParentLOIId.get(parentLOI.getId()));
            }
            parentLOS.setChildren(children);
        }
    }

    public void filter() {

        // filter out empty parent lois
        Set<String> parentLOIIdsInUse = Sets.newHashSet();
        for (ParentLOS parentLOS : this.parentLOSs) {
            for (ChildLOS childLOS : parentLOS.getChildren()) {
                for (ChildLOI childLOI : childLOS.getLois()) {
                    parentLOIIdsInUse.add(childLOI.getParentLOIId());
                }
            }
            List<ParentLOI> parentLOIsInUse = Lists.newArrayList();
            for (ParentLOI parentLOI : parentLOS.getLois()) {
                if (parentLOIIdsInUse.contains(parentLOI.getId())) {
                    parentLOIsInUse.add(parentLOI);
                }
            }
            parentLOS.setLois(parentLOIsInUse);
        }

        // filter out empty parent LOSs
        this.parentLOSs = Lists.newArrayList(
                Collections2.filter(this.parentLOSs, new Predicate<ParentLOS>() {
                    @Override
                    public boolean apply(fi.vm.sade.koulutusinformaatio.domain.ParentLOS input) {
                        return isParentLOSValid(input);
                    }
                })
        );
    }

    private List<ChildLOI> resolveChildLOIsByParentLOIId(ParentLOS parentLOS, String parentLOIId) {
        List<ChildLOI> childLOIs = Lists.newArrayList();
        for (ChildLOS childLOS : parentLOS.getChildren()) {
            for (ChildLOI childLOI : childLOS.getLois()) {
                if (childLOI.getParentLOIId().equals(parentLOIId)) {
                    childLOIs.add(childLOI);
                }
            }
        }
        return childLOIs;
    }

    private List<Exam> createExams(List<ValintakoeRDTO> valintakoes) throws KoodistoException {
        List<Exam> exams = Lists.newArrayList();
        if (valintakoes != null) {
            for (ValintakoeRDTO valintakoe : valintakoes) {
                if (valintakoe.getKuvaus() != null && valintakoe.getTyyppiUri() != null
                        && valintakoe.getValintakoeAjankohtas() != null
                        && !valintakoe.getValintakoeAjankohtas().isEmpty()) {
                    Exam exam = new Exam();
                    exam.setType(koodistoService.searchFirst(valintakoe.getTyyppiUri()));
                    exam.setDescription(new I18nText(valintakoe.getKuvaus()));
                    List<ExamEvent> examEvents = Lists.newArrayList();

                    for (ValintakoeAjankohtaRDTO valintakoeAjankohta : valintakoe.getValintakoeAjankohtas()) {
                        ExamEvent examEvent = new ExamEvent();
                        Address address = new Address();
                        address.setPostalCode(koodistoService.searchFirstCodeValue(valintakoeAjankohta.getOsoite().getPostinumero()));
                        address.setPostOffice(valintakoeAjankohta.getOsoite().getPostitoimipaikka());
                        address.setStreetAddress(valintakoeAjankohta.getOsoite().getOsoiterivi1());
                        examEvent.setAddress(address);
                        examEvent.setDescription(valintakoeAjankohta.getLisatiedot());
                        examEvent.setStart(valintakoeAjankohta.getAlkaa());
                        examEvent.setEnd(valintakoeAjankohta.getLoppuu());
                        examEvents.add(examEvent);
                    }
                    exam.setExamEvents(examEvents);
                    exams.add(exam);
                }
            }
        }
        return exams;
    }

    private ChildLOS createChildLOS(KomoDTO childKomo, String childLOSId, List<KomotoDTO> childKomotos) throws KoodistoException {
        ChildLOS childLOS = new ChildLOS();
        childLOS.setId(childLOSId);
        childLOS.setName(koodistoService.searchFirst(childKomo.getKoulutusOhjelmaKoodiUri()));
        childLOS.setQualification(koodistoService.searchFirst(childKomo.getTutkintonimikeUri()));
        childLOS.setDegreeTitle(koodistoService.searchFirst(childKomo.getKoulutusOhjelmaKoodiUri()));
        childLOS.setGoals(getI18nText(childKomo.getTavoitteet()));
        List<ChildLOI> childLOIs = Lists.newArrayList();

        for (KomotoDTO childKomoto : childKomotos) {
            String childKomotoOid = childKomoto.getOid();

            LOG.debug(Joiner.on(" ").join("Resolving child learning opportunity:", childKomotoOid));

            try {
                validateChildKomoto(childKomoto);
            } catch (TarjontaParseException e) {
                LOG.debug("Skipping child komoto " + e.getMessage());
                continue;
            }

            ChildLOI childLOI = new ChildLOI();
            childLOI.setName(childLOS.getName());
            childLOI.setId(childKomoto.getOid());
            childLOI.setLosId(childLOS.getId());
            childLOI.setParentLOIId(childKomoto.getParentKomotoOid());
            childLOI.setStartDate(childKomoto.getKoulutuksenAlkamisDate());
            childLOI.setFormOfEducation(koodistoService.searchMultiple(childKomoto.getKoulutuslajiUris()));
            childLOI.setWebLinks(childKomoto.getWebLinkkis());
            childLOI.setTeachingLanguages(koodistoService.searchCodesMultiple(childKomoto.getOpetuskieletUris()));
            childLOI.setFormOfTeaching(koodistoService.searchMultiple(childKomoto.getOpetusmuodotUris()));
            childLOI.setPrerequisite(koodistoService.searchFirstCode(childKomoto.getPohjakoulutusVaatimusUri()));
            childLOI.setProfessionalTitles(koodistoService.searchMultiple(childKomoto.getAmmattinimikeUris()));
            childLOI.setWorkingLifePlacement(getI18nText(childKomoto.getSijoittuminenTyoelamaan()));
            childLOI.setInternationalization(getI18nText(childKomoto.getKansainvalistyminen()));
            childLOI.setCooperation(getI18nText(childKomoto.getYhteistyoMuidenToimijoidenKanssa()));
            childLOI.setContent(getI18nText(childKomoto.getSisalto()));

            if (childKomoto.getYhteyshenkilos() != null) {
                for (YhteyshenkiloRDTO yhteyshenkiloRDTO : childKomoto.getYhteyshenkilos()) {
                    ContactPerson contactPerson = new ContactPerson(yhteyshenkiloRDTO.getPuhelin(), yhteyshenkiloRDTO.getTitteli(),
                            yhteyshenkiloRDTO.getEmail(), yhteyshenkiloRDTO.getSukunimi(), yhteyshenkiloRDTO.getEtunimet());
                    childLOI.getContactPersons().add(contactPerson);
                }
            }

            List<ApplicationOption> applicationOptions = Lists.newArrayList();
            List<String> applicationSystemIds = Lists.newArrayList();
            List<OidRDTO> aoIdDTOs = getHakukohdesByKomoto(childKomotoOid);
            for (OidRDTO aoIdDTO : aoIdDTOs) {
                LOG.debug(Joiner.on(" ").join("Adding application options (",
                        aoIdDTOs.size(), ") to child learning opportunity"));

                // application option
                String aoId = aoIdDTO.getOid();
                HakukohdeDTO hakukohdeDTO = getHakukohde(aoId);
                HakuDTO hakuDTO = getHakuByHakukohde(aoId);

                try {
                    validateHakukohde(hakukohdeDTO);
                } catch (TarjontaParseException e) {
                    LOG.debug("Application option skipped, " + e.getMessage());
                    continue;
                }
                try {
                    validateHaku(hakuDTO);
                } catch (TarjontaParseException e) {
                    LOG.debug("Application option skipped, " + e.getMessage());
                    continue;
                }

                ApplicationOption ao = new ApplicationOption();

                ao.setId(hakukohdeDTO.getOid());
                ao.setName(koodistoService.searchFirst(hakukohdeDTO.getHakukohdeNimiUri()));
                ao.setAoIdentifier(koodistoService.searchFirstCodeValue(hakukohdeDTO.getHakukohdeNimiUri()));
                ao.setStartingQuota(hakukohdeDTO.getAloituspaikatLkm());
                ao.setLowestAcceptedScore(hakukohdeDTO.getAlinValintaPistemaara());
                ao.setLowestAcceptedAverage(hakukohdeDTO.getAlinHyvaksyttavaKeskiarvo());
                ao.setAttachmentDeliveryDeadline(hakukohdeDTO.getLiitteidenToimitusPvm());
                ao.setLastYearApplicantCount(hakukohdeDTO.getEdellisenVuodenHakijatLkm());
                ao.setSelectionCriteria(getI18nText(hakukohdeDTO.getValintaperustekuvaus()));
                ao.setExams(createExams(hakukohdeDTO.getValintakoes()));
                List<Code> subCodes = koodistoService.searchSubCodes(childKomoto.getPohjakoulutusVaatimusUri(),
                        BASE_EDUCATION_KOODISTO_URI);
                List<String> baseEducations = Lists.transform(subCodes, new Function<Code, String>() {
                    @Override
                    public String apply(Code code) {
                        return code.getValue();
                    }
                });
                ao.setRequiredBaseEducations(baseEducations);

                ApplicationSystem as = new ApplicationSystem();
                as.setId(hakuDTO.getOid());
                as.setName(getI18nText(hakuDTO.getNimi()));
                if (hakuDTO.getHakuaikas() != null) {
                    for (HakuaikaRDTO ha : hakuDTO.getHakuaikas()) {
                        DateRange range = new DateRange();
                        range.setStartDate(ha.getAlkuPvm());
                        range.setEndDate(ha.getLoppuPvm());
                        as.getApplicationDates().add(range);
                    }
                }
                ao.setApplicationSystem(as);
                if (!Strings.isNullOrEmpty(hakukohdeDTO.getSoraKuvausKoodiUri())) {
                    ao.setSora(true);
                }

                ao.setTeachingLanguages(koodistoService.searchCodeValuesMultiple(childKomoto.getOpetuskieletUris()));
                ao.setPrerequisite(childLOI.getPrerequisite());
                ao.setSpecificApplicationDates(hakukohdeDTO.isKaytetaanHakukohdekohtaistaHakuaikaa());
                if (ao.isSpecificApplicationDates()) {
                    ao.setApplicationStartDate(hakukohdeDTO.getHakuaikaAlkuPvm());
                    ao.setApplicationEndDate(hakukohdeDTO.getHakuaikaLoppuPvm());
                }

                if (hakukohdeDTO.getLiitteidenToimitusosoite() != null) {
                    OsoiteRDTO addressDTO = hakukohdeDTO.getLiitteidenToimitusosoite();
                    Address attachmentDeliveryAddress = new Address();
                    attachmentDeliveryAddress.setStreetAddress(addressDTO.getOsoiterivi1());
                    attachmentDeliveryAddress.setStreetAddress2(addressDTO.getOsoiterivi2());
                    attachmentDeliveryAddress.setPostalCode(koodistoService.searchFirstCodeValue(addressDTO.getPostinumero()));
                    attachmentDeliveryAddress.setPostOffice(addressDTO.getPostitoimipaikka());
                    ao.setAttachmentDeliveryAddress(attachmentDeliveryAddress);
                }

                // set child loi names to application option
                List<OidRDTO> komotosByHakukohdeOID = getKomotosByHakukohde(aoId);
                for (OidRDTO s : komotosByHakukohdeOID) {
                    KomoDTO komoByKomotoOID = getKomoByKomoto(s.getOid());

                    try {
                        validateChildKomo(komoByKomotoOID);
                    } catch (TarjontaParseException e) {
                        continue;
                    }

                    KomotoDTO k = getKomoto(s.getOid());
                    try {
                        validateChildKomoto(k);
                    } catch (TarjontaParseException e) {
                        continue;
                    }

                    ChildLOIRef cRef = new ChildLOIRef();
                    cRef.setId(s.getOid());
                    cRef.setLosId(getLOSId(komoByKomotoOID.getOid(), childKomoto.getTarjoajaOid()));
                    cRef.setName(koodistoService.searchFirst(komoByKomotoOID.getKoulutusOhjelmaKoodiUri()));
                    cRef.setQualification(koodistoService.searchFirst(komoByKomotoOID.getTutkintonimikeUri()));
                    cRef.setPrerequisite(childLOI.getPrerequisite());
                    ao.getChildLOIRefs().add(cRef);
                }
                applicationOptions.add(ao);
                applicationSystemIds.add(hakuDTO.getOid());
            }
            childLOI.setApplicationOptions(applicationOptions);

            if (!childLOI.getApplicationOptions().isEmpty()) {
                childLOIs.add(childLOI);
            }
        }
        childLOS.setLois(childLOIs);
        return childLOS;
    }


    private ParentLOS createParentLOS(KomoDTO parentKomo, String providerId, List<KomotoDTO> parentKomotos) throws KoodistoException {
        LOG.debug(Joiner.on(" ").join("Creating provider specific parent LOS from komo: ", parentKomo.getOid()));

        ParentLOS parentLOS = new ParentLOS();

        // parent info
        parentLOS.setId(getLOSId(parentKomo.getOid(), providerId));
        parentLOS.setName(koodistoService.searchFirst(parentKomo.getKoulutusKoodiUri()));
        parentLOS.setStructure(getI18nText(parentKomo.getKoulutuksenRakenne()));
        parentLOS.setAccessToFurtherStudies(getI18nText(parentKomo.getJatkoOpintoMahdollisuudet()));
        //parentLOS.setAccessToFurtherStudies(getI18nText(parentKomo.getK));
        parentLOS.setGoals(getI18nText(parentKomo.getTavoitteet()));
        parentLOS.setEducationDomain(koodistoService.searchFirst(parentKomo.getKoulutusAlaUri()));
        parentLOS.setStydyDomain(koodistoService.searchFirst(parentKomo.getOpintoalaUri()));
        parentLOS.setEducationDegree(koodistoService.searchFirstCodeValue(parentKomo.getKoulutusAsteUri()));
        parentLOS.setCreditValue(parentKomo.getLaajuusArvo());
        parentLOS.setCreditUnit(koodistoService.searchFirst(parentKomo.getLaajuusYksikkoUri()));

        Provider provider = providerService.getByOID(providerId);
        parentLOS.setProvider(provider);

        List<ParentLOI> lois = Lists.newArrayList();

        for (KomotoDTO komoto : parentKomotos) {
            ParentLOI loi = new ParentLOI();
            loi.setId(komoto.getOid());
            loi.setSelectingDegreeProgram(getI18nText(komoto.getKoulutusohjelmanValinta()));
            loi.setPrerequisite(koodistoService.searchFirstCode(komoto.getPohjakoulutusVaatimusUri()));
            lois.add(loi);
        }
        parentLOS.setLois(lois);
        return parentLOS;
    }

    private boolean isParentLOSValid(ParentLOS parentLOS) {
        if (parentLOS.getChildren() == null || parentLOS.getChildren().isEmpty()) {
            return false;
        } else {
            return true;
        }

    }

    private boolean isChildLOSValid(ChildLOS childLOS) {
        if (childLOS.getLois() != null) {
            for (ChildLOI childLOI : childLOS.getLois()) {
                if (childLOI.getApplicationOptions() != null && childLOI.getApplicationOptions().size() > 0) {
                    return true;
                }
            }
        }
        return false;
    }

    private String getLOSId(String komoId, String providerId) {
        return Joiner.on("_").join(komoId, providerId);
    }

    private String resolveProviderId(String losId) {
        return losId.split("_")[1];
    }

    private I18nText getI18nText(final Map<String, String> texts) throws KoodistoException {
        if (texts != null && !texts.isEmpty()) {
            Map<String, String> translations = new HashMap<String, String>();
            Iterator<Map.Entry<String, String>> i = texts.entrySet().iterator();
            while (i.hasNext()) {
                Map.Entry<String, String> entry = i.next();
                if (!Strings.isNullOrEmpty(entry.getKey()) && !Strings.isNullOrEmpty(entry.getValue())) {
                    String key = koodistoService.searchFirstCodeValue(entry.getKey());
                    translations.put(key.toLowerCase(), entry.getValue());
                }
            }
            I18nText i18nText = new I18nText();
            i18nText.setTranslations(translations);
            return i18nText;
        }
        return null;
    }

    private void validateParentKomo(KomoDTO komo) throws TarjontaParseException {
        // parent check
        if (!komo.getModuuliTyyppi().equals(MODULE_TYPE_PARENT)) {
            throw new TarjontaParseException("Komo not of type " + MODULE_TYPE_PARENT);
        }

        // published
        if (!komo.getTila().equals(TarjontaTila.JULKAISTU)) {
            throw new TarjontaParseException("Parent komo state not " + TarjontaTila.JULKAISTU.toString());
        }
    }

    private void validateChildKomo(KomoDTO komo) throws TarjontaParseException {
        if (!komo.getTila().equals(TarjontaTila.JULKAISTU)) {
            throw new TarjontaParseException("Child komo " + komo.getOid() + " not in state " + TarjontaTila.JULKAISTU.toString());
        }
        if (komo.getKoulutusOhjelmaKoodiUri() == null) {
            throw new TarjontaParseException("Child KomoDTO koulutusOhjelmaKoodiUri (name) is null");
        }
        if (komo.getTutkintonimikeUri() == null) {
            throw new TarjontaParseException("Child KomoDTO tutkinto nimike uri is null");
        }
    }

    private void validateChildKomoto(KomotoDTO komoto) throws TarjontaParseException {
        if (!komoto.getTila().equals(TarjontaTila.JULKAISTU)) {
            throw new TarjontaParseException("Child komoto " + komoto.getOid() + " not in state " + TarjontaTila.JULKAISTU.toString());
        }

    }

    private void validateHakukohde(HakukohdeDTO hakukohde) throws TarjontaParseException {
        if (!hakukohde.getTila().equals(STATE_PUBLISHED)) {
            throw new TarjontaParseException("Application option " + hakukohde.getOid() + " not in state " + STATE_PUBLISHED);
        }
    }

    private void validateHaku(HakuDTO haku) throws TarjontaParseException {
        if (!haku.getTila().equals(STATE_PUBLISHED)) {
            throw new TarjontaParseException("Application system " + haku.getOid() + " not in state " + STATE_PUBLISHED);
        }
    }

}
