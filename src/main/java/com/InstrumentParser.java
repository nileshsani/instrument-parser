package com;

import com.instrument.Instrument;
import com.instrument.InstrumentStore;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InstrumentParser
{
    private static String LME_INSTRUMENT = "LME";
    private static String PRIME_INSTRUMENT = "PRIME";
    private static String MOCK_REQUEST_OBJECT_BASE_PATH = "src/requestObjects/";
    public static String MAPPING_RULES_FILE = "src/mappingRules/rules.json";

    private InstrumentStore instrumentStore = new InstrumentStore();
    private String mappingRuleFileName;

    public static void main( String[] args )
    {
        InstrumentParser parser = new InstrumentParser();
        JSONParser jsonParser = new JSONParser();
        ObjectMapper mapper = new ObjectMapper();
        parser.setMappingRuleFileName(InstrumentParser.MAPPING_RULES_FILE);
        /**
         * Story 1: Typecast into the desired instrument object
         */
        try {
            Reader reader = new FileReader(parser.generateMockRequestFilePath(InstrumentParser.LME_INSTRUMENT, "PB_03_2018"));
            JSONObject rawLmeRequestObject = (JSONObject) jsonParser.parse(reader);

            Instrument lmeInstrument = parser.getRequest(InstrumentParser.LME_INSTRUMENT, "PB_03_2018");
            parser.storeInstrument(InstrumentParser.LME_INSTRUMENT, "PB_03_2018", rawLmeRequestObject, lmeInstrument);

            System.out.println(mapper.writeValueAsString(lmeInstrument));
        } catch (Exception e) {
            e.printStackTrace();
        }

        /**
         * Story 2: Map multiple instruments
         */
        try {
            Reader reader = new FileReader(parser.generateMockRequestFilePath(InstrumentParser.PRIME_INSTRUMENT, "PB_03_2018"));
            JSONObject rawPrimeRequestObject = (JSONObject) jsonParser.parse(reader);

            Instrument primeInstrument = parser.getRequest(InstrumentParser.PRIME_INSTRUMENT, "PB_03_2018");
            parser.storeInstrument(InstrumentParser.PRIME_INSTRUMENT, "PB_03_2018", rawPrimeRequestObject, primeInstrument);

            System.out.println(mapper.writeValueAsString(parser.instrumentStore.getIndexedStore().get("PB_03_2018")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Instrument getRequest(String instrumentName, String exchangeCode) {
        ObjectMapper mapper = new ObjectMapper();
        Instrument instrument = new Instrument();
        String filePath = this.generateMockRequestFilePath(instrumentName, exchangeCode);
        try {
            instrument = mapper.readValue(new File(filePath), Instrument.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return instrument;
    }

    public String generateMockRequestFilePath(String instrumentName, String exchangeCode) {
        return InstrumentParser.MOCK_REQUEST_OBJECT_BASE_PATH + instrumentName + "_" + exchangeCode + ".json";
    }

    public Instrument processInstrument(List<Instrument> instrumentList, Instrument instrument, String instrumentName) {
        if (instrumentName.equals(InstrumentParser.PRIME_INSTRUMENT)) {
            for (int inc = instrumentList.size(); inc >= 1; inc--) {
                Instrument instrument1 = instrumentList.get(inc - 1);
                if (instrument1.isTradable()) {
                    instrument.setDeliveryDate(instrument1.getDeliveryDate());
                    instrument.setLastTradingDate(instrument1.getLastTradingDate());
                    break;
                }
            }
        }

        return instrument;
    }

    public void storeInstrument(String instrumentName, String code, JSONObject rawInstrumentData, Instrument instrument) {
        Map<String, List<Instrument>> indexedStore = this.instrumentStore.getIndexedStore();
        List<Instrument> instrumentList = new ArrayList<Instrument>();

        String indexKey = this.getInstrumentMappingKey(instrumentName);
        indexKey = indexKey.equals("") ? code : rawInstrumentData.get(indexKey).toString();

        if (indexedStore.containsKey(indexKey)) {
            instrumentList = indexedStore.get(indexKey);
            instrumentList.add(this.processInstrument(instrumentList, instrument, instrumentName));
        } else {
            instrumentList.add(instrument);
            indexedStore.put(indexKey, instrumentList);
        }

        this.instrumentStore.setIndexedStore(indexedStore);
    }

    public String getInstrumentMappingKey(String instrumentName) {
        String mappingKey = "";
        JSONParser jsonParser = new JSONParser();
        try {
            Reader reader = new FileReader(this.getMappingRuleFileName());
            JSONObject rulesObject = (JSONObject) jsonParser.parse(reader);

            mappingKey = rulesObject.get(instrumentName).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mappingKey;
    }

    public InstrumentStore getInstrumentStore() {
        return instrumentStore;
    }

    public void setInstrumentStore(InstrumentStore instrumentStore) {
        this.instrumentStore = instrumentStore;
    }

    public String getMappingRuleFileName() {
        return mappingRuleFileName;
    }

    public void setMappingRuleFileName(String mappingRuleFileName) {
        this.mappingRuleFileName = mappingRuleFileName;
    }
}
