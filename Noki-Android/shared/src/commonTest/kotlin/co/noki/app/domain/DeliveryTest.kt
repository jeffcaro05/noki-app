package co.noki.app.domain

import kotlin.test.*

class DeliveryTest {
 private val device=DeviceRecord("d","Aparato",DeviceCategory.PHONE,SafetyState.NORMAL,FunctionState.WORKS,Decision.DELIVER,preparation=Preparation.entries.associateWith {CheckState.DONE})
 private val receiver=ReceiverPlace("r","Lugar","Dirección","Horario",listOf("phone"),"Condiciones")
 private fun delivery(status:DeliveryStatus)=Delivery("h","s","r","Lugar","Dirección",status,listOf(DeliveryItem("d","Aparato","phone","normal")))
 @Test fun preparationIsRequiredForCode() {assertTrue(DeliveryRules.ready(device));Preparation.entries.forEach {assertFalse(DeliveryRules.ready(device.copy(preparation=device.preparation+(it to CheckState.PENDING))))}}
 @Test fun resultRequiresBothConfirmations() {DeliveryStatus.entries.forEach {assertEquals(it==DeliveryStatus.COMPLETED,DeliveryRules.canRecord(device,listOf(delivery(it))))};assertFalse(DeliveryRules.canRecord(device,emptyList()))}
 @Test fun oneActiveDeliveryAcrossAllDevices() {val second=device.copy(id="second");listOf(DeliveryStatus.PENDING,DeliveryStatus.RECEIVED).forEach {assertFalse(DeliveryRules.canCreate(listOf(second),listOf(delivery(it)),receiver))}}
 @Test fun cancellationAllowsAnotherDeliveryButCompletedItemCannotRepeat() {assertTrue(DeliveryRules.canCreate(listOf(device),listOf(delivery(DeliveryStatus.CANCELLED)),receiver));assertFalse(DeliveryRules.canCreate(listOf(device),listOf(delivery(DeliveryStatus.COMPLETED)),receiver))}
 @Test fun multipleItemsAndCategoryValidation() {assertTrue(DeliveryRules.canCreate(listOf(device,device.copy(id="two")),emptyList(),receiver));assertFalse(DeliveryRules.canCreate(listOf(device,device),emptyList(),receiver));assertFalse(DeliveryRules.canCreate(listOf(device.copy(category=DeviceCategory.COMPUTER)),emptyList(),receiver));assertFalse(DeliveryRules.canCreate(emptyList(),emptyList(),receiver))}
 @Test fun keepingOrRepairingDoesNotInventDelivery() {listOf(Decision.KEEP,Decision.REPAIR,Decision.REUSE).forEach {assertTrue(DeliveryRules.canRecord(device.copy(decision=it),emptyList()));assertFalse(DeliveryRules.ready(device.copy(decision=it)))}}
 @Test fun damagedOrUnknownConditionRequiresAgreementNotPermanentBlock() {
  val place=receiver.copy(whatsapp="573001234567")
  SafetyState.entries.filter {it!=SafetyState.NORMAL}.forEach {state->
   val items=listOf(device.copy(safety=state))
   assertTrue(DeliveryRules.needsContact(items,place))
   assertFalse(DeliveryRules.canCreate(items,emptyList(),place))
   assertTrue(DeliveryRules.canCreate(items,emptyList(),place,true))
   assertFalse(DeliveryRules.canCreate(items,emptyList(),receiver,true))
  }
 }
 @Test fun categoryExceptionRequiresContactForEntireSelection() {
  val items=listOf(device,device.copy(id="computer",category=DeviceCategory.COMPUTER))
  val place=receiver.copy(whatsapp="573001234567")
  assertFalse(DeliveryRules.canCreate(items,emptyList(),place))
  assertTrue(DeliveryRules.canCreate(items,emptyList(),place,true))
  assertFalse(DeliveryRules.canCreate(items,listOf(delivery(DeliveryStatus.PENDING)),place,true))
 }
 @Test fun contactDoesNotBypassPreparationOwnershipLocksOrKeeping() {
  val place=receiver.copy(whatsapp="573001234567")
  listOf(device.copy(decision=Decision.KEEP),device.copy(preparation=emptyMap()),device.copy(archivedAt="date")).forEach {
   assertFalse(DeliveryRules.canCreate(listOf(it),emptyList(),place,true))
  }
 }
 @Test fun whatsappLinksAcceptOnlyInternationalDigits() {
  assertEquals("https://wa.me/573001234567",ReceiverContact.whatsappUrl("573001234567"))
  listOf("","323","+573001234567","https://malicious.invalid","573001234567?text=x").forEach {assertNull(ReceiverContact.whatsappUrl(it))}
 }
 @Test fun savingPreparationIsPendingUntilBothPartiesConfirm() {
  assertFalse(DeliveryRules.inHistory(device,emptyList()))
  listOf(DeliveryStatus.PENDING,DeliveryStatus.RECEIVED,DeliveryStatus.CANCELLED).forEach {assertFalse(DeliveryRules.inHistory(device,listOf(delivery(it))))}
  assertTrue(DeliveryRules.inHistory(device,listOf(delivery(DeliveryStatus.COMPLETED))))
  assertTrue(DeliveryRules.pendingLabel(device,emptyList()).contains("pendiente"))
 }
 @Test fun archivedDraftIsHistoryButNeverDeliveredOrReusable() {
  val archived=device.copy(archivedAt="2026-10-08T16:00:00Z")
  assertTrue(DeliveryRules.inHistory(archived,emptyList()));assertFalse(archived.completed)
  assertFalse(DeliveryRules.ready(archived));assertFalse(DeliveryRules.canRecord(archived,emptyList()))
  assertFalse(DeliveryRules.canCreate(listOf(archived),emptyList(),receiver))
  assertTrue(DeliveryRules.pendingLabel(archived,emptyList()).contains("sin entrega"))
 }
}
