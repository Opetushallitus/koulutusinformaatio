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

import java.util.List;

/**
 * Child level learning opportunity.
 *
 * @author Hannu Lyytikainen
 */
public class ChildLOS extends InstantiatedLOS<ChildLOI> {

    private I18nText qualification;     // tutkintonimike
    private List<I18nText> qualifications;     // tutkintonimikkeet
    private ParentLOSRef parent;
    private List<ChildLOI> lois;
    private boolean isPseudo;

    public I18nText getQualification() {
        return qualification;
    }

    public void setQualification(I18nText qualification) {
        this.qualification = qualification;
    }

    public ParentLOSRef getParent() {
        return parent;
    }

    public void setParent(ParentLOSRef parent) {
        this.parent = parent;
    }

    public List<ChildLOI> getLois() {
        return lois;
    }

    public void setLois(List<ChildLOI> lois) {
        this.lois = lois;
    }

    public boolean isPseudo() {
        return isPseudo;
    }

    public void setPseudo(boolean isPseudo) {
        this.isPseudo = isPseudo;
    }

    public List<I18nText> getQualifications() {
        return qualifications;
    }

    public void setQualifications(List<I18nText> qualifications) {
        this.qualifications = qualifications;
    }
}
