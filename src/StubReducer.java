import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class StubReducer extends Reducer<LongWritable, Text, LongWritable, Text> {
    @Override
    public void reduce(LongWritable key, Iterable<Text> values, Context context)
            throws IOException, InterruptedException {

        final HashMap<Long, List<Long>> mutual = new HashMap<Long, List<Long>>();

        for (Text val : values) {
        	
        	final Long person= Long.parseLong(val.toString().split(",")[0]);
        	final Long friend= Long.parseLong(val.toString().split(",")[1]);
            final Boolean isFriend = friend.equals(Long.MIN_VALUE);

            if (mutual.containsKey(person)) {
                if (isFriend) {                   
                	mutual.put(person, null);                    
                } else if (mutual.get(person) != null) {
                	
                	mutual.get(person).add(friend);
                }
            } else {
                if (isFriend) {
                	mutual.put(person, null);
                	
                } else {
                	ArrayList<Long> temp = new ArrayList<Long>();
                	temp.add(friend);
                	mutual.put(person,temp);
                    
                }
            }
        }
        
        Comparator<Long> sortedComp = new Comparator<Long>() {
            @Override
            public int compare(Long key1, Long key2) {
                int size1 = mutual.get(key1).size();
                int size2 = mutual.get(key2).size();
                if (size1 > size2) {
                    return -1;
                }
                //ordering if they are equal
                else if (size1 == size2 && key1 < key2) {
                    return -1;
                } 
                else {
                    return 1;
                }
            }
		};

        SortedMap<Long, List<Long>> sortedmutual = new TreeMap<Long, List<Long>>(sortedComp);

        for (java.util.Map.Entry<Long, List<Long>> entry : mutual.entrySet()) {
            if (entry.getValue() != null) {
                sortedmutual.put(entry.getKey(), entry.getValue());
            }
        }

        Integer i = 0;
        String output = "";
        for (Map.Entry<Long, List<Long>> entry : sortedmutual.entrySet()) {            
            output += entry.getKey().toString()  + "," ;
            i++;
            if(i==10)
            	break;
            
        }
        context.write(key, new Text(output));
    }
}
