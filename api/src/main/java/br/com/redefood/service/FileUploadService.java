package br.com.redefood.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import br.com.redefood.util.RedeFoodConstants;

public class FileUploadService {

	public static String uploadFile(String userClass, String userID, MultipartFormDataInput input) {

		String fileName = "";
		String extension = "";
		int fileNameHashed = 0;
		long size = 0;
		byte[] bytes = null;
		String fileNamePath = "";

		Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
		List<InputPart> inputParts = uploadForm.get("photo");
		if (inputParts == null || inputParts.isEmpty()) {
			inputParts = uploadForm.get("image");
		}
		if (inputParts == null || inputParts.isEmpty()) {
			inputParts = uploadForm.get("imagePopUp");
		}
		if (inputParts == null || inputParts.isEmpty()) {
			inputParts = uploadForm.get("imageBanner");
		}
		if (inputParts == null || inputParts.isEmpty()) {
			inputParts = uploadForm.get("imageInfo");
		}
		if (inputParts == null || inputParts.isEmpty()) {
			inputParts = uploadForm.get("imageLogo");
		}
		if (inputParts == null || inputParts.isEmpty()) {
			inputParts = uploadForm.get("logo");
		}
		if (inputParts == null || inputParts.isEmpty())
			return "error";

		for (InputPart inputPart : inputParts) {

			try {

				MultivaluedMap<String, String> header = inputPart.getHeaders();
				fileName = getFileName(header);

				// convert the uploaded file to inputstream
				InputStream inputStream = inputPart.getBody(InputStream.class, null);

				bytes = IOUtils.toByteArray(inputStream);

				// constructs upload file name
				extension = fileName.split("\\.")[1];
				Double a = Arrays.hashCode(bytes) * 31.31;
				fileNameHashed = a.intValue();

				// constructs upload file path
				String directory = RedeFoodConstants.DEFAULT_UPLOADED_FILE_PATH + userClass + "/" + userID + "/";
				createDirectory(directory);

				fileNamePath = directory + fileNameHashed + "." + extension;
				size = writeFile(bytes, fileNamePath);

			} catch (Exception e) {
				return "error";
			}

		}

		System.out.println("Saved " + fileNameHashed + " with size " + size / 1024 + " kb.");
		return userClass + "/" + userID + "/" + fileNameHashed + "." + extension;

	}

	private static void createDirectory(String directory) throws Exception {
		File theDir = new File(directory);

		// if the directory does not exist, create it
		if (!theDir.exists()) {
			boolean result = theDir.mkdirs();
			if (result) {
				System.out.println(directory + " created.");
			}

		}
	}

	/**
	 * header sample { Content-Type=[image/png], Content-Disposition=[form-data;
	 * name="file"; filename="filename.extension"] }
	 **/
	private static String getFileName(MultivaluedMap<String, String> header) {

		String[] contentDisposition = header.getFirst("Content-Disposition").split(";");

		for (String filename : contentDisposition) {
			if (filename.trim().startsWith("filename")) {

				String[] name = filename.split("=");

				String finalFileName = name[1].trim().replaceAll("\"", "");
				return finalFileName;
			}
		}
		return "unknown";
	}

	// save to somewhere
	private static long writeFile(byte[] content, String filename) throws IOException {

		File file = new File(filename);

		if (!file.exists()) {
			file.createNewFile();
		}

		FileOutputStream fop = new FileOutputStream(file);

		fop.write(content);
		fop.flush();
		fop.close();

		return file.length();

	}

	public static void deleteOldFile(String fileName) throws Exception {
		File toDelete = new File(RedeFoodConstants.DEFAULT_UPLOADED_FILE_PATH + fileName);
		toDelete.delete();
	}
}