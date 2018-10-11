package models;

import com.gargoylesoftware.htmlunit.html.HtmlTable;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import org.apache.commons.lang3.math.NumberUtils;

/**
 *
 * @author Rafael P. Monteiro
 */
public class Indicator {
    
    public static final String Months[] = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
    
    public Country country = null;
    
    public String name="";
    public String link="";    
    public double last=0;
    public String unit_last="";
    public Timestamp reference = null;
    public double previous = 0;
    public double[] range = new double[]{0,0};
    public String frequency = "";
    
    private String arr[];
    
    public Indicator(Country country){
        this.country = country;
    }
    
    @Override
    public String toString(){       
        return name+" / "+last+" "+unit_last+" / "+getReferenceToStr("MM/YY")+" / "+frequency;
    }
    
    public String getReferenceToStr(String format){
        if(reference == null){
            return "--";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(reference);
    }
    
    private Calendar getToday(){
        Calendar today = Calendar.getInstance();        
        today.set(Calendar.DATE, 1);
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        return today;
    }
                
    public boolean setData(HtmlTable htable, int line){
        int col = 0;
        
        try{            
            name = htable.getCellAt(line, col).asText(); col++;               
            if(!setLast(htable.getCellAt(line, col).asText())){
                return false;
            }
            col++;
            setReference(htable.getCellAt(line, col).asText()); col++;
            setPrevious(htable.getCellAt(line, col).asText()); col++;
            setRange(htable.getCellAt(line, col).asText()); col++;
            frequency = htable.getCellAt(line, col).asText(); col++;   
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }    
    
    public boolean setLast(String value){
        if(value.contains(" ")){
            arr = value.split(" ");            
            if(arr.length == 0 || 
                !NumberUtils.isNumber(arr[0])){
                return false;
            }
            last = Double.parseDouble(arr[0]);
            unit_last = arr[1];            
        }else if(NumberUtils.isNumber(value)){
            last = Double.parseDouble(value);            
        }else{
            return false;
        }
        return true;
    }
    
    public void setReference(String value){        
        if(value.contains("/")){
            arr = value.split("/");
            int month = getMonth(arr[0]);
            if(month <= 0){
                System.err.println("The month doesn't in list: "+arr[0]);
                return;
            }
            int year = Integer.parseInt(arr[1]);
            Calendar today = getToday();
            today.set(Calendar.MONTH, month-1);
            today.set(Calendar.YEAR, year+2000);
                        
            reference = new Timestamp(today.getTimeInMillis());
        }/*else{
            System.err.println("Invalid reference");
        }*/
    }
    
    public void setPrevious(String value){
        if(NumberUtils.isNumber(value)){
            previous = Double.parseDouble(value);
        }
    }
    
    public void setRange(String value){
        if(value.contains(":")){
            arr = value.split(":");
            if(arr.length > 0){
                range[0] = Double.parseDouble(arr[0]);
                range[1] = Double.parseDouble(arr[1]);
            }            
        }
    }
        
    private int getMonth(String m){
        for(int i=0; i < Months.length; i++){
            if(m.contains(Months[i])){
                return i+1;
            }
        }
        return -1;
    }
}
