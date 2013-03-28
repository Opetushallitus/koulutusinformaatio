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

import java.util.*;

public class SearchResult {
    final List<Map<String, Object>> items;
    final Set<String> sortFields;


    public SearchResult(List<Map<String, Collection<Object>>> results) {
        items = new ArrayList<Map<String, Object>>();

        for (Map<String, Collection<Object>> result : results) {
            Map<String, Object> map = new HashMap<String, Object>();
            final ArrayList<String> loids = new ArrayList<String>();
            map.put("LOIIndexes", loids);
            final HashMap<String, String> los = new HashMap<String, String>();
            // SolrDocument ei implementoi entrySet()-metodia! java.lang.UnsupportedOperationException
            for (String key : result.keySet()) { 
                if (key.startsWith("LOS") || key.startsWith("LOP") || key.startsWith("LOI")) {
                    final Collection<Object> objects = result.get(key); //NOSONAR
                    int k = 0;
                    for (Object object : objects) {
                        los.put(k + "-" + key, object.toString());
                        //los.put("key", "" + k);
                        k++;
                    }
                } else {
                    map.put(key, result.get(key).iterator().next());
                }
            }


            for (String losKey : los.keySet()) {
                final String[] split = losKey.split("-");
                int k = Integer.parseInt(split[0]);
                @SuppressWarnings("unchecked")
				HashMap<String, String> o = (HashMap<String, String>) map.get("LOIIndex" + k);
                if (o == null) {
                    o = new HashMap<String, String>();
                    map.put("LOIIndex" + k, o);
                    loids.add("LOIIndex" + k);
                }

                o.put(split[1], los.get(losKey));
                //TODO: tässä viimeisin jää voimaan, korjattava!!!
                map.put(split[1], los.get(losKey));

            }
            items.add(map);
        }
        if (items.isEmpty()) {
            sortFields = Collections.emptySet();
        } else {
            this.sortFields = items.get(0).keySet();
        }
    }

    public List<Map<String, Object>> getItems() {
        return items;
    }

    public int getSize() {
        return items.size();
    }
}
