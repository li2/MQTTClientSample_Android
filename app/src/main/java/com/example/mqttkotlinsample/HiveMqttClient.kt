package com.example.mqttkotlinsample

import android.content.Context
import android.util.Log
import com.hivemq.client.mqtt.MqttClient
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient
import com.hivemq.client.mqtt.mqtt3.Mqtt3RxClient
import com.hivemq.client.mqtt.mqtt3.message.connect.connack.Mqtt3ConnAck

/**
 * Integrate the HiveMQ MQTT Client library, connect to a broker,then subscribe to
 * a topic and publish messages to a topic using the MQTT 3 asynchronous API flavour.
 *
 * https://hivemq.github.io/hivemq-mqtt-client/docs/quick-start/
 */
class HiveMqttClient(
    context: Context?,
    serverHost: String,
    clientID: String
) {
//    val rxClient: Mqtt3RxClient = MqttClient.builder()
//        .useMqttVersion3()
//        .identifier(clientID)
//        .serverHost(serverHost)
//        .serverPort(SERVER_PORT)
//        .sslWithDefaultConfig()
//        .buildRx()

    private val asyncClient: Mqtt3AsyncClient = MqttClient.builder()
        .useMqttVersion3()
        .identifier(clientID)
        .serverHost(serverHost)
        // connect to localhost://xxx.xxx.xx.xx:1883
//        .serverPort(SERVER_PORT)
//        .sslWithDefaultConfig()
        .buildAsync()

    fun connect(
        username: String = "",
        password: String = "",
//        cbConnect: IMqttActionListener = defaultCbConnect,
//        cbClient: MqttCallback = defaultCbClient
        onSuccess: () -> Unit = {},
        onError: (Throwable?) -> Unit = {}
    ) {
        asyncClient.connectWith()
//            .simpleAuth()
//            .username("my-user")
//            .password("my-password".toByteArray())
//            .applySimpleAuth()
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

/*
        rxClient.connectWith()
            .simpleAuth()
            .username("my-user")
            .password("my-password".toByteArray())
            .applySimpleAuth()
            .send()
*/

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
            .callback { publish ->
                // Process the received message
            }
            .send()
            .whenComplete { subAck, throwable: Throwable? ->
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
            .whenComplete { publish, throwable: Throwable? ->
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
    }

    companion object {
        private val TAG = HiveMqttClient::class.java.simpleName
        private const val SERVER_PORT = 1883
    }
}
