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

/**
 * 
 * @author Markus
 *
 */
public class SolrFields {
    
    public static class LearningOpportunity {
        
        public static final String TYPE = "type";
        public static final String ID = "id";
        public static final String FI_FNAME = "fi_fname"; //finnish name of a facet value
        public static final String SV_FNAME = "sv_fname"; //swedish name of a facet value
        public static final String EN_FNAME = "en_fname"; //english name of a facet value
        public static final String TEACHING_LANGUAGE = "teachingLangCode_ffm"; //The teaching language used in the learning opportunity
        public static final String EDUCATION_TYPE = "educationType_ffm"; //The education type of the learning opportunity
        public static final String TOPIC = "topic_ffm"; //The topic of the learning opportunity
        public static final String THEME = "theme_ffm"; //The theme of the learning opportunity
        public static final String LOP_ID = "lopId";
        public static final String NAME = "name";
        public static final String NAME_FI = "name_fi";
        public static final String NAME_SV = "name_sv";
        public static final String NAME_EN = "name_en";
        public static final String CHILD_NAME = "childName";
        public static final String CHILD_NAME_FI = "childName_fi";
        public static final String CHILD_NAME_SV = "childName_sv";
        public static final String CHILD_NAME_EN = "childName_en";
        public static final String LOP_NAME = "lopName";
        public static final String LOP_NAME_FI = "lopName_fi";
        public static final String LOP_NAME_SV = "lopName_sv";
        public static final String LOP_NAME_EN = "lopName_en";
        public static final String LOP_HOMEPLACE = "lopHomeplace";
        public static final String LOP_ADDRESS_FI = "lopAddress_fi";
        public static final String LOP_DESCRIPTION_FI = "lopDescription_fi";
        public static final String LOP_DESCRIPTION_SV = "lopDescription_sv";
        public static final String LOP_DESCRIPTION_EN = "lopDescription_en";
        public static final String GOALS_FI = "goals_fi";
        public static final String GOALS_SV = "goals_sv";
        public static final String GOALS_EN = "goals_en";
        public static final String AS_NAME_FI = "asName_fi";
        public static final String AS_NAME_SV = "asName_sv";
        public static final String AS_NAME_EN = "asName_en";
        public static final String PREREQUISITES = "prerequisites";
        public static final String LOS_ID = "losId";
        public static final String PARENT_ID = "parentId";
        public static final String PREREQUISITE = "prerequisite";
        public static final String PREREQUISITE_CODE = "prerequisiteCode";
        public static final String PROFESSIONAL_TITLES_FI = "professionalTitles_fi";
        public static final String PROFESSIONAL_TITLES_SV = "professionalTitles_sv";
        public static final String PROFESSIONAL_TITLES_EN = "professionalTitles_en";
        public static final String QUALIFICATION_FI = "qualification_fi";
        public static final String QUALIFICATION_SV = "qualification_sv";
        public static final String QUALIFICATION_EN = "qualification_en";
        public static final String CONTENT_FI = "content_fi";
        public static final String CONTENT_SV = "content_sv";
        public static final String CONTENT_EN = "content_en";
        public static final String CREDITS = "credits_ss";
        
        //Fields for sorting
        public static final String START_DATE_SORT = "startDate_dsort";
        public static final String NAME_SORT = "name_ssort";
        public static final String DURATION_SORT = "duration_isort";
        
        //Fields for autocomplete
        public static final String NAME_AUTO = "name_auto";
        public static final String FREE_AUTO = "free_auto";
        
    }
    
    public static class LocationFields {
        public static final String TYPE = "type"; //Type of area, i.e. kunta or maakunta
        public static final String ID = "id";
        public static final String NAME = "name"; 
        public static final String LANG = "lang"; 
        public static final String CODE = "code"; 
        public static final String PARENT = "parent"; //The parent area of the municipality
    }
    
    public static class SolrConstants {
        //Constants related to core swap
    	public static final String ALIAS_ACTION = "/admin/collections?action=CREATEALIAS&name=";
    	public static final String COLLECTIONS = "&collections=";
    	public static final String GET = "GET";
    	
    	//value constants
    	public static final String ED_TYPE_LUKIO = "et1"; 
    	public static final String ED_TYPE_KAKSOIS = "et2";
    	public static final String ED_TYPE_AMMATILLINEN = "et3";
    	public static final String ED_TYPE_AMM_ER = "et4";
    	public static final String ED_TYPE_VALMENTAVA = "et5";
    	public static final String SPECIAL_EDUCATION = "ER";
    	public static final String TIMESTAMP_DOC = "loUpdateTimestampDocument";
    	public static final String TYPE_FACET = "FASETTI";
    	public static final String KESTOTYYPPI_VUOSI = "suunniteltukesto_01";
    	public static final String KESTOTYYPPI_KK = "suunniteltukesto_02";
    	public static final String PK = "pk";
    	public static final String DISTRICT_UNKNOWN = "99";
    	public static final String MUNICIPALITY_UNKNOWN = "99";
    	
    	
    }
}
