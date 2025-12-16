package com.confluence.ConfluenceTest.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "upload-release-details")

public class UploadReleaseDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String environment;
    private String pageName;

    private Instant dateAdded;
    private Instant dateModified;

    public UploadReleaseDetails(Long id, String environment, String pageName, Instant dateAdded, Instant dateModified) {
        this.id = id;
        this.environment = environment;
        this.pageName = pageName;
        this.dateAdded = dateAdded;
        this.dateModified = dateModified;
    }

    public UploadReleaseDetails() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public Instant getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Instant dateAdded) {
        this.dateAdded = dateAdded;
    }

    public Instant getDateModified() {
        return dateModified;
    }

    public void setDateModified(Instant dateModified) {
        this.dateModified = dateModified;
    }
}
