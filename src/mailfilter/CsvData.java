package mailfilter;

import java.io.BufferedReader;  
import java.io.FileInputStream;  
import java.io.InputStreamReader;  
import java.util.ArrayList; 
import java.util.List;
//import java.io.BufferedWriter;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.OutputStreamWriter;


public class CsvData {
	
	//CSV�ļ������ʽ
	public static final String ENCODE = "UTF-8";
	
    private FileInputStream fileInputStream;  
    private InputStreamReader inputStreamReader;  
    private BufferedReader bufferedReader;
    
    private String csvfileName;
    
	
	public CsvData(String fileName) throws Exception 
	{
		this.csvfileName = fileName;
		
		fileInputStream = new FileInputStream(this.csvfileName);  
		inputStreamReader = new InputStreamReader(fileInputStream, ENCODE);  
		bufferedReader = new BufferedReader(inputStreamReader);  
		
	}
	
	
       
    
	public String readLine() throws Exception {  
		  
        StringBuffer readLine = new StringBuffer();  
        
        {
            //  
            if (readLine.length() > 0) {  
                readLine.append("\r\n");  
            }  
            // һ��  
            String strReadLine = bufferedReader.readLine();  
  
            // readLine is Null  
            if (strReadLine == null) {  
                return null;  
            }  
            readLine.append(strReadLine);  
  
            /* ���˫������������ʱ�������ȡ�������л��е��������  
            if (countChar(readLine.toString(), '"', 0) % 2 == 1) {  
                bReadNext = true;  
            } else {  
                bReadNext = false;  
            } 
            */ 
        }  
        return readLine.toString();  
    }  
	
	
	//��CSV�ļ���һ��ת�����ַ������顣��ָ�����鳤�ȡ� 
    public List<String> fromCSVLinetoArray(String source) {  
        if (source == null || source.length() == 0) {  
            return new ArrayList<String>();  
        }  
        int currentPosition = 0;  
        int maxPosition = source.length();  
        int nextComma = 0;  
        List<String> rtnArray = new ArrayList<String>();  
        while (currentPosition < maxPosition) {  
            nextComma = nextComma(source, currentPosition);  
            rtnArray.add(nextToken(source, currentPosition, nextComma));  
            currentPosition = nextComma + 1;  
            if (currentPosition == maxPosition) {  
                rtnArray.add("");  
            }  
        }  
        return rtnArray;  
    }
    
    
    /** 
     * ���ַ������͵�����ת����һ��CSV�С������CSV�ļ���ʱ���ã� 
     */  
    public static String toCSVLine(String[] strArray) {  
        if (strArray == null) {  
            return "";  
        }  
        StringBuffer cvsLine = new StringBuffer();  
        for (int idx = 0; idx < strArray.length; idx++) {  
            String item = strArray[idx];  
            cvsLine.append(item);  
            if (strArray.length - 1 != idx) {  
                cvsLine.append(',');  
            }  
        }  
        return cvsLine.toString();  
    }  
  
    /** 
     * �ַ������͵�Listת����һ��CSV�С������CSV�ļ���ʱ���ã� 
     */  
    public static String toCSVLine(List<String> strArrList) {  
        if (strArrList == null) {  
            return "";  
        }  
        String[] strArray = new String[strArrList.size()];  
        for (int idx = 0; idx < strArrList.size(); idx++) {  
            strArray[idx] = (String) strArrList.get(idx);  
        }  
        return toCSVLine(strArray);  
    }  
	
    /** 
     * �ַ������͵�Listд��csv�ļ��������CSV�ļ���ʱ���ã� 
     */  
    
	
	/** 
     * ��ѯ��һ�����ŵ�λ�á� 
     * 
     * @param source ������ 
     * @param st  ������ʼλ�� 
     * @return ��һ�����ŵ�λ�á� 
     */  
    private static int nextComma(String source, int st) {  
        int maxPosition = source.length();  
        boolean inquote = false;  
        while (st < maxPosition) {  
            char ch = source.charAt(st);  
            if (!inquote && ch == ',') {  
                break;  
            } else if ('"' == ch) {  
                inquote = !inquote;  
            }  
            st++;  
        }  
        return st;  
    }  
	
    /** 
     * ȡ����һ���ַ��� 
     */  
    private static String nextToken(String source, int st, int nextComma) {  
        StringBuffer strb = new StringBuffer();  
        int next = st;  
        while (next < nextComma) {  
            char ch = source.charAt(next++);  
            if (ch == '"') {  
                if ((st + 1 < next && next < nextComma) && (source.charAt(next) == '"')) {  
                    strb.append(ch);  
                    next++;  
                }  
            } else {  
                strb.append(ch);  
            }  
        }  
        return strb.toString();  
    }  
	
}
