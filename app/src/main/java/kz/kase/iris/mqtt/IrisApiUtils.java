package kz.kase.iris.mqtt;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import kz.kase.iris.model.IrisApiBase.Date;
import kz.kase.iris.model.IrisApiBase.DateTime;
import kz.kase.iris.model.IrisApiBase.Decimal;
import kz.kase.iris.model.IrisApiBase.ErrorMessage;
import kz.kase.iris.model.IrisApiBase.ErrorMessageCode;
import kz.kase.iris.model.IrisApiBase.Range;
import kz.kase.iris.model.IrisApiBase.Time;
import kz.kase.iris.model.IrisApiDeals.DealType;
import kz.kase.iris.model.IrisApiDeals.DealsBaseFilter;
import kz.kase.iris.model.IrisApiSecs.InstrumentsFilter;
import kz.kase.iris.model.IrisApiSecs.SecType;
import kz.kase.iris.model.IrisApiSecs.TradeArea;

/**
 * <p>Вспомогательные методы и константы для упрощения работы с IRIS API из Java.</p>
 * <p><b>Created:</b> 19.12.2018 19:17:37</p>
 * @author victor
 */
public class IrisApiUtils {
   //==============================================================
   // Константы с описанием адресов.
   //==============================================================

   public static final String JAVA_PREFIX = "java:/";


   public static final String DEALS_API_QUEUE_MQTT = "jms/queue/iris/Deals";
   public static final String DEALS_API_QUEUE_JMS = JAVA_PREFIX + DEALS_API_QUEUE_MQTT;
   public static final String DEALS_API_QUEUE_CORE = DEALS_API_QUEUE_MQTT.replace('/', '.');
   public static final String DEALS_CLIENT_DEALS_MQTT = "jms/topic/iris/Deals/client";
   public static final String DEALS_CLIENT_TOPIC_JMS = JAVA_PREFIX + DEALS_CLIENT_DEALS_MQTT;
   public static final String DEALS_CLIENT_TOPIC_CORE = DEALS_CLIENT_DEALS_MQTT.replace('/', '.');
   public static final String DEALS_BROADCAST_DEALS_MQTT = "jms/topic/iris/Deals/broadcast";
   public static final String DEALS_BROADCAST_TOPIC_JMS = JAVA_PREFIX + DEALS_BROADCAST_DEALS_MQTT;
   public static final String DEALS_BROADCAST_TOPIC_CORE = DEALS_BROADCAST_DEALS_MQTT.replace('/', '.');

   public static final String INSTRUM_API_QUEUE_MQTT = "jms/queue/iris/Totals";
   public static final String TOTALS_API_QUEUE_JMS = JAVA_PREFIX + INSTRUM_API_QUEUE_MQTT;
   public static final String TOTALS_API_QUEUE_CORE = INSTRUM_API_QUEUE_MQTT.replace('/', '.');
   public static final String INSTRUM_CLIENT_MQTT = "jms/topic/iris/Totals/client";
   public static final String TOTALS_CLIENT_TOPIC_JMS = JAVA_PREFIX + INSTRUM_CLIENT_MQTT;
   public static final String TOTALS_CLIENT_TOPIC_CORE = INSTRUM_CLIENT_MQTT.replace('/', '.');

   public static final String SECS_NAME_API_QUEUE_MQTT = "jms/queue/iris/Secs";
   public static final String SECS_API_QUEUE_JMS = JAVA_PREFIX + SECS_NAME_API_QUEUE_MQTT;
   public static final String SECS_API_QUEUE_CORE = SECS_NAME_API_QUEUE_MQTT.replace('/', '.');
   public static final String SECS_NAME_CLIENT_TOPIC_MQTT = "jms/topic/iris/Secs/client";
   public static final String SECS_CLIENT_TOPIC_JMS = JAVA_PREFIX + SECS_NAME_CLIENT_TOPIC_MQTT;
   public static final String SECS_CLIENT_TOPIC_CORE = SECS_NAME_CLIENT_TOPIC_MQTT.replace('/', '.');
   public static final String SECS_NAME_BROADCAST_TOPIC_MQTT = "jms/topic/iris/Secs/broadcast";
   public static final String SECS_BROADCAST_TOPIC_JMS = JAVA_PREFIX + SECS_NAME_BROADCAST_TOPIC_MQTT;
   public static final String SECS_BROADCAST_TOPIC_CORE = SECS_NAME_BROADCAST_TOPIC_MQTT.replace('/', '.');

   //==============================================================
   // Константы с описанием параметров сообщений.
   //==============================================================

   public static final String MQTT_VALIDATED_USER_KEY = "mqttValidatedUser";
   public static final String MQTT_VALIDATED_CLIENT_KEY = "mqttValidatedClient";

   //==============================================================
   // Фильтры инструментов.
   //==============================================================

   //@formatter:off
   public static final InstrumentsFilter FILTER_SHARES_ON_KASE = InstrumentsFilter.newBuilder()
      .addSecType(SecType.SHARES)
      .setTradeArea(TradeArea.KASE)
      .build();
   //@formatter:on

   //==============================================================
   // Базовые фильтры списка сделок.
   //==============================================================

   //@formatter:off
   public static final DealsBaseFilter FILTER_SHARES_ON_KASE_TRADE = DealsBaseFilter.newBuilder()
      .setInstrumentsFilter(FILTER_SHARES_ON_KASE)
      .addDealType(DealType.TRADE)
      .build();
   
   public static final DealsBaseFilter FILTER_SHARES_ON_KASE_TRADE_CONFIRMED = DealsBaseFilter.newBuilder()
      .setInstrumentsFilter(FILTER_SHARES_ON_KASE)
      .addDealType(DealType.TRADE)
      .setConfirmed(true)
      .build();
   //@formatter:on

   //==============================================================
   // Методы для работы с датой и временем.
   //==============================================================

   public static String dateTimeToString(DateTime dateTime) {
      Date d = dateTime.getDate();
      Time t = dateTime.getTime();
      return String.format("%02d.%02d.%04d %02d:%02d:%02d", d.getDay(), d.getMonth(), d.getYear(), t.getHour(), t.getMin(), t.getSec());
   }

   public static Date fromDMY(int day, int month, int year) {
      //@formatter:off
      return Date.newBuilder()
         .setDay(day)
         .setMonth(month)
         .setYear(year)
         .build();
      //@formatter:on
   }

   public static Time fromHMS(int hour, int min, int sec) {
      //@formatter:off
      return Time.newBuilder()
         .setHour(hour)
         .setMin(min)
         .setSec(sec)
         .build();
      //@formatter:on
   }

   public static DateTime fromDmyHms(int day, int month, int year, int hour, int min, int sec) {
      return DateTime.newBuilder().setDate(fromDMY(day, month, year)).setTime(fromHMS(hour, min, sec)).build();
   }

   public static LocalDateTime toLocalDateTime(DateTime dateTime) {
      Date d = dateTime.getDate();
      Time t = dateTime.getTime();
      return LocalDateTime.of(d.getYear(), d.getMonth(), d.getDay(), t.getHour(), t.getMin(), t.getSec());
   }

   public static LocalDate toLocalDate(Date date) {
      return LocalDate.of(date.getYear(), date.getMonth(), date.getDay());
   }

   //==============================================================
   // Методы для работы с Decimal.
   //==============================================================

   public static Decimal toDecimal(String value) {
      return toDecimal(new BigDecimal(value));
   }

   public static Decimal toDecimal(BigDecimal value) {
      return Decimal.newBuilder().setUnscaledVal(value.unscaledValue().longValue()).setScale(value.scale()).build();
   }

   public static Decimal toDecimal(int value) {
      return Decimal.newBuilder().setUnscaledVal(value).setScale(0).build();
   }

   public static Decimal toDecimal(long value) {
      return Decimal.newBuilder().setUnscaledVal(value).setScale(0).build();
   }

   public static BigDecimal fromDecimal(Decimal value) {
      return BigDecimal.valueOf(value.getUnscaledVal(), value.getScale());
   }

   //==============================================================
   // Методы для работы с диапазонами в списках.
   //==============================================================

   public static Range rangeOf(int first, int count) {
      return Range.newBuilder().setFirst(first).setCount(count).build();
   }

   //==============================================================
   // Методы для работы с сообщениями об ошибках.
   //==============================================================

   public static ErrorMessage error(Throwable error) {
      return error(error, ErrorMessageCode.EMC_UNKNOWN);
   }

   public static ErrorMessage error(Throwable error, ErrorMessageCode errorCode) {
      return error(error.getMessage(), errorCode);
   }

   public static ErrorMessage error(String errorMessage, ErrorMessageCode errorCode) {
      return ErrorMessage.newBuilder().setCode(errorCode).setMessage(errorMessage).build();
   }

   //==============================================================
   // Вспомогательные методы.
   //==============================================================

   //TODO

   //==============================================================
   // GET/SET-методы.
   //==============================================================

   //TODO

}
