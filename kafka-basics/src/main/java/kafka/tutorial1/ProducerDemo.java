package kafka.tutorial1;

import io.github.cdimascio.dotenv.Dotenv;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class ProducerDemo {
    public static void main(String [] args){
        Dotenv dotenv = Dotenv.load();
        String bootstrapServers = dotenv.get("BOOTSTRAPSERVERCONFIG");

        // Create producer properties
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // Create the producer
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);

        // Create a producer record
        ProducerRecord<String, String> record = new ProducerRecord<String, String>("first_topic","Hello World");

        // Send data
        producer.send(record);

        // Flush data
        producer.flush();
        // Flush and close producer
        producer.close();
    }
}