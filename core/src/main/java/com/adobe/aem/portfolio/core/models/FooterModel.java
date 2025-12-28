package com.adobe.aem.portfolio.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

/**
 * Sling Model for Portfolio Footer Component
 * Contains social links and copyright information
 */
@Model(
    adaptables = Resource.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class FooterModel {

    @ValueMapValue
    private String githubLink;

    @ValueMapValue
    private String linkedinLink;

    @ValueMapValue
    private String emailAddress;

    @ValueMapValue
    private String mediumLink;

    @ValueMapValue
    private String twitterLink;

    @ValueMapValue
    private String copyrightText;

    @ValueMapValue
    private String tagline;

    // Getters
    public String getGithubLink() { return githubLink; }
    public String getLinkedinLink() { return linkedinLink; }
    public String getEmailAddress() { return emailAddress; }
    public String getMediumLink() { return mediumLink; }
    public String getTwitterLink() { return twitterLink; }
    
    public String getCopyrightText() {
        return copyrightText != null ? copyrightText : "© 2024 All rights reserved.";
    }
    
    public String getTagline() { return tagline; }

    // Has checks
    public boolean getHasGithub() { return githubLink != null && !githubLink.isEmpty(); }
    public boolean getHasLinkedin() { return linkedinLink != null && !linkedinLink.isEmpty(); }
    public boolean getHasEmail() { return emailAddress != null && !emailAddress.isEmpty(); }
    public boolean getHasMedium() { return mediumLink != null && !mediumLink.isEmpty(); }
    public boolean getHasTwitter() { return twitterLink != null && !twitterLink.isEmpty(); }
    public boolean getHasTagline() { return tagline != null && !tagline.isEmpty(); }
    
    public boolean getHasSocialLinks() {
        return getHasGithub() || getHasLinkedin() || getHasEmail() || getHasMedium() || getHasTwitter();
    }

    public String getEmailLink() {
        return emailAddress != null ? "mailto:" + emailAddress : null;
    }
}
