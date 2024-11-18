package http;

import java.awt.image.BufferedImage;
import java.awt.image.ComponentColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.util.Hashtable;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.NotFoundException;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.qrcode.encoder.ByteMatrix;
import com.google.zxing.qrcode.encoder.Encoder;
import com.google.zxing.qrcode.encoder.QRCode;

public class CrearQR {
	private static final int BLACK = 0xFF000000;
	private static final int WHITE = 0x00FFFFFF;
	private static final int QUIET_ZONE_SIZE = 4;


//main() method  
	public static void main(String args[]) throws WriterException, IOException, NotFoundException {
		Display display = new Display();
		Shell shell = new Shell(display);
		Label label = new Label(shell, SWT.NONE);
		label.setText("Can't find icon");
		ImageData image = crearImagenQr("http://192.168.1.29:8080/index.html");
		Image imagen = new Image(null, image);
		label.setImage(imagen);
		label.pack();
		shell.pack();
		shell.open();
		while (!shell.isDisposed()) {
		  if (!display.readAndDispatch())
		    display.sleep();
		}
		if (image != null)
		  imagen.dispose();
		display.dispose();
	}
	
	public static ImageData crearImagenQr(String content) {
		System.out.println(content);
		return convertToSWT(createQrCodeImage(content));
	}

	private static BufferedImage createQrCodeImage(String content) {
		return createQrCodeImage(content, 350, 350);
	}

	/**
	 * Generar imagen de código QR
	 *
	 * @param content contenido de código QR
	 * @param ancho   ancho
	 * @param altura  longitud
	 * @return bufferImage
	 */
	private static BufferedImage createQrCodeImage(String content, int width, int height) {
		long start = System.currentTimeMillis();
		BufferedImage image = null;
		try {
			Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
			hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
			hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
			hints.put(EncodeHintType.MARGIN, 1);

			// Utilice un método personalizado para resolver el problema del borde blanco
			BitMatrix bitMatrix = encode(content, BarcodeFormat.QR_CODE, width, height, hints);
			// Cambiar el tamaño para cumplir con el ancho y alto de entrada
			image = resizeAndCreateBufferedImage(bitMatrix, width, height);
		} catch (WriterException e) {
			System.out.println("no se puede");
		}
		System.out.println("creado en: " + (System.currentTimeMillis() - start));
		return image;
	}

	/**
	 * Modificar la lógica de generación de codificación y eliminar el borde blanco
	 * Para obtener el código fuente, consulte com.google.zxing.qrcode.QRCodeWriter
	 * # encode (java.lang.String, com.google.zxing.BarcodeFormat, int, int,
	 * java.util.Map)
	 *
	 * @param contents contenido de código QR formato de formato @param
	 * @param ancho    ancho
	 * @param altura   longitud
	 * @param hints    hints
	 * @return BitMatrix
	 * @throws WriterException exception
	 */
	private static BitMatrix encode(String contents, BarcodeFormat format, int width, int height,
			Hashtable<EncodeHintType, ?> hints) throws WriterException {
		if (contents.isEmpty()) {
			throw new IllegalArgumentException("Found empty contents");
		}

		if (format != BarcodeFormat.QR_CODE) {
			throw new IllegalArgumentException("Can only encode QR_CODE, but got " + format);
		}

		if (width < 0 || height < 0) {
			throw new IllegalArgumentException("Requested dimensions are too small: " + width + 'x' + height);
		}

		ErrorCorrectionLevel errorCorrectionLevel = ErrorCorrectionLevel.L;
		int quietZone = QUIET_ZONE_SIZE;
		if (hints != null) {
			if (hints.containsKey(EncodeHintType.ERROR_CORRECTION)) {
				errorCorrectionLevel = ErrorCorrectionLevel
						.valueOf(hints.get(EncodeHintType.ERROR_CORRECTION).toString());
			}
			if (hints.containsKey(EncodeHintType.MARGIN)) {
				quietZone = Integer.parseInt(hints.get(EncodeHintType.MARGIN).toString());
			}
		}

		QRCode code = Encoder.encode(contents, errorCorrectionLevel, hints);
		return renderResult(code, width, height, quietZone);
	}

	/**
	 * Expanda el QRCodeWriter de zxing para solucionar el problema del exceso de
	 * bordes blancos. La lógica principal de eliminar los bordes blancos.
	 *
	 * @param code      qrcode
	 * @param width     ancho esperado
	 * @param altura    altura deseada
	 * @param quietZone quietZone
	 * @return BitMatrix
	 */
	private static BitMatrix renderResult(QRCode code, int width, int height, int quietZone) {
		ByteMatrix input = code.getMatrix();
		if (input == null) {
			throw new IllegalStateException();
		}
		// El código QR xxx tiene el mismo ancho y alto, es decir, qrWidth == qrHeight
		int inputWidth = input.getWidth();
		int inputHeight = input.getHeight();
		int qrWidth = inputWidth + (quietZone * 2);
		int qrHeight = inputHeight + (quietZone * 2);
		// hacer zoom cuando hay demasiados bordes blancos
		int minSize = Math.min(width, height);
		int scale = calculateScale(qrWidth, minSize);
		if (scale > 0) {
			int padding, tmpValue;
			// Calcula el borde en blanco
			padding = (minSize - qrWidth * scale) / QUIET_ZONE_SIZE * quietZone;
			tmpValue = qrWidth * scale + padding;
			if (width == height) {
				width = tmpValue;
				height = tmpValue;
			} else if (width > height) {
				width = width * tmpValue / height;
				height = tmpValue;
			} else {
				height = height * tmpValue / width;
				width = tmpValue;
			}
		}
		int outputWidth = Math.max(width, qrWidth);
		int outputHeight = Math.max(height, qrHeight);
		int multiple = Math.min(outputWidth / qrWidth, outputHeight / qrHeight);
		int leftPadding = (outputWidth - (inputWidth * multiple)) / 2;
		int topPadding = (outputHeight - (inputHeight * multiple)) / 2;

		BitMatrix output = new BitMatrix(outputWidth, outputHeight);
		for (int inputY = 0, outputY = topPadding; inputY < inputHeight; inputY++, outputY += multiple) {
			// Write the contents of this row of the barcode
			for (int inputX = 0, outputX = leftPadding; inputX < inputWidth; inputX++, outputX += multiple) {
				if (input.get(inputX, inputY) == 1) {
					output.setRegion(outputX, outputY, multiple, multiple);
				}
			}
		}
		return output;
	}

	/**
	 * Si el margen supera el 15%, necesita hacer zoom (15% puede modificarse según
	 * las necesidades reales)
	 *
	 * @param qrCodeSize Tamaño del código QR
	 * @param hopeSize   tamaño de salida esperado
	 * @return devuelve la relación de zoom, <= 0 significa que no hay zoom; de lo
	 *         contrario, especifique el parámetro de zoom
	 */
	private static int calculateScale(int qrCodeSize, int expectSize) {
		if (qrCodeSize >= expectSize) {
			return 0;
		}
		int scale = expectSize / qrCodeSize;
		int abs = expectSize - scale * qrCodeSize;
		if (abs < expectSize * 0.15) {
			return 0;
		}
		return scale;
	}

	/**
	 * Zoom para ajustar el tamaño del código QR para que se ajuste al tamaño
	 * esperado
	 *
	 * @param matrix matrix
	 * @param width  ancho esperado
	 * @param altura altura deseada
	 * @return bufferedImage
	 */
	private static BufferedImage resizeAndCreateBufferedImage(BitMatrix matrix, int width, int height) {
		int qrCodeWidth = matrix.getWidth();
		int qrCodeHeight = matrix.getHeight();
		BufferedImage qrCode = new BufferedImage(qrCodeWidth, qrCodeHeight, BufferedImage.TYPE_INT_RGB);

		for (int x = 0; x < qrCodeWidth; x++) {
			for (int y = 0; y < qrCodeHeight; y++) {
				qrCode.setRGB(x, y, matrix.get(x, y) ? BLACK : WHITE);
			}
		}

		// Si el ancho y el alto reales del código QR no coinciden con el ancho y alto
		// esperados, haga zoom
		if (qrCodeWidth != width || qrCodeHeight != height) {
			BufferedImage tmp = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			tmp.getGraphics().drawImage(qrCode.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH), 0, 0,
					null);
			qrCode = tmp;
		}

		return qrCode;
	}

	public static ImageData convertToSWT(BufferedImage bufferedImage) {
		if (bufferedImage.getColorModel() instanceof DirectColorModel) {
			/*
			 * DirectColorModel colorModel =
			 * (DirectColorModel)bufferedImage.getColorModel(); PaletteData palette = new
			 * PaletteData( colorModel.getRedMask(), colorModel.getGreenMask(),
			 * colorModel.getBlueMask()); ImageData data = new
			 * ImageData(bufferedImage.getWidth(), bufferedImage.getHeight(),
			 * colorModel.getPixelSize(), palette); WritableRaster raster =
			 * bufferedImage.getRaster(); int[] pixelArray = new int[3]; for (int y = 0; y <
			 * data.height; y++) { for (int x = 0; x < data.width; x++) { raster.getPixel(x,
			 * y, pixelArray); int pixel = palette.getPixel(new RGB(pixelArray[0],
			 * pixelArray[1], pixelArray[2])); data.setPixel(x, y, pixel); } }
			 */
			DirectColorModel colorModel = (DirectColorModel) bufferedImage.getColorModel();
			PaletteData palette = new PaletteData(colorModel.getRedMask(), colorModel.getGreenMask(),
					colorModel.getBlueMask());
			ImageData data = new ImageData(bufferedImage.getWidth(), bufferedImage.getHeight(),
					colorModel.getPixelSize(), palette);
			for (int y = 0; y < data.height; y++) {
				for (int x = 0; x < data.width; x++) {
					int rgb = bufferedImage.getRGB(x, y);
					int pixel = palette.getPixel(new RGB((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF));
					data.setPixel(x, y, pixel);
					if (colorModel.hasAlpha()) {
						data.setAlpha(x, y, (rgb >> 24) & 0xFF);
					}
				}
			}
			return data;
		} else if (bufferedImage.getColorModel() instanceof IndexColorModel) {
			IndexColorModel colorModel = (IndexColorModel) bufferedImage.getColorModel();
			int size = colorModel.getMapSize();
			byte[] reds = new byte[size];
			byte[] greens = new byte[size];
			byte[] blues = new byte[size];
			colorModel.getReds(reds);
			colorModel.getGreens(greens);
			colorModel.getBlues(blues);
			RGB[] rgbs = new RGB[size];
			for (int i = 0; i < rgbs.length; i++) {
				rgbs[i] = new RGB(reds[i] & 0xFF, greens[i] & 0xFF, blues[i] & 0xFF);
			}
			PaletteData palette = new PaletteData(rgbs);
			ImageData data = new ImageData(bufferedImage.getWidth(), bufferedImage.getHeight(),
					colorModel.getPixelSize(), palette);
			data.transparentPixel = colorModel.getTransparentPixel();
			WritableRaster raster = bufferedImage.getRaster();
			int[] pixelArray = new int[1];
			for (int y = 0; y < data.height; y++) {
				for (int x = 0; x < data.width; x++) {
					raster.getPixel(x, y, pixelArray);
					data.setPixel(x, y, pixelArray[0]);
				}
			}
			return data;
		} else if (bufferedImage.getColorModel() instanceof ComponentColorModel) {
			ComponentColorModel colorModel = (ComponentColorModel) bufferedImage.getColorModel();
			// ASSUMES: 3 BYTE BGR IMAGE TYPE
			PaletteData palette = new PaletteData(0x0000FF, 0x00FF00, 0xFF0000);
			ImageData data = new ImageData(bufferedImage.getWidth(), bufferedImage.getHeight(),
					colorModel.getPixelSize(), palette);
			// This is valid because we are using a 3-byte Data model with no transparent
			// pixels
			data.transparentPixel = -1;
			WritableRaster raster = bufferedImage.getRaster();
			int[] pixelArray = new int[3];
			for (int y = 0; y < data.height; y++) {
				for (int x = 0; x < data.width; x++) {
					raster.getPixel(x, y, pixelArray);
					int pixel = palette.getPixel(new RGB(pixelArray[0], pixelArray[1], pixelArray[2]));
					data.setPixel(x, y, pixel);
				}
			}
			return data;
		}
		return null;
	}
}