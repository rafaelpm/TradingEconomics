package controllers;

import models.Country;

/**
 *
 * @author Rafael P. Monteiro
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //Calls for tests
        TradingEconomics te = new TradingEconomics();
        //te.listCountries();
        
        //te.listIndicators("Brazil");
        Country c = new Country("America");
        c.link = "/brazil/indicators";
        c.name = "Brazil";
        te.listIndicators(c);
        
    }
    
}
