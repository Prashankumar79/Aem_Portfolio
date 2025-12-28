package com.adobe.aem.portfolio.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

/**
 * Sling Model for the Portfolio Header component.
 * Single-page header with logo, glowing subtitle, and section anchor links.
 */
@Model(
    adaptables = Resource.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class PortfolioHeaderModel {

    @ValueMapValue
    private String logoImage;

    @ValueMapValue
    @Default(values = "Full-Stack AEM Developer")
    private String subtitle;

    @ValueMapValue
    @Default(values = "#now")
    private String nowSectionId;

    @ValueMapValue
    @Default(values = "#writing")
    private String writingSectionId;

    @ValueMapValue
    @Default(values = "#contact")
    private String contactSectionId;

    // Getters
    public String getLogoImage() {
        return logoImage;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getNowSectionId() {
        return nowSectionId;
    }

    public String getWritingSectionId() {
        return writingSectionId;
    }

    public String getContactSectionId() {
        return contactSectionId;
    }

    public boolean getHasLogo() {
        return logoImage != null && !logoImage.isEmpty();
    }

    public boolean getHasSubtitle() {
        return subtitle != null && !subtitle.isEmpty();
    }
}
