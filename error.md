# AEM Bundle Errors & Solutions

This document captures common errors encountered during AEM development and their solutions.

---

## 1. OSGi Bundle Not Starting - Unresolved Package Dependency

### 🔴 Error

```
org.apache.sling.scripting.sightly.SightlyException: Compilation errors in 
org/apache/sling/scripting/sightly/apps/portfolio/components/Card/Card__002e__html.java:
Line 39, column 1729 : com.adobe.aem.portfolio.core.models.CardModel cannot be resolved to a type
```

**Bundle Status**: `Installed` (instead of `Active`)

**OSGi Console Message**:
```
org.apache.commons.lang3,version=[3.19.4,) -- Cannot be resolved
```

---

### 🤔 Why This Error Occurred

1. **Root Cause**: The `CardModel.java` Sling Model was using `StringUtils.isNotBlank()` from the `org.apache.commons.lang3` library.

2. **Dependency Mismatch**: The bundle was compiled against `commons-lang3` version 3.19.4, but the AEM instance had an older/different version installed.

3. **OSGi Resolution Failure**: When the bundle tried to start, OSGi couldn't find a compatible `org.apache.commons.lang3` package, so the bundle stayed in `Installed` state instead of becoming `Active`.

4. **HTL Compilation Error**: Since the bundle wasn't active, the `CardModel` class wasn't available, causing HTL to fail with "cannot be resolved to a type".

**Visual Flow**:
```
┌─────────────────────────────────────────────────────────────────────┐
│  CardModel.java uses StringUtils.isNotBlank()                       │
│              ↓                                                      │
│  Bundle imports org.apache.commons.lang3                            │
│              ↓                                                      │
│  OSGi tries to resolve dependency at runtime                        │
│              ↓                                                      │
│  ❌ Required version (3.19.4+) NOT available in AEM                 │
│              ↓                                                      │
│  Bundle stays in "Installed" state (not Active)                     │
│              ↓                                                      │
│  CardModel class NOT exported/available                             │
│              ↓                                                      │
│  HTL fails: "CardModel cannot be resolved to a type"               │
└─────────────────────────────────────────────────────────────────────┘
```

---

### ✅ Solution

**Replace external dependency with standard Java**

Instead of using `StringUtils.isNotBlank()`, create a local helper method:

```java
// ❌ BEFORE - External dependency
import org.apache.commons.lang3.StringUtils;

public boolean hasImage() {
    return StringUtils.isNotBlank(image);
}
```

```java
// ✅ AFTER - No external dependency
private boolean isNotBlank(String str) {
    return str != null && !str.trim().isEmpty();
}

public boolean hasImage() {
    return isNotBlank(image);
}
```

---

### 🔍 How to Debug This in the Future

1. **Check Bundle Status**:
   - Go to: `http://localhost:4502/system/console/bundles`
   - Search for your bundle name
   - Check if status is `Active` or `Installed`

2. **View Bundle Details**:
   - Click on your bundle
   - Look at **Imported Packages** section
   - Find any packages marked with `-- Cannot be resolved`

3. **Check Available Packages**:
   - Go to: `http://localhost:4502/system/console/depfinder`
   - Search for the package name (e.g., `org.apache.commons.lang3`)
   - See what versions are available

---

### 📚 Prevention Tips

| Tip | Description |
|-----|-------------|
| **Prefer AEM SDK APIs** | Use classes from `com.adobe.aem:aem-sdk-api` when possible |
| **Check Version Compatibility** | Verify third-party library versions match AEM's bundled versions |
| **Use Standard Java** | For simple utilities like string checks, use plain Java |
| **Embed Dependencies** | If you must use external libs, embed them in your bundle |
| **Test Bundle Start** | Always verify bundle is `Active` after deployment |

---

### 🔗 Related Resources

- [AEM OSGi Bundle Development](https://experienceleague.adobe.com/docs/experience-manager-65/developing/extending-aem/osgi.html)
- [Apache Sling Models](https://sling.apache.org/documentation/bundles/models.html)
- [Felix OSGi Console](http://localhost:4502/system/console/bundles)

---

## 2. Clientlib CSS Not Loading on Page

### 🔴 Error

CSS styles from a component's clientlib are not being applied on the page. The component HTML renders correctly, but without any styling.

**Symptoms**:
- Component appears unstyled
- CSS file exists in clientlib but not loaded in browser
- No CSS `<link>` tag in page source for your clientlib

---

### 🤔 Why This Error Occurred

There are typically **3 common causes**:

#### Cause 1: Clientlib Not Added to Page Header
The clientlib category is not included in `customheaderlibs.html` or `customfooterlibs.html`.

#### Cause 2: Wrong `css.txt` Location
The `css.txt` file must be at the **clientlib root folder**, not inside the `css/` subfolder.

#### Cause 3: Wrong Base Path in `css.txt`
The `#base=` directive points to wrong directory.

**Visual Flow**:
```
┌─────────────────────────────────────────────────────────────────────┐
│  Component clientlib has category "My-Component-Clientlib"          │
│              ↓                                                      │
│  customheaderlibs.html does NOT include this category               │
│              ↓                                                      │
│  AEM page renders WITHOUT <link> tag for this clientlib             │
│              ↓                                                      │
│  CSS file never loaded by browser                                   │
│              ↓                                                      │
│  ❌ Component appears WITHOUT any styles                            │
└─────────────────────────────────────────────────────────────────────┘
```

---

### ✅ Solution

#### Fix 1: Add Clientlib to Page Header

Edit `ui.apps/.../components/page/customheaderlibs.html`:

```html
<!-- ❌ BEFORE - Only base clientlib included -->
<sly data-sly-use.clientlib="core/wcm/components/commons/v1/templates/clientlib.html">
    <sly data-sly-call="${clientlib.css @ categories='portfolio.base'}"/>
</sly>
```

```html
<!-- ✅ AFTER - Component clientlib also included -->
<sly data-sly-use.clientlib="core/wcm/components/commons/v1/templates/clientlib.html">
    <sly data-sly-call="${clientlib.css @ categories='portfolio.base'}"/>
    <sly data-sly-call="${clientlib.css @ categories='Header-Componet-Clientlib'}"/>
</sly>
```

#### Fix 2: Correct Clientlib Structure

Ensure `css.txt` is at the clientlib root, NOT inside css folder:

```
❌ WRONG Structure:
clientlib/
├── .content.xml
└── css/
    ├── css.txt          <-- WRONG location!
    └── MyComponent.css

✅ CORRECT Structure:
clientlib/
├── .content.xml
├── css.txt              <-- CORRECT location!
└── css/
    └── MyComponent.css
```

#### Fix 3: Correct `css.txt` Content

```txt
#base=css
MyComponent.css
```

- `#base=css` means "look in the css subfolder"
- Files listed without paths are relative to #base

---

### 🔍 How to Debug This in the Future

1. **Check Page Source**:
   - View page source in browser
   - Search for your clientlib category name
   - Look for `<link>` tag with your CSS

2. **Verify Clientlib Exists**:
   - Go to: `http://localhost:4502/libs/granite/ui/content/dumplibs.html`
   - Search for your clientlib category
   - Should show your CSS files listed

3. **Test Clientlib Directly**:
   - Visit: `http://localhost:4502/etc.clientlibs/apps/portfolio/components/AEMComponent/HeaderComponent/clientlib.css`
   - If 404 → clientlib structure is wrong
   - If empty → css.txt is wrong

4. **Check Browser Network Tab**:
   - Open DevTools → Network
   - Filter by CSS
   - Look for your clientlib request

---

### 📚 Prevention Tips

| Tip | Description |
|-----|-------------|
| **Always include in page** | Add new clientlibs to `customheaderlibs.html` |
| **Use correct structure** | `css.txt` at clientlib root, CSS files in `css/` subfolder |
| **Test after deploy** | Check browser DevTools to confirm CSS is loaded |
| **Check file paths** | Ensure `#base=` matches your folder structure |
| **Use consistent naming** | Match category name in `.content.xml` exactly |

---

### 🔗 Related Resources

- [AEM Clientlibs Documentation](https://experienceleague.adobe.com/docs/experience-manager-65/developing/introduction/clientlibs.html)
- [Clientlib Debug Tool](http://localhost:4502/libs/granite/ui/content/dumplibs.html)
- [HTL Clientlib Include](https://github.com/adobe/htl-spec/blob/master/SPECIFICATION.md)

---

## 3. FileVault Package Validation Error - Invalid XML Format

### 🔴 Error

```
[ERROR] validate-package) on project portfolio.ui.apps: Found 1 violation(s) (with severity=ERROR)
[ERROR] jackrabbit-docviewparser: "[lL]" is not allowed.
filePath=jcr_root\apps\portfolio\components\navigation\.content.xml
```

**Build fails with**: `mvn clean install -PautoInstallPackage`

**Module**: `ui.apps` (FileVault package validator)

---

### 🤔 Why This Error Occurred

1. **Root Cause**: The `.content.xml` file had **empty lines before the XML declaration** (`<?xml ...?>`).

2. **XML Standard Violation**: The XML 1.0 specification requires the XML declaration to be the **very first characters** of the file. No whitespace, blank lines, or BOM characters are allowed before it.

3. **FileVault Validator**: Apache Jackrabbit FileVault (used by Maven to create AEM packages) has strict validators. The `jackrabbit-docviewparser` detected the invalid XML structure.

4. **Cryptic Error Message**: The error `"[lL]" is not allowed"` refers to the parser expecting specific characters but finding line breaks/whitespace instead.

**The Problematic File**:
```xml


    
<?xml version="1.0" encoding="UTF-8"?>    <!-- ❌ WRONG! XML declaration NOT at line 1 -->
<jcr:root .../>
```

**Visual Flow**:
```
┌─────────────────────────────────────────────────────────────────────┐
│  File starts with BLANK LINES (lines 1-3 are empty/whitespace)      │
│              ↓                                                      │
│  XML declaration <?xml...?> appears at line 4 instead of line 1     │
│              ↓                                                      │
│  FileVault jackrabbit-docviewparser reads the file                  │
│              ↓                                                      │
│  Parser expects XML header immediately, finds whitespace            │
│              ↓                                                      │
│  ❌ Validation FAILS: "[lL]" is not allowed                         │
│              ↓                                                      │
│  Maven build FAILS in ui.apps module                                │
└─────────────────────────────────────────────────────────────────────┘
```

---

### ✅ Solution

**Remove all content before the XML declaration**

```xml
<!-- ❌ BEFORE - Invalid XML (empty lines before declaration) -->


    
<?xml version="1.0" encoding="UTF-8"?>
<jcr:root .../>
```

```xml
<!-- ✅ AFTER - Valid XML (declaration at line 1, column 1) -->
<?xml version="1.0" encoding="UTF-8"?>
<jcr:root .../>
```

**The fixed file must start with `<?xml` as the very first characters.**

---

### 🔍 How I Debugged This (Troubleshooting Methodology)

Here's the **systematic debugging approach** I used:

#### Step 1: Identify the Failing Module

The error message showed:
```
[ERROR] mvn <args> -rf :portfolio.ui.apps
```

This tells us: **ui.apps module failed**. The `-rf` (resume from) suggestion confirms which module.

#### Step 2: Check if Other Modules Build

I ran a **targeted build** on just the `core` module first:
```bash
mvn clean install -pl core
```

✅ **Result**: Core built successfully → The Java code is fine.

#### Step 3: Capture Full Error Details

The terminal output was truncated. I used these techniques:

**Technique A**: Save output to file
```powershell
mvn clean install 2>&1 | Tee-Object -FilePath "build_output.txt"
```

**Technique B**: Search for ERROR patterns
```powershell
Get-Content build_output.txt | Select-String -Pattern "ERROR" -Context 2,5
```

**Technique C**: Search for specific keywords
```powershell
Get-Content build_output.txt | Select-String -Pattern "validator|Violation" -Context 2,3
```

#### Step 4: Analyze the Error

The detailed error mentioned:
- `packageTypeValidator` → FileVault package validation
- `jackrabbit-docviewparser` → XML parsing for JCR content
- `filePath=.../navigation/.content.xml` → THE EXACT FILE causing the issue

#### Step 5: Inspect the Problematic File

I opened `navigation/.content.xml` and found:
- **Lines 1-3**: Empty or whitespace
- **Line 4**: `<?xml version="1.0" ...>`

**The bug**: XML declaration wasn't at line 1!

#### Step 6: Apply the Fix

Rewrote the file with the XML declaration at the very beginning (no preceding content).

#### Step 7: Verify the Fix

```bash
mvn clean install -PautoInstallPackage
```

✅ **Build succeeded!**

---

### 🔍 Debugging Commands Cheat Sheet

| Purpose | Command |
|---------|---------|
| **Build single module** | `mvn clean install -pl core` |
| **Build with dependencies** | `mvn clean install -pl ui.apps -am` |
| **Resume failed build** | `mvn clean install -rf :portfolio.ui.apps` |
| **Save output to file** | `mvn clean install 2>&1 | Tee-Object -FilePath "log.txt"` |
| **Search for errors** | `Select-String -Pattern "ERROR" -Path log.txt` |
| **Verbose error stack** | `mvn clean install -e` |
| **Full debug output** | `mvn clean install -X` |
| **Just validate** | `mvn validate -pl ui.apps` |

---

### 📚 Prevention Tips

| Tip | Description |
|-----|-------------|
| **Check new XML files** | Ensure XML declaration is at line 1, column 1 |
| **No BOM characters** | Save files as UTF-8 **without BOM** |
| **Use proper IDE settings** | Configure IDE to not add leading whitespace |
| **Validate before commit** | Run `mvn validate` to catch issues early |
| **Copy carefully** | When copying XML snippets, remove any leading whitespace |

---

### 🔗 Related Resources

- [Apache Jackrabbit FileVault](https://jackrabbit.apache.org/filevault/)
- [XML 1.0 Specification - Prolog](https://www.w3.org/TR/xml/#sec-prolog-dtd)
- [AEM Package Validation](https://experienceleague.adobe.com/docs/experience-manager-cloud-service/content/implementing/using-cloud-manager/content-audit.html)

---

## 4. General Troubleshooting Methodology for AEM Builds

### The Systematic Approach

When a Maven build fails, follow this **5-step methodology**:

```
        ┌─────────────────────────────────────────────┐
Step 1: │ IDENTIFY which module failed               │
        │ Look for: -rf :module.name                  │
        └──────────────────┬──────────────────────────┘
                           ↓
        ┌─────────────────────────────────────────────┐
Step 2: │ ISOLATE by building modules separately      │
        │ Run: mvn install -pl core                   │
        │ Run: mvn install -pl ui.apps                │
        └──────────────────┬──────────────────────────┘
                           ↓
        ┌─────────────────────────────────────────────┐
Step 3: │ CAPTURE full error output                   │
        │ Save to file, search for ERROR/WARNING      │
        └──────────────────┬──────────────────────────┘
                           ↓
        ┌─────────────────────────────────────────────┐
Step 4: │ ANALYZE the error message                   │
        │ - Which plugin failed?                      │
        │ - Which file is mentioned?                  │
        │ - What does the error say?                  │
        └──────────────────┬──────────────────────────┘
                           ↓
        ┌─────────────────────────────────────────────┐
Step 5: │ FIX the root cause                         │
        │ Apply minimal, targeted fix                 │
        │ Then verify with full build                 │
        └─────────────────────────────────────────────┘
```

### Common Error Categories

| Module | Common Issues |
|--------|---------------|
| **core** | Java compilation errors, OSGi imports, Sling Model annotations |
| **ui.apps** | XML format issues, clientlib structure, filter.xml paths |
| **ui.frontend** | npm/node version, webpack build, SCSS syntax |
| **ui.content** | Author content structure, references to missing components |
| **all** | Dependency conflicts, embedding issues |

### Quick Debug Commands

```bash
# Check what Maven will do (dry run concept)
mvn validate

# See dependency tree
mvn dependency:tree -pl core

# Check which packages are exported/imported
# (After bundle is deployed)
# Visit: http://localhost:4502/system/console/bundles

# Rebuild only frontend
mvn clean install -pl ui.frontend

# Skip tests for faster iteration
mvn clean install -DskipTests

# Install to AEM with verbose output
mvn clean install -PautoInstallPackage -e
```

---

## 5. Maven Clean Build Fails but Install Succeeds

### 🔴 Error

```
[ERROR] Failed to execute goal org.apache.maven.plugins:maven-clean-plugin:3.0.0:clean 
       on project portfolio.ui.apps: Failed to clean
```

**When it happens**: Running `mvn clean install` fails, but `mvn install` works.

---

### 🤔 Why This Error Occurred

**File locks** prevent Maven's clean phase from deleting `target` directories.

Common causes:
1. **IDE indexing** - VS Code, IntelliJ scanning `target` files
2. **Antivirus** - Security software scanning class files
3. **Running Java process** - Previous build still holding file handles
4. **Windows file locking** - Windows locks files more aggressively than Mac/Linux

```
┌─────────────────────────────────────────────────────────────────────┐
│  mvn clean install                                                  │
│              ↓                                                      │
│  Maven tries to delete ui.apps/target/                              │
│              ↓                                                      │
│  File is LOCKED by IDE/antivirus/process                            │
│              ↓                                                      │
│  ❌ CleanPlugin FAILS: "Failed to delete file"                      │
│              ↓                                                      │
│  Entire build ABORTS                                                │
└─────────────────────────────────────────────────────────────────────┘
```

---

### ✅ Solution

**Option 1**: Skip the clean phase

```bash
# Just run install without clean
mvn install -PautoInstallPackage
```

**Option 2**: Manually delete then build

```powershell
# Delete target folders first
Remove-Item -Recurse -Force ui.apps\target -ErrorAction SilentlyContinue
Remove-Item -Recurse -Force ui.frontend\target -ErrorAction SilentlyContinue

# Then build
mvn clean install -PautoInstallPackage
```

**Option 3**: Close IDE/restart machine

If files are repeatedly locked, close your IDE and try again.

---

## 6. Legacy Package Type Validation Errors

### 🔴 Error

```
[WARNING] ValidationViolation: "jackrabbit-packagetype: Package of type 'MIXED' is legacy. 
         Use one of the other types instead!", filePath=META-INF\vault\properties.xml

[WARNING] ValidationViolation: "jackrabbit-filter: Found orphaned filter entries: 
         entry with root '/apps', entry with root '/apps/portfolio', ..."
```

**Module**: `ui.apps.structure`

---

### 🤔 Why This Error Occurred

The AEM project was generated with an older archetype that uses **legacy configurations**:

1. **MIXED package type**: Old projects used "MIXED" for packages containing both code (`/apps`) and content (`/content`). Adobe now recommends separating these.

2. **Orphaned filter entries**: The `filter.xml` references paths that either:
   - Don't exist in the repository
   - Aren't covered by module dependencies
   - Are leftover from project generation

```
┌─────────────────────────────────────────────────────────────────────┐
│  ui.apps.structure/META-INF/vault/filter.xml contains:              │
│  - /apps/sling                                                      │
│  - /apps/cq                                                         │
│  - /apps/dam                                                        │
│  - /content/dam/portfolio                                           │
│              ↓                                                      │
│  FileVault validator checks: "Do these paths exist?"                │
│              ↓                                                      │
│  ❌ Paths don't exist OR not declared as dependencies               │
│              ↓                                                      │
│  WARNING/ERROR: "Orphaned filter entries"                           │
└─────────────────────────────────────────────────────────────────────┘
```

---

### ✅ Solution

**Quick Workaround**: Use `mvn install` (without clean) - this often bypasses strict validation.

**Proper Fix** (if you want to resolve permanently):

#### 1. Update Package Type

Edit `ui.apps.structure/pom.xml`:
```xml
<properties>
    <vault.properties.packageType>application</vault.properties.packageType>
</properties>
```

#### 2. Clean up filter.xml

Edit `ui.apps.structure/src/main/content/META-INF/vault/filter.xml`:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<workspaceFilter version="1.0">
    <!-- Only include paths you actually deploy -->
    <filter root="/apps/portfolio"/>
</workspaceFilter>
```

#### 3. Or Suppress Validation Warnings

In `ui.apps/pom.xml`, add to filevault-package-maven-plugin configuration:
```xml
<configuration>
    <validatorsSettings>
        <jackrabbit-packagetype>
            <options>
                <severity>warn</severity>
            </options>
        </jackrabbit-packagetype>
    </validatorsSettings>
</configuration>
```

---

### 🔍 Why `mvn install` Works But `mvn clean install` Fails

| Command | Behavior |
|---------|----------|
| `mvn clean install` | Deletes target → Rebuilds everything → Full validation |
| `mvn install` | Reuses existing artifacts → Skips some validation steps |

When validation errors exist, `mvn install` may succeed because:
- It reuses previously validated packages
- Some validators only run during full rebuilds
- Incremental builds skip certain checks

---

### 📚 Prevention Tips

| Tip | Description |
|-----|-------------|
| **Use latest archetype** | New archetypes generate proper package types |
| **Clean filter.xml** | Only include paths you actually deploy |
| **Separate content/code** | Use `ui.apps` for code, `ui.content` for content |
| **Update dependencies** | Ensure all referenced paths are properly declared |

---

### 🔗 Related Resources

- [FileVault Package Types](https://jackrabbit.apache.org/filevault/packagetypes.html)
- [AEM Project Structure](https://experienceleague.adobe.com/docs/experience-manager-cloud-service/content/implementing/developing/aem-project-content-package-structure.html)
- [Maven Build Best Practices](https://experienceleague.adobe.com/docs/experience-manager-cloud-service/content/implementing/developing/development-guidelines.html)
