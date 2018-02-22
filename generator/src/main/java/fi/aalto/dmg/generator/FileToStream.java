package fi.aalto.dmg.generator;

import java.io.*;
import java.util.*;
import fi.aalto.dmg.statistics.ThroughputLog;
import fi.aalto.dmg.util.Constant;
import fi.aalto.dmg.util.Utils;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.log4j.Logger;

/**
 * Generator for WordCount workload
 * The distribution of works is skewed
 * Created by yangjun.wang on 26/10/15.
 */
public class FileToStream extends Generator {
    private static final Logger logger = Logger.getLogger(FileToStream.class);
    private static KafkaProducer<String, String> producer;

    private String TOPIC;
    // private long SENTENCE_NUM = 1000000000;

    private double mu;

    public FileToStream() {
        super();
        producer = createBigBufferProducer();

        TOPIC = this.properties.getProperty("topic", "FileToStream");
    }

    public void generate() throws InterruptedException {

        String sCurrentLine;
        List<String> textList = new ArrayList<>();
        FileReader stream = null; 
        // // for loop to generate message
        BufferedReader br = null;
        int sent_sentences = 0;
        long cur = 0;
        long start = 0;
        long interval = 0;
        long inter = 0;
        try {
            stream = new FileReader("/root/share/sortSBStream.txt");
            br = new BufferedReader(stream);
            while ((sCurrentLine = br.readLine()) != null) {
                if (sCurrentLine.equals("end")) {
                    start = System.nanoTime();
                    interval = 1000000000/textList.size();
                    //if (textList.size() > 70000){ 
                    for (int i=0; i<textList.size(); i++) {
                        cur = System.nanoTime();
                        ProducerRecord<String, String> newRecord = new ProducerRecord<>(TOPIC, textList.get(i));
                        producer.send(newRecord);
                        //System.out.println(System.nanoTime() - cur);
                        while ((System.nanoTime() - cur) < interval) {}
                    }
                    //}
                    System.out.println("size:"+String.valueOf(textList.size()));
                    System.out.println("time:"+String.valueOf((System.nanoTime() - start)/1000000));
                    System.out.println("interval:"+String.valueOf((System.nanoTime() - start)/(textList.size())));
                    textList.clear();
                    continue;
                }
                textList.add(sCurrentLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(stream != null) stream.close();
                if(br != null) br.close();
            } catch(IOException ex) {
                ex.printStackTrace();
            }
        }
        producer.close();
        //logger.info("LatencyLog: " + String.valueOf(System.currentTimeMillis() - time));
    }

    public static void main(String[] args) throws InterruptedException {
        new FileToStream().generate();
    }
}

