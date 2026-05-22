document.addEventListener('DOMContentLoaded', () => {
  let container = document.getElementById('w3-bar-container');

  if (!container) {
    console.warn('Navbar container not found. Creating it before map-container...');

    const mapContainer = document.getElementById('map-container');
    if (!mapContainer) {
      console.error('map-container not found!');
      return;
    }

    container = document.createElement('div');
    container.className = 'w3-bar w3-black';
    container.id = 'w3-bar-container';

    mapContainer.parentNode.insertBefore(container, mapContainer);
  }

  fetch('https://www.luigibrother.party/navbar.html')
    .then(r => r.text())
    .then(html => {
      container.innerHTML = html;
    })
    .catch(err => console.error('Failed to load navbar.', err));
});
