package fr.genin.christophe.antimonitor.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.json.bind.annotation.JsonbProperty;
import java.util.ArrayList;
import java.util.List;


public class Configuration {


    private String monithorUrl;
    private List<String> javaFilters = new ArrayList<>();
    private List<String> npmFilters = new ArrayList<>();
    private List<String> ignoreJava = new ArrayList<>();

    public String getMonithorUrl() {
        return monithorUrl;
    }

    public void setMonithorUrl(String monithorUrl) {
        this.monithorUrl = monithorUrl;
    }

    public List<String> getJavaFilters() {
        return javaFilters;
    }

    public void setJavaFilters(List<String> javaFilters) {
        this.javaFilters = javaFilters;
    }

    public List<String> getNpmFilters() {
        return npmFilters;
    }

    public void setNpmFilters(List<String> npmFilters) {
        this.npmFilters = npmFilters;
    }

    public List<String> getIgnoreJava() {
        return ignoreJava;
    }

    public void setIgnoreJava(List<String> ignoreJava) {
        this.ignoreJava = ignoreJava;
    }
}
