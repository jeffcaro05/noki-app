const CACHE='noki-app-v4-20260911';
const CORE=['./','index.html','app.js','shared.css','entregas.html','shared.mjs','api.mjs','mapa.html','map.js','leaflet.js','leaflet.css','qrcode.js','jsQR.js','noki-original.png','manifest.webmanifest','register-sw.js'];
self.addEventListener('install',e=>e.waitUntil(caches.open(CACHE).then(c=>c.addAll(CORE.map(p=>new Request(p,{cache:'reload'})))).then(()=>self.skipWaiting())));
self.addEventListener('activate',e=>e.waitUntil(caches.keys().then(keys=>Promise.all(keys.filter(k=>k.startsWith('noki-app-')&&k!==CACHE).map(k=>caches.delete(k)))).then(()=>self.clients.claim())));
self.addEventListener('fetch',e=>{
 if(e.request.method!=='GET'||new URL(e.request.url).origin!==self.location.origin)return;
 e.respondWith((async()=>{const cache=await caches.open(CACHE);try{const r=await fetch(e.request,{signal:AbortSignal.timeout(6000)});if(r.ok&&CORE.some(p=>new URL(p,self.registration.scope).pathname===new URL(e.request.url).pathname)){const copy=r.clone();e.waitUntil(cache.put(e.request,copy))}return r}catch{const saved=await cache.match(e.request,{ignoreSearch:true});return saved||(e.request.mode==='navigate'?await cache.match('index.html'):Response.error())}})());
});
