package co.noki.app.data

import co.noki.app.domain.*
import org.json.JSONObject

fun DeviceRecord.toJson() = JSONObject().put("id", id).put("description", description)
    .put("category", category.code).put("safety_state", safety.code).put("function_state", function.code)
    .put("decision", decision.name.lowercase()).put("selected_point_id", pointId ?: JSONObject.NULL)
    .put("preparation", JSONObject().apply { Preparation.entries.forEach { put(it.name.lowercase(), (preparation[it] ?: CheckState.PENDING).name.lowercase()) } })
    .put("outcome", outcome?.name?.lowercase() ?: JSONObject.NULL)

fun deviceFromJson(json: JSONObject): DeviceRecord = DeviceRecord(
    id = json.getString("id"), description = json.getString("description"),
    category = DeviceCategory.entries.first { it.code == json.getString("category") },
    safety = SafetyState.entries.first { it.code == json.getString("safety_state") },
    function = FunctionState.entries.first { it.code == json.getString("function_state") },
    decision = Decision.valueOf(json.getString("decision").uppercase()),
    pointId = if (json.isNull("selected_point_id")) null else json.getString("selected_point_id"),
    preparation = Preparation.entries.associateWith { item -> CheckState.valueOf(json.getJSONObject("preparation").optString(item.name.lowercase(), "pending").uppercase()) },
    outcome = if (json.isNull("outcome")) null else Outcome.valueOf(json.getString("outcome").uppercase()),
    completedAt = if (json.isNull("completed_at")) null else json.getString("completed_at"),
    version = json.optInt("version", 1), updatedAt = json.optString("updated_at", ""),
    archivedAt=if(json.isNull("archived_at")) null else json.getString("archived_at")
)
