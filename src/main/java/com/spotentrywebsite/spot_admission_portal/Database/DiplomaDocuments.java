package com.spotentrywebsite.spot_admission_portal.Database;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class DiplomaDocuments {

    @Column(name = "diploma_marksheet_uploaded", nullable = false)
    private boolean diplomaMarksheetUploaded;

    @Column(name = "caste_certificate_uploaded", nullable = false)
    private boolean casteCertificateUploaded;

    @Column(name = "non_creamy_layer_uploaded", nullable = false)
    private boolean nonCreamyLayerUploaded;

    // --- GETTERS AND SETTERS ---
    public boolean isDiplomaMarksheetUploaded() { return diplomaMarksheetUploaded; }
    public void setDiplomaMarksheetUploaded(boolean diplomaMarksheetUploaded) { this.diplomaMarksheetUploaded = diplomaMarksheetUploaded; }

    public boolean isCasteCertificateUploaded() { return casteCertificateUploaded; }
    public void setCasteCertificateUploaded(boolean casteCertificateUploaded) { this.casteCertificateUploaded = casteCertificateUploaded; }

    public boolean isNonCreamyLayerUploaded() { return nonCreamyLayerUploaded; }
    public void setNonCreamyLayerUploaded(boolean nonCreamyLayerUploaded) { this.nonCreamyLayerUploaded = nonCreamyLayerUploaded; }
}