package com.example.demo.model;

import java.util.List;

import lombok.Data;

@Data
public class VisionResult {

	private List<Label> labels;

	private SafeSearch safeSearch;

	private List<Logo> logos;

	private List<Landmark> landmarks;

	private List<Text> texts;

	private List<Face> faces;

	private Web web;

}
