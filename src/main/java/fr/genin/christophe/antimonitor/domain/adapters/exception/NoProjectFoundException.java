package fr.genin.christophe.antimonitor.domain.adapters.exception;

public class NoProjectFoundException extends IllegalArgumentException {

    public NoProjectFoundException(String artifactId) {
        super("No project found for artifact " + artifactId);
    }
}
