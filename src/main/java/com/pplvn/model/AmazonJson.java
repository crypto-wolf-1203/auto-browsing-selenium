package com.pplvn.model;

import java.util.List;
import java.util.Map;

public class AmazonJson {
    private Map<String, List<String>> dimensionValuesDisplayData;

    public Map<String, List<String>> getDimensionValuesDisplayData() {
        return dimensionValuesDisplayData;
    }

    public void setDimensionValuesDisplayData(Map<String, List<String>> dimensionValuesDisplayData) {
        this.dimensionValuesDisplayData = dimensionValuesDisplayData;
    }
}
