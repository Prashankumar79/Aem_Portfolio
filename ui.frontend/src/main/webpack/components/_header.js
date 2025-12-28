(function() {
  'use strict';

  const SELECTORS = {
    header: '[data-cmp-is="header"]',
    toggle: '.cmp-header__toggle',
    navigation: '.cmp-header__navigation',
    dropdownLink: '.cmp-header__nav-link--dropdown',
    searchToggle: '.cmp-header__search-toggle',
    searchPanel: '.cmp-header__search-panel',
    searchClose: '.cmp-header__search-close',
    searchInput: '.cmp-header__search-input'
  };

  class Header {
    constructor(element) {
      this.element = element;
      this.toggle = element.querySelector(SELECTORS.toggle);
      this.navigation = element.querySelector(SELECTORS.navigation);
      this.dropdownLinks = element.querySelectorAll(SELECTORS.dropdownLink);
      this.searchToggle = element.querySelector(SELECTORS.searchToggle);
      this.searchPanel = element.querySelector(SELECTORS.searchPanel);
      this.searchClose = element.querySelector(SELECTORS.searchClose);
      this.searchInput = element.querySelector(SELECTORS.searchInput);
      
      this.init();
    }

    init() {
      this.bindEvents();
      this.handleResize();
    }

    bindEvents() {
      // Mobile menu toggle
      if (this.toggle && this.navigation) {
        this.toggle.addEventListener('click', () => this.toggleMenu());
      }

      // Dropdown menus (mobile)
      this.dropdownLinks.forEach(link => {
        link.addEventListener('click', (e) => {
          if (window.innerWidth <= 1024) {
            e.preventDefault();
            this.toggleDropdown(link);
          }
        });
      });

      // Search functionality
      if (this.searchToggle && this.searchPanel) {
        this.searchToggle.addEventListener('click', () => this.toggleSearch());
      }

      if (this.searchClose) {
        this.searchClose.addEventListener('click', () => this.closeSearch());
      }

      // Close menu on outside click
      document.addEventListener('click', (e) => {
        if (!this.element.contains(e.target)) {
          this.closeMenu();
          this.closeSearch();
        }
      });

      // Handle escape key
      document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape') {
          this.closeMenu();
          this.closeSearch();
        }
      });

      // Handle window resize
      window.addEventListener('resize', () => this.handleResize());

      // Prevent body scroll when mobile menu is open
      const observer = new MutationObserver(() => {
        if (this.toggle && this.toggle.getAttribute('aria-expanded') === 'true') {
          document.body.style.overflow = 'hidden';
        } else {
          document.body.style.overflow = '';
        }
      });

      if (this.toggle) {
        observer.observe(this.toggle, { attributes: true, attributeFilter: ['aria-expanded'] });
      }
    }

    toggleMenu() {
      const isExpanded = this.toggle.getAttribute('aria-expanded') === 'true';
      this.toggle.setAttribute('aria-expanded', !isExpanded);
      
      // Close all dropdowns when closing menu
      if (isExpanded) {
        this.closeAllDropdowns();
      }
    }

    closeMenu() {
      if (this.toggle) {
        this.toggle.setAttribute('aria-expanded', 'false');
        this.closeAllDropdowns();
      }
    }

    toggleDropdown(link) {
      const isExpanded = link.getAttribute('aria-expanded') === 'true';
      
      // Close other dropdowns
      this.dropdownLinks.forEach(dl => {
        if (dl !== link) {
          dl.setAttribute('aria-expanded', 'false');
        }
      });

      // Toggle current dropdown
      link.setAttribute('aria-expanded', !isExpanded);
    }

    closeAllDropdowns() {
      this.dropdownLinks.forEach(link => {
        link.setAttribute('aria-expanded', 'false');
      });
    }

    toggleSearch() {
      const isExpanded = this.searchToggle.getAttribute('aria-expanded') === 'true';
      
      if (isExpanded) {
        this.closeSearch();
      } else {
        this.openSearch();
      }
    }

    openSearch() {
      this.searchToggle.setAttribute('aria-expanded', 'true');
      this.searchPanel.removeAttribute('hidden');
      
      // Focus on search input
      setTimeout(() => {
        if (this.searchInput) {
          this.searchInput.focus();
        }
      }, 100);

      // Close mobile menu if open
      this.closeMenu();
    }

    closeSearch() {
      if (this.searchToggle && this.searchPanel) {
        this.searchToggle.setAttribute('aria-expanded', 'false');
        this.searchPanel.setAttribute('hidden', '');
      }
    }

    handleResize() {
      // Reset mobile menu on desktop
      if (window.innerWidth > 1024) {
        this.closeMenu();
        this.closeAllDropdowns();
        document.body.style.overflow = '';
      }
    }
  }

  // Initialize all header components
  function init() {
    const headers = document.querySelectorAll(SELECTORS.header);
    headers.forEach(header => {
      new Header(header);
    });
  }

  // Initialize on DOM ready
  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', init);
  } else {
    init();
  }

  // Re-initialize on dynamic content load (for AEM editor)
  if (window.Granite && window.Granite.author) {
    document.addEventListener('cq-overlays-repositioned', init);
  }
})();