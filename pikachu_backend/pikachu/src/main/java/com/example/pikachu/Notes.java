package com.example.pikachu;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Getter;

@JsonSerialize(using = NotesSerializer.class)
public class Notes {
    @Getter
    private int x;
    @Getter
    private int y;
    @Getter
    private String color;
    @Getter
    private String text;
    @Getter
    private String textColor;
    @Getter
    private int fontSize;
}