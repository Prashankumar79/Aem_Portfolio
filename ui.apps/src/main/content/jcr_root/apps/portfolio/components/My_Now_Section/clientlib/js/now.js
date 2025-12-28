/**
 * Now Section - Experience Accordion
 * 
 * Handles click-to-expand behavior for experience cards
 */

(function () {
    'use strict';

    function initExperienceAccordion() {
        const accordionItems = document.querySelectorAll('[data-accordion-item]');

        accordionItems.forEach(function (item) {
            const trigger = item.querySelector('[data-accordion-trigger]');
            const content = item.querySelector('[data-accordion-content]');

            if (trigger && content) {
                trigger.addEventListener('click', function () {
                    const isExpanded = trigger.getAttribute('aria-expanded') === 'true';

                    // Toggle current item
                    trigger.setAttribute('aria-expanded', !isExpanded);

                    if (isExpanded) {
                        content.setAttribute('hidden', '');
                    } else {
                        content.removeAttribute('hidden');
                    }
                });
            }
        });
    }

    // Initialize on DOM ready
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', initExperienceAccordion);
    } else {
        initExperienceAccordion();
    }

    // Re-initialize on author mode changes (for AEM editor)
    if (typeof Granite !== 'undefined' && Granite.author) {
        Granite.author.on('cq-editor-loaded', initExperienceAccordion);
    }
})();
