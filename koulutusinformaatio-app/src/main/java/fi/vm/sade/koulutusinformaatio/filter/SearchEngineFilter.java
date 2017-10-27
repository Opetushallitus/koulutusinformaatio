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

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Hannu Lyytikainen
 */
public class SearchEngineFilter implements Filter {

    private final String escapedFragment = "_escaped_fragment_";
    private final String language = "descriptionLang";
    private Logger LOG = LoggerFactory.getLogger(SearchEngineFilter.class);


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        /**
         * HUOM! DEVAAJA!
         * Tämä on hoidettu nginx configuraatiossa. Kaikki _escaped_fragment_ sisältävät urlit rewritetaan /snapshot rajapintaan.
         *
         * Jos tähän tarvitsee tehdä muutoksia, kyselkää nginx vastaavilta. Jätän tämän filterin tähän varoituksena jälkipolville.
         */


        if (request.getParameterMap().containsKey(escapedFragment)) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;

            String requestInfo= "Remote: " + httpRequest.getRemoteAddr() +
                    ", User-Agent: " + httpRequest.getHeader("User-Agent") +
                    ", URI: " + httpRequest.getRequestURI();
            LOG.error("Filteriin tuli kutsu, joka pitäisi olla hoidettu nginx configuraatiossa Request [{}]", requestInfo);

            HttpServletResponse httpResponse = (HttpServletResponse) response;

            String fragmentPath = URLDecoder.decode(httpRequest.getParameter(escapedFragment), "UTF-8");

            URI uri;
            try {
                uri = new URI(fragmentPath);
            } catch (URISyntaxException e) {
                throw new ServletException("Malformed url", e);
            }

            String oid = uri.getPath().split("/")[2];
            String lang = getLang(request, uri.getQuery());

            String newUri = lang != null ?
                    String.format("/snapshot/%s_%s", oid, lang)
                    : String.format("/snapshot/%s", oid);
            httpResponse.sendRedirect(newUri);
        } else {
            filterChain.doFilter(request, response);
        }
    }

    private String getLang(ServletRequest request, String query) {
        String lang = request.getParameter(language);
        if (lang == null) {
            List<NameValuePair> params = parseQueryParams(query);
            lang = getLangParam(params);
        }
        return lang;
    }

    @Override
    public void destroy() {

    }

    private String getLangParam(List<NameValuePair> params) {
        for (NameValuePair pair : params) {
            if (pair.getName().equalsIgnoreCase(language)) {
                return pair.getValue();
            }
        }
        return null;
    }

    private List<NameValuePair> parseQueryParams(String query) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        if (query != null) {
            String[] queryParams = query.split("&");
            for (String param : queryParams) {
                String[] pair = param.split("=");
                if (pair.length >= 2) {
                    params.add(new BasicNameValuePair(pair[0], pair[1]));
                }
            }
        }

        return params;
    }
}
