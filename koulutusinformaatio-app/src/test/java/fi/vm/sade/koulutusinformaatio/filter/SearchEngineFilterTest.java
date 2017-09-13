package fi.vm.sade.koulutusinformaatio.filter;

import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;

import java.util.Enumeration;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SearchEngineFilterTest {
    SearchEngineFilter filter;

    @Mock
    ServletContext msc;
    @Mock
    RequestDispatcher mrd;
    @Mock
    HttpServletRequest req;
    @Mock
    ServletResponse res;
    @Mock
    FilterChain fc;

    @Before
    public void init() throws ServletException {
        this.filter = new SearchEngineFilter();
        FilterConfig config = new FilterConfig() {
            @Override
            public String getFilterName() {
                return "SearchEngineFilter";
            }

            @Override
            public ServletContext getServletContext() {
                return msc;
            }

            @Override
            public String getInitParameter(String name) {
                return null;
            }

            @Override
            public Enumeration getInitParameterNames() {
                return null;
            }
        };
        filter.init(config);
    }

    @Test
    public void doFilterRedirects() throws Exception {
        when(msc.getRequestDispatcher("/app/snapshot/meh")).thenReturn(mrd);
        when(req.getParameterMap()).thenReturn(ImmutableMap.of("_escaped_fragment_", "/tutkinto/meh"));
        when(req.getParameter("_escaped_fragment_")).thenReturn("/tutkinto/meh");
        filter.doFilter(req, res, fc);
        verify(mrd, times(1)).forward(req, res);
    }

}