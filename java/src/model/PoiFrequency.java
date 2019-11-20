package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author franciscovicenzi
 */
public class PoiFrequency {

    private ArrayList<String[]> content;
    private Map<String, Object> feature_dict;
    private Map<String, Object> tid_dict;
    private final int feature_column;
    private final Set<String> feature_values;
    private final Set<String> tid_values;
    int tid_column;

    public PoiFrequency(ArrayList<String[]> content, int feature_column, Set<String> feature_values, Set<String> tid_values, int tid_column) {
        this.content = content;
        this.feature_column = feature_column;
        this.feature_values = feature_values;
        this.tid_values = tid_values;
        this.feature_dict = new HashMap<>();
        this.tid_dict = new HashMap<>();
        this.tid_column = tid_column;
    }
//
    public float[][] run() {
        float bow[][] = new float[tid_values.size()][feature_values.size()];
        System.out.println("Starting PF...");
        String current_tid = (String) tid_dict.get("0");
        int current_idx = 0;
        for (int j = 1; j < content.size(); j++) {
            int key = (int) feature_dict.get(content.get(j)[feature_column]);
            if ((content.get(j)[tid_column].equals(current_tid))) {
                bow[current_idx][key] += 1;
            } else {
                current_idx++;
                current_tid = (String) tid_dict.get(Integer.toString(current_idx));
                bow[current_idx][key] += 1;
                
            }
        }
        
        return bow;
        
    }

    public void setFeatureDict() {
            Object[] aux = feature_values.toArray();
            for (int i = 0; i < feature_values.size(); i++) {
                feature_dict.put(aux[i].toString(), i);
            }
        }
    
    public void setTidDict() {
        Object[] aux = tid_values.toArray();
        for (int i = 0; i < tid_values.size(); i++) {
            tid_dict.put(Integer.toString(i), aux[i].toString());
        }
    }
}
