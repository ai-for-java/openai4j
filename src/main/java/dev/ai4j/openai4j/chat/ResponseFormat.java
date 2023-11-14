package dev.ai4j.openai4j.chat;

public class ResponseFormat {

    private String type;

    private String value = ResponseFormatType.TEXT.stringValue();

    public String getType(){
        return this.type;
    }

    public String getValue(){
        return this.value;
    }

}
