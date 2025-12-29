package com.adobe.aem.portfolio.core.servlets;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servlet to update page properties (e.g., brand) on AEM pages.
 * 
 * Endpoint: /bin/updatePageProp
 * Method: POST
 * Parameters:
 *   - pagePath: The path to the AEM page (required)
 *   - brand: The brand value to set (required)
 */
@Component(service = Servlet.class)
@SlingServletPaths(value = "/bin/updatePageProp")
public class updatePagePropertyServlet extends SlingAllMethodsServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(updatePagePropertyServlet.class);

    /**
     * Handles GET requests - returns method not allowed since data modification
     * should only happen via POST for REST compliance and security.
     */
    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(SlingHttpServletResponse.SC_METHOD_NOT_ALLOWED);
        response.getWriter().write("{\"error\": \"Use POST method to update properties\"}");
    }

    /**
     * Handles POST requests to update the 'brand' property on a page's jcr:content node.
     */
    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            // Get parameters from request
            String pagePath = request.getParameter("pagePath");
            String brand = request.getParameter("brand");

            // Validate pagePath parameter
            if (pagePath == null || pagePath.isEmpty()) {
                response.setStatus(SlingHttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\": \"pagePath parameter is required\"}");
                LOG.warn("pagePath parameter is missing");
                return;
            }

            // Validate brand parameter
            if (brand == null || brand.isEmpty()) {
                response.setStatus(SlingHttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\": \"brand parameter is required\"}");
                LOG.warn("brand parameter is missing");
                return;
            }

            // Get ResourceResolver from request
            ResourceResolver resourceResolver = request.getResourceResolver();

            // Construct path to jcr:content node
            String jcrContentPath = pagePath + "/jcr:content";
            Resource pageResource = resourceResolver.getResource(jcrContentPath);

            // Check if the page resource exists
            if (pageResource == null) {
                response.setStatus(SlingHttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"error\": \"Page not found at path: " + pagePath + "\"}");
                LOG.warn("Page not found at path: {}", jcrContentPath);
                return;
            }

            // Adapt to ModifiableValueMap to modify properties
            ModifiableValueMap properties = pageResource.adaptTo(ModifiableValueMap.class);

            // Check if we have write access
            if (properties == null) {
                response.setStatus(SlingHttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("{\"error\": \"No write access to this resource\"}");
                LOG.warn("No write access to resource: {}", jcrContentPath);
                return;
            }

            // Update the brand property
            properties.put("brand", brand);

            // Commit changes to the repository
            resourceResolver.commit();

            LOG.info("Successfully updated brand property to '{}' on page: {}", brand, pagePath);

            // Send success response
            response.setStatus(SlingHttpServletResponse.SC_OK);
            response.getWriter().write(
                "{\"success\": true, " +
                "\"message\": \"Brand property updated successfully\", " +
                "\"pagePath\": \"" + pagePath + "\", " +
                "\"brand\": \"" + brand + "\"}"
            );

        } catch (Exception e) {
            LOG.error("Error updating page property", e);
            response.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"An internal error occurred while updating the property\"}");
        }
    }
}
