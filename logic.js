(function(root){
 'use strict';
 const questions=[
  {title:'¿Has notado daños o alguna condición inusual?',description:'Piensa en el celular y su batería. Responde con lo que ya sabes: no necesitas encenderlo, cargarlo ni desmontarlo.',options:[['yes','Sí'],['no','No'],['unknown','No estoy seguro']]},
  {title:'¿Sabes si funciona actualmente?',description:'Si no conoces su estado, puedes indicarlo sin hacer ninguna prueba.',options:[['works','Sí, funciona'],['faults','Presenta fallas'],['unknown','No lo sé']]},
  {title:'¿Qué prefieres hacer con el celular?',description:'Elige tu intención. Podrás revisar otras opciones antes de decidir.',options:[['keep','Conservarlo'],['give','Entregarlo a otra persona'],['collect','Consultar recolección'],['unknown','No estoy seguro']]}
 ];
 const results={
 R1:{title:'Antes de decidir, aclara su condición.',text:'Una valoración puede ayudarte a conocer si puede seguir utilizándose. Esta orientación no confirma un daño ni sustituye una revisión técnica.',next:'Describe lo que has observado al solicitar atención. No presupongas que un punto de recolección recibe equipos con cualquier tipo de daño.'},
 R2:{title:'Conoce su estado antes de elegir.',text:'Una valoración puede orientar entre reparación, reutilización o gestión como residuo. No podemos determinar la avería ni el costo desde este cuestionario.',next:'Identifica el modelo si lo conoces y consulta las condiciones del servicio de valoración.'},
 R3:{title:'Una función que todavía te sirva.',text:'Si sus funciones y condición lo permiten, puedes considerar conservarlo como alarma o agenda. Estos ejemplos no garantizan que cualquier celular sea apto.',next:'Elige un uso que realmente necesites y que esté disponible en ese modelo.'},
 R4:{title:'Prepara una entrega a otra persona.',text:'Comprueba que sus funciones respondan a lo que necesita quien lo recibe e informa cualquier limitación conocida.',next:'Conserva la información que necesites y consulta el procedimiento de protección de datos correspondiente a tu modelo antes de entregar.'},
 R5:{title:'Consulta una entrega responsable.',text:'Elegir recolección no significa que el aparato sea irrecuperable. También puedes revisar si todavía existe una alternativa de reutilización.',next:'Busca un punto que reciba celulares y comprueba sus condiciones. El directorio verificado se incorporará en el siguiente módulo.'},
 R6:{title:'Compara antes de decidir.',text:'Puedes considerar conservarlo, entregarlo a alguien que lo necesite o consultar recolección. La opción adecuada depende del estado y de una utilidad real.',next:'Vuelve a las opciones y elige el destino que quieras explorar.'}
 };
 function route(a){if(!a[0])return {question:0};if(a[0]!=='no')return {result:'R1'};if(!a[1])return {question:1};if(a[1]!=='works')return {result:'R2'};if(!a[2])return {question:2};return {result:{keep:'R3',give:'R4',collect:'R5',unknown:'R6'}[a[2]]};}
 function answer(a,index,value){if(!questions[index]||!questions[index].options.some(o=>o[0]===value))throw new Error('Respuesta inválida');return [...a.slice(0,index),value];}
 const api={questions,results,route,answer};root.NokiLogic=api;if(typeof module!=='undefined')module.exports=api;
})(typeof globalThis!=='undefined'?globalThis:this);
