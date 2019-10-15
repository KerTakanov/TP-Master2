package ovh.meimei

import org.ardulink.core.convenience.Links
import org.ardulink.util.URIs
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttMessage

class ConsumerCallback: MqttCallback {
    val link = Links.getLink(URIs.newURI("ardulink://serial-jssc?port=COM4&baudrate=9600&pingprobe=false"))

    override fun messageArrived(topic: String?, message: MqttMessage?) {
        print("bite")
        link.sendCustomMessage("test")
    }

    override fun connectionLost(cause: Throwable?) {}

    override fun deliveryComplete(token: IMqttDeliveryToken?) {}

}

fun main(args: Array<String>) {
    val subscriber = MqttClient("tcp://localhost:1883", MqttClient.generateClientId())
    subscriber.setCallback(ConsumerCallback())
    subscriber.connect()
    subscriber.subscribe("iot_data")
}