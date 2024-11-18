package http;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.util.MimeTypeUtils;
import org.springframework.util.StreamUtils;

import spark.Request;
import spark.Response;

public class MultipartFileMusicSender {

	public static Object sender(Response res, Request req, File file) {
		HttpServletResponse raw = res.raw();

		try {
			String origin = req.headers("Origin");
			res.header("Access-Control-Allow-Credentials", "true");
			res.header("Access-Control-Allow-Origin", origin);

			// write out the request headers:
			//								for (String h : req.headers()) {
			//									log.info("Header:" + h + " = " + req.headers(h));
			//								}

			String range = req.headers("Range");


			// Check if its a non-streaming browser, for example, firefox can't stream
			Boolean nonStreamingBrowser = false;
			//				res.status(206);

			OutputStream os = raw.getOutputStream();

			BufferedOutputStream bos = new BufferedOutputStream(os);


			if (range == null || nonStreamingBrowser) {
				res.header("Content-Length", String.valueOf(file.length())); 
				Files.copy(file.toPath(), os);

				return res.raw();

			}

			int[] fromTo = fromTo(file, range);

			//					new FileInputStream(mp3).getChannel().transferTo(raw.getOutputStream().get);

			int length = (int) (fromTo[1] - fromTo[0] + 1);

			res.status(206);
			res.type("audio/mpeg");

			res.header("Accept-Ranges",  "bytes");

			//					res.header("Content-Length", String.valueOf(mp3.length())); 
			res.header("Content-Range", contentRangeByteString(fromTo));
			res.header("Content-Length", String.valueOf(length)); 
			//				res.header("Content-Length", String.valueOf(mp3.length())); 
			res.header("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
			res.header("Date", new java.util.Date(file.lastModified()).toString());
			res.header("Last-Modified", new java.util.Date(file.lastModified()).toString());
			//				res.header("Server", "Apache");
			res.header("X-Content-Duration", "30");
			res.header("Content-Duration", "30");
			res.header("Connection", "Keep-Alive");
			//					String etag = com.google.common.io.Files.hash(mp3, Hashing.md5()).toString();
			//					res.header("Etag", etag);
			res.header("Cache-Control", "no-cache, private");
			res.header("X-Pad","avoid browser bug");
			res.header("Expires", "0");
			res.header("Pragma", "no-cache");
			res.header("Content-Transfer-Encoding", "binary");
			res.header("Transfer-Encoding", "chunked");
			res.header("Keep-Alive", "timeout=15, max=100");
			res.header("If-None-Match", "webkit-no-cache");
			//					res.header("X-Sendfile", path);
			res.header("X-Stream", "true");

			// This one works, but doesn't stream



			final RandomAccessFile raf = new RandomAccessFile(file, "r");
			raf.seek(fromTo[0]);
			writeAudioToOS(length, raf, bos);

			raf.close();

			bos.flush();
			bos.close();

			return res.raw();

		} catch (Exception e) {
			res.status(666);
			e.printStackTrace();
			return e.getMessage();
		}
	}
	public static int[] fromTo(File mp3, String range) {
		int[] ret = new int[3];

		if (range == null || range.equals("bytes=0-")) {
			//			ret[0] = 0;
			//			ret[1] = mp3.length() -1;
			//			ret[2] = mp3.length();
			//			
			//			return ret;

			//			range = "bytes=0-";

		}

		String[] ranges = range.split("=")[1].split("-");
		
		Integer chunkSize = 512;
		Integer from = Integer.parseInt(ranges[0]);
		Integer to = chunkSize + from;
		if (to >= mp3.length()) {
			to = (int) (mp3.length() - 1);
		}
		if (ranges.length == 2) {
			to = Integer.parseInt(ranges[1]);
		}

		ret[0] = from;
		ret[1] = to;
		ret[2] = (int) mp3.length();
		//		ret[2] = (int) (ret[1] - ret[0] + 1);

		return ret;

	}
	public static void writeAudioToOS(Integer length, RandomAccessFile raf, BufferedOutputStream os) throws IOException {

		byte[] buf = new byte[256];
		while(length != 0) {
			int read = raf.read(buf, 0, buf.length > length ? length : buf.length);
			os.write(buf, 0, read);
			length -= read;
		}

	}
	
	public static String contentRangeByteString(int[] fromTo) {

		String responseRange = "bytes " + fromTo[0] + "-" + fromTo[1] + "/" + fromTo[2];

		return responseRange;

	}
	/**
	 * Write parts of the resource as indicated by the request {@code Range} header.
	 * @param request current servlet request
	 * @param response current servlet response
	 * @param resource the identified resource (never {@code null})
	 * @param contentType the content type
	 * @return 
	 * @throws IOException in case of errors while writing the content
	 */
	public static Object writePartialContent(HttpServletRequest request, HttpServletResponse response,
			File file, MediaType contentType) throws IOException {
		
		long length = file.length();

		List<HttpRange> ranges;
		try {
			HttpHeaders headers = new ServletServerHttpRequest(request).getHeaders();
			ranges = headers.getRange();
		}
		catch (IllegalArgumentException ex) {
			response.addHeader("Content-Range", "bytes */" + length);
			response.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
            return request;
		}

		response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);

		if (ranges.size() == 1) {
			HttpRange range = ranges.get(0);

			long start = range.getRangeStart(length);
			long end = range.getRangeEnd(length);
			long rangeLength = end - start + 1;

			setHeaders(response, file, contentType);
			response.addHeader("Content-Range", "bytes " + start + "-" + end + "/" + length);
            response.setContentLength((int) rangeLength);

			InputStream in = new FileInputStream(file);
			try {
				copyRange(in, response.getOutputStream(), start, end);
			}
			finally {
				try {
					in.close();
				}
				catch (IOException ex) {
					// ignore
				}
			}
		}
		else {
			String boundaryString = MimeTypeUtils.generateMultipartBoundaryString();
			response.setContentType("multipart/byteranges; boundary=" + boundaryString);

			ServletOutputStream out = response.getOutputStream();

			for (HttpRange range : ranges) {
				long start = range.getRangeStart(length);
				long end = range.getRangeEnd(length);

				InputStream in = new FileInputStream(file);

                // Writing MIME header.
                out.println();
                out.println("--" + boundaryString);
                if (contentType != null) {
	                out.println("Content-Type: " + contentType);
                }
                out.println("Content-Range: bytes " + start + "-" + end + "/" + length);
                out.println();

                // Printing content
                copyRange(in, out, start, end);
			}
			out.println();
            out.print("--" + boundaryString + "--");
		}
		return request;
		
	}
	private static void copyRange(InputStream in, OutputStream out, long start, long end) throws IOException {

		long skipped = in.skip(start);

		if (skipped < start) {
			throw new IOException("Skipped only " + skipped + " bytes out of " + start + " required.");
		}

		long bytesToCopy = end - start + 1;

		byte buffer[] = new byte[StreamUtils.BUFFER_SIZE];
		while (bytesToCopy > 0) {
			int bytesRead = in.read(buffer);
			if (bytesRead <= bytesToCopy) {
				out.write(buffer, 0, bytesRead);
				bytesToCopy -= bytesRead;
			}
			else {
				out.write(buffer, 0, (int) bytesToCopy);
				bytesToCopy = 0;
			}
			if (bytesRead < buffer.length) {
				break;
			}
		}
	}

	
	/**
	 * Set headers on the given servlet response.
	 * Called for GET requests as well as HEAD requests.
	 * @param response current servlet response
	 * @param resource the identified resource (never {@code null})
	 * @param mediaType the resource's media type (never {@code null})
	 * @throws IOException in case of errors while setting the headers
	 */
	protected static void setHeaders(HttpServletResponse response, File file, MediaType mediaType) throws IOException {
		long length = file.length();
		response.setContentLength((int) length);

		if (mediaType != null) {
			response.setContentType(mediaType.toString());
		}
		
		response.setHeader(HttpHeaders.ACCEPT_RANGES, "bytes");
	}
}


