package com.portfolio.porfolio.dto;

import java.util.List;

public class PortfolioDto {
    private int id;
    private String name;
    private WebSiteFormDto website;
    private String file;
    private String jobTitle;
    private String description;
    private List<ProjectFormDto> projects;
    private List<LinkFormDto> links;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public WebSiteFormDto getWebsite() {
        return website;
    }

    public void setWebsite(WebSiteFormDto website) {
        this.website = website;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getImage() {
        return this.file;
    }

    public void setImage(String image) {
        if (this.file == null || this.file.isEmpty()) {
            this.file = image;
        }
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ProjectFormDto> getProjects() {
        return projects;
    }

    public void setProjects(List<ProjectFormDto> projects) {
        this.projects = projects;
    }

    public List<LinkFormDto> getLinks() {
        return links;
    }

    public void setLinks(List<LinkFormDto> links) {
        this.links = links;
    }
}