package com.example.demo.xmlmodel;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "command")
public class CurrentDataXml {
    @JacksonXmlProperty(isAttribute = true)
    private String id;
    @JacksonXmlProperty(localName = "get")
    private GetRequest getRequest;

    public static class GetRequest {
        @JacksonXmlProperty(isAttribute = true)
        private String consumer;
        @JacksonXmlProperty(isAttribute = true)
        private String currency;

        public String getConsumer() {
            return consumer;
        }

        public String getCurrency() {
            return currency;
        }
    }

    public String getId() {
        return id;
    }

    public GetRequest getGetRequest() {
        return getRequest;
    }
}
