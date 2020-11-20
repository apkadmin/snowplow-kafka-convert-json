import java.util

import org.apache.kafka.clients.consumer.KafkaConsumer
import java.util.Properties
import java.util.UUID

import scala.collection.JavaConverters._
import java.io.{PrintStream, UnsupportedEncodingException}

import com.snowplowanalytics.snowplow.analytics.scalasdk.Event
import io.circe.Json
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}


object KafaConnect {
  def main(args: Array[String]): Unit = {
    consumeFromKafka("enriched")
  }


  def consumeFromKafka(topic: String) = {
    val props = new Properties()
    props.put("bootstrap.servers", "192.168.5.6:9092")
    props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    props.put("auto.offset.reset", "latest")
    props.put("group.id", "consumer-group")
    val consumer: KafkaConsumer[String, String] = new KafkaConsumer[String, String](props)
    consumer.subscribe(util.Arrays.asList(topic))
    while (true) {
      val record = consumer.poll(1000).asScala
      for (data <- record.iterator) {
       val out = Event.parse(data.value())
        val events = out.map(event => event.toJson(true) )

//        println(events)
        val data2 = events.toEither
        data2.fold(error=>{println(error)}, success => {
  println(success.deepDropNullValues)
          writeToKafka("clickhouse",success.deepDropNullValues.toString() )
        })


      }
    }

  }
  def writeToKafka(topic: String, value: String): Unit = {
    val props = new Properties()
    props.put("bootstrap.servers", "192.168.5.6:9092")
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    val producer = new KafkaProducer[String, String](props)
    val uuid = UUID.randomUUID
    val record = new ProducerRecord[String, String](topic, uuid.toString, value)
    producer.send(record)
    producer.close()
  }
}
