package fi.vm.sade.koulutusinformaatio.event;

import fi.vm.sade.events.Event;
import fi.vm.sade.events.EventHandler;
import fi.vm.sade.events.impl.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Hannu Lyytikainen
 */
//@Component
public class TarjontaEventHandler implements EventHandler {

    public static final Logger LOGGER = LoggerFactory.getLogger(TarjontaEventHandler.class);

    @Autowired
    public TarjontaEventHandler(EventListener eventListener) {
        eventListener.addEventHandler(this);
    }

    @Override
    public void handleEvent(Event event) {
        // update education data...
        LOGGER.info("Received event: " + "\n" +
        event.getCategory() + "\n" +
        event.toString());
    }
}
