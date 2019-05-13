**Requirements for Instruments Parser**
* Should accept data from multiple data sources (instruments)
* Ability to map multiple data sources on runtime
* Store data recd from multiple data sources in standardised application format.
* Run business logic on the data objects (from various instruments)

---

**Configuration for Rules Engine on runtime**
* Rules Engine file is a simple JSON file consisting key value pairs of instrument name and its mapping key (in instrument's request object).
* For the case, when the value is left empty or null. Rule Engine uses the Instrument Code (eg. LME Code)