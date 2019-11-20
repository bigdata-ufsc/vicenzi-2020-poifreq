/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author franciscovicenzi
 */
public class CSVHandler {

    private ArrayList<String[]> content;
    
    public CSVHandler() {
        content = new ArrayList();
    }
    
    public void readFile(String file_name) {
        FileReader file_reader = null;
        String line = "";
//        String separator = ",";
        try {
            file_reader = new FileReader(file_name);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CSVHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        BufferedReader br = new BufferedReader(file_reader);
        
        try {
            while ((line = br.readLine()) != null) {
                this.content.add(line.split(","));
                
            }
        } catch (IOException ex) {
            Logger.getLogger(CSVHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    public ArrayList<String[]> getContent() {
        return content;
    }

    public void setContent(ArrayList<String[]> content) {
        this.content = content;
    }
    
    public void writeXFile(String file_name, float[][] x) throws IOException {
        FileWriter file_writer = new FileWriter(file_name);
        
        for (int i = 0; i < x.length; i++){
            for (int j = 0; j < x[0].length; j++){
                file_writer.append(Float.toString(x[i][j]));
                file_writer.append(",");
            }
            file_writer.append("\n");
        }
        
        file_writer.flush();
        file_writer.close();
        
    }
    
    public void writeYFile(String file_name, ArrayList<String> y) throws IOException {        
        FileWriter file_writer = new FileWriter(file_name);
        file_writer.append("label\n");
        for (String y1 : y) {
            file_writer.append(y1);
            file_writer.append("\n");            
        }
        file_writer.flush();
        file_writer.close();
    }
    
    
    
}
