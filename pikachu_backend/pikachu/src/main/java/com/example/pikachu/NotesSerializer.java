package com.example.pikachu;

import java.io.IOException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.stereotype.Component;

@Component
public class NotesSerializer extends JsonSerializer<Notes>{
    @Override
    public void serialize(Notes notes, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("x", notes.getX());
        jsonGenerator.writeNumberField("y", notes.getY());
        jsonGenerator.writeStringField("color", notes.getColor());
        jsonGenerator.writeStringField("text", notes.getText());
        jsonGenerator.writeStringField("textColor", notes.getTextColor());
        jsonGenerator.writeNumberField("fontSize", notes.getFontSize());
        jsonGenerator.writeEndObject();
    }
}
