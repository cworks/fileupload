package github.cworks.fileupload;

import net.cworks.json.JsonArray;
import net.cworks.json.JsonObject;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.Part;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static net.cworks.json.Json.Json;
import static spark.Spark.post;
import static spark.SparkBase.externalStaticFileLocation;

public final class App {

    static final DateTimeFormatter FORMATTER =
        DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");

    static final String UPLOAD_DIR = "uploads";

    /**
     * Boot it up
     * @param args
     */
    public static void main(String[] args) {

        externalStaticFileLocation(getWebRoot(args));
        createUploadsDir(getUploadsDir(args));

        final MultipartConfigElement multipartConfigElement = new MultipartConfigElement("/tmp");

        post("/upload", (request, response) -> {

            request.raw().setAttribute("org.eclipse.multipartConfig", multipartConfigElement);

            JsonArray uploads = Json().array().build();
            request.raw().getParts().stream().forEach( part -> {
                processUpload(part);
                JsonObject uploaded = Json().object()
                    .string("name", part.getName())
                    .string("contentType", part.getContentType())
                    .number("size", part.getSize())
                    .string("uploadedOn", FORMATTER.format(LocalDateTime.now()))
                        .build();

                uploads.addObject(uploaded);
            });

            return uploads.asString();
        });

    }

    static void createUploadsDir(String uploadsDir) {

        File dir = new File(uploadsDir);
        dir.mkdirs();
    }

    static void processUpload(Part part) {

        System.out.println("part.name: " + part.getName());
        System.out.println("part.contentType: " + part.getContentType());
        System.out.println("part.size :" + part.getSize());

        InputStream in = null;
        OutputStream out = null;

        try {
            in = part.getInputStream();
            out = new FileOutputStream(new File(UPLOAD_DIR, part.getName()));
            copy(in, out);
        } catch(IOException ex) {
            ex.printStackTrace();
        } finally {
            if(in  != null) try { in.close();  } catch (Exception ex) { }
            if(out != null) try { out.close(); } catch (Exception ex) { }
        }

    }

    static String getWebRoot(String[] args) {
        if(args.length < 1) {
            throw new IllegalArgumentException("please specify the webroot");
        }

        return args[0];
    }

    static String getUploadsDir(String[] args) {
        if(args.length < 2) {
            throw new IllegalArgumentException("please specify the uploads dir");
        }

        return args[1];
    }

    static long copy(InputStream from, OutputStream to) throws IOException {
        byte[] buf = new byte[4096];
        long total = 0;
        while (true) {
            int r = from.read(buf);
            if (r == -1) {
                break;
            }
            to.write(buf, 0, r);
            total += r;
        }
        return total;
    }
	
}