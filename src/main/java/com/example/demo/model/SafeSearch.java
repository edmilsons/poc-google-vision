package com.example.demo.model;

import com.google.cloud.vision.v1.Likelihood;

import lombok.Data;

@Data
public class SafeSearch {

	private Likelihood adult;
	private Likelihood spoof;
	private Likelihood medical;
	private Likelihood violence;
	private float adultRating;
	private float spoofRating;
	private float medicalRating;
	private float violenceRating;
}
