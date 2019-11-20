/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author franciscovicenzi
 */
public class Preprocessing {
    
    private final String separator;

    public Preprocessing(String separator) {
        this.separator = separator;
    }
    
    public int getFeatureColumn(ArrayList<String[]> content, String feature) {
        String[] first_line = content.get(0);
        for (int i = 0; i < first_line.length; i++) {
            if (first_line[i].equals(feature)) {
                System.out.println("For " + feature + ", column = " + i);
                return i;
            }
        }        
        return -1;
    }
    
    public Set<String> getUniqueValueByFeature(ArrayList<String[]> content,int feature_column) {
        ArrayList<String> features_aux = new ArrayList();
        for (int i = 1; i < content.size(); i++) {
             features_aux.add(content.get(i)[feature_column]);
        }
        Set<String> uniqueFeatures = new LinkedHashSet<>(features_aux);
        return uniqueFeatures;
    }
    
    public ArrayList<String> getLabelsByTraj(ArrayList<String[]> content, int label_column, int tid_column) {
        ArrayList<String> features_aux = new ArrayList();
        String current_tid = content.get(1)[tid_column];
        boolean already = false;
        for (int i = 1; i < content.size(); i++) {
           String inside_tid = content.get(i)[tid_column];           
            if (current_tid.equals(inside_tid)) {
                if (!already) {
                    features_aux.add(content.get(i)[label_column]);
                    already = true;
                }            
            } 
            else {
                current_tid = inside_tid;
                features_aux.add(content.get(i)[label_column]);
                already = true;
            }
        }
        return features_aux;
    }
    
    public boolean[] setFalse(boolean[] checked) {
        for (int i = 0; i < checked.length; i++) {
            checked[i] = false;
        }
        return checked;
    }
    
    
    public Map<String, Float> countFeatures(ArrayList<String[]> content, int label_column, int feature_column, Map<String, Object> feature_dict, int label_count) {
        String current_label = content.get(1)[label_column];
        Map<String, Float> count_feature_dict = new HashMap<String, Float>();
        boolean checked[] = new boolean[feature_dict.size()];
        checked = setFalse(checked);
        for (int i = 1; i < content.size(); i++) {
            String inside_label = content.get(i)[label_column];
            String key = content.get(i)[feature_column];
            int key_idx = (int) feature_dict.get(key);
            boolean feature = checked[key_idx];
            if (current_label.equals(inside_label)) {
                if ((!feature)) {
                    Float count = count_feature_dict.get(key);
                    count_feature_dict.put(key, count == null ? 1 : count + 1);
                    checked[key_idx] = true;
                }            
            } 
            else {
                checked = setFalse(checked);
                current_label = inside_label;
                Float count = count_feature_dict.get(key);
                checked[key_idx] = true;
                count_feature_dict.put(key, count == null ? 1 : count + 1);
            }
        }
        
        for (String key : count_feature_dict.keySet()) {
            float aux = (float) count_feature_dict.get(key);
            aux = (float) Math.log10((label_count/aux));
            count_feature_dict.put(key, aux);
        }

        return count_feature_dict;
    }
}
