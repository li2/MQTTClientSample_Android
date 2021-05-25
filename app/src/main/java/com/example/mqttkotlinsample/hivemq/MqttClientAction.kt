/*
 * @created: 25 May 2021.
 * @author: Weiyi Li
 * Copyright (c) 2021 EROAD Limited. All rights reserved.
 */

package com.example.mqttkotlinsample.hivemq

import com.hivemq.client.mqtt.datatypes.MqttQos
import com.hivemq.client.mqtt.datatypes.MqttQos.AT_LEAST_ONCE
import com.hivemq.client.mqtt.mqtt3.message.connect.connack.Mqtt3ConnAck
import com.hivemq.client.mqtt.mqtt3.message.publish.Mqtt3Publish
import com.hivemq.client.mqtt.mqtt3.message.subscribe.suback.Mqtt3SubAck

interface MqttClientAction {

    fun connect(
        listener: MqttClientActionListener<Mqtt3ConnAck>? = null
    )

    fun disconnect(
        listener: MqttClientActionListener<Void>? = null
    )

    fun publish(
        topic: String,
        message: ByteArray,
        qos: MqttQos = AT_LEAST_ONCE,
        retained: Boolean = false,
        listener: MqttClientActionListener<Mqtt3Publish>? = null
    )

    fun subscribe(
        topic: String,
        listener: MqttClientActionListener<Mqtt3SubAck>? = null,
        onMessageReceived: (Mqtt3Publish) -> Unit = {}
    )

    fun unsubscribe(
        topic: String,
        listener: MqttClientActionListener<Void>? = null
    )
}
