export const URL_BASE='https://cwsxjsmelrcfhbtbkssd.supabase.co';
const KEY='sb_publishable_D6FWErgyxMpTN4cZfPLoSw_PkrH-cpa';
let session;try{session=JSON.parse(sessionStorage.getItem('noki-auth'))}catch{}
let refreshing;
function save(value){session=value;if(value)sessionStorage.setItem('noki-auth',JSON.stringify(value));else sessionStorage.removeItem('noki-auth')}
async function request(path,{method='GET',body,auth=true}={}){
 if(auth&&!session)throw new Error('Inicia sesión para continuar.');
 if(auth&&session.expires_at*1000<Date.now()+30000){
  refreshing ||= request('/auth/v1/token?grant_type=refresh_token',{method:'POST',body:{refresh_token:session.refresh_token},auth:false}).then(save).catch(e=>{save(null);throw e}).finally(()=>{refreshing=null});
  await refreshing;
 }
 let response;
 try{response=await fetch(URL_BASE+path,{method,headers:{apikey:KEY,'Content-Type':'application/json',...(auth?{Authorization:'Bearer '+session.access_token}:{})},body:body===undefined?undefined:JSON.stringify(body),signal:AbortSignal.timeout(20000),cache:'no-store'})}catch{throw new Error('No pudimos conectar. Comprueba internet y reintenta; conservarás lo escrito.')}
 const data=await response.json().catch(()=>null);
 if(!response.ok){if(response.status===401&&auth)save(null);throw new Error(data?.message||data?.msg||data?.error_description||'No se pudo completar la operación.')}
 return data;
}
export const user=()=>session?.user;
export async function login(email,password){const s=await request('/auth/v1/token?grant_type=password',{method:'POST',auth:false,body:{email,password}});save(s);return s.user}
export async function logout(){try{if(session)await request('/auth/v1/logout',{method:'POST'})}finally{save(null)}}
export const rpc=(name,body={})=>request('/rest/v1/rpc/'+name,{method:'POST',body});
export async function snapshot(){
 await rpc('noki_sync_expired');
 const [points,slots,roles,deliveries]=await Promise.all([
  request('/rest/v1/noki_points?select=*'),request('/rest/v1/noki_slots?select=*&order=starts_at'),
  request('/rest/v1/noki_receivers?select=*'),
  request('/rest/v1/noki_deliveries?select=*,noki_items(*)&order=created_at.desc')]);
 return {points,slots,roles,deliveries};
}
