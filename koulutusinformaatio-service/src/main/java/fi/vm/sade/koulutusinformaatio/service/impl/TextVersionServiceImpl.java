package fi.vm.sade.koulutusinformaatio.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
            Process process = Runtime.getRuntime().exec(String.format("make all install -C %s SOURCE=%s INSTALL_DIR=%s",
                    script, source, destinationFolder));
            int exitStatus = process.waitFor();
            
            if (exitStatus != 0) {
                // read error stream
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                StringBuilder stringBuilder = new StringBuilder("");
                String currentLine = null;
                currentLine = bufferedReader.readLine();
                while (currentLine != null) {
                    stringBuilder.append(currentLine + "\n");
                    currentLine = bufferedReader.readLine();
                }
                
                // read input stream
                BufferedReader bufferedInputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                StringBuilder stringInputBuilder = new StringBuilder("");
                currentLine = null;
                currentLine = bufferedInputReader.readLine();
                while (currentLine != null) {
                    stringInputBuilder.append(currentLine + "\n");
                    currentLine = bufferedInputReader.readLine();
                }
                
                throw new KIException(String.format("Rendering text version failed: %s",
                        stringBuilder.toString()));
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
