import java.io.IOException;
import java.util.ArrayList;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;


public class StubMapper extends Mapper<LongWritable, Text, LongWritable, Text> {

    @Override
    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String val[]; 
        val = value.toString().split("\t");
        Long person = Long.parseLong(val[0]);
        
        ArrayList<Long> friends = new ArrayList<Long>();

        if (val.length == 2) {
        	
        	for (String friend : val[1].split(",")){
        		
        		friends.add(Long.parseLong(friend));
        		Text temp = new Text(friend.toString() + "," + Long.MIN_VALUE);
                context.write(new LongWritable(person), temp);
      		
        		
        	}
        	

            for (int i = 0; i < friends.size(); i++) {
                for (int j = i + 1; j < friends.size(); j++) {
                	
                	Text oneway = new Text((friends.get(j)).toString() + "," + person.toString());
                    context.write(new LongWritable(friends.get(i)), oneway);
                   
                    Text otherway = new Text((friends.get(i)).toString() + "," + person.toString());
                    context.write(new LongWritable(friends.get(j)), otherway);
                
                }
            }
        }
    }
}
