package com.adobe.aem.portfolio.core.models;

import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

/**
 * Sling Model for Header Component 2 (WKND Style).
 * 
 * Simple model with logo properties.
 * Navigation is handled by Core Navigation component.
 */
@Model(
    adaptables = Resource.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class HeaderComponent2Model {

    @Inject
    private String logoImage;

    @Inject
    private String logoLink;

    @Inject
    private String logoAltText;

    @Inject
    private String logoText;

    public String getLogoImage() {
        return logoImage;
    }

    public String getLogoLink() {
        return logoLink != null ? logoLink : "/";
    }

    public String getLogoAltText() {
        return logoAltText != null ? logoAltText : "Logo";
    }

    public String getLogoText() {
        return logoText != null ? logoText : "LOGO";
    }
}
