package fi.vm.sade.koulutusinformaatio.service.impl;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.mongodb.morphia.Datastore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

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
/**
 * 
 * @author Tero Ahonen
 *
 */
public class SitemapBuilder {
    public final static String PROPERTY_COLLECTIONS = "sitemap.collections";
    public final static String PROPERTY_BASE_URL = "baseurl";
    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");
    public static final Logger LOG = LoggerFactory.getLogger(SitemapBuilder.class);
    private static final DocumentBuilderFactory xmlFactory = DocumentBuilderFactory.newInstance();
    private static DocumentBuilder builder;
    static {
        try {
            xmlFactory.setNamespaceAware(true);
            builder = xmlFactory.newDocumentBuilder();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    /**
     * Utilitys function for creating sitemap xml and returning it as byte array.
     * @param datastore Morphia datastore
     * @param properties Map<String,String> of properties. Code uses two properties; base url of loc 
     * elements and csv list of collections. Syntax for list is urlprefix:mongocollection:restriction.
     * Restriction is a field name and + or - at the start describing must field exist or not.
     * @return byte array of XML document 
     * @throws Exception In case something went wrong.
     */
    public byte[] buildSitemap(Datastore datastore, Map<String,String> properties) throws Exception{
        LOG.debug("Starting sitemap building");
        String[] sitemapCollections = properties.get(PROPERTY_COLLECTIONS).split(COLLECTION_SEPARATOR);
        String collection = null;
        String idPrefix = null;
        DBObject fields = new BasicDBObject();
        fields.put(FIELD_ID, 1);
        fields.put(TEACHING_LANGUAGES, 2);
        Document dom = builder.newDocument();
        Element root  = createNode(dom, ELEMENT_URLSET, null);
        root.setAttribute(ATTRIBUTE_XMLNS, NAMESPACE);
        int counter = 0;
        int counterByCollection = 0;
        Date lastModifiedDate = null;
        String[] collectionValues = null;
        String restriction = null;
        DBObject query = null;
        for (int i = 0; i < sitemapCollections.length; i++) {
            //value must contains :
            if (sitemapCollections[i].indexOf(PREFIX_COLLECTION_SEPARATOR) > 0){
                LOG.debug("Processing collection "+sitemapCollections[i]);
                collectionValues = sitemapCollections[i].split(PREFIX_COLLECTION_SEPARATOR);
                idPrefix = collectionValues[0];
                collection = collectionValues[1];
                if (collectionValues.length > 2){
                    restriction = collectionValues[2];
                    if (restriction.length() > 1 && restriction.startsWith(RESTICTION_CONTAINS)){
                        //remove + or - char at the start of the field name
                        query = new BasicDBObject(restriction.substring(1), new BasicDBObject(QUERY_EXISTS, true));
                    }else{
                        query = new BasicDBObject(restriction.substring(1), new BasicDBObject(QUERY_EXISTS, false));
                    }
                }else{
                    query = new BasicDBObject();
                }
                DBCollection col = datastore.getDB().getCollection(collection);
                DBCursor cursor = col.find(query,fields);
                while (cursor.hasNext()){
                    counter++;
                    counterByCollection++;
                    DBObject dbo = cursor.next();
                    lastModifiedDate = this.getDate(dbo);
                    List<String> teachingLanguages = getTeachingLanguageCodes( (BasicDBList)dbo.get(TEACHING_LANGUAGES) );
                    
                    // separate node for each higher education teaching language
                    if (idPrefix.equals(ID_PREFIX_HIGHERED) && !teachingLanguages.isEmpty()) {
                        for(String lang: teachingLanguages) {
                            root.appendChild(this.createUrlElement(dom, (String)dbo.get(FIELD_ID), lastModifiedDate, idPrefix, lang, properties.get(PROPERTY_BASE_URL)));
                        }
                    } else {
                        root.appendChild(this.createUrlElement(dom, (String)dbo.get(FIELD_ID), lastModifiedDate, idPrefix, null, properties.get(PROPERTY_BASE_URL)));
                    }
                }
                LOG.debug("Processed " + counterByCollection + " entities for "+idPrefix);
                counterByCollection = 0;
            }else{
                LOG.warn("Collection name value pair is not well formed, "+sitemapCollections[i]);
            }
        }
        dom.appendChild(root);
        LOG.info("Processed "+counter+" entities for entire sitemap");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(root);
        StreamResult result = new StreamResult(out);
        transformer.transform(source, result);
        LOG.debug("Sitemap building done.");
        return out.toByteArray();

    }
    
    private List<String> getTeachingLanguageCodes(BasicDBList languages) {
        List<String> result = new ArrayList<String>();
        if (languages != null) {
            for (Object language: languages) {
               String lang = (String)((DBObject)language).get("value");
               result.add(lang.toLowerCase());
            }
        }
        
        return result;
    }
    
    private Date getDate(DBObject dbo){
        //using System.currentTimeMillist until proper date can be found from DBOjbect
        return new Date();
    }
    
    private Element createUrlElement(Document dom, String id, Date lastModified, String idPrefix, String lang, String baseUrl){
        Element url = this.createNode(dom, ELEMENT_URL, null);
        
        String locationUrl = baseUrl.concat(idPrefix).concat(CHAR_SLASH).concat(id);
        if (lang != null) {
            locationUrl = locationUrl.concat("?").concat(QUERY_PARAM_LANG).concat("=").concat(lang);
        }
        
        url.appendChild(this.createNode(dom, ELEMENT_LOC, locationUrl));
        if (lastModified != null){
            url.appendChild(this.createNode(dom,ELEMENT_LASTMOD,SDF.format(lastModified)));
        }
        url.appendChild(this.createNode(dom,ELEMENT_CHANGEFREQ,CHANGEFREQ_VALUE));
        url.appendChild(this.createNode(dom,ELEMENT_PRIORITY,PRIORITY_VALUE));
        return url;
    }
    
    private Element createNode(Document dom, String name, String value) {
        Element e = dom.createElement(name);
        if (value != null) {
            e.appendChild(dom.createTextNode(value));
        }
        return e;
    }
    
    public static final String ELEMENT_URLSET = "urlset";
    public static final String ELEMENT_URL = "url";
    public static final String ELEMENT_LASTMOD = "lastmod";
    public static final String ELEMENT_PRIORITY = "priority";
    public static final String ELEMENT_LOC = "loc";
    public static final String ELEMENT_CHANGEFREQ = "changefreq";
    public static final String CHANGEFREQ_VALUE = "weekly";
    public static final String PRIORITY_VALUE = "0.5";
    public static final String CHAR_SLASH = "/";
    public static final String FIELD_ID = "_id";
    public static final String TEACHING_LANGUAGES = "teachingLanguages";
    public static final String ATTRIBUTE_XMLNS = "xmlns";
    public static final String NAMESPACE = "http://www.sitemaps.org/schemas/sitemap/0.9";
    public static final String COLLECTION_SEPARATOR = ",";
    public static final String PREFIX_COLLECTION_SEPARATOR = ":";
    public static final String RESTICTION_CONTAINS = "+";
    public static final String QUERY_EXISTS = "$exists";
    public static final String QUERY_PARAM_LANG = "descriptionLang";
    public static final String ID_PREFIX_HIGHERED = "korkeakoulu";
}
