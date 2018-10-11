package models;

/**
 *
 * @author Rafael P. Monteiro
 */
public class Country {
    
    public String name;
    public String link;
    public String continent;//or group
    
    public Country(String continent){
        this.continent = continent;
    }
        
}
