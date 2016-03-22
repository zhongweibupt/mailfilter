package mailfilter;

import mailfilter.Analysis;
import mailfilter.Stemmer;

import java.io.*;
import java.util.List;
import java.util.ArrayList;

public class ContentParticiple {

	public String contentName;
	public String content;
	
	public void readContent(String filename)
	{
		this.contentName = filename;
		
		if (null != contentName)
        {
        	StringBuffer strBuff = new StringBuffer();
	        try
	        { 	
	        	FileInputStream in = new FileInputStream(contentName);
	        	try
	        	{ 
	        		while(true)
	        		{  
	        			int ch = in.read();
	        			if (ch < 0) 
		                {
		                	in.close();
		                	break;
		               	}
	        			
		                if (Character.isLetter((char) ch))
		                {
		                   ch = Character.toLowerCase((char) ch);
		                }
		           		strBuff = strBuff.append((char) ch);
		                
		           }
	           }
	           catch (IOException e)
	           {  
	        	   System.out.println("error reading " + contentName);
	           }
	        }
	        catch (FileNotFoundException e)
	        {  
	        	System.out.println("file " + contentName + " not found");
	        }
	        
	        this.content = strBuff.toString();
        }
	}
	
	public void setContent(String str)
	{
		this.content = str;
	}
	
	
	public String getContent()
	{
		return this.content;
	}
	
	
	public String getContentName()
	{
		return this.contentName;
	}
	
	
	public List<String> getWordsList()
	{		
		List<String> wordsList = new ArrayList<String>();
		wordsList = Stemmer.normalize(Analysis.participleToken(content, 0));
		
		return wordsList;
	}
	
	
}
