package com.example.mqttkotlinsample

import android.content.Context
import com.hivemq.client.mqtt.MqttClient
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient
import com.hivemq.client.mqtt.mqtt3.message.connect.connack.Mqtt3ConnAck
import com.hivemq.client.mqtt.mqtt3.message.publish.Mqtt3Publish
import com.hivemq.client.mqtt.mqtt3.message.subscribe.suback.Mqtt3SubAck

/**
 * Integrate the HiveMQ MQTT Client library, connect to a broker,then subscribe to
 * a topic and publish messages to a topic using the MQTT 3 asynchronous API flavour.
 *
 * - [Quick Start](https://hivemq.github.io/hivemq-mqtt-client/docs/quick-start)
 * - [Reconnect Handling](https://www.hivemq.com/blog/hivemq-mqtt-client-features/reconnect-handling)
 */
class HiveMqttClient(
    context: Context,
    clientID: String
) {
    private val asyncClient: Mqtt3AsyncClient = MqttClient.builder()
        .useMqttVersion3()
        .identifier(clientID)
        .automaticReconnectWithDefaultConfig()
        .sslWithDefaultConfig()
        .buildAsync()

    fun connect(
//        cbConnect: IMqttActionListener = defaultCbConnect,
//        cbClient: MqttCallback = defaultCbClient
        onSuccess: () -> Unit = {},
        onError: (Throwable?) -> Unit = {}
    ) {
        asyncClient.connectWith()
            .send()
            .whenComplete { connAck: Mqtt3ConnAck?, throwable: Throwable? ->
                if (throwable != null) {
                    // handle failure
                    onError(throwable)
                } else {
                    // setup subscribes or start publishing
                    onSuccess()
                }
            }
    }

    fun isConnected(): Boolean {
        return true
    }

    fun subscribe(
        topic: String,
//        cbSubscribe: IMqttActionListener = defaultCbSubscribe
        onSuccess: () -> Unit = {},
        onError: (Throwable?) -> Unit = {}
    ) {
        asyncClient.subscribeWith()
            .topicFilter(topic)
            .callback { publish: Mqtt3Publish ->
                // Process the received message
            }
            .send()
            .whenComplete { subAck: Mqtt3SubAck?, throwable: Throwable? ->
                if (throwable != null) {
                    onError(throwable)
                } else {
                    onSuccess()
                }
            }
    }

    fun unsubscribe(
        topic: String
//        cbUnsubscribe: IMqttActionListener = defaultCbUnsubscribe
    ) {
    }

    fun publish(
        topic: String,
        msg: String,
        qos: Int = 1,
        retained: Boolean = false,
//        cbPublish: IMqttActionListener = defaultCbPublish
        onSuccess: () -> Unit = {},
        onError: (Throwable?) -> Unit = {}
    ) {
        asyncClient.publishWith()
            .topic(topic)
            .payload(msg.toByteArray())
            .send()
            .whenComplete { publish: Mqtt3Publish?, throwable: Throwable? ->
                if (throwable != null) {
                    onError(throwable)
                } else {
                    onSuccess()
                }
            }
    }

    fun disconnect(
//        cbDisconnect: IMqttActionListener = defaultCbDisconnect
    ) {
        asyncClient.disconnect()
    }

    companion object {
        private val TAG = HiveMqttClient::class.java.simpleName
    }
}
