package co.noki.app.ui

import androidx.compose.runtime.Composable

@Composable internal fun DeliveryTrustNote(reviewed:Boolean=false,referenceOnly:Boolean=false,practice:Boolean=false) {
    Note(when {
        practice->"Práctica local: este lugar es ficticio. No lleves aparatos a esta dirección."
        reviewed->"Noki revisó la solicitud de este receptor. Revisa sus horarios y condiciones y muestra el código solo al entregar."
        referenceOnly->"Estos lugares se consultaron en fuentes públicas. No son receptores aprobados de Noki ni reciben sus códigos. Revisa la fuente y confirma las condiciones antes de ir."
        else->"Las entregas con código se realizan con receptores aprobados por Noki. Tu entrega quedará pendiente hasta que el receptor compruebe el código y confirme la recepción."
    })
}
