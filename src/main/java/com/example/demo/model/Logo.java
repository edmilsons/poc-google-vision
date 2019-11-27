package com.example.demo.model;

import java.util.List;

import lombok.Data;

@Data
public class Logo {

	private String description;

	private Float score;

	private List<Vertex> boundingPoly;

}
