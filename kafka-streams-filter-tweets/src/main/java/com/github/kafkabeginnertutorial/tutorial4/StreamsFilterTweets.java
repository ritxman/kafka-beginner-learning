package com.github.kafkabeginnertutorial.tutorial4;

import com.google.gson.JsonParser;
import io.github.cdimascio.dotenv.Dotenv;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;

import java.util.Properties;

public class StreamsFilterTweets {
    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();

        String bootstrapServerConfig = dotenv.get("BOOTSTRAPSERVERCONFIG");
        String applicationIDConfig = "demo-kafka-streams";
        String topic = "twitter_tweets";
        String filterStreamTo = "important_tweets";

        //Create Properties
        Properties properties = new Properties();
        properties.setProperty(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServerConfig);
        properties.setProperty(StreamsConfig.APPLICATION_ID_CONFIG, applicationIDConfig);
        properties.setProperty(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.StringSerde.class.getName());
        properties.setProperty(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.StringSerde.class.getName());

        //Create a topology
        StreamsBuilder streamsBuilder = new StreamsBuilder();

        //Input topic
        KStream<String, String> inputTopic = streamsBuilder.stream(topic);
        KStream<String, String> filteredStream = inputTopic.filter(
                //filter tweets which has a user of over 10K followers
                (k, jsonTweet) -> extractUserFollowersInTweet(jsonTweet) > 10000
        );
        filteredStream.to(filterStreamTo);

        //Build the topology
        KafkaStreams kafkaStreams = new KafkaStreams(streamsBuilder.build(), properties);

        //Starts our streams application
        kafkaStreams.start();
    }

    private static JsonParser jsonParser = new JsonParser();

    private static Integer extractUserFollowersInTweet(String tweetJson){
        // gson library
        try {
            return jsonParser.parse(tweetJson)
                    .getAsJsonObject()
                    .get("user")
                    .getAsJsonObject()
                    .get("followers_count")
                    .getAsInt();
        }catch(NullPointerException e){
            return 0;
        }
    }
}
