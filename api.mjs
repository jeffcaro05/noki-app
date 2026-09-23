export const URL_BASE='https://cwsxjsmelrcfhbtbkssd.supabase.co';
const KEY='sb_publishable_D6FWErgyxMpTN4cZfPLoSw_PkrH-cpa';
const REQUEST_TIMEOUT=20000;
let session;try{session=JSON.parse(sessionStorage.getItem('noki-auth'))}catch{}
let refreshing;

function save(value){
 session=value;
 if(value)sessionStorage.setItem('noki-auth',JSON.stringify(value));
 else sessionStorage.removeItem('noki-auth');
}

async function timedFetch(url,options={},timeout=REQUEST_TIMEOUT){
 const controller=typeof AbortController==='function'?new AbortController():null;
 const timer=controller?setTimeout(()=>controller.abort(),timeout):null;
 try{
  return await fetch(url,{...options,...(controller?{signal:controller.signal}:{})});
 }finally{
  if(timer)clearTimeout(timer);
 }
}

function readableError(response,data){
 const code=data?.error_code||data?.code;
 if(code==='invalid_credentials'||response.status===400&&/invalid login credentials/i.test(data?.error_description||data?.msg||data?.message||'')){
  return 'Usuario o contraseña incorrectos. Revisa los datos e inténtalo de nuevo.';
 }
 if(response.status===429)return 'Se hicieron demasiados intentos seguidos. Espera un momento y vuelve a intentar.';
 if(response.status===401)return data?.message||data?.msg||data?.error_description||'La sesión no es válida. Vuelve a iniciar sesión.';
 return data?.message||data?.msg||data?.error_description||'No se pudo completar la operación.';
}

async function request(path,{method='GET',body,auth=true}={}){
 if(auth&&!session)throw new Error('Inicia sesión para continuar.');

 if(auth&&session.expires_at*1000<Date.now()+30000){
  refreshing ||= request('/auth/v1/token?grant_type=refresh_token',{
   method:'POST',
   body:{refresh_token:session.refresh_token},
   auth:false
  }).then(save).catch(e=>{save(null);throw e}).finally(()=>{refreshing=null});
  await refreshing;
 }

 let response;
 try{
  response=await timedFetch(URL_BASE+path,{
   method,
   headers:{
    apikey:KEY,
    'Content-Type':'application/json',
    ...(auth?{Authorization:'Bearer '+session.access_token}:{})
   },
   body:body===undefined?undefined:JSON.stringify(body),
   cache:'no-store',
   credentials:'omit'
  });
 }catch(error){
  if(error?.name==='AbortError'){
   throw new Error('El servidor de Noki tardó demasiado en responder. Tu internet puede estar funcionando correctamente; reintenta en unos segundos.');
  }
  throw new Error('No pudimos conectar con el servidor de Noki. Tu conexión a internet puede estar bien; el servicio de acceso no respondió. Reintenta. Si continúa, hay que revisar el backend de Supabase.');
 }

 const data=await response.json().catch(()=>null);
 if(!response.ok){
  if(response.status===401&&auth)save(null);
  throw new Error(readableError(response,data));
 }
 return data;
}

export const user=()=>session?.user;

export async function login(email,password){
 const s=await request('/auth/v1/token?grant_type=password',{
  method:'POST',
  auth:false,
  body:{email,password}
 });
 save(s);
 return s.user;
}

export async function logout(){
 try{if(session)await request('/auth/v1/logout',{method:'POST'})}
 finally{save(null)}
}

export const rpc=(name,body={})=>request('/rest/v1/rpc/'+name,{method:'POST',body});

export async function snapshot(){
 await rpc('noki_sync_expired');
 const [points,slots,roles,deliveries]=await Promise.all([
  request('/rest/v1/noki_points?select=*'),
  request('/rest/v1/noki_slots?select=*&order=starts_at'),
  request('/rest/v1/noki_receivers?select=*'),
  request('/rest/v1/noki_deliveries?select=*,noki_items(*)&order=created_at.desc')
 ]);
 return {points,slots,roles,deliveries};
}
