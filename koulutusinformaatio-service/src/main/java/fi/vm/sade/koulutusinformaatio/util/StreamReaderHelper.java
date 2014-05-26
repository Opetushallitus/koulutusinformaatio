package fi.vm.sade.koulutusinformaatio.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StreamReaderHelper implements Runnable {

private static final Logger LOG = LoggerFactory.getLogger(StreamReaderHelper.class);
    
    private InputStream stream;
    private boolean run;

    public StreamReaderHelper(InputStream i) {
        stream = i;
        run = true;
    }

    @Override
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
            
        } catch (IOException ex) {
            LOG.error(String.format("Stream reading failed due to IOException: %s\n%s",
                    ex.getMessage(), stringBuilder.toString() ));
        } finally {
            try {
                reader.close();
                run = false;
            } catch (IOException e) {
                LOG.error(String.format("Closing stream failed due to IOException: %s\n%s",
                        e.getMessage(), stringBuilder.toString() ));
            }
        }
    }

}
