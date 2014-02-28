package fi.vm.sade.koulutusinformaatio.resource.impl;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import fi.vm.sade.koulutusinformaatio.domain.Location;
import fi.vm.sade.koulutusinformaatio.domain.dto.LocationDTO;
import fi.vm.sade.koulutusinformaatio.domain.exception.ResourceNotFoundException;
import fi.vm.sade.koulutusinformaatio.domain.exception.SearchException;
import fi.vm.sade.koulutusinformaatio.service.SearchService;

import static org.mockito.Mockito.mock;

/**
 * 
 * @author Markus
 */
public class LocationResourceImplTest {
    
    @Mock
    private SearchService searchService;
    
    private LocationResourceImpl resource;
    
    List<Location> municipalities;
    
    private final static String UUSIMAA = "Uusimaa";
    private final static String HELSINKI = "Helsinki";
    
    @Before
    public void init() throws ResourceNotFoundException, SearchException {
        
        
        List<Location> districts = new ArrayList<Location>();
        
        Location loc1 = new Location();
        loc1.setCode("code_uusimaa");
        loc1.setId("id_uusimaa");
        loc1.setLang("fi");
        loc1.setName(UUSIMAA);
        loc1.setType("DISTRICT");     
        districts.add(loc1);
   
        Location loc2 = new Location();
        loc2.setCode("code_kymi");
        loc2.setId("id_kymi");
        loc2.setLang("fi");
        loc2.setName("Kymi");
        loc2.setType("DISTRICT");
        districts.add(loc2);
        
        searchService = mock(SearchService.class);
        
        when(searchService.getDistricts(anyString())).thenReturn(districts);
        
        municipalities = new ArrayList<Location>();
        Location loc3 = new Location();
        loc3.setCode("code_helsinki");
        loc3.setId("id_helsinki");
        loc3.setLang("fi");
        loc3.setName(HELSINKI);
        loc3.setType("MUNICIPALITY");
        loc3.setParent("code_uusimaa");
        municipalities.add(loc3);
   
        Location loc4 = new Location();
        loc4.setCode("code_espoo");
        loc4.setId("id_espoo");
        loc4.setLang("fi");
        loc4.setName("Espoo");
        loc4.setType("MUNICIPALITY");
        loc4.setParent("code_uusimaa");
        municipalities.add(loc4);
        
        when(searchService.getChildLocations(anyList(), anyString())).thenReturn(municipalities);
        
        when(searchService.searchLocations(anyString(), anyString())).thenReturn(municipalities);
        
        resource = new LocationResourceImpl(searchService);

    }
    
    @Test
    public void testGetDistricts() {
        List<LocationDTO> districts = resource.getDistricts("fi");
        assertEquals(2, districts.size());
        boolean wasUusimaa = false;
        for (LocationDTO curLoc : districts) {
            if (curLoc.getName().equals(UUSIMAA)) {
                wasUusimaa = true;
            }
        }
        assertTrue(wasUusimaa);
    }
    
    @Test
    public void testGetChildLocations() {
        List<LocationDTO> children = resource.getChildLocations(Arrays.asList("code_uusimaa"), "fi");
        assertEquals(2, children.size());
    }
    
    @Test
    public void testSearchLocations() {
    	List<LocationDTO> locs = resource.searchLocations("termi", "fi");
    	assertEquals(2, locs.size());
    	assertEquals(locs.get(0).getName(), municipalities.get(0).getName());
    	assertEquals(locs.get(0).getCode(), municipalities.get(0).getCode());
    }
    
}
