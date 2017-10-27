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

package fi.vm.sade.koulutusinformaatio.domain;

import com.google.common.collect.Sets;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;

import java.util.List;
import java.util.Set;

/**
 * Learning Opportunity Specification
 * @author Hannu Lyytikainen
 */
public abstract class LOS {

    protected Provider provider;
    protected String id;
    protected I18nText name;
    protected I18nText shortTitle;
    protected I18nText goals;
    protected String type;
    protected List<Code> topics; // aiheet
    protected List<Code> themes; // teemat
    protected List<I18nText> professionalTitles;
    private ToteutustyyppiEnum toteutustyyppi;
    private Set<ApplicationOption> applicationOptions = Sets.newHashSet();

    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    
    public I18nText getName() {
        return name;
    }

    public void setName(I18nText name) {
        this.name = name;
    }

    public I18nText getShortTitle() {
        return shortTitle;
    }

    public void setShortTitle(I18nText shortTitle) {
        this.shortTitle = shortTitle;
    }

    public I18nText getGoals() {
        return goals;
    }

    public void setGoals(I18nText goals) {
        this.goals = goals;
    }

    public String getType() {

        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    public List<Code> getTopics() {
        return topics;
    }

    public void setTopics(List<Code> topics) {
        this.topics = topics;
    }

    public List<Code> getThemes() {
        return themes;
    }

    public void setThemes(List<Code> themes) {
        this.themes = themes;
    }

    public List<I18nText> getProfessionalTitles() {
        return professionalTitles;
    }

    public void setProfessionalTitles(List<I18nText> professionalTitles) {
        this.professionalTitles = professionalTitles;
    }

    public ToteutustyyppiEnum getToteutustyyppi() {
        return toteutustyyppi;
    }

    public void setToteutustyyppi(ToteutustyyppiEnum toteutustyyppi) {
        this.toteutustyyppi = toteutustyyppi;
    }

    public Set<ApplicationOption> getApplicationOptions() {
        return applicationOptions;
    }

    public void setApplicationOptions(Set<ApplicationOption> applicationOptions) {
        this.applicationOptions = applicationOptions;
    }

    public Provider getProvider() {
        return provider;
    }
    public void setProvider(Provider provider) {
        this.provider = provider;
    }


}
