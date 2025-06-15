// MaâtCore Mobile Web Signup Scripts

document.addEventListener('DOMContentLoaded', function() {
    // Éléments du DOM
    const registrationForm = document.getElementById('registrationForm');
    const passwordInput = document.getElementById('password');
    const confirmPasswordInput = document.getElementById('confirmPassword');
    const strengthBar = document.querySelector('.strength-bar');
    const strengthText = document.querySelector('.strength-text');
    
    // Configuration de l'API
    const API_URL = 'http://localhost:8081/api/auth/register';
    
    // Validation du mot de passe en temps réel
    passwordInput.addEventListener('input', function() {
        const password = this.value;
        const strength = calculatePasswordStrength(password);
        
        // Mise à jour de la barre de force
        strengthBar.style.setProperty('--strength-width', `${strength.score * 25}%`);
        strengthBar.style.setProperty('--strength-color', getColorForStrength(strength.score));
        
        // Mise à jour du texte
        strengthText.textContent = strength.message;
        strengthText.style.color = getColorForStrength(strength.score);
    });
    
    // Vérification de la correspondance des mots de passe
    confirmPasswordInput.addEventListener('input', function() {
        if (this.value !== passwordInput.value) {
            this.setCustomValidity('Les mots de passe ne correspondent pas');
        } else {
            this.setCustomValidity('');
        }
    });
    
    // Soumission du formulaire
    registrationForm.addEventListener('submit', function(e) {
        e.preventDefault();
        
        // Validation finale
        if (!validateForm()) {
            return;
        }
        
        // Préparation des données
        const userData = {
            displayName: document.getElementById('displayName').value,
            email: document.getElementById('email').value,
            password: passwordInput.value,
            roles: ['ROLE_END_USER']
        };
        
        // Envoi des données à l'API
        registerUser(userData);
    });
    
    // Fonction de validation du formulaire
    function validateForm() {
        // Vérification du nom d'utilisateur
        const displayName = document.getElementById('displayName').value;
        if (displayName.length < 3) {
            showError('Le nom d\'utilisateur doit contenir au moins 3 caractères');
            return false;
        }
        
        // Vérification de l'email
        const email = document.getElementById('email').value;
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(email)) {
            showError('Veuillez entrer une adresse email valide');
            return false;
        }
        
        // Vérification du mot de passe
        const password = passwordInput.value;
        const strength = calculatePasswordStrength(password);
        if (strength.score < 2) {
            showError('Votre mot de passe est trop faible. ' + strength.message);
            return false;
        }
        
        // Vérification de la correspondance des mots de passe
        if (password !== confirmPasswordInput.value) {
            showError('Les mots de passe ne correspondent pas');
            return false;
        }
        
        // Vérification des conditions d'utilisation
        if (!document.getElementById('termsAccepted').checked) {
            showError('Vous devez accepter les conditions d\'utilisation');
            return false;
        }
        
        return true;
    }
    
    // Fonction pour calculer la force du mot de passe
    function calculatePasswordStrength(password) {
        let score = 0;
        let message = '';
        
        // Longueur
        if (password.length < 8) {
            message = 'Trop court (minimum 8 caractères)';
        } else {
            score += 1;
            
            // Complexité
            if (/[A-Z]/.test(password)) score += 1;
            if (/[0-9]/.test(password)) score += 1;
            if (/[^A-Za-z0-9]/.test(password)) score += 1;
            
            // Messages selon le score
            if (score === 1) message = 'Faible';
            else if (score === 2) message = 'Moyen';
            else if (score === 3) message = 'Fort';
            else if (score === 4) message = 'Très fort';
        }
        
        return { score, message };
    }
    
    // Fonction pour obtenir la couleur selon la force
    function getColorForStrength(score) {
        if (score === 0) return 'var(--error-color)';
        if (score === 1) return 'var(--error-color)';
        if (score === 2) return '#f39c12'; // Orange
        if (score === 3) return '#3498db'; // Bleu
        if (score === 4) return 'var(--success-color)';
    }
    
    // Fonction pour afficher les erreurs
    function showError(message) {
        // Vérifier si une alerte d'erreur existe déjà
        let errorAlert = document.querySelector('.error-alert');
        
        // Si elle n'existe pas, la créer
        if (!errorAlert) {
            errorAlert = document.createElement('div');
            errorAlert.className = 'error-alert';
            registrationForm.prepend(errorAlert);
        }
        
        // Mettre à jour le message
        errorAlert.textContent = message;
        errorAlert.style.display = 'block';
        
        // Faire défiler vers le haut pour voir l'erreur
        window.scrollTo({ top: 0, behavior: 'smooth' });
        
        // Masquer après 5 secondes
        setTimeout(() => {
            errorAlert.style.display = 'none';
        }, 5000);
    }
    
    // Fonction pour enregistrer l'utilisateur
    function registerUser(userData) {
        // Afficher un indicateur de chargement
        const submitButton = registrationForm.querySelector('button[type="submit"]');
        const originalText = submitButton.textContent;
        submitButton.disabled = true;
        submitButton.textContent = 'Création en cours...';
        
        fetch(API_URL, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(userData)
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Erreur lors de l\'inscription');
            }
            return response.json();
        })
        .then(data => {
            // Redirection vers la page de connexion avec un message de succès
            localStorage.setItem('registrationSuccess', 'true');
            window.location.href = 'login.html';
        })
        .catch(error => {
            showError(error.message || 'Une erreur est survenue lors de l\'inscription');
            submitButton.disabled = false;
            submitButton.textContent = originalText;
        });
    }
    
    // Initialisation des boutons de connexion sociale
    document.querySelector('.btn-google').addEventListener('click', function() {
        // Implémentation de l'authentification Google à venir
        alert('Fonctionnalité en cours de développement');
    });
    
    document.querySelector('.btn-facebook').addEventListener('click', function() {
        // Implémentation de l'authentification Facebook à venir
        alert('Fonctionnalité en cours de développement');
    });
    
    // Ajout du style CSS pour les alertes d'erreur
    const style = document.createElement('style');
    style.textContent = `
        .error-alert {
            background-color: var(--error-color);
            color: white;
            padding: 12px;
            border-radius: 8px;
            margin-bottom: 20px;
            display: none;
        }
        
        .strength-bar::before {
            width: var(--strength-width, 0%);
            background-color: var(--strength-color, var(--error-color));
        }
    `;
    document.head.appendChild(style);
});
