package fi.vm.sade.koulutusinformaatio.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
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
            
            //Set up two threads to read on the output of the external process.
            Thread stdout = new Thread(new StreamReader(process.getInputStream()));
            Thread stderr = new Thread(new StreamReader(process.getErrorStream()));
            
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
        }
        
        LOG.info("Rendering text version html finished");
    }
    
    private class StreamReader implements Runnable {
        private InputStream stream;
        private boolean run;

        public StreamReader(InputStream i) {
            stream = i;
            run = true;
        }

        public void run() {
            BufferedReader reader = null;
            StringBuilder stringBuilder = new StringBuilder("");
            
            try {
                reader = new BufferedReader(new InputStreamReader(stream));
                String line = reader.readLine();

                while(run && line != null) {
                    stringBuilder.append(line + "\n");
                    line = reader.readLine();
                }
                
                LOG.debug(stringBuilder.toString());
            } catch(IOException ex) {
                LOG.error(String.format("Rendering text version failed due to IOException: %s\n%s",
                        ex.getMessage(), stringBuilder.toString() ));
            } finally {
                try {
                    reader.close();
                    run = false;
                } catch (IOException e) {
                    LOG.error(String.format("Closing stream failed due to IOException during text version generation: %s",
                            e.getMessage() ));
                }
            }
        }
    }

}
