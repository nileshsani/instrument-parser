package com.instrument;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InstrumentStore {
    private Map<String, List<Instrument>> indexedStore = new HashMap<>();

    public Map<String, List<Instrument>> getIndexedStore() {
        return indexedStore;
    }

    public void setIndexedStore(Map<String, List<Instrument>> indexedStore) {
        this.indexedStore = indexedStore;
    }
}
