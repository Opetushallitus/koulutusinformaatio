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

package fi.vm.sade.koulutusinformaatio.domain.search;

import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;

/**
 * An option that a user can apply for (hakukohde).
 *
 * @author Hannu Lyytikainen
 */
public class ApplicationOption implements Serializable {

    private static final long serialVersionUID = 8709483730387515771L;

    private String id;
    private String name;
    private String educationDegree;

    public ApplicationOption(@JsonProperty(value = "id") String id, @JsonProperty(value = "name") String name,
                             @JsonProperty(value = "educationDegree") String educationDegree) {
        this.id = id;
        this.name = name;
        this.educationDegree = educationDegree;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEducationDegree() {
        return educationDegree;
    }
}
