package com.example.echart;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import javax.imageio.ImageIO;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@SpringBootApplication
public class EchartApplication {

	public static Gson gson = new GsonBuilder().setPrettyPrinting().setDateFormat("yyyy-MM-dd HH:mm:ss")
			.serializeNulls().create();

	public static void main(String[] args) throws IOException, InterruptedException, InvalidFormatException {
		SpringApplication.run(EchartApplication.class, args);
//		PDDocument document = new PDDocument();
//		PDPage page = new PDPage(PDRectangle.A4);
//		PDPageContentStream contentStream = 
//		  new PDPageContentStream(document, page, 
//		          PDPageContentStream.AppendMode.APPEND, false);
//		PDImageXObject image = PDImageXObject.createFromByteArray(
//		        document, getChartData(), "chart.png");
//		// Scale the image to full-width with a 20-point margin
//		float ratio = (float) image.getWidth() / image.getHeight();
//		float w = page.getCropBox().getWidth() - 40f;
//		float h = w / ratio;
//		float x = 20f;
//		float y = page.getCropBox().getHeight() - h - 20f;
//		contentStream.drawImage(image, x, y, w, h);
//		contentStream.close();
//		document.addPage(page);
//		document.save("chart.pdf");
//		document.close();
//		BufferedImage bImage = ImageIO.read(new File("sample.jpg"));
//	      ByteArrayOutputStream bos = new ByteArrayOutputStream();
//	      ImageIO.write(bImage, "jpg", bos );
//	      byte [] data = bos.toByteArray();

		ByteArrayInputStream bis = new ByteArrayInputStream(getChartData());
		BufferedImage bImage2 = ImageIO.read(bis);
		ImageIO.write(bImage2, "png", new File("C:\\\\sample\\\\chart.png"));
		System.out.println("image created");

		String outputFile = "C:\\sample\\Template-TagSplitByFormatting.docx";
		FileInputStream fis = new FileInputStream("C:\\sample\\Template.docx");
		String imgFile = "C:\\sample\\chart.png";
		XWPFDocument doc = new XWPFDocument(fis);

		File image = new File(imgFile);
		FileInputStream imageData = new FileInputStream(image);

		// Step 5: Retrieving the image file name and image
		// type
		XWPFParagraph paragraph = doc.createParagraph();
		XWPFRun run = paragraph.createRun();
		int imageType = XWPFDocument.PICTURE_TYPE_PNG;
		String imageFileName = image.getName();

		// Step 6: Setting the width and height of the image
		// in pixels.
		int width = 450;
		int height = 400;

		// Step 7: Adding the picture using the addPicture()
		// method and writing into the document
		run.addPicture(imageData, imageType, imageFileName, Units.toEMU(width), Units.toEMU(height));
		System.out.println("Writing overall result to " + outputFile);
		try (OutputStream out = new FileOutputStream(outputFile)) {
			doc.write(out);
		}
	}

	private static byte[] getChartData() throws IOException, InterruptedException {
		HttpClient httpClient = HttpClient.newHttpClient();
		JsonObject option = new JsonObject();
		JsonObject title = new JsonObject();
		title.addProperty("text", "ECharts");
		JsonObject textStyle = new JsonObject();
		textStyle.addProperty("aniation", "true");
		JsonObject tooltip = new JsonObject();
		JsonObject legend = new JsonObject();
		JsonArray legendData = new JsonArray();
		legendData.add("sales");
		JsonObject xAxis = new JsonObject();
		JsonArray xAxisData = new JsonArray();
		xAxisData.add("Shirts");
		xAxisData.add("Cardigans");
		xAxisData.add("Chiffons");
		xAxisData.add("Pants");
		xAxisData.add("Heels");
		xAxisData.add("Socks");
		xAxis.add("data", xAxisData);

		JsonObject yAxis = new JsonObject();
		JsonArray series = new JsonArray();
		JsonObject seriesData = new JsonObject();
		seriesData.addProperty("name", "sales");
		seriesData.addProperty("type", "bar");
		JsonArray seriesDataOFData = new JsonArray();
		seriesDataOFData.add(5);
		seriesDataOFData.add(20);
		seriesDataOFData.add(36);
		seriesDataOFData.add(10);
		seriesDataOFData.add(0);
		seriesData.add("data", seriesDataOFData);
		series.add(seriesData);
		option.add("title", title);
		option.add("textStyle", textStyle);
		option.add("tooltip", tooltip);
		option.add("legend", legend);
		option.add("xAxis", xAxis);
		option.add("yAxis", yAxis);
		option.add("series", series);

		String opionData = gson.toJson(option);

		HttpRequest request = HttpRequest.newBuilder().header("Content-Type", "application/json")
				.header("X-Chart-Width", "300").header("X-Chart-Height", "200").POST(BodyPublishers.ofString(opionData))
				.uri(URI.create("http://localhost:8080/")).build();
		HttpResponse<byte[]> response = httpClient.send(request, BodyHandlers.ofByteArray());
		return response.statusCode() == 200 ? response.body() : null;
	}

}
