package fi.vm.sade.koulutusinformaatio.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import fi.vm.sade.koulutusinformaatio.domain.exception.KIException;
import fi.vm.sade.koulutusinformaatio.service.TextVersionService;

@Component
public class TextVersionServiceImpl implements TextVersionService {
    
    private static final Logger LOG = LoggerFactory.getLogger(TextVersionServiceImpl.class);
    
    private String script;
    private String destinationFolder;
    private String source;

    @Autowired
    public TextVersionServiceImpl(@Value("${koulutusinformaatio.textversion.script}") String script,
            @Value("${koulutusinformaatio.textversion.folder}") String destinationFolder,
            @Value("${koulutusinformaatio.textversion.source}") String source) {
        this.script = script;
        this.destinationFolder = destinationFolder;
        this.source = source;
        
    }
    
    @Override
    public void update() throws KIException {
        LOG.info("Rendering text version html");
        try {
            Process process = Runtime.getRuntime().exec(String.format("make all install -C %s SOURCE=%s INSTALL_DIR=%s SCRIPT=%s",
                    script, source, destinationFolder, script));
            int exitStatus = process.waitFor();
            
            if (exitStatus != 0) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                StringBuilder stringBuilder = new StringBuilder("");
                String currentLine = null;
                currentLine = bufferedReader.readLine();
                while (currentLine != null) {
                    stringBuilder.append(currentLine + "\n");
                    currentLine = bufferedReader.readLine();
                }
                BufferedReader bufferedOutputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                StringBuilder stringOutputBuilder = new StringBuilder("");
                String currentOutputLine = null;
                currentOutputLine = bufferedOutputReader.readLine();
                while (currentOutputLine != null) {
                    stringOutputBuilder.append(currentOutputLine + "\n");
                    currentOutputLine = bufferedOutputReader.readLine();
                }
                throw new KIException(String.format("Rendering text version failed: ERROR: %s, INPUT: %s",
                        stringBuilder.toString(), stringOutputBuilder.toString() ));
            }
        } catch (IOException e) {
            throw new KIException(String.format("Rendering text version failed due to IOException: %s",
                    e.getMessage()));
        } catch (InterruptedException e) {
            throw new KIException(String.format("Rendering text version failed due to InterruptedException: %s",
                    e.getMessage()));
        }
        
        LOG.info("Rendering text version html finished");
    }

}
