package mailfilter;

import test.CsvData;
import test.DataSet;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import test.ContentParticiple;
import test.WordCount;

public class WordFilter {
	
	
	public static void main(String argv[])
	{
		String csvFileName = "C:\\Users\\zhwei\\Desktop\\csv\\Payment confirmation.csv";
		String content = new String();
		
		ContentParticiple contentParticiple = new ContentParticiple();
		
		//String fileName = "C:\\Users\\zhwei\\workspace\\test\\src\\test\\test.txt";
		//contentParticiple.readContent(fileName);
		try
		{
			CsvData csvData = new CsvData(csvFileName);
			DataSet dataSet = new DataSet(csvData);
			
			for(Map<String, String> data : dataSet.dataList)
			{
				content = data.get("Html Body");
				System.out.println(data.get("Html Body"));
				
				contentParticiple.setContent(content);
				
				System.out.println("========т╜нд========\n");
				System.out.println(contentParticiple.getContent());
				System.out.println("\n");
				
				
				List<String> wordsList = contentParticiple.getWordsList();
				WordCount wordCount = new WordCount(wordsList);
				
				List<String> stopWordsList = new ArrayList<String>();
				wordCount.filter(stopWordsList);
				
				List<Map.Entry<String,Integer>> wordsCountList = wordCount.getWordCountList();
				
				for(int i = 0; i < 10; i++)
				{
					Map.Entry<String,Integer> itemWord = wordsCountList.get(i);
					System.out.println(itemWord.getKey() + " : " + itemWord.getValue());
					System.out.println("\n");
				}
				
				System.out.println("ALL AMOUNT OF WORDS : " + wordsList.size()+ "\n");
				System.out.println("ALL KINDS OF WORDS : " + wordsCountList.size());
				
			}

		}
		catch(Exception e)
		{
			System.out.println("ERROR");
		}	
		
		
	}

}
