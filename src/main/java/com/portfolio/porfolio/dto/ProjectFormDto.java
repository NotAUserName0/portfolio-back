package com.portfolio.porfolio.dto;

import java.util.List;

public class ProjectFormDto {
    private int id;
    private String name;
    private String description;
    private String file;
    private String[] tags;
    private List<LinkFormDto> links;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return this.name;
    }

    public void setTitle(String title) {
        this.name = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public List<LinkFormDto> getLinks() {
        return links;
    }

    public void setLinks(List<LinkFormDto> links) {
        this.links = links;
    }
}
