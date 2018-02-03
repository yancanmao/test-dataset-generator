package fi.aalto.dmg.generator;

import java.io.*;
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

    // private int zipfSize;
    // private double zipfExponent;
    private double mu;
    // private double sigma;

    public FileToStream() {
        super();
        producer = createBigBufferProducer();

        TOPIC = this.properties.getProperty("topic", "FileToStream");
    }

    // a func that not make sense, but just save
    // public void check(String sCurrentLine) {
    //     String currentBatch = null;
    //     long counter = System.currentTimeMillis();
    //     int interval = 0;
    //     // String[] textArr = new String[];
    //     String[] textArr = sCurrentLine.split("\\|");
    //     if (currentBatch == null) {
    //         currentBatch = textArr[2];
    //     }
    //     if (currentBatch != textArr[2]) {
    //         interval = (int)(System.currentTimeMillis() - counter);
    //         if (interval >= 1000) {
    //             System.out.println("interval >= 1000, output too slow");
    //         } else {
    //             Thread.sleep(1000-interval);
    //         }
    //         currentBatch = textArr[2];
    //         counter = System.currentTimeMillis();
    //     }
    // }

    public void generate() throws InterruptedException {

        long time = System.currentTimeMillis();
        long counter = time;
        int interval = 0;
        String sCurrentLine;
        String[] textArr = null;
        String currentBatch = null;
        FileReader stream = null; 
        //ThroughputLog throughput = new ThroughputLog(this.getClass().getSimpleName());
        // // for loop to generate message
        BufferedReader br = null;
        int sent_sentences = 0;
        try {
            //stream = this.getClass().getClassLoader().getResourceAsStream("CJ20100913.txt");
            stream = new FileReader("/root/sort_CJ.txt");
            br = new BufferedReader(stream);
            while ((sCurrentLine = br.readLine()) != null) {
                //throughput.execute();
                sent_sentences++;
                textArr = sCurrentLine.split("\\|");
                if (currentBatch == null) {
                    currentBatch = textArr[2];
                }
                //System.out.println(currentBatch + " " + textArr[2]);
                if (!currentBatch.equals(textArr[2])) {
                    interval = (int)(System.currentTimeMillis() - counter);
                    System.out.println("interval " + interval + " sent"+ sent_sentences);
                    sent_sentences = 0;
                    if (interval >= 1000) {
                        System.out.println("interval " + interval + ", output too slow");
                    } else {
                        Thread.sleep(1000-interval);
                    }
                    currentBatch = textArr[2];
                    counter = System.currentTimeMillis();
                }
                ProducerRecord<String, String> newRecord = new ProducerRecord<>(TOPIC, sCurrentLine);
                producer.send(newRecord);
                //Thread.sleep(1000);
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
        logger.info("LatencyLog: " + String.valueOf(System.currentTimeMillis() - time));
    }

    public static void main(String[] args) throws InterruptedException {
        // int SLEEP_FREQUENCY = -1;
        // if (args.length > 0) {
        //     SLEEP_FREQUENCY = Integer.parseInt(args[0]);
        // }
        new FileToStream().generate();
    }
}

