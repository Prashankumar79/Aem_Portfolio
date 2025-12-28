/**
 * Header Component 2 - WKND Style JavaScript
 * Handles mobile menu toggle
 */
(function () {
    'use strict';

    function initHeader2() {
        const toggles = document.querySelectorAll('.cmp-header2__menu-toggle');

        toggles.forEach(function (toggle) {
            toggle.addEventListener('click', function () {
                const isExpanded = this.getAttribute('aria-expanded') === 'true';
                this.setAttribute('aria-expanded', !isExpanded);
                this.setAttribute('aria-label', isExpanded ? 'Open navigation' : 'Close navigation');
            });
        });

        // Close menu on escape key
        document.addEventListener('keydown', function (e) {
            if (e.key === 'Escape') {
                toggles.forEach(function (toggle) {
                    toggle.setAttribute('aria-expanded', 'false');
                    toggle.setAttribute('aria-label', 'Open navigation');
                });
            }
        });
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', initHeader2);
    } else {
        initHeader2();
    }
})();
