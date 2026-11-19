#!/usr/bin/env python3
"""Fuente IoT simulada (P06). Publica lecturas periodicas en MQTT.
No usa sensores ni redes externas: los datos son ficticios y reproducibles.

Escenarios:
  normal   : valores dentro de operacion.
  alerta   : valores altos que disparan ALERTA.
  anomalia : incluye un valor fuera de rango fisico (ANOMALA).
  demo     : secuencia fija normal -> alerta -> anomalia -> desconexion.
"""
import argparse, json, math, os, random, time
import paho.mqtt.client as mqtt

BROKER = os.getenv("MQTT_HOST", "localhost")
PORT = int(os.getenv("MQTT_PORT", "11883"))
TOPIC = os.getenv("MQTT_TOPIC", "uv/dsw/telemetry")
DEVICE = os.getenv("DEVICE_ID", "sensor-simulado-01")
VARIABLE = os.getenv("VARIABLE", "temperatura")
UNIT = os.getenv("UNIT", "C")


def publicar(client, valor):
    msg = {"deviceId": DEVICE, "variable": VARIABLE, "unit": UNIT, "value": valor}
    client.publish(TOPIC, json.dumps(msg))
    print("PUBLICADO " + json.dumps(msg, ensure_ascii=False), flush=True)


def valores(escenario, n, rng):
    if escenario == "demo":
        return [24.1, 25.3, 31.5, 34.2, 85.0, None, 24.6]
    if escenario == "alerta":
        return [31.0 + rng.uniform(0, 4) for _ in range(n)]
    if escenario == "anomalia":
        return [24.0, 26.0, 85.0, 24.5]
    return [round(24 + math.sin(i / 3) * 1.8 + rng.uniform(-0.3, 0.3), 2) for i in range(n)]


def main():
    p = argparse.ArgumentParser(description=__doc__)
    p.add_argument("--scenario", choices=["normal", "alerta", "anomalia", "demo"], default="demo")
    p.add_argument("--interval", type=float, default=3, help="Segundos entre lecturas (min 1).")
    p.add_argument("--count", type=int, default=10)
    p.add_argument("--seed", type=int, default=6)
    p.add_argument("--loop", action="store_true")
    args = p.parse_args()
    if args.interval < 1:
        p.error("interval debe ser >= 1")
    rng = random.Random(args.seed)
    client = mqtt.Client(mqtt.CallbackAPIVersion.VERSION2)
    client.connect(BROKER, PORT, 30)
    client.loop_start()
    try:
        while True:
            for v in valores(args.scenario, args.count, rng):
                if v is None:
                    print("DESCONEXION SIMULADA: 35 s sin publicar (el dashboard marcara DESCONECTADO).", flush=True)
                    time.sleep(35)
                    continue
                publicar(client, v)
                time.sleep(args.interval)
            if not args.loop:
                break
    except KeyboardInterrupt:
        print("Publicador detenido. El historial permanece en PostgreSQL.", flush=True)
    finally:
        client.loop_stop()
        client.disconnect()


if __name__ == "__main__":
    main()
