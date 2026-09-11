import json, os, time
import paho.mqtt.client as mqtt
import requests

BROKER=os.getenv("MQTT_HOST","mosquitto")
TOPIC=os.getenv("MQTT_TOPIC","uv/dsw/telemetry")
API=os.getenv("API_URL","http://api:8080/api/telemetry")
KEY=os.getenv("LAB_API_KEY","lab-only-key")

def on_connect(client, userdata, flags, reason_code, properties):
    client.subscribe(TOPIC)

def on_message(client, userdata, message):
    payload=json.loads(message.payload.decode("utf-8"))
    response=requests.post(API,json=payload,headers={"X-Lab-Key":KEY},timeout=5)
    response.raise_for_status()
    print(f"MQTT_PERSISTED {payload['deviceId']} {response.status_code}",flush=True)

client=mqtt.Client(mqtt.CallbackAPIVersion.VERSION2)
client.on_connect=on_connect
client.on_message=on_message
while True:
    try:
        client.connect(BROKER,1883,30)
        client.loop_forever()
    except Exception as exc:
        print(f"BRIDGE_RETRY {exc}",flush=True)
        time.sleep(2)
