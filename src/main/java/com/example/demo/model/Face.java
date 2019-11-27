package com.example.demo.model;

import java.util.List;

import com.google.cloud.vision.v1.Likelihood;

import lombok.Data;

@Data
public class Face {

	private Float rollAngle;
	private Float panAngle;
	private Float tiltAngle;
	private Float detectionConfidence;
	private Float landmarkingConfidence;

	private Likelihood joy;
	private Likelihood sorrow;
	private Likelihood anger;
	private Likelihood surprise;
	private Likelihood underExposed;
	private Likelihood blurred;
	private Likelihood headwear;

	private float joyRating;
	private float sorrowRating;
	private float angerRating;
	private float surpriseRating;
	private float underExposedRating;
	private float blurredRating;
	private float headwearRating;

	private List<Vertex> boundingPoly;
	private List<Vertex> fdBoundingPoly;
	private List<FaceLandmark> landmarks;

}
