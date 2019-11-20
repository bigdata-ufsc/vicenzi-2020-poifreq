package control;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;
import utils.CSVHandler;

import java.util.Set;
import model.PoiFrequency;
import model.WNPF;
import model.NPF;
import utils.Preprocessing;

/**
 *
 * @author franciscovicenzi
 */
public class ACMSAC_POIFREQ {


    public static void main(String[] args) throws IOException {
        if (args.length < 5) {
            System.out.println("Please run as: ");
            System.out.println("java -jar ACMSAC_POIFREQ.jar dataset path_to_dataset path_to_timers path_to_results label_name freq_column");
            System.exit(0);
        }       
        
        String dataset = args[0];
        String input_path = args[1];
        String output_timers_path = args[2];
        String output_results_path = args[3];
        String label_name = args[4];
        String freq_column = args[5];

        
        Path currentRelativePath = Paths.get("");
        String s = currentRelativePath.toAbsolutePath().toString();
        output_results_path = s + output_results_path;
        output_timers_path = s + output_timers_path;
        input_path = s + input_path;
        System.out.println(output_results_path);
        System.out.println(output_timers_path);
        System.out.println(input_path);
        
        
        CSVHandler train_csv_handler = new CSVHandler();
        train_csv_handler.readFile(input_path + dataset+ "_train.csv");
        CSVHandler test_csv_handler = new CSVHandler();
        test_csv_handler.readFile(input_path + dataset+ "_test.csv");
       
        runPF(dataset, train_csv_handler, test_csv_handler, output_timers_path, output_results_path, label_name, freq_column);
        runNPF(dataset, train_csv_handler, test_csv_handler, output_timers_path, output_results_path, label_name, freq_column);
        runWNPF(dataset, train_csv_handler, test_csv_handler, output_timers_path, output_results_path, label_name, freq_column);
        
        
        
        
    }    
    
    public static void runPF(String dataset, CSVHandler train_csv_handler, CSVHandler test_csv_handler,
                              String output_timers_path, String output_results_path, String label_name, String freq_column) throws IOException {
        long startTime = System.currentTimeMillis();
        Preprocessing preprocesser = new Preprocessing(",");
        int feature_column = preprocesser.getFeatureColumn(train_csv_handler.getContent(), freq_column);
        Set<String> train_unique = preprocesser.getUniqueValueByFeature(train_csv_handler.getContent(), feature_column);
        Set<String> test_unique = preprocesser.getUniqueValueByFeature(test_csv_handler.getContent(), feature_column);
        train_unique.addAll(test_unique);
        int label_column = preprocesser.getFeatureColumn(train_csv_handler.getContent(), label_name);
        int tid_column = preprocesser.getFeatureColumn(train_csv_handler.getContent(), "tid");
        
        Set<String> train_tids = preprocesser.getUniqueValueByFeature(train_csv_handler.getContent(), tid_column);
        Set<String> test_tids = preprocesser.getUniqueValueByFeature(test_csv_handler.getContent(), tid_column);
        
        PoiFrequency pf_train = new PoiFrequency(train_csv_handler.getContent(), feature_column, train_unique, train_tids, tid_column);
        PoiFrequency pf_test = new PoiFrequency(test_csv_handler.getContent(), feature_column, train_unique, test_tids, tid_column);        
        
        
        pf_train.setFeatureDict();
        pf_train.setTidDict();
        
        pf_test.setFeatureDict();
        pf_test.setTidDict();
        
        
        float[][] x_train = pf_train.run();
        float[][] x_test = pf_test.run();
        
        ArrayList<String> y_train = preprocesser.getLabelsByTraj(train_csv_handler.getContent(), label_column, tid_column);
        ArrayList<String> y_test = preprocesser.getLabelsByTraj(test_csv_handler.getContent(), label_column, tid_column);
        long endTime = System.currentTimeMillis();
        System.out.println("PF!");
        lastPrint("pf", dataset, x_train, y_train, x_test, y_test, (endTime-startTime), output_timers_path);
        writeFiles("pf_", train_csv_handler, test_csv_handler, dataset, x_train, y_train, x_test, y_test, output_results_path);
    }
    
    public static void lastPrint(String method, String dataset, float[][] x_train, ArrayList<String> y_train,
                                float[][] x_test, ArrayList<String> y_test,
                                long time_spent, String output_path) throws IOException {
        System.out.println("Processing done for " + dataset + " dataset...");
        System.out.println("Total execution time: " + (time_spent) + "ms or " + ((time_spent)/1000) + "s\n");        
        System.out.println("x_train shape: " + x_train.length + ", " + x_train[0].length);
        System.out.println("x_test shape: " + x_test.length + ", " + x_test[0].length);
        System.out.println("y_train shape: " + y_train.size() + ", ");
        System.out.println("y_test shape: " + y_test.size() + ", ");
        String file_name = output_path+method + "-" + dataset + "-stats.txt";
        FileWriter file_writer = new FileWriter(file_name);
        file_writer.append("Method: "+method);
        file_writer.append("\nDataset: "+dataset);
        file_writer.append("\nTotal execution time: " + (time_spent) + "ms or " + ((time_spent)/1000) + "s");
        file_writer.append("\nx_train shape: " + x_train.length + ", " + x_train[0].length);
        file_writer.append("\nx_test shape: " + x_test.length + ", " + x_test[0].length);
        file_writer.append("\ny_train shape: " + y_train.size() + ", ");
        file_writer.append("\ny_test shape: " + y_test.size() + ", ");
        file_writer.flush();
        file_writer.close();        
    }
    
    public static void writeFiles(String method, CSVHandler train_csv_handler, CSVHandler test_csv_handler, 
                           String dataset, float[][] x_train, ArrayList<String> y_train, 
                           float[][] x_test, ArrayList<String> y_test, String output_path) throws IOException {
        train_csv_handler.writeYFile(output_path+method + dataset+"-y_train.csv", y_train);
        train_csv_handler.writeXFile(output_path+method + dataset+"-x_train.csv", x_train);
        test_csv_handler.writeYFile(output_path+method + dataset+"-y_test.csv", y_test);
        test_csv_handler.writeXFile(output_path+method + dataset+"-x_test.csv", x_test);
    }
    
    
    public static void runNPF(String dataset, CSVHandler train_csv_handler, CSVHandler test_csv_handler,
                             String output_timers_path, String output_results_path, String label_name, String freq_column) throws IOException {
        
        long startTime = System.currentTimeMillis();
        Preprocessing preprocesser = new Preprocessing(",");
        int feature_column = preprocesser.getFeatureColumn(train_csv_handler.getContent(), freq_column);
        Set<String> train_unique = preprocesser.getUniqueValueByFeature(train_csv_handler.getContent(), feature_column);
        Set<String> test_unique = preprocesser.getUniqueValueByFeature(test_csv_handler.getContent(), feature_column);
        train_unique.addAll(test_unique);
        
        int label_column = preprocesser.getFeatureColumn(train_csv_handler.getContent(), label_name);
        int tid_column = preprocesser.getFeatureColumn(train_csv_handler.getContent(), "tid");
        
        Set<String> train_tids = preprocesser.getUniqueValueByFeature(train_csv_handler.getContent(), tid_column);
        Set<String> test_tids = preprocesser.getUniqueValueByFeature(test_csv_handler.getContent(), tid_column);
        
        NPF npf_train = new NPF(train_csv_handler.getContent(), feature_column, train_unique, train_tids, tid_column);
        NPF npf_test = new NPF(test_csv_handler.getContent(), feature_column, train_unique, test_tids, tid_column);        
        
        
        npf_train.setFeatureDict();
        npf_train.setTidDict();
        
        npf_test.setFeatureDict();
        npf_test.setTidDict();
        
        
        float[][] x_train = npf_train.run();
        float[][] x_test = npf_test.run();
        
        ArrayList<String> y_train = preprocesser.getLabelsByTraj(train_csv_handler.getContent(), label_column, tid_column);
        ArrayList<String> y_test = preprocesser.getLabelsByTraj(test_csv_handler.getContent(), label_column, tid_column);
        long endTime = System.currentTimeMillis();
        
        System.out.println("NPF!");
        lastPrint("npf", dataset, x_train, y_train, x_test, y_test, (endTime-startTime), output_timers_path);
        writeFiles("npf_", train_csv_handler, test_csv_handler, dataset, x_train, y_train, x_test, y_test, output_results_path);
        
    }
    
    public static void runWNPF(String dataset, CSVHandler train_csv_handler, CSVHandler test_csv_handler,
                                String output_timers_path, String output_results_path, String label_name, String freq_column) throws IOException {
        long startTime = System.currentTimeMillis();
        Preprocessing preprocesser = new Preprocessing(",");
        int feature_column = preprocesser.getFeatureColumn(train_csv_handler.getContent(), freq_column);
        Set<String> train_unique = preprocesser.getUniqueValueByFeature(train_csv_handler.getContent(), feature_column);
        Set<String> test_unique = preprocesser.getUniqueValueByFeature(test_csv_handler.getContent(), feature_column);
        train_unique.addAll(test_unique);
        
        int label_column = preprocesser.getFeatureColumn(train_csv_handler.getContent(), label_name);
        int tid_column = preprocesser.getFeatureColumn(train_csv_handler.getContent(), "tid");
        
        Set<String> train_tids = preprocesser.getUniqueValueByFeature(train_csv_handler.getContent(), tid_column);
        Set<String> test_tids = preprocesser.getUniqueValueByFeature(test_csv_handler.getContent(), tid_column);
        
        Set<String> unique_labels = preprocesser.getUniqueValueByFeature(train_csv_handler.getContent(), label_column);
        
        WNPF wnpf_train = new WNPF(train_csv_handler.getContent(), feature_column, train_unique, train_tids, tid_column, label_column);
        WNPF wnpf_test = new WNPF(test_csv_handler.getContent(), feature_column, train_unique, test_tids, tid_column, label_column);        
        
        wnpf_train.setFeatureDict();
        wnpf_train.setTidDict();

        Map<String, Float> icf_dict =  preprocesser.countFeatures(train_csv_handler.getContent(), label_column, feature_column, wnpf_train.getFeature_dict(), unique_labels.size());
        
        
        wnpf_test.setFeatureDict();
        wnpf_test.setTidDict();
        
        float[][] x_test = wnpf_test.run(icf_dict);
        float[][] x_train = wnpf_train.run(icf_dict);
        
        ArrayList<String> y_train = preprocesser.getLabelsByTraj(train_csv_handler.getContent(), label_column, tid_column);
        ArrayList<String> y_test = preprocesser.getLabelsByTraj(test_csv_handler.getContent(), label_column, tid_column);
        long endTime = System.currentTimeMillis();
        
        
        System.out.println("WNPF!");
        lastPrint("wnpf_", dataset, x_train, y_train, x_test, y_test, (endTime-startTime), output_timers_path);
        writeFiles("wnpf_", train_csv_handler, test_csv_handler, dataset, x_train, y_train, x_test, y_test, output_results_path);
    }
    
}
