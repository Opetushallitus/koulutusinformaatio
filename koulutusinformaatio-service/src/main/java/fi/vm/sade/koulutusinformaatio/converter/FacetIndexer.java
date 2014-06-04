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
package fi.vm.sade.koulutusinformaatio.converter;

import com.google.common.collect.Lists;
import fi.vm.sade.koulutusinformaatio.domain.*;
import org.apache.solr.common.SolrInputDocument;

import java.util.List;

/**
 * 
 * @author Markus
 */
public class FacetIndexer {

    public List<SolrInputDocument> createFacetDocs(UpperSecondaryLOI loi, UpperSecondaryLOS los) {
        List<SolrInputDocument> docs = Lists.newArrayList();

        //Teaching languages
        SolrUtil.indexCodeAsFacetDoc(loi.getTeachingLanguages().get(0), docs, true);
        //Prerequisites
        SolrUtil.indexCodeAsFacetDoc(loi.getPrerequisite(), docs, true);

        for (Code curCode : los.getTopics()) {
            SolrUtil.indexCodeAsFacetDoc(curCode, docs, false);
        }

        for (Code curCode : los.getThemes()) {
            SolrUtil.indexCodeAsFacetDoc(curCode, docs, false);
        }
        
        if (loi.getFotFacet() != null) {
            for (Code curFOT : loi.getFotFacet()) {
                SolrUtil.indexCodeAsFacetDoc(curFOT, docs, false);
            }
        }
        
        if (loi.getTimeOfTeachingFacet() != null) {
            for (Code curTimeOfTeaching : loi.getTimeOfTeachingFacet()) {
                SolrUtil.indexCodeAsFacetDoc(curTimeOfTeaching, docs, false);
            }
        }
        
        if (loi.getFormOfStudyFacet() != null) {
            for (Code curFormOfStudy : loi.getFormOfStudyFacet()) {
                SolrUtil.indexCodeAsFacetDoc(curFormOfStudy, docs, false);
            }
        }
        
        if (loi.getKoulutuslaji() != null) {
            SolrUtil.indexCodeAsFacetDoc(loi.getKoulutuslaji(), docs, false);
        }

        return docs;
    }

    /*
     * Creates the solr docs needed in facet search.
     */
    public List<SolrInputDocument> createFacetsDocs(
            ParentLOS parent) {
        List<SolrInputDocument> docs = Lists.newArrayList();
        for (ChildLOS childLOS : parent.getChildren()) {
            for (ChildLOI childLOI : childLOS.getLois()) {
                docs.addAll(createFacetDocs(childLOI));
                
                if (childLOI.getFotFacet() != null) {
                    for (Code curFOT : childLOI.getFotFacet()) {
                        SolrUtil.indexCodeAsFacetDoc(curFOT, docs, false);
                    }
                }
                if (childLOI.getTimeOfTeachingFacet() != null) {
                    for (Code curTimeOfTeaching : childLOI.getTimeOfTeachingFacet()) {
                        SolrUtil.indexCodeAsFacetDoc(curTimeOfTeaching, docs, false);
                    }
                }
                if (childLOI.getFormOfStudyFacet() != null) {
                    for (Code curFormOfStudy : childLOI.getFormOfStudyFacet()) {
                        SolrUtil.indexCodeAsFacetDoc(curFormOfStudy, docs, false);
                    }
                }
                if (childLOI.getKoulutuslaji() != null) {
                    SolrUtil.indexCodeAsFacetDoc(childLOI.getKoulutuslaji(), docs, false);
                }
            }
        }

        for (Code curCode : parent.getTopics()) {
            SolrUtil.indexCodeAsFacetDoc(curCode, docs, false);
        }

        for (Code curCode : parent.getThemes()) {
            SolrUtil.indexCodeAsFacetDoc(curCode, docs, false);
        }

        return docs;
    }

    /*
     * Creates the solr docs needed in facet search.
     */
    public List<SolrInputDocument> createFacetDocs(ChildLOI childLOI) {
        List<SolrInputDocument> docs = Lists.newArrayList();
        SolrUtil.indexCodeAsFacetDoc(childLOI.getTeachingLanguages().get(0), docs, true);
        SolrUtil.indexCodeAsFacetDoc(childLOI.getPrerequisite(), docs, true);



        return docs;
    }

    /*
     * Creates the solr docs needed in facet search.
     */
    public List<SolrInputDocument> createFacetDocs(ChildLOI childLOI, SpecialLOS los) {
        List<SolrInputDocument> docs = Lists.newArrayList();
        SolrUtil.indexCodeAsFacetDoc(childLOI.getTeachingLanguages().get(0), docs, true);
        SolrUtil.indexCodeAsFacetDoc(childLOI.getPrerequisite(), docs, true);

        for (Code curCode : los.getTopics()) {
            SolrUtil.indexCodeAsFacetDoc(curCode, docs, false);
        }

        for (Code curCode : los.getThemes()) {
            SolrUtil.indexCodeAsFacetDoc(curCode, docs, false);
        }
        
        if (childLOI.getFotFacet() != null) {
            for (Code curFOT : childLOI.getFotFacet()) {
                SolrUtil.indexCodeAsFacetDoc(curFOT, docs, false);
            }
        }
        
        if (childLOI.getTimeOfTeachingFacet() != null) {
            for (Code curTimeOfTeaching : childLOI.getTimeOfTeachingFacet()) {
                SolrUtil.indexCodeAsFacetDoc(curTimeOfTeaching, docs, false);
            }
        }
        
        if (childLOI.getFormOfStudyFacet() != null) {
            for (Code curFormOfStudy : childLOI.getFormOfStudyFacet()) {
                SolrUtil.indexCodeAsFacetDoc(curFormOfStudy, docs, false);
            }
        }
        
        if (childLOI.getKoulutuslaji() != null) {
            SolrUtil.indexCodeAsFacetDoc(childLOI.getKoulutuslaji(), docs, false);
        }

        return docs;
    }

    /*
     * Creates the solr docs needed in facet search.
     */
    public List<SolrInputDocument> createFacetDocs(HigherEducationLOS los) {
        List<SolrInputDocument> docs = Lists.newArrayList();
        for (Code curLang : los.getTeachingLanguages()) {
            SolrUtil.indexCodeAsFacetDoc(curLang, docs, true);
        }
        if (los.getPrerequisites() != null && !los.getPrerequisites().isEmpty()) {
            for (Code curPrereq : los.getPrerequisites()) {
                SolrUtil.indexCodeAsFacetDoc(curPrereq, docs, true);
            }
        }

        for (Code curCode : los.getTopics()) {
            SolrUtil.indexCodeAsFacetDoc(curCode, docs, false);
        }

        for (Code curCode : los.getThemes()) {
            SolrUtil.indexCodeAsFacetDoc(curCode, docs, false);
        }
        
        if (los.getFotFacet() != null) {
            for (Code curFOT : los.getFotFacet()) {
                SolrUtil.indexCodeAsFacetDoc(curFOT, docs, false);
            }
        }
        
        
        if (los.getTimeOfTeachingFacet() != null) {
            for (Code curTimeOfTeaching : los.getTimeOfTeachingFacet()) {
                SolrUtil.indexCodeAsFacetDoc(curTimeOfTeaching, docs, false);
            }
        }
        
        if (los.getFormOfStudyFacet() != null) {
            for (Code curFormOfStudy : los.getFormOfStudyFacet()) {
                SolrUtil.indexCodeAsFacetDoc(curFormOfStudy, docs, false);
            }
        }
        
        if (los.getKoulutuslaji() != null) {
            SolrUtil.indexCodeAsFacetDoc(los.getKoulutuslaji(), docs, false);
        }

        return docs;
    }


}
