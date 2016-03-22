package mailfilter;

import mailfilter.CsvData;
import mailfilter.DataSet;
import mailfilter.TextSegment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.io.File;
//import java.math.*;

public class TrainingDataManager {
	
	public int numOfText;
	public int numOfWord;
	
	public Map<String, Float> averLengthMap;
	
	public String csvDataFileName = new String();
	public String csvFileSavePath = new String("C:\\Users\\zhwei\\workspace\\mailfilter\\train\\");
	
	public List<String> classNameList;//train包含的分类
	public Map<String, Integer> textCountMap;
	public Map<String, Map<String, Integer>> textCountTableMap;
	
	public Map<String, Integer> wordTableMap;//单词表
	public Map<String, Map<String, Integer>> wordCountTableMap;//String,String:class,word  Integer:class中word个数
	
	public Map<String, Map<String, Float>> CHITableMap;//String,String:class,word
	public Map<String, Map<String, Float>> weightTableMap;
	
	
	public TrainingDataManager(String fileName)
	{
		this.csvDataFileName = fileName;
		this.numOfText = 0;
		this.numOfWord = 0;
		
		this.averLengthMap = new HashMap<String, Float>();
		
		this.classNameList = new ArrayList<String>();
		
		this.textCountMap = new HashMap<String, Integer> ();
		this.textCountTableMap = new HashMap<String, Map<String, Integer>>();
		
		this.wordTableMap = new HashMap<String, Integer>();
		this.wordCountTableMap = new HashMap<String, Map<String, Integer>>();
		
		this.CHITableMap = new HashMap<String, Map<String, Float>>();
		this.weightTableMap = new HashMap<String, Map<String, Float>>();
		
		//classifyAndSave();
		//wordCount();
	}
	
	public void init()
	{
		deleteDir(csvFileSavePath);
	}
	
	public void setSavePath(String savePath)
	{
		this.csvFileSavePath = savePath;
	}
	
	
	
	public void classifyAndSave()
	{
		try
		{
			System.out.println("LENGTH" + csvDataFileName);
			CsvData csvData = new CsvData(csvDataFileName);
			System.out.println("LENGTH" + csvDataFileName);
			DataSet dataSet = new DataSet(csvData);
			
			
			createDir(csvFileSavePath);
			
			dataSet.classify(csvFileSavePath);
			
		}
		catch(Exception e)
		{
			System.out.println("ERROR??");
		}
	}
	
	
	public void wordCount()
	{
		File root = new File(csvFileSavePath);
		File[] files = root.listFiles();
		
		numOfText = 0;
		
		for(File file:files)
		{
			
			Map<String, Integer> wordTableOfThisClassMap = new HashMap<String, Integer>();
			
			String classFileName = file.getName();
			String className = classFileName.substring(0, classFileName.indexOf("."));
			classNameList.add(className);
			
			String text = new String();

			
			//String fileName = "C:\\Users\\zhwei\\workspace\\test\\src\\test\\test.txt";
			//contentParticiple.readContent(fileName);
			try
			{
				CsvData csvData = new CsvData(csvFileSavePath + classFileName);
				DataSet dataSet = new DataSet(csvData);
				
				float length = 0.0F;
				
				for(Map<String, String> data : dataSet.mailDataList)
				{
					text = data.get("Html Body");
					//System.out.println(data.get("Html Body"));
					
					TextSegment textSegment = new TextSegment();
					textSegment.readText(text);
					
					//System.out.println("========原文========\n");
					
					List<String> wordsList = textSegment.getWordsList();
					length = length + wordsList.size();
					
					WordCount wordCount = new WordCount(wordsList);
					
					List<String> stopWordsList = new ArrayList<String>();
					//wordCount.filter(stopWordsList);
					//wordCountMap.put(className, wordCount);
					wordCount.countAndFilter(stopWordsList);
					
					List<Map.Entry<String,Integer>> wordsCountList = wordCount.getWordCountList();
					
					for(int i = 0; i < wordsCountList.size(); i++)
					{
						Map.Entry<String,Integer> itemWord = wordsCountList.get(i);
						
						Integer freq = wordTableOfThisClassMap.get(itemWord.getKey());
						wordTableOfThisClassMap.put(itemWord.getKey(), freq == null ? itemWord.getValue() : freq + itemWord.getValue());
						
						Integer freqOther = wordTableMap.get(itemWord.getKey());
						wordTableMap.put(itemWord.getKey(), freqOther == null ? itemWord.getValue() : freqOther + itemWord.getValue());
						
						Map<String, Integer> textMap = textCountTableMap.get(className);
						
						Integer count;
						if(null == textMap)
						{
							textMap = new HashMap<String, Integer>();
						}
						count = textMap.get(itemWord.getKey());
						textMap.put(itemWord.getKey(), count == null ? 0 : count + 1);

						
						textCountTableMap.put(className, textMap);

					}
					
					
					
					//System.out.println("ALL AMOUNT OF WORDS : " + wordsList.size()+ "\n");
					//System.out.println("ALL KINDS OF WORDS : " + wordsCountList.size());
					
					
				}
				averLengthMap.put(className, length/(float)dataSet.mailDataList.size());
				wordCountTableMap.put(className, wordTableOfThisClassMap);
				
				textCountMap.put(className, dataSet.mailDataList.size());
				numOfText = numOfText + dataSet.mailDataList.size();

			}
			catch(Exception e)
			{
				e.printStackTrace();
				System.out.println("ERROR");
			}	
			
		}	
	}
	
	public void computeCHI()
	{
		for(String classStr : this.classNameList)
		{
			Map<String, Float> CHIMap = new HashMap<String, Float>();
			
			Map<String, Integer> wordMap = wordCountTableMap.get(classStr);
			
			float result = 1.0F;
			float numberOfThisClass = (float)textCountMap.get(classStr);
			
			//Map<String, Integer> map = getCountHasWordOfClassification(word);
	        
	        for(Map.Entry<String, Integer> entry : wordMap.entrySet())
	        {
	        	String word = entry.getKey();
	        	float A = (float)textCountTableMap.get(classStr).get(word);
		        float B = 0.0F;
		        
		        for (String elemOfList : classNameList) {
		        	if(elemOfList != classStr)
		        	{
		        		if(textCountTableMap.get(elemOfList) != null && textCountTableMap.get(elemOfList).get(word) != null)
		        			B = B + (float)textCountTableMap.get(elemOfList).get(word);
		        	}
		        }
		        
		        float C = numberOfThisClass - A;
		        float D = (float)numOfText - A - B - C;
		        
		        result = (float)numOfText * (A*D - B*C)*(A*D - B*C)/((A+B)*(C+D)*(A+C)*(B+D));
		        if(0 == A)
		        	result = 1.0F;
		        //System.out.println(result);
		        
		        result = (float)Math.sqrt((double)result);
		        System.out.println(A+":"+B +":"+C+":"+D+":"+numOfText);
		        
		        CHIMap.put(word, result);
	        }		
	        
	        CHITableMap.put(classStr, CHIMap);
		}	
	}
	
	public void computeWeight()
	{
		for(String classStr : this.classNameList)
		{
			Map<String, Float> weightMap = new HashMap<String, Float>();
			
			Map<String, Integer> wordMap = wordCountTableMap.get(classStr);
			
			float freqOfClass = (float)textCountMap.get(classStr);
			
			float numOfWordsInClass = (float)wordMap.size();
			float averLengthInClass = averLengthMap.get(classStr);
			
			
			float result = 0.0F;
			
			//Map<String, Integer> map = getCountHasWordOfClassification(word);
			
				
	        
	        for(Map.Entry<String, Integer> entry : wordMap.entrySet())
	        {
	        	String word = entry.getKey();
	        	
	        	float freqOfWordInClass = (float)textCountTableMap.get(classStr).get(word);
	        	
	        	if(numOfWordsInClass > 0 && averLengthInClass > 0)
	        		result = (float)Math.log((freqOfWordInClass + 1)/(freqOfClass + 2))/(numOfWordsInClass * averLengthInClass);
		      
		        weightMap.put(word, result);
	        }		
	        
	        weightTableMap.put(classStr, weightMap);
		}	
	}
	
	
	
	
	
	
	
	/**
	 * calculateProd(List<Map.Entry<String,Integer>> wordsCountList, String classStr)
	 * 
	 * 计算文本属性向量wordsCountList在给定分类中的类条件概率
	 * @param wordsCountList 给定文本属性向量
	 * @param classStr 给定的分类
	 * @return 给定条件下的先验概率
	 **/
	public float calculateProd(List<Map.Entry<String,Integer>> wordsCountList, String classStr)
	{
		float result = 1.0F;
		 // 类条件概率连乘
		
		
		for (int i = 0; i < wordsCountList.size(); i++)                   //步骤4
		{
			String word = wordsCountList.get(i).getKey();
			 //因为结果过小，因此在连乘之前放大10倍，这对最终结果并无影响，因为我们只是比较概率大小而已
			
			//System.out.println(CHITableMap.get(classStr).get(word));
			if(CHITableMap.get(classStr).get(word) != null)
				result *= CHITableMap.get(classStr).get(word) ;//* weightTableMap.get(classStr).get(word);//*zoomFactor;
			
		}
		// 再乘以先验概率
		
		
		result *= calculatePriorProbability(classStr);                   //步骤5
		
		//System.out.println(calculatePriorProbability(classStr));
		
		return result;
	}
	
	/**
	 * calculateProd(String word, String classStr)
	 * 
	 * 计算给定文本属性word在给定分类classStr中的类条件概率
	 * @param word 给定文本属性
	 * @param classStr 给定的分类
	 * @return 给定条件下的先验概率
	 
	public float calculateWordProd(String word, String classStr)
	{
		float result = 0F;
		float numberOfThisClass = contentCountMap.get(classStr);
		
        float numberOfWordInClass = getCountContainWordOfClassification(word, classStr);
        float numberOfClass = numberOfThisClass;
        float V = this.wordCountMap.size();//单词表
        //System.out.println(numberOfWordInClass);
        result = (numberOfWordInClass + 1) / (numberOfClass + (float)0F + V); //为了避免出现0这样极端情况，进行加权处理
        return result;
	}
	
	public float calculateWordProd(String word, String classStr)
	{//卡方检验
		float result = 1.0F;
		float numberOfThisClass = contentCountMap.get(classStr);
		
		Map<String, Integer> map = getCountHasWordOfClassification(word);
		
        float A = (float)map.get(classStr);
        float B = 0.0F;
        
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
        	if(entry.getKey() != word)
        	   B = B + (float)entry.getValue();
        }
        
        float C = numberOfThisClass - A;
        
        float D = (float)sumOfContent - A - B - C;
        
        result = (float)sumOfContent * (A*D - B*C)*(A*D - B*C)/((A+B)*(C+D)*(A+C)*(B+D));
        if(0 == A)
        	result = 1.0F;
        //System.out.println(result);
        return (float)Math.sqrt((double)result);
	}	
	
	public Map<String, Integer> getCountHasWordOfClassification(String word)
	{
		
		Map<String, Integer> resultMap = new HashMap<String, Integer>();
		
		for(String className : classNameList)
        {
			int result = 0;
			try
			{
				
				CsvData csvData = new CsvData(csvFileSavePath + className + ".csv");
				
				DataSet dataSet = new DataSet(csvData);
				
				for(Map<String, String> data : dataSet.dataList)
				{
					String content = data.get("Html Body");
					
					ContentParticiple contentParticiple = new ContentParticiple();
					contentParticiple.setContent(content);
					
					List<String> wordsList = contentParticiple.getWordsList();
					WordCount wordCount = new WordCount(wordsList);
					
					List<String> stopWordsList = new ArrayList<String>();
					wordCount.filter(stopWordsList);
					
					if(wordCount.wordCount.get(word) > 0 && wordCount.wordCount.get(word) != null)
						result++;
				}
				
			}
			catch(Exception e)
			{
				//System.out.println("ERROR");
			}
			
			resultMap.put(className, result);
        }
		
		
		return resultMap;
		
	}
	
	
	
	public int getCountContainWordOfClassification(String word, String classStr)
	{
		int result = 0;
		
		try
		{
			CsvData csvData = new CsvData(csvFileSavePath + classStr + ".csv");
			
			DataSet dataSet = new DataSet(csvData);
			
			for(Map<String, String> data : dataSet.dataList)
			{
				String content = data.get("Html Body");
				
				ContentParticiple contentParticiple = new ContentParticiple();
				contentParticiple.setContent(content);
				
				List<String> wordsList = contentParticiple.getWordsList();
				WordCount wordCount = new WordCount(wordsList);
				
				List<String> stopWordsList = new ArrayList<String>();
				wordCount.filter(stopWordsList);
				
				result = result + wordCount.wordCount.get(word);
				
			}
			
		}
		catch(Exception e)
		{
			//System.out.println("ERROR");
		}
		
		return result;
		
	}
	
	**/
	
	
	
	/**
	 * calculatePriorProbability(String classStr)
	 * 
	 * 计算先验概率
	 * @param classStr 给定的分类
	 * @return 给定条件下的先验概率
	 **/
	public float calculatePriorProbability(String classStr)
	{
		float result = 0F;

		float numberOfThisClass = textCountMap.get(classStr);     //训练文本集中在给定分类下的训练文本数目
		                          
		result = numberOfThisClass / numOfText;   //训练文本集中所有的文本数目
		
		return result;
	}
	
	
	
	
	 private static boolean createDir(String destDirName) {  
	        File dir = new File(destDirName);  
	        if (dir.exists()) {  
	            System.out.println("Create dir : " + destDirName + " failed, it is existed.");  
	            return false;  
	        }  
	        if (!destDirName.endsWith(File.separator)) {  
	            destDirName = destDirName + File.separator;  
	        }  
	        //
	        if (dir.mkdirs()) {  
	            System.out.println("Create dir : " + destDirName + " successed.");  
	            return true;  
	        } else {  
	            System.out.println("Create dir : " + destDirName + " failed.");  
	            return false;  
	        }  
	    }  
	 
	 /**
	     * 递归删除目录下的所有文件及子目录下所有文件
	     * @param dir 将要删除的文件目录
	     * @return boolean Returns "true" if all deletions were successful.
	     *                 If a deletion fails, the method stops attempting to
	     *                 delete and returns "false".
	     */
	    private static boolean deleteDir(String destDirName) {
	    	
	    	File dir = new File(destDirName);
	        if (dir.isDirectory()) {
	            String[] children = dir.list();

	            for (int i=0; i<children.length; i++) {
	                boolean success = deleteDir(children[i]);
	                if (!success) {
	                    return false;
	                }
	            }
	        }
	        // 目录此时为空，可以删除
	        return dir.delete();
	    }
	 
	 
	 public static void main(String argv[])
	 {
		 TrainingDataManager tdm = new TrainingDataManager("C:\\Users\\zhwei\\Desktop\\1.csv");
		 tdm.classifyAndSave();
		 tdm.wordCount();
		 System.out.println(tdm.numOfText + "\n"); 
		 
		 
		 int s = 0;
		 
		 for(Map.Entry<String, Integer> m : tdm.textCountMap.entrySet())
		 {
			 System.out.println(m.getKey() + ":" + m.getValue());
			 s = s + m.getValue();
		 }
		 System.out.println(s);
	 }
	
	
	
	

}
