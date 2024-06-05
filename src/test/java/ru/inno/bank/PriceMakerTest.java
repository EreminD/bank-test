package ru.inno.bank;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeoutException;
import java.util.function.Predicate;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PriceMakerTest {

    private static KafkaConsumer<String, String> consumer;
    private double priceToSend = 50.01;

    @BeforeAll
    public static void setUp() throws UnknownHostException {
        Properties config = new Properties();
        config.put("client.id", InetAddress.getLocalHost().getHostName());
        config.put("group.id", "autotest");
        config.put("bootstrap.servers", "localhost:29092");
        config.put("enable.auto.commit", "true");
        config.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        config.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        consumer = new KafkaConsumer<>(config);
        consumer.subscribe(List.of("prices"));
    }

    @AfterAll
    public static void tearDown() {
        if (consumer != null) {
            consumer.close();
        }
    }

    @Test
    public void getTest() throws TimeoutException {
        given().baseUri("http://localhost:8081").get("/rate/" + priceToSend)
                .then().statusCode(200);

        String priceToGet = Double.toString(priceToSend + 0.05);

        String message = awaitMessage(10_000, msg -> msg.contains(priceToGet));
        assertTrue(message.contains(priceToGet));

    }

    private static String awaitMessage(long timeInMillis, Predicate<String> filter) throws TimeoutException {
        long startTime = System.currentTimeMillis();
        long timeout = startTime + timeInMillis;

        while (System.currentTimeMillis() < timeout) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(10));
            for (ConsumerRecord<String, String> record : records) {
                if(filter.test(record.value())){
                    return record.value();
                }
            }
        }
        throw new TimeoutException("Не дождались сообщения");
    }
}
