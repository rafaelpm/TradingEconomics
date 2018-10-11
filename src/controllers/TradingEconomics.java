package controllers;

import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlUnorderedList;
import java.util.ArrayList;
import java.util.List;
import models.Country;
import models.Indicator;

/**
 * Library to get data from site Trading Economics
 * @author Rafael P. Monteiro
 */
public class TradingEconomics extends Browser {
    
    public TradingEconomics(){
        
    }
    
    public List<Country> listCountries(){        
        List<Country> list = new ArrayList<>();
        
        try{
            String url = "https://tradingeconomics.com/countries";                                    
            HtmlPage hp = wc.getPage(url);
            
            HtmlDivision div = hp.getHtmlElementById("ctl00_ContentPlaceHolder1_ctl00_tableCountries");
            List<HtmlElement> lstUL = div.getElementsByAttribute("ul","class","list-unstyled");
            List<String> lstLI;
            Country contry = null;
            String continent="";
            
            for(HtmlElement ul: lstUL){                
                lstLI = getTags("<li class=",">","</li>",ul.asXml());
                continent = "";
                
                for(String li: lstLI){                          
                    if(continent.isEmpty()){                        
                        if(li.contains("G20")){
                            break;
                        }
                        continent = getTag("</i>","</a>",li);
                        if(continent.isEmpty()){
                            continent = li;
                        }else{
                            continent = continent.replace("\r", "");
                            continent = continent.replace("\n", "");
                            continent = continent.replace("\t", "").trim();                        
                        }
                    }else{
                        contry = new Country(continent);                        
                        contry.link = getTag("href=\"", "\"", li);
                        contry.name = getTag(">","</a>",li);
                        list.add(contry);
                    }                    
                }
                
            }            
            //Save("countries.html", continent);            
            //Save("contries.html",hp.asXml());            
        }catch(Exception e){
            e.printStackTrace();
        }
                
        return list;
    }
    
    public List<Indicator> listIndicators(String country){
        List<Country> lstCountries = listCountries();
        for(Country c: lstCountries){
            if(c.name.equals(country)){
                return listIndicators(c);
            }
        }
        return new ArrayList<>();
    }
    
    public List<Indicator> listIndicators(Country country){
        List<Indicator> list = new ArrayList<>();
        try{
            String url = "https://tradingeconomics.com"+country.link;                                    
            HtmlPage hp = wc.getPage(url);
            
            //Save("indicators.html", hp.asXml()); 
            HtmlDivision div = (HtmlDivision)hp.getElementById("ctl00_ContentPlaceHolder1_ctl00_Panel1");
            List<HtmlElement> lstTable = div.getElementsByAttribute("table", "class", "table table-hover");
            HtmlTable htable;
            
            Indicator indicator;
            
            for(HtmlElement he: lstTable){
                htable = (HtmlTable) he;
                //System.out.println(he.asXml());
                
                for(int l=2; l < htable.getRowCount(); l++){
                    indicator = new Indicator(country);
                    if(indicator.setData(htable,l)){
                        indicator.link = getTag("href=\"", "\"", htable.asXml());
                        list.add(indicator);
                    }                        
                }                
            }
            
            System.out.println("List indicators");
            for(Indicator i: list){
                System.out.println(i);
            }
            
        }catch(Exception e){
            e.printStackTrace();
        }
            
        return list;
    }
        
}
