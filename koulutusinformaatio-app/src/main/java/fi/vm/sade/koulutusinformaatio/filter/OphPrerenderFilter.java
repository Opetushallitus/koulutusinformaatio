package fi.vm.sade.koulutusinformaatio.filter;

import com.github.greengerong.PreRenderSEOFilter;
import com.github.greengerong.PrerenderSeoService;
import com.google.common.collect.Maps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

public class OphPrerenderFilter extends PreRenderSEOFilter {
    private static final Logger LOG = LoggerFactory.getLogger(OphPrerenderFilter.class);

    private OphPrerenderWrapper ophPrerenderWrapper;
    private OphPrerenderSeoService ophPrerenderSeoService;
    private ExecutorService prerenderExecutorService;

    @Override
    public void init(FilterConfig filterConfig) {
        ophPrerenderWrapper = OphPrerenderWrapper.INSTANCE.get();
        LOG.info(String.format("enablePrerender = %s", ophPrerenderWrapper.enablePrerender));
        LOG.info(String.format("socketTimeoutMillis = %d", ophPrerenderWrapper.socketTimeoutMillis));
        LOG.info(String.format("prerenderMaxThreads = %d", ophPrerenderWrapper.prerenderMaxThreads));
        if (ophPrerenderWrapper.enablePrerender) {
            Map<String, String> configAsMap = toMap(filterConfig);
            LOG.info(String.format("Initialising %s with config: %s", PrerenderSeoService.class.getSimpleName(), configAsMap));
            this.prerenderExecutorService = Executors.newFixedThreadPool(ophPrerenderWrapper.prerenderMaxThreads, new CustomizableThreadFactory("ki-prerender-"));
            this.ophPrerenderSeoService = new OphPrerenderSeoService(configAsMap, this.prerenderExecutorService);
        } else {
            LOG.warn(String.format("ophPrerenderWrapper.enablePrerender == %s , not initialising service.", ophPrerenderWrapper.enablePrerender));
        }
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        if (ophPrerenderWrapper.enablePrerender) {
            boolean isPrerendered = ophPrerenderSeoService.prerenderIfEligible((HttpServletRequest) servletRequest, (HttpServletResponse) servletResponse);
            if (!isPrerendered) {
                filterChain.doFilter(servletRequest, servletResponse);
            }
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    @Override
    protected void setPrerenderSeoService(PrerenderSeoService prerenderSeoService) {
        throw new UnsupportedOperationException("This setter should not be called");
    }

    protected Map<String, String> toMap(FilterConfig filterConfig) {
        Map<String, String> config = Maps.newHashMap();
        for (String parameterName : PARAMETER_NAMES) {
            config.put(parameterName, filterConfig.getInitParameter(parameterName));
        }
        config.put("socketTimeout", Integer.toString(ophPrerenderWrapper.socketTimeoutMillis)); // See PrerenderConfig.getSocketTimeout()
        return config;
    }

    @Component
    public static class OphPrerenderWrapper {
        public static final AtomicReference<OphPrerenderWrapper> INSTANCE = new AtomicReference<>();

        private final boolean enablePrerender;
        private final int socketTimeoutMillis;
        private final int prerenderMaxThreads;

        @Autowired
        public OphPrerenderWrapper(@Value("${koulutusinformaatio.prerender.enable}") boolean enablePrerender,
                                   @Value("${koulutusinformaatio.prerender.socket.timeout.millis}") int socketTimeoutMillis,
                                   @Value("${koulutusinformaatio.prerender.max.threads}") int prerenderMaxThreads) {
            this.enablePrerender = enablePrerender;
            this.socketTimeoutMillis = socketTimeoutMillis;
            this.prerenderMaxThreads = prerenderMaxThreads;
            if (INSTANCE.getAndSet(this) != null) {
                throw new IllegalStateException("Did not expect " + getClass().getSimpleName() + " to be initialised already! Looks like a bug.");
            }
        }
    }
}
