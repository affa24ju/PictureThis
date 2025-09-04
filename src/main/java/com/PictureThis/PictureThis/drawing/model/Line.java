package com.PictureThis.PictureThis.drawing.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Line {
    private double[] points;
    private String color;
    private String tool;
    private boolean newLine;

}
