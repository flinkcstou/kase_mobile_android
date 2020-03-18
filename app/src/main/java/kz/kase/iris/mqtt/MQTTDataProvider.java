package kz.kase.iris.mqtt;

import android.util.Log;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import kz.kase.iris.model.IrisApi;
import kz.kase.iris.model.IrisApiDeals;
import kz.kase.iris.model.IrisApiSecs;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.MqttTopic;

import kz.kase.iris.model.IrisApi.TotalsApiReply;
import kz.kase.iris.model.IrisApi.TotalsApiReply.ReplayCase;
import kz.kase.iris.model.IrisApi.TotalsApiRequest;
import kz.kase.iris.model.IrisApi.TotalsApiRequest.Builder;
import kz.kase.iris.model.IrisApiBase.ErrorMessage;
import kz.kase.iris.model.IrisApiBase.Ohlc;
import kz.kase.iris.model.IrisApiDeals.DealType;
import kz.kase.iris.model.IrisApiDeals.DealsBaseFilter;
import kz.kase.iris.model.IrisApiDeals.DealsUpdate;
import kz.kase.iris.model.IrisApiSecs.InstrumentsFilter;
import kz.kase.iris.model.IrisApiTotals.Total;
import kz.kase.iris.model.IrisApiTotals.TotalsCountReply;
import kz.kase.iris.model.IrisApiTotals.TotalsCountRequest;
import kz.kase.iris.model.IrisApiTotals.TotalsFilter;
import kz.kase.iris.model.IrisApiTotals.TotalsPeriod;
import kz.kase.iris.model.IrisApiTotals.TotalsRequest;
import kz.kase.terminal.entities.InstrumentItem;
import kz.kase.terminal.entities.InstrumentPack;
import kz.kase.terminal.provider.StorageProvider;
import kz.kase.terminal.other.TableType;
import kz.kase.terminal.entities.RowValue;
import kz.kase.terminal.other.Utils;
import kz.kase.terminal.viewmodel.InstrumentViewModel;


public class MQTTDataProvider extends MQTTBaseDebuger {
    private static final String TAG = "IRIS";
    private static final BigDecimal MLN = new BigDecimal(1000000);
    private static final LocalDate BEG = LocalDate.of(2019, 2, 4);
    private static final LocalDate END = LocalDate.of(2019, 2, 6);
    private MqttTopic secsTopic;
    private MqttTopic totalsTopic;


    public final static MQTTDataProvider share = new MQTTDataProvider();


    private InstrumentViewModel instrumentViewModel = InstrumentViewModel.Companion.getShare();


    private MqttTopic mqttTopic;
    private Map<String, InstrumentItem> instrumentInfo = new HashMap<>();
    //TODO Необходим фильтр периодов, для отображения разных данных в разных таблицах инструментов /За 7, 30, 90... дней
    //TODO Текущее хранение данных в одной переменной приведёт к неверному отображению данных при загрузке за разные периоды
    private ConcurrentHashMap<String, InstrumentItem> instruments = new ConcurrentHashMap<>();

    //TODO Должнобыть примерно так Map<Period, ConcurrentHashMap<String, InstrumentItem>> instruments
    //TODO Добавить на сервер возможность запроса данных по флагам Current, Period_7, Period_30, Period_90... в ответе сервера флаг должен возвращаться

    private ConcurrentHashMap<Long, Builder> apiRequestMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Long, InstrumentPack> instrumentPack = new ConcurrentHashMap<>();

    private TotalsFilter filter;
    private Builder apiRequestDeals;
    private IrisApi.SecsApiRequest.Builder allSecListRequest;

    private MQTTDataProvider() {
        super();
        Log.d(TAG, "MQTTDataProvider++");
        if(isDisabled)
            return;
        TotalsPeriod period = TotalsPeriod.newBuilder().setDay(IrisApiUtils.fromDMY(1, 6, 2019)).build();

        InstrumentsFilter instrumentsFilter = InstrumentsFilter.newBuilder()
                .addSecType(IrisApiSecs.SecType.SHARES)
                .addSecType(IrisApiSecs.SecType.BONDS)
                .setTradeArea(IrisApiSecs.TradeArea.KASE)
                .addStatus(IrisApiSecs.SecurityRecordStatus.SRS_ACTIVE)
                .build();
        //InstrumentsFilter instrumentsFilter = InstrumentsFilter.newBuilder().addAllCodes(codes).build();

        //@formatter:off
        DealsBaseFilter baseFilter = DealsBaseFilter.newBuilder()
                .setInstrumentsFilter(instrumentsFilter)
                .addDealType(DealType.TRADE)
                .setConfirmed(true)
                .build();
        //@formatter:on
        allSecListRequest = IrisApi.SecsApiRequest.newBuilder()
                .setInstrumentRequest(
                        IrisApiSecs.InstrumentsRequest.newBuilder()
                                .setFilter(instrumentsFilter)
                                .setIncludeIssuerInfo(true) // Запрашивает краткую информацию об эмитенте.
                );

        filter = TotalsFilter.newBuilder().setPeriod(period).setFilter(baseFilter).build();

        apiRequestDeals = TotalsApiRequest.newBuilder().setTotalsRequest(TotalsRequest.newBuilder().setFilter(filter));
        instrumentViewModel.getSubjectRequest()
                .subscribe(new Observer<InstrumentPack>() {
            @Override
            public void onSubscribe(Disposable d) {
            }
            @Override
            public void onNext(InstrumentPack item) {
                requestInstruments(item);
            }
            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "MQTTDataProvider: instrumentViewModel: " + e.getMessage());
                e.printStackTrace();
            }
            @Override
            public void onComplete() {
            }
        });
        launch();
        Log.d(TAG, "MQTTDataProvider--");
    }
    public InstrumentItem getInstrment(String symbol){
        return instruments.get(symbol);
    }
    public String getInstrumDesc(String symbol){
        InstrumentItem item = instrumentInfo.get(symbol);
        if(item != null)
            return item.getDescription();
        return "";
    }
    public IrisApiSecs.SecType getInstrumType(String symbol) {
        InstrumentItem item = instrumentInfo.get(symbol);
        if(item != null) {
            return item.getType();
        }
        return IrisApiSecs.SecType.UNRECOGNIZED;
    }
    public List<InstrumentItem> getInstrumentsInfo(){
        return new ArrayList<>(instrumentInfo.values());
    }
    public void launch(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "launch init++");
                init();
            }
        }).run();
    }
    @Override
    protected int getLatchCount() {
        return 300;
    }

    @Override
    protected void subscribeToTopics() throws MqttException {
        // ШАГ 1: Создание топиков для отправки сообщений.
        Log.d(TAG, "subscribeToTopics++");
        totalsTopic = mqttClient.getTopic(IrisApiUtils.INSTRUM_API_QUEUE_MQTT);
        secsTopic = mqttClient.getTopic(IrisApiUtils.SECS_NAME_API_QUEUE_MQTT);


        mqttTopic = mqttClient.getTopic(IrisApiUtils.INSTRUM_API_QUEUE_MQTT);

        // Подписываемся на сообщения для текущего клиента.
        Log.d(TAG,String.format("Subscribe to %s", IrisApiUtils.INSTRUM_CLIENT_MQTT));
        mqttClient.subscribe(IrisApiUtils.INSTRUM_CLIENT_MQTT);
        // Подписываемся на широковещательные сообщения по сделкам (см. документацию).
        Log.d(TAG,String.format("Subscribe to %s", IrisApiUtils.DEALS_BROADCAST_DEALS_MQTT));
        mqttClient.subscribe(IrisApiUtils.DEALS_BROADCAST_DEALS_MQTT);

        Log.d(TAG,String.format("Subscribe to %s", IrisApiUtils.SECS_NAME_CLIENT_TOPIC_MQTT));
        mqttClient.subscribe(IrisApiUtils.SECS_NAME_CLIENT_TOPIC_MQTT);
        Log.d(TAG,"Subscribed");
    }

    @Override
    protected void publishInitialMessages() throws MqttPersistenceException, MqttException {
        // Формируем сообщение (считаем что условие фильтрации - константа).
//        TotalsCountRequest cntReqv = TotalsCountRequest.newBuilder().setFilter(filter).build();
//        TotalsApiRequest apiReqv = TotalsApiRequest.newBuilder().setCountRequest(cntReqv).setSerialNum(serialNmb++).build();
//        // Отправляем первоначальный запрос.
//        sendMqttMessage(mqttTopic, apiReqv, apiReqv.getSerialNum());

        IrisApi.SecsApiRequest secsReqv = allSecListRequest.setSerialNum(serialNmb++).build();
        sendMqttMessage(secsTopic, secsReqv, secsReqv.getSerialNum());

    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        Log.d(TAG,String.format("messageArrived from %s id: %s", topic, message.getId()));

        if (IrisApiUtils.SECS_NAME_CLIENT_TOPIC_MQTT.equals(topic)) {
            // ШАГ 4: Сохряняем информацию по ценным бумагам для дальнейшего использования.
            IrisApi.SecsApiReply secsApiRepy = IrisApi.SecsApiReply.parseFrom(message.getPayload());
            if (secsApiRepy.getReplayCase() == IrisApi.SecsApiReply.ReplayCase.INSTRUMENT_REPLY) {
                instrumentInfo.clear();
                IrisApiSecs.InstrumentsReply instrReply = secsApiRepy.getInstrumentReply();
                for (IrisApiSecs.Instrument instr : instrReply.getInstrumentsList()) {
                    Log.d(TAG,String.format("   %s: %d (%s)", instr.getCode(), instr.getInstrumentId(), instr.getIssuerInfo().getName()));
                    InstrumentItem item = new InstrumentItem(instr.getInstrumentId(), instr.getCode());
                    if (instr.hasIssuerInfo() && instr.getIssuerInfo().getName() != null)
                        item.setDescription(instr.getIssuerInfo().getName());
                    item.setType(instr.getType());

                    instrumentInfo.put(instr.getCode(), item);
                }

                Log.d(TAG,"instruments list size: " + instrumentInfo.size() + " SN:" + secsApiRepy.getSerialNum());
                requestInstruments(new InstrumentPack(TableType.INIT, StorageProvider.defaultInstruments));
            }

        } else if (IrisApiUtils.INSTRUM_CLIENT_MQTT.equals(topic)) {
            TotalsApiReply apiReply = TotalsApiReply.parseFrom(message.getPayload());
            if (apiReply.getReplayCase() == ReplayCase.TOTALS_COUNT_REPLY) {
                TotalsCountReply countReply = apiReply.getTotalsCountReply();
                Log.d(TAG,String.format("totals count = %s, serial=%d", countReply.getCount(), apiReply.getSerialNum()));
                //latch.countDown();
                // Отправляем запрос на информацию о торгах.
                Builder apiRequest = apiRequestMap.get(apiReply.getSerialNum());
                if (apiRequest != null) {
                    apiRequestMap.remove(apiReply.getSerialNum());
                    long sn = apiReply.getSerialNum() + 100000;
                    TotalsApiRequest req = apiRequest.setSerialNum(sn).build();
                    sendMqttMessage(mqttTopic, req, sn);
                    Log.d(TAG,String.format("Request Sent  SN: %d",apiReply.getSerialNum()));
                }
            } else if (apiReply.getReplayCase() == ReplayCase.TOTALS_REPLY) {
                // Рапечатываем результаты запроса по инструменту.
                TableType type = TableType.REALTIME;
                InstrumentPack pack = instrumentPack.get(apiReply.getSerialNum());
                if (pack != null){
                    instrumentPack.remove(apiReply.getSerialNum());
                    type = pack.getType();
                } else {
                    pack = new InstrumentPack(TableType.REALTIME, new ArrayList<String>());
                }
                Log.d(TAG,"TOTALS count:" + apiReply.getTotalsReply().getTotalsList().size());
                List<InstrumentItem> array = new ArrayList<>();
                for (Total instrument : apiReply.getTotalsReply().getTotalsList()) {
                    //Log.d(TAG,"Instrument: " + instrument.getInstrumentCode());
                    InstrumentItem item = new InstrumentItem(instrument.getInstrumentId(), instrument.getInstrumentCode());
                    Ohlc price = instrument.getPrice();
                    item.setDate(Utils.Companion.dateFrom(price.getCloseTime()));
                    InstrumentItem instrInfo = instrumentInfo.get(instrument.getInstrumentCode());
                    if(instrInfo != null) {
                        item.setDescription(instrInfo.getDescription());
                        item.setType(instrInfo.getType());
                    }

                    item.setLast(new RowValue(price.getClose()));
                    item.setVolume(new RowValue(instrument.getVolume().getClose()));

                    item.setBid(new RowValue(instrument.getBidPrice()));
                    item.setBidQty(new RowValue(instrument.getBidQty()));

                    item.setAsk(new RowValue(instrument.getAskPrice()));
                    item.setAskQty(new RowValue(instrument.getAskQty()));

                    item.setTrend(new RowValue(price.getTrend()));
                    item.setTrendPrc(new RowValue(price.getTrendPs()));

                    item.setDealsCount(new RowValue(instrument.getDealCount()));
                    item.setOrdersCount(new RowValue(instrument.getOrderCount()));

                    item.setOpen(new RowValue(price.getOpen()));
                    item.setOpenVolume(new RowValue(instrument.getVolume().getOpen()));

                    item.setAvg(new RowValue(price.getWa()));

                    item.setMax(new RowValue(price.getHigh()));
                    item.setMin(new RowValue(price.getLow()));
                    item.setTradeStatus("n/a");
                    item.setVolumeKZT(new RowValue(instrument.getKztSum()));
                    if(type != TableType.REALTIME) {
                        instruments.put(item.getSymbol(), item);
                    }
                    array.add(item);
                    Log.d(TAG,"Instrument added: " + instrument.getInstrumentCode());
//                    String mlnKzt = IrisApiUtils.fromDecimal(price.getClose()).divide(MLN).setScale(2, RoundingMode.HALF_UP).toPlainString();
//                    String lastPrice = IrisApiUtils.fromDecimal(price.getClose()).toPlainString();
//                    String lastPriceTime = IrisApiUtils.dateTimeToString(price.getCloseTime());
//                    Log.d(TAG,String.format("%s: deals count = %d, volum mln kzt = %s, last price = %s, last price time = %s", instrument.getInstrumentCode(), instrument.getDealCount(), mlnKzt, lastPrice, lastPriceTime));
                }
                pack.setInstruments(array);
                instrumentViewModel.getSubjectUpdate().onNext(pack);
                //latch.countDown();
            } else if (apiReply.getReplayCase() == ReplayCase.ERROR_MESSAGE) {
                ErrorMessage errorMessage = apiReply.getErrorMessage();
                Log.e(TAG,String.format("ERROR: serial=%d, type=%s, message=[%s]", apiReply.getSerialNum(), errorMessage.getCode().toString(), errorMessage.getMessage()));
            } else {
                Log.w(TAG,String.format("Bad reply type: %s", apiReply.getReplayCase().toString()));
            }
        } else if (IrisApiUtils.DEALS_BROADCAST_DEALS_MQTT.equals(topic)) {
            // Отправляем запрос на изменившиеся данные.
            DealsUpdate dealsUpdate = DealsUpdate.parseFrom(message.getPayload());
            Log.d(TAG,String.format("deals in update count = %d", dealsUpdate.getDealsList().size()));

            // Прверяем, что среди добавленных или обновленных сделок есть затрагивающие интересующий нас период.
            //@formatter:off
//            dealsUpdate.getDealsList().stream()
//                    .map(di -> IrisApiUtils.toLocalDate(di.getDealTime().getDate()))
//                    .filter(d -> BEG.isBefore(d) && END.isAfter(d))
//                    .findFirst()
//                    .ifPresent(dt -> {
//                        // Переотправляем запрос на информацию о торгах.
//                        TotalsApiRequest req = apiRequest.setSerialNum(serialNmb++).build();
//                        sendMqttMessage(mqttTopic, req, req.getSerialNum());
//                    });
            // Прверяем, что среди добавленных или обновленных сделок есть затрагивающие интересующий нас период.
            //TODO Неоходимо отслеживать поступающие пакеты по Symbol или InstrumentId и отправлять запросы на обновление
            for (IrisApiDeals.ShortDealInfo di : dealsUpdate.getDealsList()) {
                LocalDate d = IrisApiUtils.toLocalDate(di.getDealTime().getDate());
                if (BEG.isBefore(d) && END.isAfter(d)) {
                    // Переотправляем запрос на информацию о торгах.
                    TotalsApiRequest req = apiRequestDeals.setSerialNum(serialNmb++).build();
                    sendMqttMessage(totalsTopic, req, req.getSerialNum());
                    break;
                }
            }

            //@formatter:on
        } else {
            Log.w(TAG,String.format("Bad message queue: %s", topic));
        }
    }

    @Override
    public void connectionLost(Throwable cause) {
        super.connectionLost(cause);
        latch.countDown();
        latch.countDown();
    }

    public void requestInstruments(InstrumentPack pack){
        List<String> notFound = new ArrayList<>();
        Log.d(TAG, String.format("requestInstruments: %s", pack.getSymbols()));

        List<String> arrayList = pack.getSymbols();
        List<InstrumentItem> found = new ArrayList<>();

        for (String symbol : arrayList) {
            InstrumentItem item = instruments.get(symbol);
            notFound.add(symbol);
            if (item != null) {
                found.add(item);
            }
        }
        if (found.size() == arrayList.size()) {
            pack.setInstruments(found);
            instrumentViewModel.getSubjectUpdate().onNext(pack);
            return;
        }

        if (notFound.size() > 0) {
            //TODO Временно дата не устанавливается, так как данные рандомны
            TotalsPeriod period = TotalsPeriod.newBuilder().setDay(IrisApiUtils.fromDMY(5, 2, 2019)).build();

            InstrumentsFilter instrumentsFilter = InstrumentsFilter.newBuilder()
                    .addSecType(IrisApiSecs.SecType.SHARES)
                    .addSecType(IrisApiSecs.SecType.BONDS)
                    .setTradeArea(IrisApiSecs.TradeArea.KASE)
                    .addStatus(IrisApiSecs.SecurityRecordStatus.SRS_ACTIVE)
                    .addAllCodes(notFound)
                    .build();
            DealsBaseFilter baseFilter = DealsBaseFilter.newBuilder()
                    .setInstrumentsFilter(instrumentsFilter)
                    .addDealType(DealType.TRADE)
                    .setConfirmed(true)
                    .build();
            long requestId = serialNmb++;
            TotalsFilter filter = TotalsFilter.newBuilder().setPeriod(period).setFilter(baseFilter).build();
            TotalsCountRequest cntReqv = TotalsCountRequest.newBuilder().setFilter(filter).build();
            TotalsApiRequest apiReqv = TotalsApiRequest.newBuilder().setCountRequest(cntReqv).setSerialNum(requestId).build();

            apiRequestMap.put(requestId, TotalsApiRequest.newBuilder().setTotalsRequest(TotalsRequest.newBuilder().setFilter(filter)));
            instrumentPack.put(requestId + 100000, pack);
            sendMqttMessage(totalsTopic, apiReqv, apiReqv.getSerialNum());


        }
    }
}
