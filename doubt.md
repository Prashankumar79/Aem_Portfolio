# AEM Development - Doubts & Clarifications

Common questions and answers related to AEM component development.

---

## 📚 ClientLibs - Why CSS/JS Not Loading?

### Q1: I created a clientlib but CSS/JS is not applied. Why?

**Reason**: Creating a clientlib is NOT enough. You must **include it on the page template**.

```
Clientlib created: ✅ portfolio.card (css/card.css, js/card.js)
                      ↓
Page template:     ❌ Only includes portfolio.base
                      ↓
Component:         ❌ No styles/scripts loaded!
```

**Solution**: Add clientlib to page template:

```html
<!-- customheaderlibs.html (for CSS) -->
<sly data-sly-use.clientlib="core/wcm/components/commons/v1/templates/clientlib.html">
    <sly data-sly-call="${clientlib.css @ categories='portfolio.base'}"/>
    <sly data-sly-call="${clientlib.css @ categories='portfolio.card'}"/>  <!-- ADD THIS -->
</sly>

<!-- customfooterlibs.html (for JS) -->
<sly data-sly-use.clientlib="core/wcm/components/commons/v1/templates/clientlib.html">
    <sly data-sly-call="${clientlib.js @ categories='portfolio.base'}"/>
    <sly data-sly-call="${clientlib.js @ categories='portfolio.card'}"/>  <!-- ADD THIS -->
</sly>
```

---

### Q2: What are clientlib categories?

Categories are **identifiers** to include CSS/JS on pages:

```xml
<!-- Clientlib definition (.content.xml) -->
<jcr:root jcr:primaryType="cq:ClientLibraryFolder"
    categories="[portfolio.card]"
    dependencies="[portfolio.base]"/>
```

---

### Q3: Difference between `dependencies` and `embed`?

| Property | Behavior |
|----------|----------|
| `dependencies` | Other clientlibs load BEFORE this one (separate HTTP requests) |
| `embed` | Other clientlibs are MERGED into this one (single HTTP request) |

---

## 📦 OSGi & Bundles

### Q4: Difference between "Installed" and "Active" bundle states?

| State | Meaning |
|-------|---------|
| **Installed** | Bundle JAR is present but dependencies NOT resolved |
| **Active** | Bundle is running and all services available |

**If stuck in Installed**: Check Imported Packages for unresolved dependencies.

---

### Q5: Why did commons-lang3 cause issues?

`pom.xml` is for **compile time**. At **runtime**, OSGi resolves from what's **available in AEM**.

```
Compile Time → pom.xml has commons-lang3:3.14.0 → Code compiles ✅
Runtime      → AEM has older version           → Bundle fails ❌
```

---

## 🧩 Sling Models

### Q6: What does `DefaultInjectionStrategy.OPTIONAL` mean?

Without it, if ANY property is missing, the **entire model fails**.

```java
// With OPTIONAL - Model works even if properties are empty
@Model(defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class CardModel { }
```

---

### Q7: Difference between `@Inject` vs `@ValueMapValue`?

| Annotation | Purpose |
|------------|---------|
| `@ValueMapValue` | Injects from JCR properties (specific) |
| `@SlingObject` | Injects Sling objects (Resource, ResourceResolver) |
| `@Inject` | Generic injection (less explicit) |

**Best Practice**: Use specific annotations for clarity.

---

## 📄 HTL (Sightly)

### Q8: What does `data-sly-use` do?

Initializes a Java class and makes it available:

```html
<sly data-sly-use.card="com.adobe.aem.portfolio.core.models.CardModel"/>
<h1>${card.title}</h1>
```

---

### Q9: `data-sly-test` vs `data-sly-unwrap`?

| Attribute | Purpose |
|-----------|---------|
| `data-sly-test` | Conditionally renders element |
| `data-sly-unwrap` | Removes wrapper tag, keeps children |

---

## 🏗️ Component Structure

### Q10: What files make up an AEM component?

```
component-name/
├── .content.xml          # Component definition
├── component-name.html   # HTL template
├── _cq_dialog/           # Author dialog
│   └── .content.xml
└── clientlib-*/          # Component CSS/JS (optional)
```

---

## 💡 Interview Tips

1. **Always check bundle status** after deployment
2. **Include clientlibs on page template** - they don't auto-load!
3. **Use specific injection annotations** in Sling Models
4. **Understand compile-time vs runtime** dependency resolution

---

## 🔄 Header Component 1 vs Header Component 2 - Approach Comparison

### Overview

| Aspect | HeaderComponent (V1) | HeaderComponent2 (V2) |
|--------|---------------------|----------------------|
| **Approach** | Custom-built from scratch | Overlays Core Navigation Component |
| **Navigation Source** | Multifield with manual nav items | Core Component's dynamic navigation |
| **Flexibility** | Full control | Constrained by Core Component |
| **Maintenance** | Higher effort | Lower (Adobe maintains core) |

---

### 🏗️ Architectural Difference

```
┌─────────────────────────────────────────────────────────────────┐
│                    HEADER COMPONENT 1 (Custom)                  │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │  HeaderComponent.html (Custom HTL)                        │  │
│  │  ├── Logo (hardcoded path or dialog)                      │  │
│  │  ├── Nav Links (multifield → List<HeaderSubNavModel>)     │  │
│  │  └── Actions (login/signup links)                         │  │
│  └───────────────────────────────────────────────────────────┘  │
│                            ↓                                    │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │  HeaderComponentModel.java                                │  │
│  │  - Manual @Inject for each property                       │  │
│  │  - @ChildResource for nested navigation                   │  │
│  └───────────────────────────────────────────────────────────┘  │
│                                                                 │
│  🔴 Everything is manually created and maintained               │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│              HEADER COMPONENT 2 (Core Navigation Overlay)       │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │  HeaderComponent2.html (Wrapper HTL)                      │  │
│  │  ├── Logo (from Model)                                    │  │
│  │  ├── ┌──────────────────────────────────────────────────┐ │  │
│  │  │   │  data-sly-resource: Core Navigation v2           │ │  │
│  │  │   │  (Adobe maintains this!)                         │ │  │
│  │  │   └──────────────────────────────────────────────────┘ │  │
│  │  └── Actions (CTAs from Model)                            │  │
│  └───────────────────────────────────────────────────────────┘  │
│                            ↓                                    │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │  sling:resourceSuperType = core/wcm/components/           │  │
│  │                            navigation/v2/navigation       │  │
│  └───────────────────────────────────────────────────────────┘  │
│                                                                 │
│  🟢 Navigation logic delegated to Adobe's Core Component        │
└─────────────────────────────────────────────────────────────────┘
```

---

### ✅ Benefits & ❌ Drawbacks

#### HeaderComponent 1 (Custom Approach)

| ✅ Benefits | ❌ Drawbacks |
|------------|-------------|
| Full control over markup & behavior | Must maintain navigation logic yourself |
| No dependency on Core Components version | More boilerplate code |
| Can implement unique navigation patterns | No automatic page structure sync |
| Smaller footprint (no core libs) | Manual accessibility handling |
| Custom hover/dropdown behaviors | More testing required |

#### HeaderComponent 2 (Core Navigation Overlay)

| ✅ Benefits | ❌ Drawbacks |
|------------|-------------|
| Adobe maintains navigation logic | Tied to Core Components version |
| Auto-syncs with page structure changes | Less control over nav HTML structure |
| Built-in accessibility (ARIA, keyboard nav) | Must style around Core's markup |
| Inherits Core's dialog for nav config | Heavier dependency chain |
| Battle-tested by Adobe | Customization requires CSS overrides |
| Follows industry best practices | Learning curve for overlay pattern |

---

### 🏆 Industry Standard Recommendation

```
┌───────────────────────────────────────────────────────────────┐
│  INDUSTRY BEST PRACTICE: Prefer Core Component Overlay (V2)  │
└───────────────────────────────────────────────────────────────┘

Why?
├── 1. Reduced maintenance burden
├── 2. Automatic security patches from Adobe
├── 3. Accessibility compliance out-of-box
├── 4. Consistent with AEM Sites best practices
└── 5. Easier onboarding for new developers
```

**When to use Custom (V1)?**
- Unique navigation patterns not supported by Core
- Strict performance requirements (minimal JS)
- Legacy projects without Core Components

---

### 📋 Extra Work Required for HeaderComponent2 vs HeaderComponent1

#### 1. **Styling the Core Navigation Markup**

HeaderComponent2 requires you to style around Core Component's HTML structure:

```scss
/* Must target Core Component's classes INSIDE your wrapper */
.cmp-header2__navigation {
    /* Core Navigation renders its own structure */
    .cmp-navigation { }                    /* Core's wrapper */
    .cmp-navigation__group { }             /* <ul> list */
    .cmp-navigation__item { }              /* <li> items */
    .cmp-navigation__item--level-0 { }     /* Top-level items */
    .cmp-navigation__item--level-1 { }     /* Dropdown items */
    .cmp-navigation__item-link { }         /* <a> links */
}
```

HeaderComponent1 has full control over classes since you write the HTML.

---

#### 2. **Dialog Inheritance**

HeaderComponent2 includes Core Navigation's dialog tab:

```xml
<!-- HeaderComponent2 dialog - includes Core Navigation properties -->
<navigation
    jcr:primaryType="nt:unstructured"
    jcr:title="Navigation"
    sling:resourceType="granite/ui/components/coral/foundation/include"
    path="core/wcm/components/navigation/v2/navigation/cq:dialog/content/items/tabs/items/properties"/>
```

This gives authors access to:
- Navigation Root
- Exclude Root
- Collect child pages
- Structure depth

---

#### 3. **JavaScript for Mobile Dropdowns**

Core Navigation doesn't handle mobile toggle automatically. HeaderComponent2 needs extra JS:

```javascript
// Must manually handle mobile dropdown toggle for Core nav items
const dropdownLinks = navigation.querySelectorAll(
    '.cmp-navigation__item--level-0 > .cmp-navigation__item-link'
);

dropdownLinks.forEach(link => {
    link.addEventListener('click', function(e) {
        // Toggle aria-expanded and max-height
    });
});
```

---

#### 4. **Dependency Management**

HeaderComponent2 requires Core Components to be installed in AEM:

```xml
<!-- pom.xml - must ensure this dependency exists -->
<dependency>
    <groupId>com.adobe.cq</groupId>
    <artifactId>core.wcm.components.core</artifactId>
    <version>2.24.0</version>
    <scope>provided</scope>
</dependency>
```

---

#### 5. **Version Compatibility**

When upgrading AEM or Core Components, HeaderComponent2 may need style adjustments if Adobe changes the Core Navigation's HTML structure.

```
AEM Version Upgrade Path:
├── HeaderComponent1: Only update if your code breaks
└── HeaderComponent2: Must verify Core Nav styles still work
```

---

### 🎯 Summary Decision Matrix

| Scenario | Recommended Approach |
|----------|---------------------|
| Enterprise with Core Components | **HeaderComponent2** (Overlay) |
| Need custom mega-menu | HeaderComponent1 (Custom) |
| Strict accessibility requirements | **HeaderComponent2** (Adobe handles a11y) |
| Performance-critical (minimal JS) | HeaderComponent1 (Custom) |
| Long-term maintainability | **HeaderComponent2** (Less code to maintain) |
| Unique brand/animation requirements | HeaderComponent1 (Full control) |

---

### 💡 Interview Talking Points

1. **Q: When would you overlay vs build custom?**
   > "I overlay Core Components when I want Adobe to handle complexity like accessibility and page structure sync. I build custom when I need unique patterns Core doesn't support."

2. **Q: What's the trade-off of using sling:resourceSuperType?**
   > "You get inheritance and Adobe's maintenance, but you're constrained by Core's HTML structure and must style around their classes."

3. **Q: How do you handle mobile navigation with Core Components?**
   > "Core Navigation doesn't auto-toggle on mobile, so I add JavaScript to handle aria-expanded states and max-height transitions for dropdowns."

---

## 📄 customheaderlibs.html - Page Script Loading Architecture

### Q11: What is `customheaderlibs.html` and why is it important?

`customheaderlibs.html` is a **page-level include file** that controls which CSS files load in the `<head>` section of all pages using that page component.

```
┌───────────────────────────────────────────────────────────────────────┐
│                    HOW PAGE RENDERING WORKS                           │
├───────────────────────────────────────────────────────────────────────┤
│                                                                       │
│   Browser requests: /content/portfolio/us/en/home.html                │
│                          ↓                                            │
│   AEM Page Component (portfolio/components/page)                      │
│                          ↓                                            │
│   ┌─────────────────────────────────────────────────────────────────┐ │
│   │  page.html (main page template)                                 │ │
│   │                                                                 │ │
│   │  <head>                                                         │ │
│   │      <sly data-sly-include="customheaderlibs.html"/>            │ │
│   │      <!-- CSS files are injected here! -->                      │ │
│   │  </head>                                                        │ │
│   │                                                                 │ │
│   │  <body>                                                         │ │
│   │      <!-- Page content -->                                      │ │
│   │      <sly data-sly-include="customfooterlibs.html"/>            │ │
│   │      <!-- JS files are injected here! -->                       │ │
│   │  </body>                                                        │ │
│   └─────────────────────────────────────────────────────────────────┘ │
│                                                                       │
└───────────────────────────────────────────────────────────────────────┘
```

**File Location**: `ui.apps/.../components/page/customheaderlibs.html`

**Purpose**:
- Load CSS clientlibs in `<head>` (blocking, ensures styles before content)
- Load critical JS that must run before page renders
- Include Context Hub for personalization

---

### Q12: What goes in customheaderlibs vs customfooterlibs?

| File | Content | Timing | Best For |
|------|---------|--------|----------|
| **customheaderlibs.html** | CSS + Critical JS | Blocking (before content) | Styles, fonts, critical scripts |
| **customfooterlibs.html** | JS | Non-blocking (after content) | Interactive features, analytics |

**Example customheaderlibs.html**:
```html
<sly data-sly-use.clientlib="core/wcm/components/commons/v1/templates/clientlib.html">
    <!-- Base styles for entire site -->
    <sly data-sly-call="${clientlib.css @ categories='portfolio.base'}"/>
    
    <!-- Header component styles -->
    <sly data-sly-call="${clientlib.css @ categories='portfolio.header'}"/>
    
    <!-- Other component styles -->
    <sly data-sly-call="${clientlib.css @ categories='portfolio.card'}"/>
</sly>
```

**Example customfooterlibs.html**:
```html
<sly data-sly-use.clientlib="core/wcm/components/commons/v1/templates/clientlib.html">
    <!-- Base JavaScript -->
    <sly data-sly-call="${clientlib.js @ categories='portfolio.base', async=true}"/>
    
    <!-- Header component JavaScript (theme toggle, mobile menu) -->
    <sly data-sly-call="${clientlib.js @ categories='portfolio.header', async=true}"/>
</sly>
```

---

### Q13: Why won't my component CSS/JS load even with a valid clientlib?

**Reason**: Creating a clientlib does NOT automatically load it on pages!

```
┌───────────────────────────────────────────────────────────────────────┐
│  ❌ COMMON MISTAKE                                                     │
├───────────────────────────────────────────────────────────────────────┤
│                                                                       │
│  ✅ Step 1: Create clientlib/                                         │
│     └── .content.xml → categories="[portfolio.header]"                │
│     └── css/header.css                                                │
│     └── js/header.js                                                  │
│                                                                       │
│  ❌ Step 2: FORGOT to add to customheaderlibs.html!                    │
│                                                                       │
│  Result: Component HTML renders, but UNSTYLED!                        │
│                                                                       │
└───────────────────────────────────────────────────────────────────────┘
```

**Solution**: Always add new clientlib categories to `customheaderlibs.html` (CSS) and `customfooterlibs.html` (JS).

---

## 🏷️ Clientlib Category Naming: What is `portfolio.header`?

### Q14: What does the category name `portfolio.header` mean?

A clientlib **category** is just a **unique identifier** - it's a name you choose to:
1. Group related CSS/JS files
2. Reference them in page includes

```xml
<!-- Defining the clientlib (in component folder) -->
<jcr:root jcr:primaryType="cq:ClientLibraryFolder"
    categories="[portfolio.header]"    <!-- This is the category name -->
    dependencies="[portfolio.base]"/>  <!-- Load base first -->
```

```html
<!-- Using the clientlib (in customheaderlibs.html) -->
<sly data-sly-call="${clientlib.css @ categories='portfolio.header'}"/>
```

**Naming Convention**: `[project].[component/feature]`

| Category Name | Purpose |
|---------------|---------|
| `portfolio.base` | Site-wide base styles & utilities |
| `portfolio.header` | Header component styles |
| `portfolio.card` | Card component styles |
| `portfolio.dependencies` | Third-party libraries (jQuery, etc) |

---

### Q15: How do clientlib dependencies work?

The `dependencies` property ensures correct loading ORDER:

```xml
<!-- Header depends on base -->
<jcr:root categories="[portfolio.header]"
          dependencies="[portfolio.base]"/>
```

**Result**: When `portfolio.header` loads, AEM first loads `portfolio.base`.

```
Loading Order:
1. portfolio.base.css     (dependency)
2. portfolio.header.css   (your styles)
```

---

## 🧱 Experience Fragments - Structure & Removal

### Q16: What is an Experience Fragment and why use it for headers?

An **Experience Fragment (XF)** is a reusable, authorable content block stored at `/content/experience-fragments/`.

```
┌───────────────────────────────────────────────────────────────────────┐
│                   WHY USE XF FOR HEADER/FOOTER?                       │
├───────────────────────────────────────────────────────────────────────┤
│                                                                       │
│  ❌ WITHOUT Experience Fragment:                                       │
│     - Header content DUPLICATED on every page template                │
│     - Change header → Edit every template                             │
│                                                                       │
│  ✅ WITH Experience Fragment:                                          │
│     - Header content stored in ONE place                              │
│     - All pages REFERENCE the same XF                                 │
│     - Change header → Update ONCE, affects all pages                  │
│                                                                       │
└───────────────────────────────────────────────────────────────────────┘
```

**XF Path**: `/content/experience-fragments/portfolio/us/en/site/header/master`

**Template Reference**:
```xml
<!-- In template structure -->
<experiencefragment-header
    jcr:primaryType="nt:unstructured"
    sling:resourceType="portfolio/components/experiencefragment"
    fragmentVariationPath="/content/experience-fragments/portfolio/us/en/site/header/master"/>
```

---

### Q17: How do I REMOVE the Experience Fragment from the page template?

If you want to **remove** the XF header (e.g., to use a different approach), you need to:

#### Method 1: Remove from Template Structure (Code)

**File**: `ui.content/.../templates/page-content/structure/.content.xml`

**Before** (with XF header):
```xml
<root jcr:primaryType="nt:unstructured" sling:resourceType="portfolio/components/container" layout="responsiveGrid">
    <!-- REMOVE THIS BLOCK -->
    <experiencefragment-header
        jcr:primaryType="nt:unstructured"
        sling:resourceType="portfolio/components/experiencefragment"
        fragmentVariationPath="/content/experience-fragments/portfolio/us/en/site/header/master"/>
    
    <container .../>
    
    <experiencefragment-footer .../>
</root>
```

**After** (XF header removed):
```xml
<root jcr:primaryType="nt:unstructured" sling:resourceType="portfolio/components/container" layout="responsiveGrid">
    <!-- XF header removed - can add custom header component here or leave empty -->
    
    <container .../>
    
    <experiencefragment-footer .../>
</root>
```

#### Method 2: Remove from AEM UI (Authoring)

1. Navigate to: **Tools → General → Templates**
2. Select your template (e.g., "Content Page")
3. Click **Edit** → **Structure** mode
4. Find the Experience Fragment (header) component
5. Select it → Click delete icon
6. **Save & Close**

⚠️ **Warning**: Removing from AEM UI only affects new pages. Existing pages may still reference the old structure.

---

### Q18: What happens if I delete the XF but pages still reference it?

If you delete the Experience Fragment content but templates still reference it:

```
┌───────────────────────────────────────────────────────────────────────┐
│  ❌ BROKEN REFERENCE                                                   │
├───────────────────────────────────────────────────────────────────────┤
│                                                                       │
│  Template references: /content/experience-fragments/.../header        │
│                                      ↓                                │
│  XF deleted or not found → Component renders as EMPTY                 │
│  (No error, just nothing appears)                                     │
│                                                                       │
└───────────────────────────────────────────────────────────────────────┘
```

**Best Practice**: 
1. Update template structure FIRST (remove XF reference)
2. Deploy changes
3. THEN delete the XF content if no longer needed

---

## 🔄 Complete Header Architecture Options

### Q19: What are my options for implementing a site header?

```
┌───────────────────────────────────────────────────────────────────────┐
│                    HEADER IMPLEMENTATION OPTIONS                       │
├───────────────────────────────────────────────────────────────────────┤
│                                                                       │
│  OPTION 1: Experience Fragment (Current - Most Flexible)              │
│  ─────────────────────────────────────────────────────────────────── │
│  Template → includes XF component → XF has header component inside    │
│  ✅ Authors can edit header without template changes                   │
│  ✅ Same header shared across all pages                                │
│  ❌ Extra layer of indirection                                         │
│                                                                       │
│  OPTION 2: Direct in Template Structure (Simpler)                     │
│  ─────────────────────────────────────────────────────────────────── │
│  Template → header component directly in structure                    │
│  ✅ Simpler architecture                                               │
│  ❌ Authors cannot edit header (locked in structure)                   │
│  ❌ Must deploy code to change header                                  │
│                                                                       │
│  OPTION 3: Page Properties (Centralized)                              │
│  ─────────────────────────────────────────────────────────────────── │
│  Header reads from site root page properties                          │
│  ✅ Single source of truth                                             │
│  ✅ Navigation auto-populates from page tree                           │
│  ❌ More complex implementation                                        │
│                                                                       │
└───────────────────────────────────────────────────────────────────────┘
```

---

### 💡 Interview Talking Points - Page Structure

1. **Q: Why do we use customheaderlibs.html?**
   > "It's the central place to register CSS libraries that should load in the `<head>`. Without it, component styles won't apply even if the clientlib exists."

2. **Q: When would you use an Experience Fragment vs direct template inclusion?**
   > "XF for author-editable shared content like headers/footers. Direct inclusion for locked structure where authors shouldn't change the configuration."

3. **Q: How do you debug missing CSS in AEM?**
   > "I check: 1) Clientlib exists in dumplibs, 2) Category is included in customheaderlibs.html, 3) Page source has the `<link>` tag, 4) Browser network tab shows CSS loaded."
