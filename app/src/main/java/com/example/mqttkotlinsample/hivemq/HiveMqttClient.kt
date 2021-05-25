/*
 * @created: 25 May 2021.
 * @author: Weiyi Li
 * Copyright (c) 2021 EROAD Limited. All rights reserved.
 */
package com.example.mqttkotlinsample.hivemq

import android.util.Log
import com.example.mqttkotlinsample.MQTT_SERVER_URI
import com.hivemq.client.mqtt.MqttClient
import com.hivemq.client.mqtt.datatypes.MqttQos
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient
import com.hivemq.client.mqtt.mqtt3.message.connect.connack.Mqtt3ConnAck
import com.hivemq.client.mqtt.mqtt3.message.publish.Mqtt3Publish
import com.hivemq.client.mqtt.mqtt3.message.subscribe.suback.Mqtt3SubAck
import java.util.concurrent.TimeUnit
import java.util.function.BiConsumer

/**
 * Integrate the HiveMQ MQTT Client library, connect to a broker, then subscribe to
 * a topic and publish messages to a topic using the MQTT 3 asynchronous API flavour.
 *
 * - [Quick Start](https://hivemq.github.io/hivemq-mqtt-client/docs/quick-start)
 * - [Reconnect Handling](https://www.hivemq.com/blog/hivemq-mqtt-client-features/reconnect-handling)
 */
class HiveMqttClient(clientID: String) : MqttClientAction {

    private val tag = "${this::class.java.simpleName}#$clientID#wy21"

    private val asyncClient: Mqtt3AsyncClient =
        MqttClient.builder()
            .useMqttVersion3()
            .identifier(clientID)
            .serverHost(MQTT_SERVER_URI)
            .serverPort(1883)
//            .automaticReconnectWithDefaultConfig()
            .addDisconnectedListener { context ->
                Log.e(tag, "disconnected source:${context.source}, cause:${context.cause.message}", context.cause)
                context.reconnector.reconnect(true).delay(30, TimeUnit.SECONDS)
            }
            .sslWithDefaultConfig()
            .buildAsync()


    override fun connect(listener: MqttClientActionListener<Mqtt3ConnAck>?) {
        asyncClient
            .connectWith()
            .send()
            .whenComplete { connAcK: Mqtt3ConnAck, throwable: Throwable? ->
                if (throwable != null) {
                    Log.e(tag, "connect failed: ${throwable.message}", throwable)
                    listener?.onError(throwable)
                } else {
                    Log.d(tag, "connect succeed")
                    listener?.onSuccess(connAcK)
                }
            }
    }

    override fun disconnect(listener: MqttClientActionListener<Void>?) {
        asyncClient
            .disconnect()
            .whenComplete(listener.toBiConsumer("Disconnect"))
    }

    override fun publish(
        topic: String,
        message: ByteArray,
        qos: MqttQos,
        retained: Boolean,
        listener: MqttClientActionListener<Mqtt3Publish>?
    ) {
        asyncClient
            .publishWith()
            .topic(topic)
            .payload(message)
            .qos(qos)
            .retain(retained)
            .send()
            .whenComplete(listener.toBiConsumer("Publish to $topic: $message"))
    }

    override fun subscribe(
        topic: String,
        listener: MqttClientActionListener<Mqtt3SubAck>?,
        onMessageReceived: (Mqtt3Publish) -> Unit
    ) {
        asyncClient
            .subscribeWith()
            .topicFilter(topic)
            .callback { publish: Mqtt3Publish -> onMessageReceived(publish) }
            .send()
            .whenComplete(listener.toBiConsumer("Subscribe to $topic"))
    }

    override fun unsubscribe(
        topic: String,
        listener: MqttClientActionListener<Void>?
    ) {
        asyncClient
            .unsubscribeWith()
            .topicFilter(topic)
            .send()
            .whenComplete(listener.toBiConsumer("Unsubscribe to $topic"))
    }

    private fun <T> MqttClientActionListener<T>?.toBiConsumer(action: String): BiConsumer<in T, in Throwable> {
        return BiConsumer<T, Throwable> { t, throwable: Throwable? ->
            if (throwable != null) {
                Log.e(tag, "$action failed ${throwable.message}", throwable)
                this?.onError(throwable)
            } else {
                Log.d(tag, "$action succeed")
                this?.onSuccess(t)
            }
        }
    }
}
