package org.example;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.io.Closeable;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.regex.Pattern;

class KafkaService<T> implements Closeable {

    private final KafkaConsumer<String, T> consumer;
    private final ConsumerFunction parse;

    KafkaService(String groupdId, String topic, ConsumerFunction parse, Class<T> typeClass, Map<String, String> properties){
        this(parse, groupdId, typeClass, properties);
        consumer.subscribe(Collections.singletonList(topic));
    }

    public KafkaService(String groupId, Pattern topic, ConsumerFunction parse, Class<T> typeClass, Map<String, String> properties) {
        this(parse, groupId, typeClass, properties);
        consumer.subscribe(topic);
    }

    private KafkaService(ConsumerFunction parse, String groupId, Class<T> typeClass, Map<String, String> properties) {
        this.parse = parse;
        this.consumer = new KafkaConsumer<String, T>(getProperties(groupId, typeClass, properties));
    }

    void run(){
        while (true){
            var records = consumer.poll(Duration.ofMillis(100));
            if (!records.isEmpty()){
                System.out.println("Encontrei "+ records.count() +"registros.");
                for(var record : records) {
                    parse.consume(record);
                }
            }
        }
    }

    private Properties getProperties(String groupId, Class<T> type, Map<String, String> ovveridePropreties){
        var properties = new Properties();

        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, GsonDeserializer.class.getName());
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        //Define uma chave para o cliente consumidor que está escutando a partição
        properties.put(ConsumerConfig.CLIENT_ID_CONFIG, UUID.randomUUID().toString());
        //O kafka será notificado logo após o recebimento de uma mensagem. Evitando que o commit seja feito após o
        //recebimento de todas as mensagens
        properties.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "1");
        properties.setProperty(GsonDeserializer.TYPE_CONFIG, type.getName());
        properties.putAll(ovveridePropreties);

        return properties;
    }

    @Override
    public void close() {
        consumer.close();
    }
}
