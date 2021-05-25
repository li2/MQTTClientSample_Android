/*
 * @created: 25 May 2021.
 * @author: Weiyi Li
 * Copyright (c) 2021 EROAD Limited. All rights reserved.
 */
package com.example.mqttkotlinsample.hivemq

interface MqttClientActionListener<in T> {
    fun onError(throwable: Throwable) {}
    fun onSuccess(t: T) {}
}
