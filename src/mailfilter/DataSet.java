package mailfilter;

import mailfilter.CsvData;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class DataSet {
	
	public static String[] classes = new String[]{"AP processing errors","Audit request","Bank queries","Bank rejections","BMG","CBCP escalation","CBCP request","Claim status","Concur issues","Contract Related","Credit Card Maintenance  ","FYI","GL requests/ PC uplifts","Invoice Status","Invoices for scan","JBA/SMF Maintenance","Manual payment request","Matching Report","Missing documents/ supporting documentation","Non AP documents","Non PO Invoices (coding information, VAT)","OIR","Open Item reports","Other","Out of office messages","Payment confirmation","Payment reminder","Payments (MPR, urgent payment requests, BMG)","PO related","Process updates","Proof of payment","Recall","Two Way match report","Urgent payment request","Vendor master data","Vendor statement","Web Ex Trainings"};
	public static final String ENCODE = "UTF-8";
	
	public int size;
	public int number;
	
	public List<String> key;
	public List<String> lineDataList;
	public List<Map<String, String>> mailDataList;
	public Map<String,List<String>> mailDataClassified;
	
	private FileOutputStream out;
    private OutputStreamWriter osw;
    private BufferedWriter bw;
	
	
	public DataSet(CsvData csvData) throws Exception 
	{
		this.size = 0;
		this.number = 0;
		this.mailDataList = new ArrayList<Map<String, String>>();
		this.lineDataList = new ArrayList<String>();
		this.mailDataClassified = new HashMap<String,List<String>>();
		
		this.out = null;
		this.osw = null;
		this.bw = null;
		
		readCsv(csvData);
	}
	
	public void readCsv(CsvData csvData)
	{
		size = 0;
		number = -1;
		
		while(true)
		{
			try
			{
				String line = csvData.readLine();
				if(null != line)
				{
					lineDataList.add(line);
					List<String> dataOfLine = new ArrayList<String>();
					dataOfLine = csvData.fromCSVLinetoArray(line);
					if(number < 0)
					{
						size = dataOfLine.size();
						key = dataOfLine;
					}
					else
					{
						Map<String,String> data = new HashMap<String,String>();
						int i = 0;
						for(String value : dataOfLine)
						{
							data.put(key.get(i++), value);
						}
						mailDataList.add(data);
					}
					number++;
				}
				else
				{
					break;
				}
			}
			catch(Exception e)
			{
				break;
			}
		}	
	}
	
	public boolean writeCsv(String fileName, List<String> strDataList) {  
    	
    	boolean isSucess=false;

        try {
            
        	out = new FileOutputStream(fileName);
            osw = new OutputStreamWriter(out, ENCODE);
            bw =new BufferedWriter(osw);
        	
            if(strDataList!=null && !strDataList.isEmpty()){
            	int count = 0;
                for(String data : strDataList){
                	count++;
                	if(count < strDataList.size())
                		bw.append(data).append("\r");
                	else
                		bw.append(data);//.append("\0");
                }
            }
            isSucess=true;
        } catch (Exception e) {
            isSucess=false;
        }finally{
            if(bw!=null){
                try {
                    bw.close();
                    bw=null;
                } catch (IOException e) {
                    e.printStackTrace();
                } 
            }
            if(osw!=null){
                try {
                    osw.close();
                    osw=null;
                } catch (IOException e) {
                    e.printStackTrace();
                } 
            }
            if(out!=null){
                try {
                    out.close();
                    out=null;
                } catch (IOException e) {
                    e.printStackTrace();
                } 
            }
        }
        
        return isSucess;
    }
	
	
	public List<Map<String, String>> getData()
	{
		return this.mailDataList;
	}
	
	public List<String> getKey()
	{
		return this.key;
	}
	
	public int getSize()
	{
		return this.size;
	}
	
	public int getNumber()
	{
		return this.number;
	}
	
	public Map<String,List<String>> classify(String savePath)
	{
		int i = 1;
		for(Map<String, String> data : mailDataList)
		{
			String classification = data.get("Classification");
			List<String> strList = mailDataClassified.get(classification);
			if(null == strList)
			{
				strList = new ArrayList<String>();
				strList.add(lineDataList.get(0));
				
			}
			strList.add(lineDataList.get(i));
			mailDataClassified.put(classification, strList);
			i++;
		}
		
		for(int j = 0; j < classes.length; j++)
		{
			try
			{
				String fileName = savePath + classes[j] + ".csv";
				List<String> strList = mailDataClassified.get(classes[j]);
				this.writeCsv(fileName, strList);
			}
			catch(Exception e)
			{
				System.out.println("ERROR");
				break;
			}
		}
		
		return mailDataClassified;
	}
	
	/*
	public static void main(String argv[])
	{	
		String fileName = "C:\\Users\\zhwei\\Desktop\\1.csv";
		String savePath = "C:\\Users\\zhwei\\Desktop\\csv\\";
		
		try
		{
			CsvData csvData = new CsvData(fileName);
			DataSet dataSet = new DataSet(csvData);
			
			System.out.println(dataSet.classes.length);
			
			Map<String,List<String>> map = dataSet.classify(savePath);
			
			System.out.println(map.size());
			System.out.println(dataSet.getNumber());
			System.out.println(dataSet.getKey().get(0));
			
			for(Map<String, String> data : dataSet.dataList)
			{
				//System.out.println(data.get("Id"));
			}
			
		}
		catch(Exception e)
		{
			System.out.println("Error");
		}
		
	}
	*/
	
	
}
