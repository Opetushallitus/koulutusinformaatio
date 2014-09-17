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

package fi.vm.sade.koulutusinformaatio.filter;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

/**
 * @author Hannu Lyytikainen
 */
public class SearchEngineFilter implements Filter {

    private final String escapedFragment = "_escaped_fragment_";
    private final String language = "descriptionLang";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        
        String lang = request.getParameter(language);

        if (request.getParameterMap().containsKey(escapedFragment)) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            String fragmentPath = httpRequest.getParameter(escapedFragment);
            fragmentPath = URLDecoder.decode(fragmentPath, "UTF-8");
            
            String path = null;
            String query = null;
            URI uri = null;
            String newUri;
            
            try {
                uri = new URI(fragmentPath);
                path = uri.getPath();
                query = uri.getQuery();
            } catch (URISyntaxException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
            path = path.split("/")[2];
            
            if (lang == null) {
                List<NameValuePair> params = parseQueryParams(query);
                lang = getLangParam(params);
            }
            
            if (lang != null) {
                newUri = String.format("snapshot/%s_%s.html", path, lang);
            } else {
                newUri = String.format("snapshot/%s.html", path);
            }
            
            //String newUri = String.format("/snapshots/%s.html", httpRequest.getParameter(escapedFragment).split("/")[2]);
            httpRequest.getRequestDispatcher(newUri).forward(request, response);
            //FileInputStream snapshot = new FileInputStream(String.format("/Users/klu/cases/snapshots/%s.html", httpRequest.getParameter(escapedFragment).split("/")[2]));
            //IOUtils.copy(snapshot, response.getOutputStream());
        }
        else {
            filterChain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {

    }
    
    private String getLangParam(List<NameValuePair> params) {
        for (NameValuePair pair : params) {
            if (pair.getName().equals(language)) {
                return pair.getValue();
            }
        }
        
        return null;
    }
    
    private List<NameValuePair> parseQueryParams(String query) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        
        if (query != null) {
            String[] queryParams = query.split("&");
            for (String param: queryParams) {
                String[] pair = param.split("=");
                if (pair.length >= 2) {
                    params.add( new BasicNameValuePair(pair[0], pair[1]) );
                }
            }
        }
        
        return params;
    }
}
