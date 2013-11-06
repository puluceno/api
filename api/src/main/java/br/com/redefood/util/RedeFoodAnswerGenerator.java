package br.com.redefood.util;

import javax.ws.rs.core.Response;

public class RedeFoodAnswerGenerator {
    
    public static Response generateErrorAnswer(int status, String answer) {
	
	String formattedAnswer = "{\"error\":{\"" + RedeFoodAnswers.DEFAULT_ANSWER_HEADER1 + "\":" + "\"" + status
		+ "\",\"" + RedeFoodAnswers.DEFAULT_ANSWER_HEADER2 + "\":" + "\"" + answer + "\"}}";
	
	return Response.status(status).entity(formattedAnswer).build();
    }
    
    public static String generateErrorAnswerString(int status, String answer) {
	
	return "{\"error\":{\"" + RedeFoodAnswers.DEFAULT_ANSWER_HEADER1 + "\":" + "\"" + status + "\",\""
		+ RedeFoodAnswers.DEFAULT_ANSWER_HEADER2 + "\":" + "\"" + answer + "\"}}";
    }
    
    public static Response generateSuccessAnswer(int status, String answer) {
	
	String formattedAnswer = "{\"success\":{\"" + RedeFoodAnswers.DEFAULT_ANSWER_HEADER1 + "\":" + "\"" + status
		+ "\",\"" + RedeFoodAnswers.DEFAULT_ANSWER_HEADER2 + "\":" + "\"" + answer + "\"}}";
	
	return Response.status(status).entity(formattedAnswer).build();
    }
    
    public static Response generateSuccessAnswerWithoutSuccess(int status, String answer) {
	
	String formattedAnswer = "{\"" + RedeFoodAnswers.DEFAULT_PHOTO_ANSWER_HEADER + "\":" + "\"" + answer + "\"}";
	
	return Response.status(status).entity(formattedAnswer).build();
    }
    
    public static Response generateSuccessOrderPOST(int id, int orderNumber, int status, String message) {
	String formattedAnswer = "";
	if (message != null) {
	    formattedAnswer = "{\"id\":\"" + id + "\",\"orderNumber\":\"" + orderNumber + "\",\"errorMessage\":\""
		    + message + "\"}";
	} else {
	    formattedAnswer = "{\"id\":\"" + id + "\",\"orderNumber\":\"" + orderNumber + "\"}";
	}
	
	return Response.status(status).entity(formattedAnswer).build();
    }
    
    public static Response generateSuccessPOST(int id, int status) {
	String formattedAnswer = "{\"id\":\"" + id + "\"}";
	
	return Response.status(status).entity(formattedAnswer).build();
    }
    
    public static String generateRestaurantMenu(String meals, String beverages) {
	return "{\"meals\":" + meals + ",\"beverages\":" + beverages + "}";
    }
    
    public static String generateDashboard(Long data1, String data2) {
	return "";
    }
    
    public static Response unauthorizedProfile() {
	
	int status = 403;
	
	String formattedAnswer = "{\"error\":{\"" + RedeFoodAnswers.DEFAULT_ANSWER_HEADER1 + "\":" + "\"" + status
		+ "\",\"" + RedeFoodAnswers.DEFAULT_ANSWER_HEADER2 + "\":" + "\""
		+ LocaleResource.getProperty("pt_br").getProperty("exception.unauthorized") + "\"}}";
	
	return Response.status(403).entity(formattedAnswer).build();
    }
    
    public static String unauthorizedProfileString() {
	
	int status = 403;
	
	return "{\"error\":{\"" + RedeFoodAnswers.DEFAULT_ANSWER_HEADER1 + "\":" + "\"" + status + "\",\""
	+ RedeFoodAnswers.DEFAULT_ANSWER_HEADER2 + "\":" + "\""
	+ LocaleResource.getProperty("pt_br").getProperty("exception.unauthorized") + "\"}}";
    }
    
    public static Response generateSuccessPOSTwithImage(Short id, String image, int status) {
	return Response
		.status(status)
		.entity("{\"id\":" + id + ",\"" + RedeFoodAnswers.DEFAULT_PHOTO_ANSWER_HEADER + "\":" + "\"" + image
			+ "\"}").build();
    }
    
    public static Response generateSuccessPOSTwithImageEmployee(Short id, String image, int status) {
	Response build = Response
		.status(status)
		.entity("{\"id\":" + id + ",\"" + RedeFoodAnswers.DEFAULT_EMPLOYEE_PHOTO_ANSWER_HEADER + "\":" + "\""
			+ image + "\"}").build();
	return build;
    }
    
    public static Response generateSuccessPOSTExhibitionOrder(Short id, Integer exhibitionOrder, int status) {
	return Response
		.status(status)
		.entity("{\"id\":" + id + ",\"" + RedeFoodAnswers.DEFAULT_EXHIBITION_ORDER_HEADER + "\":" + "\""
			+ exhibitionOrder + "\"}").build();
    }
    
    public static Response generateSuccessPOSTExhibitionOrder(Integer id, Integer exhibitionOrder, int status) {
	return Response
		.status(status)
		.entity("{\"id\":" + id + ",\"" + RedeFoodAnswers.DEFAULT_EXHIBITION_ORDER_HEADER + "\":" + "\""
			+ exhibitionOrder + "\"}").build();
    }
    
    public static Response generateSuccessPOSTwithImageExhibitionOrder(Short id, String image, Integer exhibitionOrder,
	    int status) {
	return Response
		.status(status)
		.entity("{\"id\":" + id + ",\"" + RedeFoodAnswers.DEFAULT_EXHIBITION_ORDER_HEADER + "\":"
			+ exhibitionOrder + ",\"" + RedeFoodAnswers.DEFAULT_PHOTO_ANSWER_HEADER + "\":" + "\"" + image
			+ "\"}").build();
    }
    
    /**
     * Returns an empty json string
     * 
     * @return {@link Response}
     */
    public static Response generateEmptyAnswer() {
	return Response.status(400).entity("[]").build();
    }
    
}