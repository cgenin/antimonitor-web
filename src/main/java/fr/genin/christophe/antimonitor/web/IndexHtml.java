package fr.genin.christophe.antimonitor.web;


import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

@Provider
public class IndexHtml implements ExceptionMapper<NotFoundException> {

    @Override
    public Response toResponse(NotFoundException exception) {
        final InputStream stream = this.getClass().getResourceAsStream("/META-INF/resources/index.html");
        String text = new Scanner(stream, StandardCharsets.UTF_8)
                .useDelimiter("\\A")
                .next();
        return Response.status(404).entity(text)
                .header(HttpHeaders.CACHE_CONTROL, "no-store")
                .build();
    }
}

