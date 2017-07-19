package fi.vm.sade.koulutusinformaatio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class StartupLogger {
    private static final Logger LOG = LoggerFactory.getLogger(StartupLogger.class);

    @PostConstruct
    public void init(){
        LOG.info("//=================================\\\\");
        LOG.info("||   Koulutusinformaatio started   ||");
        LOG.info("\\\\=================================//");
    }
}
