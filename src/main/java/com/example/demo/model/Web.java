package com.example.demo.model;

import java.util.List;

import lombok.Data;

@Data
public class Web {

	private List<WebEntity> webEntities;

	private List<WebUrl> fullMatchingImages;

	private List<WebUrl> partialMatchingImages;

	private List<WebUrl> pagesWithMatchingImages;

	
}
