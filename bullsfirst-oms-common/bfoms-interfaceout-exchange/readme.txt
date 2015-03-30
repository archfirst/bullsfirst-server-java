Generating Client Code for MarketData Web Service
-------------------------------------------------
> cd bullsfirst-oms-common\bfoms-interfaceout-exchange
> mvn -P gen-marketdataservice-client install

This will generate client code in the following directory under src\main\java:
    org\archfirst\bfoms\interfaceout\exchange\marketdataadapter\client

From the generated code delete the following files (they are not needed):
    GetMarketPrice.java
    GetMarketPriceResponse.java
    GetMarketPrices.java
    GetMarketPricesResponse.java
    ObjectFactory.java

The client directory should now have only 5 files:
    MarketDataService.java
    MarketDataWebService.java
    MarketPrice.java
    Money.java
    package-info.java

From MarketDataWebService.java remove the section @XmlSeeAlso (lines 21-23)
and also the import for @XmlSeeAlso (line 9).

Generating Client Code for ReferenceData Web Service
----------------------------------------------------
> cd bullsfirst-oms-common\bfoms-interfaceout-exchange
> mvn -P gen-referencedataservice-client install

This will generate client code in the following directory under src\main\java:
    org\archfirst\bfoms\interfaceout\exchange\referencedataadapter\client

From the generated code delete the following files (they are not needed):
    GetInstruments.java
    GetInstrumentsResponse.java
    Lookup.java
    LookupResponse.java
    ObjectFactory.java

The client directory should now have only 4 files:
    Instrument.java
    package-info.java
    ReferenceDataService.java
    ReferenceDataWebService.java

From ReferenceDataWebService.java remove the section @XmlSeeAlso (lines 21-23)
and also the import for @XmlSeeAlso (line 9).