package indiana.cgl.hadoop.pagerank;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;
import java.lang.StringBuffer;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapreduce.Reducer;
 
public class PageRankReduce extends Reducer<LongWritable, Text, LongWritable, Text>{
	public void reduce(LongWritable key, Iterable<Text> values,
			Context context) throws IOException, InterruptedException {
		double sumOfRankValues = 0.0;
		String targetUrlsList = "";
		
		int sourceUrl = (int)key.get();
		int numUrls = context.getConfiguration().getInt("numUrls",1);
		
		//hints each tuple may include: rank value tuple or link relation tuple  
		//For each URL say for URL1 (Coming from Different mappers say mapper 1,2,3,4 have sent values)		
		for (Text value: values)
		{
			String[] strArray = value.toString().split("#");

			//if(strArray.length > 0)
			{
				if(!strArray[0].isEmpty())
				{
					sumOfRankValues += Double.parseDouble(strArray[0]);   // Summation of all the ranks
				}				
				if(strArray.length > 1)
				{
					targetUrlsList = "";
					for(int i=1;i<strArray.length;++i)
						targetUrlsList+= "#" + strArray[i];  //append all outlinks
					
				}
				else 
				{
					targetUrlsList="#";      //If no outlinks
				}
			}

		} // end for loop
		sumOfRankValues = 0.85*sumOfRankValues+0.15*(1.0)/(double)numUrls;
		context.write(key, new Text(sumOfRankValues+targetUrlsList));
	}
}
