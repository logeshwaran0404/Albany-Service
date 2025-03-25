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

const btn = document.querySelector('.btn');
        
btn.addEventListener('click', function(e) {
    if (!this.classList.contains('clicked') && !this.classList.contains('done')) {
        this.classList.add('clicked');
    } else if (this.classList.contains('clicked')) {
        this.classList.remove('clicked');
        this.classList.add('done');
    }
});


