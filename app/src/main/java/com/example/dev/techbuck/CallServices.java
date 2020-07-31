package com.example.dev.techbuck;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

public class CallServices
{
	
	byte[] data;//for storage data
    HttpPost httppost;//post data on url
    StringBuffer buffer;//temp storage
    HttpResponse response;//return response from server
    DefaultHttpClient httpclient;//execute url in background
    InputStream inputStream;//return response
    
    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();//store key and value pair data
    
    @SuppressLint("NewApi")
		public String CallServices(Context context,String link,String method,ArrayList<String> key,ArrayList<String> value)
	    {
			// TODO Auto-generated constructor stub
	    	String buffervalue="";
	    	try
	    	{
	    		//set thread policy for access network thread
	    		//NetworkOnMainThreadException
	    		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
     		    StrictMode.setThreadPolicy(policy); 
     		    
     		    //to connect with http url
     		    httpclient= new DefaultHttpClient();
     		
     		    //to pass data on url
     		    httppost = new HttpPost(link);
     		    //namevaluepair for add key and value
     		    nameValuePairs.add(new BasicNameValuePair("method",method));
	  			//LOOP FOR KEY SIZE L
     		    //ENGTH
     		    for(int i=0;i<key.size();i++)
		  		{
		  			nameValuePairs.add(new BasicNameValuePair(key.get(i),value.get(i)));
		  		}
	  			//convert entry into url 
	  			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	  			//execute that
	  			response = httpclient.execute(httppost);
	  			//TO GET CONTENT AND STORE IN INPUT STREAM
	  			inputStream = response.getEntity().getContent();
	  			
				//BUFFER STORAGE ARRAY
	  			data = new byte[512];
				//BUFFER
	  			buffer = new StringBuffer();
	  			int len = 0;
				
	  			//loop for read byte data
	  			
	  			for(len=0;len!=-1;len=inputStream.read(data))
                {
	  				/*Log.e("IS DATA", data+"");
	  				Log.e("msg", len+"");*/
	  				
	  				/*Log.e("test",new String(data,0,len));*/
	  				
	  				//append data in buffer storage
	  				buffer.append(new String(data,0,len));
                  //buffer.append(new String(data, offset, byteCount))
                }
	  			
	  			//close input stream
	  			inputStream.close();
	  			//convert into string
	  			buffervalue=buffer.toString();
	  			//return value
	  			return buffervalue;
	    	}
	  		catch(Exception e)
	  		{
	  			Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
	  		}
			return buffervalue;
		}
}
