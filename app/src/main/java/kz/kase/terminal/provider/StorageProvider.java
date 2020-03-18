package kz.kase.terminal.provider;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kz.kase.iris.model.IrisApiSecs;
import kz.kase.iris.mqtt.MQTTDataProvider;
import kz.kase.terminal.other.TableType;

import static kz.kase.iris.model.IrisApiSecs.SecType.BONDS;
import static kz.kase.iris.model.IrisApiSecs.SecType.SHARES;

//TODO Так как версия является демонстрационной, данные хранятся только на одну сессию, в версии Production необходимо реализовать хранение не диске
public class StorageProvider {
    private static final String TAG = "StorageProvider";
    public static List<String> defaultInstruments = new ArrayList<>(Arrays.asList("KZTK", "KZTO", "HSBK", "GB_KZMS", "KEGC", "KCEL", "GB_NTRM", "RU_SBER", "CCBN", "RU_GAZP",
                "USDKZT_TOM",
                "EURKZT_TOD",
                "CNYKZT_TOD",
                "EURUSD_TOD",
                "USDKZT_SPT",
                "RUBKZT_TOD",
                /////
                "MM Index",
                "TONIA",
                "SWAP-1D",
                "TWINA",
                "SWAP-2D",
                "KazPrime-3M",
                /////

//        Deals    Yield    DTM
                "NTK028_2556",
                "NTK364_2552",
                "NTK182_2554",
                "KZ_05_2410",
                "NTK028_2555",
                "MUM072_0011",
                //////
                "TEBNb14",
                "LARIb2",
                "TEBNb12",
                "DTJLb1",
                "EUBNb13",
                "TEBNb6",
                "AKNPb3",
                "AKNPb2",
                "TEBNb7",
                "KSYSb1"));

    public static Map<TableType, List<String>> tableDict = new HashMap<>();

    //TODO списки бумаг по раздлам BestShares, BestBounds... должны подгружаться с сервера отдельным запросом
    static {
        tableDict.put(TableType.FAVORITE_SHARES, new ArrayList<>(Arrays.asList("HSBK","CCBN","GB_KZMS","RU_SBER", "GB_NTRM", "RU_GAZP")));
        tableDict.put(TableType.FAVORITE_BONDS, new ArrayList<>(Arrays.asList("TEBNb14","LARIb2","TEBNb12","DTJLb1","TEBNb7")));

        tableDict.put(TableType.INDEX_MAIN, new ArrayList<>(Arrays.asList("MM Index", "TONIA", "SWAP-1D", "TWINA", "SWAP-2D", "KazPrime-3M")));
        tableDict.put(TableType.SHARE_MAIN, new ArrayList<>(Arrays.asList("KZTK", "KZTO", "HSBK", "GB_KZMS", "KEGC")));
        tableDict.put(TableType.CURRENCY_MAIN, new ArrayList<>(Arrays.asList("USDKZT_TOM", "EURKZT_TOD", "CNYKZT_TOD", "EURUSD_TOD", "USDKZT_SPT","RUBKZT_TOD")));

        tableDict.put(TableType.SHARES_PAGE, new ArrayList<>(Arrays.asList("KZTK", "KZTO", "HSBK", "GB_KZMS", "KEGC", "KCEL", "GB_NTRM", "RU_SBER", "CCBN", "RU_GAZP")));
        tableDict.put(TableType.BONDS_PAGE, new ArrayList<>(Arrays.asList("TEBNb14","LARIb2","TEBNb12","DTJLb1", "EUBNb13","TEBNb6","AKNPb3","AKNPb2","TEBNb7","KSYSb1")));

        tableDict.put(TableType.CURRENCY, new ArrayList<>(Arrays.asList("USDKZT_TOM", "EURKZT_TOD", "CNYKZT_TOD", "EURUSD_TOD", "USDKZT_SPT","RUBKZT_TOD")));
        tableDict.put(TableType.GS, new ArrayList<>(Arrays.asList("NTK028_2556", "NTK364_2552", "NTK182_2554", "KZ_05_2410", "NTK028_2555", "MUM072_0011")));
    }
    public static Boolean isFavorite(String symbol){
        Boolean status = tableDict.get(TableType.FAVORITE_BONDS).contains(symbol);
        Log.d(TAG, "isFavorite: " + symbol + " status: " + status);
        if(status)
            return status;
        return tableDict.get(TableType.FAVORITE_SHARES).contains(symbol);
    }
    public static Boolean isFavoriteCanAdd(String symbol){
        IrisApiSecs.SecType type = MQTTDataProvider.share.getInstrumType(symbol);
        if (type == BONDS){
            return true;
        }else if (type == SHARES){
            return true;
        }
        return false;
    }
    public static void setFavorite(String symbol){
        Log.d(TAG, "setFavorite: " + symbol);
        IrisApiSecs.SecType type = MQTTDataProvider.share.getInstrumType(symbol);
        if (type == BONDS && !tableDict.get(TableType.FAVORITE_BONDS).contains(symbol)){
            tableDict.get(TableType.FAVORITE_BONDS).add(symbol);
        }else if (type == SHARES && !tableDict.get(TableType.FAVORITE_SHARES).contains(symbol)){
            tableDict.get(TableType.FAVORITE_SHARES).add(symbol);
        }
    }
    public static void removeFavorite(String symbol) {
        Log.d(TAG, "removeFavorite: " + symbol);
        if (tableDict.get(TableType.FAVORITE_BONDS).contains(symbol))
            tableDict.get(TableType.FAVORITE_BONDS).remove(symbol);
        else if (tableDict.get(TableType.FAVORITE_SHARES).contains(symbol))
            tableDict.get(TableType.FAVORITE_SHARES).remove(symbol);
    }
}
/*
    FAVORITE_SHARES,
    FAVORITE_SHARES_ARC,
    FAVORITE_BONDS,
    FAVORITE_BONUS_ARC,

    INDEX_MAIN
    SHARE_MAIN,
    BONDS_MAIN,
    CURRENCY_MAIN,

    SHARES_PAGE,
    SHARES_PAGE_ARC,
    BONDS_PAGE,
    BONDS_PAGE_ARC,

    CURRENCY,
    GS,
 */
