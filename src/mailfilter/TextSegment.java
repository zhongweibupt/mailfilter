package mailfilter;

import mailfilter.Analysis;
import mailfilter.Stemmer;

import java.io.*;
import java.util.List;
import java.util.ArrayList;

/**
*
*  TextSegment.java   
*
*  @version £º 1.0
*  
*  @author  £º Zhongwei Zhao    <a href="zhongwei_bupt@163.com">·¢ËÍÓÊ¼þ</a>
*     
*  TODO     : 
*
*/

public class TextSegment {

	private String textFileName;
	private String text;
	
	public TextSegment()
	{
		this.text = null;
		this.textFileName = null;
	}
	
	public void readText(String str)
	{
		this.text = str;
	}
	
	
	public void readTextFile(String filename)
	{
		this.textFileName = filename;
		
		if (null != textFileName)
        {
        	StringBuffer strBuff = new StringBuffer();
	        try
	        { 	
	        	FileInputStream in = new FileInputStream(textFileName);
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
	        	   System.out.println("error reading " + textFileName);
	           }
	        }
	        catch (FileNotFoundException e)
	        {  
	        	System.out.println("file " + textFileName + " not found");
	        }
	        
	        this.text = strBuff.toString();
        }
	}
	
	
	public String getText()
	{
		return this.text;
	}
	
	
	public String getTextFileName()
	{
		return this.textFileName;
	}
	
	
	public List<String> getWordsList()
	{		
		List<String> wordsList = new ArrayList<String>();
		Analysis analysis = new Analysis(text,"Standard");
		
		wordsList = Stemmer.normalize(analysis.segmentToken());
		
		return wordsList;
	}
	
	
}
