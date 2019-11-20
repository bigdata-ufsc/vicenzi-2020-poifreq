package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author franciscovicenzi
 */
public class NPF {

    private ArrayList<String[]> content;
    private Map<String, Object> feature_dict;
    private Map<String, Object> tid_dict;
    private final int feature_column;
    private final Set<String> feature_values;
    private final Set<String> tid_values;
    int tid_column;

    public NPF(ArrayList<String[]> content, int feature_column, Set<String> feature_values, Set<String> tid_values, int tid_column) {
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
        System.out.println("Starting NPF...");

        float bow[][] = new float[tid_values.size()][feature_values.size()];
        String current_tid = (String) tid_dict.get("0");
        int current_idx = 0;
        int traj_size = 0;
        for (int j = 1; j < content.size(); j++) {
            int key = (int) feature_dict.get(content.get(j)[feature_column]);
            if ((content.get(j)[tid_column].equals(current_tid))) {
                bow[current_idx][key] += 1;
                traj_size++;
            } else {
                bow[current_idx] = divideAllElements(bow[current_idx], traj_size);
                current_idx++;
                traj_size = 1;
                current_tid = (String) tid_dict.get(Integer.toString(current_idx));
                bow[current_idx][key] += 1;
            if (j == content.size() - 1) {
                bow[current_idx] = divideAllElements(bow[current_idx], traj_size);
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

    private float[] divideAllElements(float[] bow, int traj_size) {
        for (int i = 0; i < bow.length; i++) {
            bow[i] = bow[i]/traj_size;
        }
        return bow;
    }
}
