package com.adobe.aem.portfolio.core.models;

import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

@Model(adaptables = Resource.class , defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class HeaderSubNavigationModel {
    

    @Inject
    private String aboutUsNavTitle;

     @Inject
    private String aboutUsSubNavUrl;

     public String getAboutUsNavTitle() {
         return aboutUsNavTitle;
     }

     public String getAboutUsSubNavUrl() {
         return aboutUsSubNavUrl;
     }


} 
