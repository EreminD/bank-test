package ru.inno.bank;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class PriceTakerTest {
    private static KafkaProducer<String, String> producer;

    @BeforeAll
    public static void setUp() throws UnknownHostException {
        Properties config = new Properties();
        config.put("client.id", InetAddress.getLocalHost().getHostName());
        config.put("bootstrap.servers", "localhost:29092");
        config.put("acks", "all");
        config.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        config.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producer = new KafkaProducer<>(config);
    }

    @AfterAll
    public static void tearDown() {
        if (producer != null) {
            producer.close();
        }
    }


    @Test
    public void getTest() {
        double price = 120.0;
        String message = "{\"currency\":\"USD\",\"price\": " + price + "}";

        ProducerRecord<String, String> record = new ProducerRecord<>("prices", message);
        record.headers().add("__TypeId__", "com.example.Price".getBytes());
        producer.send(record);

        given().baseUri("http://localhost:8082").get("/loan")
                .then().statusCode(200)
                .body("totalPrice", equalTo(126F));

    }
}
