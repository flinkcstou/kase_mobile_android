package kz.kase.iris.mqtt;

import android.util.Log;

import java.time.LocalTime;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import com.google.protobuf.GeneratedMessageV3;

/**
 * <p>Базовый класс для отладки отправки и получения сообщений через IRIS API 2.0 по протоколу MQTT.</p>
 * <p><b>Created:</b> 12.04.2019 14:47:30</p>
 * @author victor
 */
public abstract class MQTTBaseDebuger implements MqttCallback {
    protected static final int QOS = 1;
    
    protected static long serialNmb = System.currentTimeMillis();

    protected static CountDownLatch latch = new CountDownLatch(2);

    protected Boolean isDisabled = false;

    private final String userName;
    private final String password;
    private final String urlType;
    private final String url;
    private final String store;
    private final String storePassword;
    private final String clientId;

    protected MqttClient mqttClient;

    public MQTTBaseDebuger() {

        userName = "mqtt1";
        password = "mqtt1";
        urlType = "ws";

        String server = "www.fininfo.kz";

        String wss = "wss://" + server + ":443/mqtt/iris/api20";
        String ws = "ws://" + server + "/mqtt/iris/api20";
        String tcp = "tcp://" + server + ":1883";
        String http = "tcp://" + server + ":8080";

        if (urlType.equals("wss"))
            url = wss;
        else if (urlType.equals("ws"))
            url = ws;
        else if(urlType.equals("tcp"))
            url = tcp;
        else
            url = http;

        store = "ssl/letsencrypt_root_1.jks";
        storePassword = "qwerty123";
        //clientId = String.format("mqttJava%04d", new Random().nextInt(1000) + 1);




        LocalTime time = LocalTime.now();
        int id = time.getHour() * 10000000 + time.getMinute() * 100000 + time.getSecond() * 1000 + new Random().nextInt(999);
        clientId = String.format("mqttAndroid%d", id);

        Log.d("IRIS","url:      " + url);
        Log.d("IRIS","userName: " + userName);
        Log.d("IRIS","clientId: " + clientId);
        Log.d("IRIS","store:    " + store);
    }

    public void init() {
        if (isDisabled)
            return;
        latch = new CountDownLatch(getLatchCount());

        MemoryPersistence persistence = new MemoryPersistence();

        try {
            mqttClient = new MqttClient(url, clientId, persistence);

            mqttClient.setCallback(this);

            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            connOpts.setUserName(userName);
            connOpts.setPassword(password.toCharArray());
            //connOpts.setAutomaticReconnect(true);
            //connOpts.setConnectionTimeout(60 * 10);
            //connOpts.setKeepAliveInterval(60 * 5);

            if ("wss".equals(urlType)) {
                Log.d("IRIS","setup ssl props");
                Properties sslProps = new Properties();
                sslProps.put("com.ibm.ssl.trustStore", store);
                sslProps.put("com.ibm.ssl.trustStorePassword", storePassword);
                connOpts.setSSLProperties(sslProps);
            }

            //sampleClient.setManualAcks(false);

            Log.d("IRIS","Connecting to broker: " + url);
            mqttClient.connect(connOpts);
            Log.d("IRIS","Connected");

            // Подписываемся на требуемые очереди сообщений.
            subscribeToTopics();

            // Отправляем первоначальные запросы.
            Log.d("IRIS","Publishing initial messages");
            publishInitialMessages();
            Log.d("IRIS","Initial messages published");

//            try {
//                // Ожидаем поступления требуемого количества сообщений.
//                Log.d("IRIS","waiting for process completion");
//                latch.await();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }


            //System.exit(0);
        } catch (MqttException me) {
            Log.e("IRIS","reason " + me.getReasonCode());
            Log.e("IRIS","msg " + me.getMessage());
            Log.e("IRIS","loc " + me.getLocalizedMessage());
            Log.e("IRIS","cause " + me.getCause());
            Log.e("IRIS","excep " + me);
            me.printStackTrace();
            if (mqttClient != null) {
                try {
                    if (mqttClient.isConnected()) {
                        Log.d("IRIS","Try to forcibly disconnect");
                        mqttClient.disconnectForcibly();
                    }
                    Log.d("IRIS","Close after error");
                    mqttClient.close();
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public void disconnect() throws Exception{
        Log.d("IRIS","Disconnect");
        mqttClient.disconnect();
        Log.d("IRIS","Disconnected");
        mqttClient.close();
        Log.d("IRIS","Closed");
    }
    protected void sendMqttMessage(MqttTopic topic, GeneratedMessageV3 message, long serial) {
        Log.d("IRIS","sendMqttMessage with serial = "+serial+":" + message.toString());

        MqttMessage message2 = new MqttMessage(message.toByteArray());
        message2.setQos(QOS);

        try {
            topic.publish(message2);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    protected abstract int getLatchCount();

    protected abstract void subscribeToTopics() throws MqttException;

    protected abstract void publishInitialMessages() throws MqttPersistenceException, MqttException;

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        Log.d("IRIS","deliveryComplete");
    }

    @Override
    public void connectionLost(Throwable cause) {
        cause.printStackTrace();;
    }
}
