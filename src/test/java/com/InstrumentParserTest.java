package com;

import com.instrument.Instrument;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Test;

import java.io.FileReader;
import java.io.Reader;

import static org.junit.Assert.*;

/**
 * Unit test for simple InstrumentParser.
 */
public class InstrumentParserTest
{
    @Test
    public void testGenerateMockRequestFilePath_Valid()
    {
        InstrumentParser parser = new InstrumentParser();
        String actual1 = parser.generateMockRequestFilePath(InstrumentParser.LME_INSTRUMENT, "PB_03_2018");
        String actual2 = parser.generateMockRequestFilePath("DUMMY1", "PB_03_2018");

        String expected1 = "src/requestObjects/LME_PB_03_2018.json";
        String expected2 = "src/requestObjects/DUMMY1_PB_03_2018.json";

        assertEquals(expected1, actual1);
        assertEquals(expected2, actual2);
    }

    @Test
    public void testGenerateMockRequestFilePath_Invalid()
    {
        InstrumentParser parser = new InstrumentParser();
        String actual1 = parser.generateMockRequestFilePath("requestObjects/LME", "PB_03_2018");
        String actual2 = parser.generateMockRequestFilePath("DUMMY10", "PB_03_2018");

        String expected1 = "src/requestObjects/LME_PB_03_2018.json";
        String expected2 = "src/requestObjects/DUMMY1_PB_03_2018.json";

        assertNotEquals(expected1, actual1);
        assertNotEquals(expected2, actual2);
    }

    @Test
    public void testGetRequest_typecastJSONToInstrumentObject_LME_Valid()
    {
        InstrumentParser parser = new InstrumentParser();
        parser.setMappingRuleFileName(InstrumentParser.MAPPING_RULES_FILE);
        Instrument actual = parser.getRequest(InstrumentParser.LME_INSTRUMENT, "PB_03_2018");

        Instrument expected = new Instrument();
        expected.setLastTradingDate("15-03-2018");
        expected.setDeliveryDate("17-03-2018");
        expected.setLabel("Lead 13 March 2018");
        expected.setMarket("PB");
        expected.setTradable(true);

        assertEquals(expected.getDeliveryDate(), actual.getDeliveryDate());
        assertEquals(expected.getLastTradingDate(), actual.getLastTradingDate());
        assertEquals(expected.getLabel(), actual.getLabel());
        assertEquals(expected.getMarket(), actual.getMarket());
    }

    @Test
    public void testGetRequest_typecastJSONToInstrumentObject_PRIME_Valid()
    {
        InstrumentParser parser = new InstrumentParser();
        parser.setMappingRuleFileName(InstrumentParser.MAPPING_RULES_FILE);
        Instrument actual = parser.getRequest(InstrumentParser.PRIME_INSTRUMENT, "PB_03_2018");

        Instrument expected = new Instrument();
        expected.setLastTradingDate("14-03-2018");
        expected.setDeliveryDate("18-03-2018");
        expected.setMarket("LME_PB");
        expected.setLabel("Lead 13 March 2018");
        expected.setTradable(false);

        assertEquals(expected.getDeliveryDate(), actual.getDeliveryDate());
        assertEquals(expected.getLastTradingDate(), actual.getLastTradingDate());
        assertEquals(expected.getLabel(), actual.getLabel());
        assertEquals(expected.getMarket(), actual.getMarket());
    }

    @Test
    public void testGetRequest_storeMultipleInstrumentObjects_Valid()
    {
        InstrumentParser parser = new InstrumentParser();
        JSONParser jsonParser = new JSONParser();
        ObjectMapper mapper = new ObjectMapper();
        parser.setMappingRuleFileName(InstrumentParser.MAPPING_RULES_FILE);
        String expectedString = "[{\"LAST_TRADING_DATE\":\"15-03-2018\",\"DELIVERY_DATE\":\"17-03-2018\",\"MARKET\":\"PB\",\"LABEL\":\"Lead 13 March 2018\",\"TRADABLE\":true},{\"LAST_TRADING_DATE\":\"15-03-2018\",\"DELIVERY_DATE\":\"17-03-2018\",\"MARKET\":\"LME_PB\",\"LABEL\":\"Lead 13 March 2018\",\"TRADABLE\":false}]";

        try {
            Instrument instrument1 = parser.getRequest(InstrumentParser.LME_INSTRUMENT, "PB_03_2018");
            Instrument instrument2 = parser.getRequest(InstrumentParser.PRIME_INSTRUMENT, "PB_03_2018");

            Reader lmeReader = new FileReader(parser.generateMockRequestFilePath(InstrumentParser.LME_INSTRUMENT, "PB_03_2018"));
            JSONObject rawLmeRequestObject = (JSONObject) jsonParser.parse(lmeReader);
            parser.storeInstrument(InstrumentParser.LME_INSTRUMENT, "PB_03_2018", rawLmeRequestObject, instrument1);

            Reader primeReader = new FileReader(parser.generateMockRequestFilePath(InstrumentParser.PRIME_INSTRUMENT, "PB_03_2018"));
            JSONObject rawPrimeRequestObject = (JSONObject) jsonParser.parse(primeReader);
            parser.storeInstrument(InstrumentParser.PRIME_INSTRUMENT, "PB_03_2018", rawPrimeRequestObject, instrument2);

            String actualString = mapper.writeValueAsString(parser.getInstrumentStore().getIndexedStore().get("PB_03_2018"));

            assertEquals(expectedString, actualString);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetRequest_storeMultipleInstrumentObjects_ValidateScalability()
    {
        InstrumentParser parser = new InstrumentParser();
        JSONParser jsonParser = new JSONParser();
        ObjectMapper mapper = new ObjectMapper();
        parser.setMappingRuleFileName("src/mappingRules/dummy_rules.json");
        String expectedString = "[{\"LAST_TRADING_DATE\":\"15-03-2018\",\"DELIVERY_DATE\":\"17-03-2018\",\"MARKET\":\"PB\",\"LABEL\":\"Lead 13 March 2018\",\"TRADABLE\":true},{\"LAST_TRADING_DATE\":\"15-03-2018\",\"DELIVERY_DATE\":\"17-03-2018\",\"MARKET\":\"DUMMY1_PB\",\"LABEL\":\"Lead 13 March 2018\",\"TRADABLE\":true},{\"LAST_TRADING_DATE\":\"14-03-2018\",\"DELIVERY_DATE\":\"18-03-2018\",\"MARKET\":\"DUMMY2_PB\",\"LABEL\":\"Lead 13 March 2018\",\"TRADABLE\":true},{\"LAST_TRADING_DATE\":\"14-03-2018\",\"DELIVERY_DATE\":\"18-03-2018\",\"MARKET\":\"DUMMY3_PB\",\"LABEL\":\"Lead 13 March 2018\",\"TRADABLE\":true},{\"LAST_TRADING_DATE\":\"14-03-2018\",\"DELIVERY_DATE\":\"18-03-2018\",\"MARKET\":\"LME_PB\",\"LABEL\":\"Lead 13 March 2018\",\"TRADABLE\":false}]";
        try {
            Instrument instrument1 = parser.getRequest(InstrumentParser.LME_INSTRUMENT, "PB_03_2018");
            Instrument instrument2 = parser.getRequest("DUMMY1", "PB_03_2018");
            Instrument instrument3 = parser.getRequest("DUMMY2", "PB_03_2018");
            Instrument instrument4 = parser.getRequest("DUMMY3", "PB_03_2018");
            Instrument instrument5 = parser.getRequest(InstrumentParser.PRIME_INSTRUMENT, "PB_03_2018");

            Reader lmeReader = new FileReader(parser.generateMockRequestFilePath(InstrumentParser.LME_INSTRUMENT, "PB_03_2018"));
            JSONObject rawLmeRequestObject = (JSONObject) jsonParser.parse(lmeReader);
            parser.storeInstrument(InstrumentParser.LME_INSTRUMENT, "PB_03_2018", rawLmeRequestObject, instrument1);

            Reader dummy1Reader = new FileReader(parser.generateMockRequestFilePath("DUMMY1", "PB_03_2018"));
            JSONObject rawDummy1RequestObject = (JSONObject) jsonParser.parse(dummy1Reader);
            parser.storeInstrument("DUMMY1", "PB_03_2018", rawDummy1RequestObject, instrument2);

            Reader dummy2Reader = new FileReader(parser.generateMockRequestFilePath("DUMMY2", "PB_03_2018"));
            JSONObject rawDummy2RequestObject = (JSONObject) jsonParser.parse(dummy2Reader);
            parser.storeInstrument("DUMMY2", "PB_03_2018", rawDummy2RequestObject, instrument3);

            Reader dummy3Reader = new FileReader(parser.generateMockRequestFilePath("DUMMY3", "PB_03_2018"));
            JSONObject rawDummy3RequestObject = (JSONObject) jsonParser.parse(dummy3Reader);
            parser.storeInstrument("DUMMY3", "PB_03_2018", rawDummy3RequestObject, instrument4);

            Reader primeReader = new FileReader(parser.generateMockRequestFilePath(InstrumentParser.PRIME_INSTRUMENT, "PB_03_2018"));
            JSONObject rawPrimeRequestObject = (JSONObject) jsonParser.parse(primeReader);
            parser.storeInstrument(InstrumentParser.PRIME_INSTRUMENT, "PB_03_2018", rawPrimeRequestObject, instrument5);

            String actualString = mapper.writeValueAsString(parser.getInstrumentStore().getIndexedStore().get("PB_03_2018"));

            assertEquals(expectedString, actualString);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
