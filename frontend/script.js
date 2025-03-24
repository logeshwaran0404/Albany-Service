// Add smooth scrolling to all links
document.querySelectorAll('a[href^="#"]').forEach(anchor => {
    anchor.addEventListener('click', function (e) {
        e.preventDefault();
        document.querySelector(this.getAttribute('href')).scrollIntoView({
            behavior: 'smooth'
        });
    });
});

// Simple form submission alert (for demo)
document.querySelector('form').addEventListener('submit', function(e) {
    e.preventDefault();
    alert('Service booking submitted successfully!');
});