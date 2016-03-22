package mailfilter;


import mailfilter.TrainingDataManager;
import mailfilter.ClassifyResult;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class BayesFilter {
	
	public TrainingDataManager trainingDataManager;
	public List<ClassifyResult> classifyResultList;
	public double curr;
	
	public BayesFilter(String trainingDataFileName)
	{
		this.trainingDataManager = new TrainingDataManager(trainingDataFileName);
		this.classifyResultList = new ArrayList<ClassifyResult>();
	}
	
	public void train()
	{
		trainingDataManager.init();
		trainingDataManager.classifyAndSave();
		trainingDataManager.wordCount();
		trainingDataManager.computeCHI();
		trainingDataManager.computeWeight();
	}
	
	public void filter(String csvTest)
	{
		try
		{
			CsvData csvData = new CsvData(csvTest);
			DataSet dataSet = new DataSet(csvData);
			
			int currResult = 0;
			
			
			for(Map<String, String> data : dataSet.mailDataList)
			{
				String text = data.get("Html Body");
				System.out.println(data.get("Html Body"));
				
				TextSegment textSegment = new TextSegment();
				textSegment.readText(text);
				
				List<String> wordsList = textSegment.getWordsList();
				WordCount wordCount = new WordCount(wordsList);
				
				List<String> stopWordsList = new ArrayList<String>();
				wordCount.countAndFilter(stopWordsList);
				
				//wordCountMap.put(className, wordCount);
				
				
				List<Map.Entry<String,Integer>> wordsCountList = wordCount.getWordCountList();
				
				double maxProbility = 0;
				String result = new String("default");
				
				for(int i = 0; i < trainingDataManager.classNameList.size(); i++)
				{
					ClassifyResult classifyResult = new ClassifyResult();
					//System.out.println("classifyResult.probility");
					classifyResult.probility = trainingDataManager.calculateProd(wordsCountList,trainingDataManager.classNameList.get(i));
					classifyResult.classification = trainingDataManager.classNameList.get(i);
					//System.out.println("classifyResult.probility");
					
					if(classifyResult.probility >= maxProbility)
					{
						result = classifyResult.classification;
						maxProbility = classifyResult.probility;
					}
					
					System.out.println(classifyResult.classification + ":" + classifyResult.probility*10);
					
					classifyResultList.add(classifyResult);
				}
				System.out.println(result + "-" + data.get("Classification"));
				if(result == data.get("Classification"))
					currResult++;
			}
			
			this.curr = (double)currResult/(double)dataSet.mailDataList.size();
			System.out.println(currResult + "-" + dataSet.mailDataList.size());
		}
		catch(Exception e)
		{
			System.out.println("ERROR");
		}
		
	}
	
	
	public void displayCurr()
	{
		System.out.println(this.curr);
	}
	
	public static void main(String argv[])
	{
		BayesFilter bayesFilter = new BayesFilter("C:\\Users\\zhwei\\workspace\\mailfilter\\train1.csv");
		
		bayesFilter.train();
		
		String csvTest = "C:\\Users\\zhwei\\workspace\\mailfilter\\test1.csv";
		bayesFilter.filter(csvTest);
		
		bayesFilter.displayCurr();
		
	}
	
	
	
}
