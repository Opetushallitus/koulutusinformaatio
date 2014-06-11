package fi.vm.sade.koulutusinformaatio.service.impl;

import java.io.IOException;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import fi.vm.sade.koulutusinformaatio.domain.exception.KIException;
import fi.vm.sade.koulutusinformaatio.service.TextVersionService;
import fi.vm.sade.koulutusinformaatio.util.StreamReaderHelper;

@Service
public class TextVersionServiceImpl implements TextVersionService {
    
    private static final Logger LOG = LoggerFactory.getLogger(TextVersionServiceImpl.class);
    
    private String script;
    private String destinationFolder;
    private String source;
    private boolean running = false;
    private String lastTextVersionUpdateFinished;

    @Autowired
    public TextVersionServiceImpl(@Value("${koulutusinformaatio.textversion.script}") String script,
            @Value("${koulutusinformaatio.textversion.folder}") String destinationFolder,
            @Value("${koulutusinformaatio.textversion.source}") String source) {
        this.script = script;
        this.destinationFolder = destinationFolder;
        this.source = source;
        
    }
    
    @Async
    @Override
    public void update() throws KIException {
        LOG.info("Rendering text version html");
        try {
            running = true;
            Process process = Runtime.getRuntime().exec(String.format("make all install -C %s SOURCE=%s INSTALL_DIR=%s",
                    script, source, destinationFolder));
            
            //Set up two threads to read on the output of the external process.
            Thread stdout = new Thread(new StreamReaderHelper(process.getInputStream()));
            Thread stderr = new Thread(new StreamReaderHelper(process.getErrorStream()));
            
            stdout.start();
            stderr.start();
            
            int exitStatus = process.waitFor();
            process.destroy();
            
            if (exitStatus != 0) {
                throw new KIException(String.format("Rendering text version failed with exit status: %d",
                        exitStatus));
            }
        } catch (IOException e) {
            throw new KIException(String.format("Rendering text version failed due to IOException: %s",
                    e.getMessage()));
        } catch (InterruptedException e) {
            throw new KIException(String.format("Rendering text version failed due to InterruptedException: %s",
                    e.getMessage()));
        } finally {
            running = false;
            lastTextVersionUpdateFinished = new Date().toString();
        }
        
        LOG.info("Rendering text version html finished");
    }
    
    @Override
    public boolean isRunning() {
        return running;
    }
    
    @Override
    public String getLastTextVersionUpdateFinished() {
        return lastTextVersionUpdateFinished;
    }

}
