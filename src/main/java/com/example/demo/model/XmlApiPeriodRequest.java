package com.example.demo.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "command")
public class XmlApiPeriodRequest {
    @JacksonXmlProperty(isAttribute = true)
    private String id;

    @JacksonXmlProperty(localName = "history")
    private HistoryRequest historyRequest;

    public String getId() {
        return id;
    }

    public HistoryRequest getHistoryRequest() {
        return historyRequest;
    }

    public static class HistoryRequest {

        @JacksonXmlProperty(isAttribute = true)
        private String consumer;

        @JacksonXmlProperty(isAttribute = true)
        private String currency;

        @JacksonXmlProperty(isAttribute = true)
        private int period;

        public String getConsumer() {
            return consumer;
        }

        public String getCurrency() {
            return currency;
        }

        public int getPeriod() {
            return period;
        }
    }
}
