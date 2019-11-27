package com.example.demo.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gcp.vision.CloudVisionTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.model.Face;
import com.example.demo.model.FaceLandmark;
import com.example.demo.model.Label;
import com.example.demo.model.Landmark;
import com.example.demo.model.LngLat;
import com.example.demo.model.Logo;
import com.example.demo.model.SafeSearch;
import com.example.demo.model.Text;
import com.example.demo.model.Vertex;
import com.example.demo.model.VisionResult;
import com.example.demo.model.Web;
import com.example.demo.model.WebUrl;
import com.example.demo.service.BD;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.FaceAnnotation;
import com.google.cloud.vision.v1.Feature.Type;
import com.google.cloud.vision.v1.Likelihood;
import com.google.cloud.vision.v1.SafeSearchAnnotation;
import com.google.cloud.vision.v1.WebDetection;
import com.google.cloud.vision.v1.WebDetection.WebEntity;
import com.google.cloud.vision.v1.WebDetection.WebImage;
import com.google.cloud.vision.v1.WebDetection.WebPage;

import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

@RestController
@RequestMapping(value = "scoreImage")
public class VisionController {

	@Autowired
	private CloudVisionTemplate cloudVisionTemplate;

	@PostMapping("/getExtractTextFromImage")
	public Map<String, String> getExtractTextFromImage(
			@RequestPart(value = "file", required = true) @NotNull(message = "File is required") MultipartFile file)
			throws IOException, Exception {

		Map<String, String> compilation = new HashMap<String, String>();

		String extractTextFromImage = this.cloudVisionTemplate.extractTextFromImage(file.getResource());

		compilation.put("Retorno Google", extractTextFromImage);

		System.out.println(extractTextFromImage);

		List<String> result = splitText(extractTextFromImage);
		compilation.put("String Tratada", result.toString());

		for (String item : result) {
			for (String bd : BD.listFruit()) {
				if (bd.equalsIgnoreCase(item)) {
					compilation.put("Item Encontrado : ".concat(bd), item);
					break;
				}
			}
		}

		return compilation;
	}

	@PostMapping("/getAnalyzeImage")
	public VisionResult getAnalyzeImage(
			@RequestPart(value = "file", required = true) @NotNull(message = "File is required") MultipartFile file,
			@RequestParam Type atributoPesquisa) throws IOException, Exception {

		AnnotateImageResponse response;

		if (Type.UNRECOGNIZED == atributoPesquisa) {
			response = this.cloudVisionTemplate.analyzeImage(file.getResource(), Type.LOGO_DETECTION,
					Type.LABEL_DETECTION, Type.WEB_DETECTION, Type.DOCUMENT_TEXT_DETECTION, Type.LANDMARK_DETECTION);
		} else {
			response = this.cloudVisionTemplate.analyzeImage(file.getResource(), atributoPesquisa);
		}

		return vision(response, atributoPesquisa);
	}

	public VisionResult vision(AnnotateImageResponse resp, Type atributoPesquisa) throws IOException, Exception {

		VisionResult result = new VisionResult();
		if (resp != null) {

			if (resp.getLabelAnnotationsList() != null && Type.LABEL_DETECTION == atributoPesquisa) {
				List<Label> labels = new ArrayList<>();
				for (EntityAnnotation ea : resp.getLabelAnnotationsList()) {
					Label l = new Label();
					l.setScore(ea.getScore());
					l.setDescription(ea.getDescription());
					labels.add(l);
				}
				result.setLabels(labels);
			}

			if (resp.getLandmarkAnnotationsList() != null && Type.LANDMARK_DETECTION == atributoPesquisa) {
				List<Landmark> landmarks = new ArrayList<>();
				for (EntityAnnotation ea : resp.getLandmarkAnnotationsList()) {
					Landmark l = new Landmark();
					l.setScore(ea.getScore());
					l.setDescription(ea.getDescription());

					if (ea.getBoundingPoly() != null) {
						l.setBoundingPoly(ea.getBoundingPoly().getVerticesList().stream().map(v -> {
							Vertex vertex = new Vertex();
							vertex.setX(v.getX());
							vertex.setY(v.getY());
							return vertex;
						}).collect(Collectors.toList()));
					}
					if (ea.getLocationsList() != null) {
						l.setLocations(ea.getLocationsList().stream().map(loc -> {
							LngLat ll = new LngLat();
							ll.setLng(loc.getLatLng().getLongitude());
							ll.setLat(loc.getLatLng().getLatitude());
							return ll;
						}).collect(Collectors.toList()));
					}
					landmarks.add(l);
				}
				result.setLandmarks(landmarks);
			}
			if (resp.getLogoAnnotationsList() != null && Type.LOGO_DETECTION == atributoPesquisa) {
				List<Logo> logos = new ArrayList<>();
				for (EntityAnnotation ea : resp.getLogoAnnotationsList()) {
					Logo l = new Logo();
					l.setScore(ea.getScore());
					l.setDescription(ea.getDescription());

					if (ea.getBoundingPoly() != null) {
						l.setBoundingPoly(ea.getBoundingPoly().getVerticesList().stream().map(v -> {
							Vertex vertex = new Vertex();
							vertex.setX(v.getX());
							vertex.setY(v.getY());
							return vertex;
						}).collect(Collectors.toList()));
					}
					logos.add(l);
				}
				result.setLogos(logos);
			}
			if (resp.getTextAnnotationsList() != null && Type.TEXT_DETECTION == atributoPesquisa) {
				List<Text> texts = new ArrayList<>();
				for (EntityAnnotation ea : resp.getTextAnnotationsList()) {
					Text t = new Text();
					t.setDescription(ea.getDescription());

					if (ea.getBoundingPoly() != null) {
						t.setBoundingPoly(
								ea.getBoundingPoly().getVerticesList().stream().filter(Objects::nonNull).map(v -> {
									Vertex vertex = new Vertex();
									vertex.setX(v.getX());
									vertex.setY(v.getY());
									return vertex;
								}).collect(Collectors.toList()));
					}

					texts.add(t);
				}
				result.setTexts(texts);
			}
			if (resp.getFaceAnnotationsList() != null && Type.FACE_DETECTION == atributoPesquisa) {
				List<Face> faces = new ArrayList<>();
				for (FaceAnnotation fa : resp.getFaceAnnotationsList()) {
					Face face = new Face();
					face.setRollAngle(fa.getRollAngle());
					face.setPanAngle(fa.getPanAngle());
					face.setTiltAngle(fa.getTiltAngle());
					face.setDetectionConfidence(fa.getDetectionConfidence());
					face.setLandmarkingConfidence(fa.getLandmarkingConfidence());

					face.setJoy(fa.getJoyLikelihood());
					face.setJoyRating(likelihoodToNumber(fa.getJoyLikelihood()));

					face.setSorrow(fa.getSorrowLikelihood());
					face.setSorrowRating(likelihoodToNumber(fa.getSorrowLikelihood()));

					face.setAnger(fa.getAngerLikelihood());
					face.setAngerRating(likelihoodToNumber(fa.getAngerLikelihood()));

					face.setSurprise(fa.getSurpriseLikelihood());
					face.setSurpriseRating(likelihoodToNumber(fa.getSurpriseLikelihood()));

					face.setUnderExposed(fa.getUnderExposedLikelihood());
					face.setUnderExposedRating(likelihoodToNumber(fa.getUnderExposedLikelihood()));

					face.setBlurred(fa.getBlurredLikelihood());
					face.setBlurredRating(likelihoodToNumber(fa.getBlurredLikelihood()));

					face.setHeadwear(fa.getHeadwearLikelihood());
					face.setHeadwearRating(likelihoodToNumber(fa.getHeadwearLikelihood()));

					if (fa.getBoundingPoly() != null) {
						face.setBoundingPoly(fa.getBoundingPoly().getVerticesList().stream().map(v -> {
							Vertex vertex = new Vertex();
							vertex.setX(v.getX());
							vertex.setY(v.getY());
							return vertex;
						}).collect(Collectors.toList()));
					}

					if (fa.getFdBoundingPoly() != null) {
						face.setFdBoundingPoly(fa.getFdBoundingPoly().getVerticesList().stream().map(v -> {
							Vertex vertex = new Vertex();
							vertex.setX(v.getX());
							vertex.setY(v.getY());
							return vertex;
						}).collect(Collectors.toList()));
					}

					if (fa.getLandmarksList() != null) {
						face.setLandmarks(fa.getLandmarksList().stream().map(l -> {
							FaceLandmark fl = new FaceLandmark();
							fl.setType(l.getType());
							fl.setX(l.getPosition().getX());
							fl.setY(l.getPosition().getY());
							fl.setZ(l.getPosition().getZ());
							return fl;
						}).collect(Collectors.toList()));
					}

					faces.add(face);
				}
				result.setFaces(faces);
			}
			SafeSearchAnnotation safeSearchAnnotation = resp.getSafeSearchAnnotation();
			if (safeSearchAnnotation != null && Type.SAFE_SEARCH_DETECTION == atributoPesquisa) {
				SafeSearch safeSearch = new SafeSearch();
				safeSearch.setAdult(safeSearchAnnotation.getAdult());
				safeSearch.setAdultRating(likelihoodToNumber(safeSearchAnnotation.getAdult()));
				safeSearch.setMedical(safeSearchAnnotation.getMedical());
				safeSearch.setMedicalRating(likelihoodToNumber(safeSearchAnnotation.getMedical()));
				safeSearch.setSpoof(safeSearchAnnotation.getSpoof());
				safeSearch.setSpoofRating(likelihoodToNumber(safeSearchAnnotation.getSpoof()));
				safeSearch.setViolence(safeSearchAnnotation.getViolence());
				safeSearch.setViolenceRating(likelihoodToNumber(safeSearchAnnotation.getViolence()));

				result.setSafeSearch(safeSearch);
			}

			WebDetection webDetection = resp.getWebDetection();
			if (webDetection != null && Type.WEB_DETECTION == atributoPesquisa) {
				Web web = new Web();
				List<WebImage> fullMatchingImagesList = webDetection.getFullMatchingImagesList();
				List<WebPage> pagesWithMatchingImagesList = webDetection.getPagesWithMatchingImagesList();
				List<WebImage> partialMatchingImagesList = webDetection.getPartialMatchingImagesList();
				List<WebEntity> webEntitiesList = webDetection.getWebEntitiesList();

				if (fullMatchingImagesList != null) {
					web.setFullMatchingImages(fullMatchingImagesList.stream().map(e -> {
						WebUrl wu = new WebUrl();
						wu.setScore(e.getScore());
						wu.setUrl(e.getUrl());
						return wu;
					}).collect(Collectors.toList()));
				}

				if (pagesWithMatchingImagesList != null) {
					web.setPagesWithMatchingImages(pagesWithMatchingImagesList.stream().map(e -> {
						WebUrl wu = new WebUrl();
						wu.setScore(e.getScore());
						wu.setUrl(e.getUrl());
						return wu;
					}).collect(Collectors.toList()));
				}

				if (partialMatchingImagesList != null) {
					web.setPartialMatchingImages(partialMatchingImagesList.stream().map(e -> {
						WebUrl wu = new WebUrl();
						wu.setScore(e.getScore());
						wu.setUrl(e.getUrl());
						return wu;
					}).collect(Collectors.toList()));
				}

				if (webEntitiesList != null) {
					web.setWebEntities(webEntitiesList.stream().map(e -> {
						com.example.demo.model.WebEntity we = new com.example.demo.model.WebEntity();
						we.setDescription(e.getDescription());
						we.setEntityId(e.getEntityId());
						we.setScore(e.getScore());
						return we;
					}).collect(Collectors.toList()));
				}

				result.setWeb(web);
			}
		}

		return result;
	}

	private List<String> splitText(String text) throws IOException {
		InputStream inputStream = getClass().getResourceAsStream("/models/pt-token.bin");
		TokenizerModel model = new TokenizerModel(inputStream);
		TokenizerME tokenizer = new TokenizerME(model);
		String[] tokens = tokenizer.tokenize(text);
		inputStream.close();

		List<String> resultado = new ArrayList<>();

		for (String string : tokens) {
			String retiraAcento = removePunctuations(string);
			if (retiraAcento.length() > 0) {
				resultado.add(retiraAcento);
			}
		}

		return resultado.stream().distinct().collect(Collectors.toList());
	}

	private String removePunctuations(String s) {
		String res = "";
		for (Character c : s.toCharArray()) {
			if (Character.isLetterOrDigit(c))
				res += c;
		}
		return res;
	}

	// Mapeamento de grau de probabilidade
	private static float likelihoodToNumber(Likelihood likelihood) {
		switch (likelihood) {
		case UNKNOWN:
			return 0f;
		case VERY_UNLIKELY:
			return 0.2f;
		case UNLIKELY:
			return 0.4f;
		case POSSIBLE:
			return 0.6f;
		case LIKELY:
			return 0.8f;
		case VERY_LIKELY:
			return 1f;
		case UNRECOGNIZED:
			return 0f;
		default:
			return 0f;
		}
	}

}
