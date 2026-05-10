document.addEventListener('DOMContentLoaded', () => {
  const container = document.getElementById('w3-bar-container');
  if (!container) {
    console.error('Navbar container not found!');
    return;
  }

  fetch('https://www.luigibrother.party/navbar.html')
    .then(r => r.text())
    .then(html => container.innerHTML = html)
    .catch(err => console.error('Failed to load navbar.', err));
});
