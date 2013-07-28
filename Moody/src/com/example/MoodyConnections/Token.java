package com.example.MoodyConnections;

import java.net.URL;
import java.util.StringTokenizer;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

import com.example.moody.R;
 
import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.TextView;
 
public class Token extends Activity {
 
	
	
	
    // HTML page
    public String URL = "";
    // XPath query
    static final String XPATH_STATS = "";
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // init view layout
       
//    	StrictMode.ThreadPolicy policy = new StrictMode.
//    			ThreadPolicy.Builder().permitAll().build();
//    			StrictMode.setThreadPolicy(policy); 
//    			
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
 
       
        
        // decide output
        String value = "";
        try {
            value = getBlogStats();
            StringTokenizer tokens = new StringTokenizer(value, "\"");
            String userToken="";
            do{
            	userToken = tokens.nextToken();
            	           	
            }while(userToken.length()!=32);
          
            Log.d("MoodyDebug",userToken);
            ((TextView)findViewById(R.id.textView1)).setText(userToken);
        } catch(Exception ex) {
        ((TextView)findViewById(R.id.textView1)).setText(ex.toString());
      
        }
    }
 
    /*
     * get blog statistics
     */
    public String getBlogStats() throws Exception {
        String stats = "";
 
        // config cleaner properties
        
        HtmlCleaner htmlCleaner = new HtmlCleaner();
        CleanerProperties props = htmlCleaner.getProperties();
        props.setAllowHtmlInsideAttributes(false);
        props.setAllowMultiWordAttributes(true);
        props.setRecognizeUnicodeChars(true);
        props.setOmitComments(true);
 
        // create URL object
        URL url = new URL(URL);
        // get HTML page root node
        TagNode root = htmlCleaner.clean(url);
 
        // query XPath
        Object[] statsNode = root.evaluateXPath(XPATH_STATS);
        // process data if found any node
        if(statsNode.length > 0) {
            // I already know there's only one node, so pick index at 0.
            TagNode resultNode = (TagNode)statsNode[0];
            // get text data from HTML node
            stats = resultNode.getText().toString();
        }
 
        // return value
        return stats;
    }
}