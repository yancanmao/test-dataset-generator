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

    public void generate(int sleep_frequency) throws InterruptedException {

        long time = System.currentTimeMillis();
        String sCurrentLine;
        FileReader stream = null; 
        ThroughputLog throughput = new ThroughputLog(this.getClass().getSimpleName());
        // // for loop to generate message
        BufferedReader br = null;
        int sent_sentences = 0;
        try {
        //stream = this.getClass().getClassLoader().getResourceAsStream("CJ20100913.txt");
        stream = new FileReader("/root/CJ20100913.txt");
        br = new BufferedReader(stream);
        while ((sCurrentLine = br.readLine()) != null) {
            throughput.execute();
            sent_sentences++;
            ProducerRecord<String, String> newRecord = new ProducerRecord<>(TOPIC, sCurrentLine);
            producer.send(newRecord);
             if (sleep_frequency > 0 && sent_sentences % sleep_frequency == 0) {
                 Thread.sleep(1);
             }
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
        int SLEEP_FREQUENCY = -1;
        if (args.length > 0) {
            SLEEP_FREQUENCY = Integer.parseInt(args[0]);
        }
        new FileToStream().generate(SLEEP_FREQUENCY);
    }
}

