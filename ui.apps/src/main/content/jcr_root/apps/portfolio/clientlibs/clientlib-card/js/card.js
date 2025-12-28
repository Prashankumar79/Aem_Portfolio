/**
 * Card Component - Interactive JavaScript
 * 
 * Features:
 * - 3D tilt effect on hover
 * - Mouse-following glow
 * - Intersection Observer for scroll animations
 * - Touch support for mobile
 */
(function() {
    'use strict';

    /**
     * Card Component Class
     * Handles all interactive behaviors for the card component
     */
    class CardComponent {
        constructor(element) {
            this.element = element;
            this.glow = element.querySelector('.cmp-card__glow');
            this.shine = element.querySelector('.cmp-card__shine');
            this.isTouch = 'ontouchstart' in window;
            this.animationFrame = null;
            
            this.init();
        }
        
        /**
         * Initialize the card component
         */
        init() {
            if (!this.isTouch) {
                this.bindMouseEvents();
            }
            this.bindScrollAnimation();
        }
        
        /**
         * Bind mouse events for desktop interactions
         */
        bindMouseEvents() {
            // 3D Tilt effect
            this.element.addEventListener('mousemove', this.handleMouseMove.bind(this));
            this.element.addEventListener('mouseleave', this.handleMouseLeave.bind(this));
            this.element.addEventListener('mouseenter', this.handleMouseEnter.bind(this));
        }
        
        /**
         * Handle mouse movement for 3D tilt and glow following
         * @param {MouseEvent} e - The mouse event
         */
        handleMouseMove(e) {
            if (this.animationFrame) {
                cancelAnimationFrame(this.animationFrame);
            }
            
            this.animationFrame = requestAnimationFrame(() => {
                const rect = this.element.getBoundingClientRect();
                const x = e.clientX - rect.left;
                const y = e.clientY - rect.top;
                
                const centerX = rect.width / 2;
                const centerY = rect.height / 2;
                
                // Calculate rotation (max 8 degrees)
                const rotateX = ((y - centerY) / centerY) * -8;
                const rotateY = ((x - centerX) / centerX) * 8;
                
                // Apply 3D transform
                this.element.style.transform = `
                    perspective(1000px)
                    translateY(-8px)
                    scale(1.02)
                    rotateX(${rotateX}deg)
                    rotateY(${rotateY}deg)
                `;
                
                // Move glow to follow mouse
                if (this.glow) {
                    const glowX = (x / rect.width) * 100;
                    const glowY = (y / rect.height) * 100;
                    this.glow.style.background = `
                        radial-gradient(
                            circle at ${glowX}% ${glowY}%,
                            rgba(99, 102, 241, 0.25) 0%,
                            transparent 50%
                        )
                    `;
                }
            });
        }
        
        /**
         * Handle mouse entering the card
         */
        handleMouseEnter() {
            this.element.style.transition = 'none';
        }
        
        /**
         * Handle mouse leaving the card
         */
        handleMouseLeave() {
            if (this.animationFrame) {
                cancelAnimationFrame(this.animationFrame);
            }
            
            this.element.style.transition = 'all 0.4s cubic-bezier(0.4, 0, 0.2, 1)';
            this.element.style.transform = 'translateY(0) scale(1) rotateX(0) rotateY(0)';
            
            if (this.glow) {
                this.glow.style.background = `
                    radial-gradient(
                        circle at center,
                        rgba(99, 102, 241, 0.15) 0%,
                        transparent 50%
                    )
                `;
            }
        }
        
        /**
         * Setup scroll-triggered animations using Intersection Observer
         */
        bindScrollAnimation() {
            // Check if the card has animation class
            const hasAnimation = 
                this.element.classList.contains('cmp-card--animate-fade') ||
                this.element.classList.contains('cmp-card--animate-slide') ||
                this.element.classList.contains('cmp-card--animate-zoom');
            
            if (!hasAnimation) return;
            
            // Pause animation initially
            this.element.style.animationPlayState = 'paused';
            
            // Create observer
            const observer = new IntersectionObserver((entries) => {
                entries.forEach(entry => {
                    if (entry.isIntersecting) {
                        // Play animation when visible
                        entry.target.style.animationPlayState = 'running';
                        observer.unobserve(entry.target);
                    }
                });
            }, {
                threshold: 0.2,
                rootMargin: '0px 0px -50px 0px'
            });
            
            observer.observe(this.element);
        }
        
        /**
         * Cleanup method to remove event listeners
         */
        destroy() {
            if (this.animationFrame) {
                cancelAnimationFrame(this.animationFrame);
            }
        }
    }

    /**
     * Initialize all card components on the page
     */
    function initCards() {
        const cards = document.querySelectorAll('[data-cmp-is="card"]');
        
        cards.forEach(card => {
            // Check if already initialized
            if (!card.dataset.cardInitialized) {
                new CardComponent(card);
                card.dataset.cardInitialized = 'true';
            }
        });
    }

    /**
     * Initialize on DOM ready
     */
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', initCards);
    } else {
        initCards();
    }

    /**
     * Re-initialize on AEM editor mode changes
     * This ensures cards work properly in author mode
     */
    if (window.Granite && window.Granite.author) {
        const MutationObserver = window.MutationObserver || window.WebKitMutationObserver;
        
        if (MutationObserver) {
            const observer = new MutationObserver(function(mutations) {
                mutations.forEach(function(mutation) {
                    if (mutation.addedNodes.length) {
                        setTimeout(initCards, 100);
                    }
                });
            });
            
            observer.observe(document.body, {
                childList: true,
                subtree: true
            });
        }
    }

    // Expose for external use
    window.CardComponent = CardComponent;
    window.initCards = initCards;

})();
