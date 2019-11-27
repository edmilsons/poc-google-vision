package com.example.demo.model;

import java.util.List;

import lombok.Data;

@Data
public class Text {

	private String description;

	private List<Vertex> boundingPoly;

}
