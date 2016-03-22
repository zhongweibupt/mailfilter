package mailfilter;

import java.io.*;
import java.util.List;
import java.util.ArrayList;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;

/**
 *
 *  Analysis.java   
 *
 *  @version ： 1.0
 *  
 *  @author  ： Zhongwei Zhao    <a href="zhongwei_bupt@163.com">发送邮件</a>
 *     
 *  TODO     : 
 *
 */
public class Analysis {

    /**
     *
     * Description:       对文本分词处理
     * @param text        待分词的文本
     * @param analyzer    分词器
     *
     */
	public Analyzer analyzer;
	public String text;
	public String mode;
	
	public Analysis(String str, String analyzerMode)
	{
		this.text =  str;
		this.mode = analyzerMode;
    	
    	if("Stop" == mode)
    	{
    		analyzer = new StopAnalyzer(Version.LUCENE_36);
    	}
    	else if("Simple" == mode)
    	{
    		analyzer = new SimpleAnalyzer(Version.LUCENE_36);
    	}
    	else if("Simple" == mode)
    	{
    		analyzer = new WhitespaceAnalyzer(Version.LUCENE_36);
    	}
    	else if("Standard" == mode)
    	{
    		analyzer = new StandardAnalyzer(Version.LUCENE_36);
    	}
    	else
    	{
    		System.out.println("ERROR:" + analyzerMode + " is not a available analyzer.");
    	}
	}
	
    public List<String> segmentToken()
    {		
    	List<String> wordsList = new ArrayList<String>();
    	
        try {
            //将一个字符串创建成Token流
            TokenStream stream  = analyzer.tokenStream("", new StringReader(text));
            //保存相应词汇
            CharTermAttribute cta = stream.addAttribute(CharTermAttribute.class);
            while(stream.incrementToken()){
            	String word = cta.toString();
                wordsList.add(word);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return wordsList;
    }
    
    /**
     * Examples:
     * Analyzer aly1 = new StandardAnalyzer(Version.LUCENE_36);
     * Analyzer aly2 = new StopAnalyzer(Version.LUCENE_36);
     * Analyzer aly3 = new SimpleAnalyzer(Version.LUCENE_36);
     * Analyzer aly4 = new WhitespaceAnalyzer(Version.LUCENE_36);
     * 
     * Analysis.displayToken(str, aly1);
     * Analysis.displayToken(str, aly2);
     * Analysis.displayToken(str, aly3);
     * Analysis.displayToken(str, aly4);
     */
}