/*
 *  Card Model - Adobe Experience Manager Portfolio
 *
 *  A Sling Model for the Card component that provides
 *  properties for rendering beautiful portfolio cards.
 */
package com.adobe.aem.portfolio.core.models;

import javax.annotation.PostConstruct;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

/**
 * Sling Model for the Card component.
 * 
 * This model provides all the properties needed to render
 * a beautiful, interactive card in the portfolio.
 */
@Model(
    adaptables = Resource.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class CardModel {

    @SlingObject
    private Resource currentResource;

    @ValueMapValue
    @Default(values = "Card Title")
    private String title;

    @ValueMapValue
    @Default(values = "")
    private String description;

    @ValueMapValue
    @Default(values = "")
    private String image;

    @ValueMapValue
    @Default(values = "")
    private String imageAlt;

    @ValueMapValue
    @Default(values = "")
    private String link;

    @ValueMapValue
    @Default(values = "_self")
    private String linkTarget;

    @ValueMapValue
    @Default(values = "Learn More")
    private String buttonText;

    @ValueMapValue
    @Default(values = "default")
    private String theme;

    @ValueMapValue
    @Default(values = "")
    private String icon;

    @ValueMapValue
    @Default(values = "")
    private String tagText;

    @ValueMapValue
    @Default(values = "")
    private String animationStyle;

    private String componentId;

    @PostConstruct
    protected void init() {
        // Generate a unique ID for this card instance
        if (currentResource != null) {
            componentId = "card-" + Math.abs(currentResource.getPath().hashCode());
        } else {
            componentId = "card-" + System.currentTimeMillis();
        }
    }

    /**
     * Helper method to check if a string is not blank.
     * Replaces StringUtils.isNotBlank() to avoid bundle dependency issues.
     */
    private boolean isNotBlank(String str) {
        return str != null && !str.trim().isEmpty();
    }

    /**
     * Gets the card title.
     * @return The title text
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets the card description.
     * @return The description text
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the image path.
     * @return The image path
     */
    public String getImage() {
        return image;
    }

    /**
     * Gets the image alt text for accessibility.
     * @return The alt text, defaults to title if empty
     */
    public String getImageAlt() {
        return isNotBlank(imageAlt) ? imageAlt : title;
    }

    /**
     * Gets the link URL.
     * @return The link URL
     */
    public String getLink() {
        return link;
    }

    /**
     * Gets the link target.
     * @return The target (_self, _blank, etc.)
     */
    public String getLinkTarget() {
        return linkTarget;
    }

    /**
     * Gets the button text.
     * @return The button label
     */
    public String getButtonText() {
        return buttonText;
    }

    /**
     * Gets the card theme.
     * Options: default, dark, gradient, glass
     * @return The theme class name
     */
    public String getTheme() {
        return "cmp-card--" + theme;
    }

    /**
     * Gets the icon class.
     * @return The icon class (e.g., for Font Awesome)
     */
    public String getIcon() {
        return icon;
    }

    /**
     * Gets the tag/badge text.
     * @return The tag text
     */
    public String getTagText() {
        return tagText;
    }

    /**
     * Gets the animation style.
     * Options: fade, slide, zoom
     * @return The animation class
     */
    public String getAnimationStyle() {
        return isNotBlank(animationStyle) ? "cmp-card--animate-" + animationStyle : "";
    }

    /**
     * Gets the unique component ID.
     * @return The generated component ID
     */
    public String getComponentId() {
        return componentId;
    }

    /**
     * Checks if the card has an image.
     * @return true if image path is not empty
     */
    public boolean hasImage() {
        return isNotBlank(image);
    }

    /**
     * Checks if the card has a link.
     * @return true if link is not empty
     */
    public boolean hasLink() {
        return isNotBlank(link);
    }

    /**
     * Checks if the card has a tag.
     * @return true if tag text is not empty
     */
    public boolean hasTag() {
        return isNotBlank(tagText);
    }

    /**
     * Checks if the card has an icon.
     * @return true if icon is not empty
     */
    public boolean hasIcon() {
        return isNotBlank(icon);
    }
}
