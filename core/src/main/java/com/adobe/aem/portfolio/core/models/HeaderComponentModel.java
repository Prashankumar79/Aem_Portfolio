package com.adobe.aem.portfolio.core.models;

import java.util.List;

import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;


@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class HeaderComponentModel {

    @Inject
    private String logoImage;

    @Inject
    private String homePageTitle;

    @Inject
    private String homePageUrl;

    @Inject
    private String aboutUsTitle;

    @Inject
    private String aboutUsUrl;

    @Inject
    private String signupTitle;

    @Inject
    private String signupUrl;


        
    @Inject
    private String loginTitle;

    @ChildResource
    private List<HeaderSubNavigationModel> aboutUsNavigation;


    

    public List<HeaderSubNavigationModel> getAboutUsNavigation() {
        return aboutUsNavigation;
    }

    @Inject
    private String loginUrl ;

    public String getLogoImage() {
        return logoImage;
    }

    public String getHomePageTitle() {
        return homePageTitle;
    }

    public String getHomePageUrl() {
        return homePageUrl;
    }

    public String getAboutUsTitle() {
        return aboutUsTitle;
    }

    public String getAboutUsUrl() {
        return aboutUsUrl;
    }

    public String getSignupTitle() {
        return signupTitle;
    }

    public String getSignupUrl() {
        return signupUrl;
    }

    public String getLoginTitle() {
        return loginTitle;
    }

    public String getLoginUrl() {
        return loginUrl;
    }

}
