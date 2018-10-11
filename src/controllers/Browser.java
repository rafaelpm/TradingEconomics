package controllers;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.Cookie;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class Browser extends Thread {
    
    protected WebClient wc;
    protected HtmlPage pageTemp;
    
    public String fileCookies = "cookie.temp";
    
    public Browser(){
        wc = new WebClient(BrowserVersion.CHROME); //                
        wc.getOptions().setCssEnabled(false);
        wc.getOptions().setJavaScriptEnabled(false);                
        wc.getOptions().setActiveXNative(false);
        wc.getOptions().setAppletEnabled(false);
    }
    
    protected double castToDouble(String value){
        value = value.replace("+", "");
        //value = value.replace("-", "");
        value = value.replace("%", "");
        value = value.replace(",", ".");
        return Double.parseDouble(value);
    }
    
    protected int castToInt(String value){        
        value = value.replace(".", "");
        return Integer.parseInt(value);
    }
        
    public boolean Save(String name, String data){
        if(name.isEmpty()){
            System.err.println("Please insert file name!");
            return false;
        }
        if(data == null || data.isEmpty()){
            System.err.println("Any data!");
            return false;
        }
        System.out.println("Recording: "+name);        
        try{
            OutputStream fout= new FileOutputStream(name);
            OutputStream bout= new BufferedOutputStream(fout);
            OutputStreamWriter out = new OutputStreamWriter(bout, "UTF-8");
            
            out.write(data);
            out.flush();
            
            out.close();
            bout.close();
            fout.close();
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }        
        return true;
    }
    
    public boolean ClearCookies(){
        try{
            wc.getCookieManager().getCookies().clear();
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return SaveCookies();
    }
    
    public boolean SaveCookies(){
        try{
            if(wc == null){
                return false;
            }
            ObjectOutput out = new ObjectOutputStream(new FileOutputStream(fileCookies));
            out.writeObject(wc.getCookieManager().getCookies());
            out.close();
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    public boolean OpenCookies(){
        try{
            if(wc == null){
                return false;
            }
            
            File f = new File(fileCookies);
            if(!f.exists()){
                return false;
            }            
            FileInputStream fis = new FileInputStream(f);
            ObjectInputStream in = new ObjectInputStream(fis);
            Set<Cookie> cookies = (Set<Cookie>) in.readObject();
            in.close();
            
            Iterator<Cookie> i = cookies.iterator();
            while (i.hasNext()) {
                wc.getCookieManager().addCookie(i.next());
            }
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    public int endTag = 0;
    public String getTag(String start, String end, String buffer){
        endTag = 0;
        int pos = buffer.indexOf(start);
        if(pos < 0){            
            return "";
        }
        pos += start.length();
        String temp = buffer.substring(pos);
        endTag = pos+temp.length();
        pos = temp.indexOf(end);
        if(pos < 0){            
            return temp;
        }
        temp = temp.substring(0,pos);
        endTag = pos;
        return temp.trim();
    }
    
    public String getTag(String start1, String start2, String end, String buffer){
        endTag = 0;
        int pos = buffer.indexOf(start1);
        if(pos < 0){            
            return "";
        }
        pos += start1.length();
        String temp = buffer.substring(pos);
        endTag = pos;
        
        pos = temp.indexOf(start2);
        if(pos < 0){            
            return temp;
        }
        pos += start2.length();
        temp = temp.substring(pos);
        endTag += pos;
        
        pos = temp.indexOf(end);
        if(pos < 0){            
            return temp;
        }
        temp = temp.substring(0,pos);
        endTag += pos;
        return temp.trim();
    }
    
    public List<String> getTags(String start1, String start2, String end, String buffer){
        List<String> out = new ArrayList<>();
        String tag;
        do{
            tag = getTag(start1,start2, end, buffer);
            if(tag.isEmpty()){
                break;
            }
            out.add(tag);
            buffer = buffer.substring(endTag);
        }while(!tag.isEmpty());
        return out;
    }
    
    public String getAfter(String end, String buffer, int pular){
        String res = buffer;
        while(pular > 0){
            res = getAfter(end, res);
            pular--;
        }
        return res.trim();
    }
    
    public String getAfter(String end, String buffer){
        int pos = buffer.indexOf(end);
        if(pos < 0){
            return "";
        }
        pos += end.length()+1;
        buffer = buffer.substring(pos);
        return buffer.trim();
    }
        
}
