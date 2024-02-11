package Server;

import Parser.CSVParse;

public class DataWrapper<T> {
    private CSVParse<T> parser;

    public DataWrapper(CSVParse<T> parser) {
        this.parser = parser;
    }

    public CSVParse<T> getCSVParser() {
        return this.parser;
    }

    public void setCSVParser(CSVParse<T> parser) {
        this.parser = parser;
    }
}
