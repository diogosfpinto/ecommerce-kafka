package org.example;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class NewOrderProducerMain {
    public static void main(String[] args) throws ExecutionException, InterruptedException {

        try (var orderDispatcher = new KafkaDispatcher<Order>()) {
            try (var stringDispatcher = new KafkaDispatcher<Email>()) {
                for (int i = 0; i < 10; i++) {

                    var key = UUID.randomUUID().toString();

                    var userId = UUID.randomUUID().toString();
                    var orderId = UUID.randomUUID().toString();
                    var amount = new BigDecimal(Math.random() * 5000 + 1);
                    Order order = new Order(userId, orderId, amount);
                    orderDispatcher.send("ECOMMERCE_NEW_ORDER", key, order);

                    Email email = new Email("Teste", "teste");
                    stringDispatcher.send("ECOMMERCE_SEND_EMAIL", key, email);
                }
            }
        }
    }
}