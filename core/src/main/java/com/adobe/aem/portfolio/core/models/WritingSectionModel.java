package com.adobe.aem.portfolio.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Sling Model for Writing Section Component
 * Displays a list of blog/article links (e.g., Medium articles)
 */
@Model(
    adaptables = Resource.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class WritingSectionModel {

    @SlingObject
    private ResourceResolver resourceResolver;

    @ChildResource(name = "articles")
    private List<Resource> articleResources;

    @ValueMapValue
    private String sectionTitle;

    @ValueMapValue
    private String sectionDescription;

    private List<Article> articles;

    @PostConstruct
    protected void init() {
        articles = new ArrayList<>();
        if (articleResources != null) {
            for (Resource res : articleResources) {
                Article article = new Article();
                article.setTitle(res.getValueMap().get("title", String.class));
                article.setDescription(res.getValueMap().get("description", String.class));
                article.setLink(res.getValueMap().get("link", String.class));
                article.setPublishDate(res.getValueMap().get("publishDate", String.class));
                article.setPlatform(res.getValueMap().get("platform", String.class));
                articles.add(article);
            }
        }
    }

    // Getters
    public List<Article> getArticles() {
        return articles != null ? articles : Collections.emptyList();
    }

    public String getSectionTitle() {
        return sectionTitle != null ? sectionTitle : "Writing";
    }

    public String getSectionDescription() {
        return sectionDescription;
    }

    public boolean getHasArticles() {
        return articles != null && !articles.isEmpty();
    }

    public boolean getHasSectionDescription() {
        return sectionDescription != null && !sectionDescription.isEmpty();
    }

    public int getArticleCount() {
        return articles != null ? articles.size() : 0;
    }

    // Switch to compact/grid mode when more than 4 articles
    public boolean getIsCompactMode() {
        return getArticleCount() > 4;
    }

    public String getLayoutClass() {
        return getIsCompactMode() ? "writing-section__articles--grid" : "writing-section__articles--list";
    }

    // Inner class for Article
    public static class Article {
        private String title;
        private String description;
        private String link;
        private String publishDate;
        private String platform;

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public String getLink() { return link; }
        public void setLink(String link) { this.link = link; }

        public String getPublishDate() { return publishDate; }
        public void setPublishDate(String publishDate) { this.publishDate = publishDate; }

        public String getPlatform() { return platform != null ? platform : "Medium"; }
        public void setPlatform(String platform) { this.platform = platform; }

        public boolean getHasLink() { return link != null && !link.isEmpty(); }
        public boolean getHasDescription() { return description != null && !description.isEmpty(); }
    }
}
