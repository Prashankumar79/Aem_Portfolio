package com.adobe.aem.portfolio.core.models;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import javax.annotation.PostConstruct;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

/**
 * Sling Model for the Now Section Component.
 * 
 * Features:
 * - Experience timeline with company, role, and dates
 * - Projects list with title, description, and link
 * - CV/Resume download link
 * 
 * Dialog structure:
 * - experiences (multifield) -> company, role, startDate, endDate, current
 * - projects (multifield) -> title, description, link
 * - cvLink (pathfield)
 * - cvLinkText (textfield)
 */
@Model(
    adaptables = Resource.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class NowSectionModel {

    @SlingObject
    private ResourceResolver resourceResolver;

    @ChildResource(name = "experiences")
    private List<Resource> experienceResources;

    @ChildResource(name = "projects")
    private List<Resource> projectResources;

    @ValueMapValue
    private String cvLink;

    @ValueMapValue
    private String cvLinkText;

    @ValueMapValue
    private String sectionTitle;

    @ValueMapValue
    private String experienceTitle;

    @ValueMapValue
    private String projectsTitle;

    @ValueMapValue
    private String profileSummary;

    @ChildResource(name = "skillCategories")
    private List<Resource> skillCategoryResources;

    private List<Experience> experiences;
    private List<Project> projects;
    private List<SkillCategory> skillCategories;

    @PostConstruct
    protected void init() {
        // Parse experience resources
        experiences = new ArrayList<>();
        if (experienceResources != null) {
            for (Resource res : experienceResources) {
                Experience exp = new Experience();
                exp.setCompany(res.getValueMap().get("company", String.class));
                exp.setRole(res.getValueMap().get("role", String.class));
                exp.setStartDate(res.getValueMap().get("startDate", String.class));
                exp.setEndDate(res.getValueMap().get("endDate", String.class));
                exp.setCurrent(res.getValueMap().get("current", false));
                exp.setDescription(res.getValueMap().get("description", String.class));
                
                // Parse technologies as comma-separated string
                String techStr = res.getValueMap().get("technologies", String.class);
                if (techStr != null && !techStr.isEmpty()) {
                    List<String> techList = new ArrayList<>();
                    for (String tech : techStr.split(",")) {
                        String trimmed = tech.trim();
                        if (!trimmed.isEmpty()) {
                            techList.add(trimmed);
                        }
                    }
                    exp.setTechnologies(techList);
                }
                
                experiences.add(exp);
            }
        }

        // Parse project resources
        projects = new ArrayList<>();
        if (projectResources != null) {
            for (Resource res : projectResources) {
                Project proj = new Project();
                proj.setTitle(res.getValueMap().get("title", String.class));
                proj.setDescription(res.getValueMap().get("description", String.class));
                proj.setLink(res.getValueMap().get("link", String.class));
                proj.setTechStack(res.getValueMap().get("techStack", String.class));
                projects.add(proj);
            }
        }

        // Parse skill categories
        skillCategories = new ArrayList<>();
        if (skillCategoryResources != null) {
            for (Resource res : skillCategoryResources) {
                SkillCategory cat = new SkillCategory();
                cat.setCategoryName(res.getValueMap().get("categoryName", String.class));
                
                String skillsStr = res.getValueMap().get("skills", String.class);
                if (skillsStr != null && !skillsStr.isEmpty()) {
                    List<String> skillList = new ArrayList<>();
                    for (String skill : skillsStr.split(",")) {
                        String trimmed = skill.trim();
                        if (!trimmed.isEmpty()) {
                            skillList.add(trimmed);
                        }
                    }
                    cat.setSkills(skillList);
                }
                
                skillCategories.add(cat);
            }
        }
    }

    // Getters
    public List<Experience> getExperiences() {
        return experiences != null ? experiences : Collections.emptyList();
    }

    public List<Project> getProjects() {
        return projects != null ? projects : Collections.emptyList();
    }

    public String getCvLink() {
        return cvLink;
    }

    public String getCvLinkText() {
        return cvLinkText != null ? cvLinkText : "Download CV";
    }

    public String getSectionTitle() {
        return sectionTitle != null ? sectionTitle : "Now";
    }

    public String getExperienceTitle() {
        return experienceTitle != null ? experienceTitle : "Experience";
    }

    public String getProjectsTitle() {
        return projectsTitle != null ? projectsTitle : "Projects";
    }

    public String getProfileSummary() {
        return profileSummary;
    }

    public boolean getHasProfileSummary() {
        return profileSummary != null && !profileSummary.isEmpty();
    }

    public boolean getHasExperiences() {
        return experiences != null && !experiences.isEmpty();
    }

    public boolean getHasProjects() {
        return projects != null && !projects.isEmpty();
    }

    public List<SkillCategory> getSkillCategories() {
        return skillCategories != null ? skillCategories : Collections.emptyList();
    }

    public boolean getHasSkillCategories() {
        return skillCategories != null && !skillCategories.isEmpty();
    }

    public boolean getHasCvLink() {
        return cvLink != null && !cvLink.isEmpty();
    }

    // Inner class for Experience
    public static class Experience {
        private String company;
        private String role;
        private String startDate;
        private String endDate;
        private boolean current;
        private String description;
        private List<String> technologies = new ArrayList<>();

        public String getCompany() { return company; }
        public void setCompany(String company) { this.company = company; }

        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }

        public String getStartDate() { return startDate; }
        public void setStartDate(String startDate) { this.startDate = startDate; }

        public String getEndDate() { return current ? "Present" : endDate; }
        public void setEndDate(String endDate) { this.endDate = endDate; }

        public boolean isCurrent() { return current; }
        public void setCurrent(boolean current) { this.current = current; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public List<String> getTechnologies() { return technologies; }
        public void setTechnologies(List<String> technologies) { this.technologies = technologies; }

        public boolean getHasTechnologies() { return technologies != null && !technologies.isEmpty(); }

        public String getTimeline() {
            if (startDate == null) return "";
            return startDate + " - " + getEndDate();
        }
    }

    // Inner class for Project
    public static class Project {
        private String title;
        private String description;
        private String link;
        private String techStack;

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public String getLink() { return link; }
        public void setLink(String link) { this.link = link; }

        public String getTechStack() { return techStack; }
        public void setTechStack(String techStack) { this.techStack = techStack; }

        public boolean getHasLink() { return link != null && !link.isEmpty(); }
    }

    // Inner class for SkillCategory
    public static class SkillCategory {
        private String categoryName;
        private List<String> skills = new ArrayList<>();

        public String getCategoryName() { return categoryName; }
        public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

        public List<String> getSkills() { return skills; }
        public void setSkills(List<String> skills) { this.skills = skills; }

        public boolean getHasSkills() { return skills != null && !skills.isEmpty(); }
    }
}
