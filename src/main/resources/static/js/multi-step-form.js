// Multi-Step Form JavaScript - Fichier externe
console.log('📋 Loading multi-step form script...');

function initMultiStepForm() {
    if (window.multiStepInitialized) {
        console.log('⏭️ Multi-step form already initialized, skipping...');
        return;
    }
    
    console.log('🚀 Initializing multi-step form...');
    
    const form = document.getElementById('candidatureForm');
    const steps = document.querySelectorAll('.form-step');
    const stepIndicators = document.querySelectorAll('.step-indicator');
    const progressBar = document.getElementById('progressBar');
    const nextButtons = document.querySelectorAll('.next-step');
    const prevButtons = document.querySelectorAll('.prev-step');
    
    // Debug logging
    console.log('Elements found:', {
        form: !!form,
        steps: steps.length,
        stepIndicators: stepIndicators.length,
        nextButtons: nextButtons.length,
        prevButtons: prevButtons.length,
        progressBar: !!progressBar
    });
    
    if (!form) {
        console.error('❌ Form not found! DOM might not be ready yet.');
        return false;
    }
    
    if (nextButtons.length === 0) {
        console.error('❌ No next buttons found! DOM might not be ready yet.');
        return false;
    }
    
    // Mark as initialized
    window.multiStepInitialized = true;
    console.log('✅ Multi-step form successfully initialized!');
    
    let currentStep = 1;
    const totalSteps = 5;
    const stepData = {};
    
    // File inputs
    const cvInput = document.getElementById('cv');
    const lettreInput = document.getElementById('lettreMotivation');
    const photoInput = document.getElementById('photo');
    const photoPreview = document.getElementById('photo-preview');
    const photoPreviewImg = document.getElementById('photo-preview-img');
    const removePhotoBtn = document.getElementById('remove-photo');
    const photoPlaceholder = document.querySelector('.photo-placeholder');
    
    // Initialize form
    showStep(currentStep);
    
    // Navigation event listeners
    nextButtons.forEach(btn => {
        btn.addEventListener('click', function(e) {
            e.preventDefault();
            console.log('⏭️ Next button clicked');
            const nextStep = parseInt(this.getAttribute('data-next'));
            console.log('Current step:', currentStep, 'Next step:', nextStep);
            
            if (validateStep(currentStep)) {
                saveStepData(currentStep);
                goToStep(nextStep);
            } else {
                console.log('❌ Step validation failed');
            }
        });
    });
    
    prevButtons.forEach(btn => {
        btn.addEventListener('click', function(e) {
            e.preventDefault();
            const prevStep = parseInt(this.getAttribute('data-prev'));
            saveStepData(currentStep);
            goToStep(prevStep);
        });
    });
    
    // Step indicator clicks
    stepIndicators.forEach(indicator => {
        indicator.addEventListener('click', function() {
            const targetStep = parseInt(this.getAttribute('data-step'));
            if (targetStep <= getMaxAvailableStep()) {
                saveStepData(currentStep);
                goToStep(targetStep);
            }
        });
    });
    
    // Navigation functions
    function goToStep(step) {
        console.log(`📍 Going to step ${step}`);
        if (step >= 1 && step <= totalSteps) {
            currentStep = step;
            showStep(currentStep);
            updateProgressBar();
            updateStepIndicators();
            scrollToTop();
            console.log(`✅ Successfully moved to step ${step}`);
        }
    }
    
    function showStep(step) {
        steps.forEach(stepElement => {
            stepElement.classList.remove('active');
            if (parseInt(stepElement.getAttribute('data-step')) === step) {
                stepElement.classList.add('active');
            }
        });
    }
    
    function updateProgressBar() {
        const progress = (currentStep / totalSteps) * 100;
        if (progressBar) {
            progressBar.style.width = progress + '%';
        }
    }
    
    function updateStepIndicators() {
        stepIndicators.forEach((indicator, index) => {
            const stepNum = index + 1;
            indicator.classList.remove('active', 'completed');
            
            if (stepNum === currentStep) {
                indicator.classList.add('active');
            } else if (stepNum < currentStep) {
                indicator.classList.add('completed');
            }
        });
    }
    
    function scrollToTop() {
        const header = document.querySelector('.cv-header');
        if (header) {
            header.scrollIntoView({ 
                behavior: 'smooth', 
                block: 'start' 
            });
        }
    }
    
    function getMaxAvailableStep() {
        return Math.max(1, currentStep);
    }
    
    // Step validation
    function validateStep(step) {
        console.log(`🔍 Validating step ${step}`);
        let isValid = true;
        const stepElement = document.querySelector(`[data-step="${step}"]`);
        
        if (!stepElement) {
            console.error(`❌ Step element not found for step ${step}`);
            return false;
        }
        
        console.log('Step element found:', stepElement);
        
        switch(step) {
            case 1: // Personal information
                const requiredFields = stepElement.querySelectorAll('input[required], select[required]');
                requiredFields.forEach(field => {
                    if (!field.value.trim()) {
                        showFieldError(field);
                        isValid = false;
                    } else {
                        clearFieldError(field);
                    }
                });
                
                // Email validation
                const email = stepElement.querySelector('#email');
                if (email && email.value && !isValidEmail(email.value)) {
                    showFieldError(email, 'Adresse email invalide');
                    isValid = false;
                }
                break;
                
            case 2: // Formations
            case 3: // Competences
                // Optional validation - can proceed even without selections
                break;
                
            case 4: // Documents
                if (!cvInput || !cvInput.files || cvInput.files.length === 0) {
                    showAlert('Veuillez télécharger votre CV (obligatoire)', 'danger');
                    if (cvInput) highlightField(cvInput);
                    isValid = false;
                } else {
                    if (cvInput) clearFieldError(cvInput);
                }
                break;
                
            case 5: // Finalization
                const consentement = document.getElementById('consentement');
                if (!consentement || !consentement.checked) {
                    showAlert('Vous devez accepter le traitement de vos données personnelles', 'danger');
                    if (consentement) highlightField(consentement);
                    isValid = false;
                } else {
                    if (consentement) clearFieldError(consentement);
                }
                break;
        }
        
        if (!isValid) {
            stepElement.classList.add('step-invalid');
            setTimeout(() => {
                stepElement.classList.remove('step-invalid');
            }, 500);
        }
        
        return isValid;
    }
    
    function saveStepData(step) {
        const stepElement = document.querySelector(`[data-step="${step}"]`);
        if (!stepElement) return;
        
        const formData = {};
        const inputs = stepElement.querySelectorAll('input, select, textarea');
        
        inputs.forEach(input => {
            if (input.type === 'checkbox') {
                formData[input.name] = input.checked;
            } else if (input.type === 'file') {
                formData[input.name] = input.files.length > 0;
            } else {
                formData[input.name] = input.value;
            }
        });
        
        stepData[step] = formData;
        markStepAsCompleted(step);
    }
    
    function markStepAsCompleted(step) {
        const indicator = document.querySelector(`.step-indicator[data-step="${step}"]`);
        if (indicator && step < currentStep) {
            indicator.classList.add('completed');
        }
    }
    
    // Validation helpers
    function isValidEmail(email) {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailRegex.test(email);
    }
    
    function showFieldError(field, message = 'Ce champ est obligatoire') {
        if (!field) return;
        field.classList.add('is-invalid');
        
        // Remove existing error message
        const existingError = field.parentNode.querySelector('.invalid-feedback');
        if (existingError) {
            existingError.remove();
        }
        
        // Add error message
        const errorDiv = document.createElement('div');
        errorDiv.className = 'invalid-feedback';
        errorDiv.textContent = message;
        field.parentNode.appendChild(errorDiv);
    }
    
    function clearFieldError(field) {
        if (!field) return;
        field.classList.remove('is-invalid');
        field.classList.add('is-valid');
        
        const errorDiv = field.parentNode.querySelector('.invalid-feedback');
        if (errorDiv) {
            errorDiv.remove();
        }
    }
    
    function highlightField(field) {
        if (!field) return;
        field.style.border = '2px solid #dc3545';
        setTimeout(() => {
            field.style.border = '';
        }, 3000);
    }
    
    // Alert system
    function showAlert(message, type = 'info') {
        const alertDiv = document.createElement('div');
        alertDiv.className = `alert alert-${type} alert-dismissible fade show position-fixed`;
        alertDiv.style.cssText = 'top: 20px; right: 20px; z-index: 9999; min-width: 300px;';
        alertDiv.innerHTML = `
            <i class="bi bi-${type === 'danger' ? 'exclamation-triangle' : type === 'success' ? 'check-circle' : 'info-circle'} me-2"></i>
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        `;
        document.body.appendChild(alertDiv);
        
        setTimeout(() => {
            if (alertDiv.parentNode) {
                alertDiv.remove();
            }
        }, 5000);
    }
    
    // Initialize counters for selections
    function updateSelectionCounters() {
        const formationCheckboxes = document.querySelectorAll('input[name="formationIds"]');
        const competenceCheckboxes = document.querySelectorAll('input[name="competanceIds"]');
        
        function updateFormationCounter() {
            const count = document.querySelectorAll('input[name="formationIds"]:checked').length;
            const header = document.querySelector('[data-step="2"] h4');
            updateCounterBadge(header, count, 'info');
        }
        
        function updateCompetenceCounter() {
            const count = document.querySelectorAll('input[name="competanceIds"]:checked').length;
            const header = document.querySelector('[data-step="3"] h4');
            updateCounterBadge(header, count, 'success');
        }
        
        function updateCounterBadge(header, count, type) {
            if (!header) return;
            
            let badge = header.querySelector('.counter-badge');
            if (!badge) {
                badge = document.createElement('span');
                badge.className = `badge bg-${type} ms-2 counter-badge`;
                header.appendChild(badge);
            }
            
            badge.textContent = count > 0 ? `${count} sélectionnée(s)` : '';
            badge.style.display = count > 0 ? 'inline-block' : 'none';
        }
        
        formationCheckboxes.forEach(cb => cb.addEventListener('change', updateFormationCounter));
        competenceCheckboxes.forEach(cb => cb.addEventListener('change', updateCompetenceCounter));
        
        // Initialize
        updateFormationCounter();
        updateCompetenceCounter();
    }
    
    updateSelectionCounters();
    return true;
}

// Initialize with multiple strategies
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initMultiStepForm);
} else if (document.readyState === 'interactive') {
    // DOM is ready but resources may still be loading
    setTimeout(initMultiStepForm, 100);
} else {
    // Document and resources are fully loaded
    initMultiStepForm();
}

// Fallback initialization
window.addEventListener('load', function() {
    if (!window.multiStepInitialized) {
        console.log('🔄 Fallback initialization...');
        setTimeout(initMultiStepForm, 250);
    }
});

// Additional fallback for very slow loading
setTimeout(function() {
    if (!window.multiStepInitialized) {
        console.log('🔄 Final fallback initialization...');
        initMultiStepForm();
    }
}, 2000);
