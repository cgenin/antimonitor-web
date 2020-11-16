package fr.genin.christophe.antimonitor.dto;

import org.jboss.resteasy.annotations.providers.multipart.PartType;

import javax.ws.rs.FormParam;
import javax.ws.rs.core.MediaType;
import java.io.InputStream;

public class JsonFileDto {

    @FormParam("file")
    @PartType(MediaType.APPLICATION_OCTET_STREAM)
    public InputStream file;

    @FormParam("filename")
    @PartType(MediaType.TEXT_PLAIN)
    public String filename;

    @Override
    public String toString() {
        return "JsonFileDto{" +
                "file=" + file +
                ", filename='" + filename + '\'' +
                '}';
    }
}
