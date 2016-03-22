package mailfilter;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class WordCount {
	
	public List<String> wordsList;
	public Map<String,Integer> wordCountMap;
	public List<Map.Entry<String,Integer>> wordCountList;
	
	public WordCount(List<String> strList)
	{
		this.wordsList = strList;
		this.wordCountMap = new HashMap<String, Integer>();
		this.wordCountList = new ArrayList<Map.Entry<String,Integer>>();
	}
	
	public void count(String word)
	{
		Integer freq = wordCountMap.get(word);
		wordCountMap.put(word, freq == null ? 1 : freq + 1);
	}
	
	public void filter(List<String> stopWordsList)
	{
		if(!stopWordsList.isEmpty())
		{
			for(String stopWord : stopWordsList)
			{
				if(null != wordCountMap.get(stopWord))
				{
					wordCountMap.remove(stopWord);
				}
			}
		}
	}
	
	public void sortCount()
	{
		this.wordCountList = new ArrayList<Map.Entry<String,Integer>>(this.wordCountMap.entrySet());
		
		Collections.sort(this.wordCountList,new Comparator<Map.Entry<String,Integer>>() {
			 //Ωµ–Ú≈≈–Ú
			 public int compare(Map.Entry<String,Integer> o1,
					 Map.Entry<String,Integer> o2) {
				 int a = o1.getValue();
				 
				 int b = o2.getValue();
				 
		    	 if (a < b) {  
	                    return 1;  
	                } else if (a == b) {  
	                    return 0;  
	                } else {  
	                    return -1;  
	                } 
			 }
		 });

	}
	
	public int queryCount(String word)
	{	
		return this.wordCountMap.get(word) == null ? 0 : this.wordCountMap.get(word);
	}
	
	
	public void countAndFilter(List<String> stopWordsList)
	{
		for(String word : wordsList)
		{
			count(word);
		}
		filter(stopWordsList);
		sortCount();
	}
	
	
	
	public Map<String,Integer> getWordCountMap()
	{
		return this.wordCountMap;
	}
	
	public List<Map.Entry<String,Integer>> getWordCountList()
	{
		return this.wordCountList;
	}
	
	public void clear()
	{
		this.wordsList.clear();
		this.wordCountMap.clear();
		this.wordCountList.clear();
	}
	
	
}
