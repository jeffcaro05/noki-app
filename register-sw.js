if('serviceWorker' in navigator && location.protocol!=='file:') {
  navigator.serviceWorker.register('sw.js',{updateViaCache:'none'}).then(r=>r.update()).catch(()=>{});
}
