/*
 * Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software:  Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * European Union Public Licence for more details.
 */

package fi.vm.sade.koulutusinformaatio.selenium;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * A quick selenium test setup try-out
 * 
 * @author Kalle Lundahn
 *
 */
@Ignore
public class KoulutusinformaatioTest {
    private WebDriver driver;


    public KoulutusinformaatioTest() {
        driver = new FirefoxDriver();
    }


    @Before
    public void init() {
        driver.get("http://localhost:8080/koulutusinformaatio-app/");
    }

    @Test
    public void testSimpleSearch() {
        WebElement searchField = driver.findElement(By.name("text"));
        searchField.sendKeys("Mus*");
        searchField.submit();
        
        List<WebElement> waited = this.waitFor(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("#search-results li")));
        assertTrue(waited.size() == 6);
        
        WebElement filters = driver.findElement(By.className("infobox"));
        assertNotNull(filters);
    }
    
    @Test(expected=NoSuchElementException.class)
    public void testEmptySearch() {
        WebElement searchField = driver.findElement(By.name("text"));
        searchField.sendKeys("");
        searchField.submit();
        
        List<WebElement> searchResults = driver.findElements(By.cssSelector("#search-results li"));
        assertTrue(searchResults.size() == 0);
        
        WebElement filters = driver.findElement(By.className("infobox"));
        assertNull(filters);
        
    }

    @After
    public void teardown() {
        driver.close();
    }
    
    public List<WebElement> waitFor(ExpectedCondition<List<WebElement>> condition) {
        WebDriverWait driverWait = new WebDriverWait(driver, 10);
        List<WebElement> dynamicElement = driverWait.until(condition);
        
        return dynamicElement;
    }
}
