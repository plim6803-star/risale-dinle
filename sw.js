const CACHE_NAME = 'risale-dinle-v11';
const ASSETS = ['./', './index.html', './manifest.json', './icon.svg'];
self.addEventListener('install', (e) => {
  e.waitUntil(caches.open(CACHE_NAME).then(c => c.addAll(ASSETS)).then(() => self.skipWaiting()));
});
self.addEventListener('activate', (e) => {
  e.waitUntil(caches.keys().then(keys => Promise.all(keys.filter(k => k !== CACHE_NAME).map(k => caches.delete(k)))).then(() => self.clients.claim()));
});
self.addEventListener('fetch', (e) => {
  if (e.request.method !== 'GET' || e.request.url.includes('.mp3')) return;
  e.respondWith(caches.match(e.request).then(cached => cached || fetch(e.request)));
});
