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

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author Hannu Lyytikainen
 */
public class SearchEngineFilter implements Filter {

    //private Pattern pattern = Pattern.compile("^.*\\?_escaped_fragment_=.*$");

    private final String escapedFragment = "_escaped_fragment_";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        if (request.getParameterMap().containsKey(escapedFragment)) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            String newUri = String.format("snapshot/%s.html", httpRequest.getParameter(escapedFragment).split("/")[2]);
            httpRequest.getRequestDispatcher(newUri).forward(request, response);
        }
        else {
            filterChain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {

    }
}
