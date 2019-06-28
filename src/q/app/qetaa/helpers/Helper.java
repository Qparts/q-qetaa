package q.app.qetaa.helpers;

import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.imageio.ImageIO;

public class Helper {
	private static String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxuz1234567890";


	public static byte[] base64ToByteArray(String imageString){
		return Base64.getMimeDecoder().decode(imageString.split(",")[1]);
		//return Base64.getDecoder().decode(new String(name).getBytes(StandardCharsets.UTF_8));
	}


	public static byte[] inputStreamToBytesArray(InputStream is) throws IOException {
		final byte[] bytes;
		try(is){
			bytes = is.readAllBytes();
			return bytes;
		} catch(Exception e){
		}
		return new byte[0];

	}

	public static List<Integer> getYearsRange(int from, int to) {
		List<Integer> ints = new ArrayList<Integer>();
		for (int i = from; i <= to; i++) {
			ints.add(i);
		}
		return ints;
	}

	public static void redirect(String path) {
		try {
			FacesContext context = FacesContext.getCurrentInstance();
			context.getExternalContext().redirect(path);
			return;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static Integer paymentIntegerFormat(double am) {
		return Double.valueOf(am * 100).intValue();
	}

	public static String getParam(String key) {
		FacesContext context = FacesContext.getCurrentInstance();
		String code = context.getExternalContext().getRequestParameterMap().get(key);
		return code;
	}

	public static void addInfoMessage(String text) {
		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, text, null));
	}

	public static void addErrorMessage(String text) {
		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, text, null));
	}

	public static void addErrorMessage(String text, String clientId) {
		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage(clientId, new FacesMessage(FacesMessage.SEVERITY_ERROR, text, null));
	}

	public static String getRandomSaltString() {
		StringBuilder salt = new StringBuilder();
		Random rnd = new Random();
		while (salt.length() < 6) { // length of the random string.
			int index = (int) (rnd.nextFloat() * SALTCHARS.length());
			salt.append(SALTCHARS.charAt(index));
		}
		return salt.toString();
	}

	public static String cypherSha256(String text) {
		try {
			String shaval = "";
			MessageDigest algorithm = MessageDigest.getInstance("SHA-256");

			byte[] defaultBytes = text.getBytes();

			algorithm.reset();
			algorithm.update(defaultBytes);
			byte messageDigest[] = algorithm.digest();
			StringBuilder hexString = new StringBuilder();

			for (int i = 0; i < messageDigest.length; i++) {
				String hex = Integer.toHexString(0xFF & messageDigest[i]);
				if (hex.length() == 1) {
					hexString.append('0');
				}
				hexString.append(hex);
			}
			shaval = hexString.toString();

			return shaval;
		} catch (NoSuchAlgorithmException na) {
			return text;
		}
	}

	public static void writeImage(String base64Image, Long cartId, Date date) throws IOException {
		String imageString = base64Image.split(",")[1];
		BufferedImage image = null;
		byte[] imageBytes;
		Decoder decoder = Base64.getDecoder();
		imageBytes = decoder.decode(imageString);
		ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
		image = ImageIO.read(bis);
		bis.close();
		String directory = getVinImageDirectory(date);
		createDirectory(directory);
		String imageName = cartId + ".jpg";
		// create directories

		// write the image to a file
		File outputfile = new File(directory + imageName);
		ImageIO.write(image, "png", outputfile);

	}

	private static void createDirectory(String directory) throws IOException {
		Files.createDirectories(Paths.get(directory));
	}

	public static String getVinImageDirectory(Date date) {
		Calendar c = new GregorianCalendar();
		c.setTime(date);
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH) + 1;
		int day = c.get(Calendar.DAY_OF_MONTH);
		//return AppConstants.getVINDirectoryWithDate(year, month, day);
		return "";
	}

}
