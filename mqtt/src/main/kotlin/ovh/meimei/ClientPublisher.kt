package ovh.meimei

import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttMessage

fun main(args: Array<String>) {
    val publisher = MqttClient("tcp://localhost:1883", MqttClient.generateClientId())
    publisher.connect()
    val message = MqttMessage()
    message.payload = "Hello world !".toByteArray()
    publisher.publish("iot_data", message)
    publisher.disconnect()
}