export type Section =
  | "overview"
  | "users"
  | "receivers"
  | "points"
  | "deliveries"
  | "reports"
  | "audit";
export type ListSection =
  "users" | "receivers" | "points" | "deliveries" | "audit";
export interface Admin {
  id: string;
  email: string;
  name: string;
}
export interface Overview {
  users: number;
  confirmedUsers: number;
  receivers: number;
  pendingReviews: number;
  points: number;
  pendingDeliveries: number;
  completedDeliveries: number;
  cancelledDeliveries: number;
  devices: number;
  deliveredDevices: number;
  checkedAt: string;
  byMonth: { month: string; completed: number }[];
}
export interface UserRow {
  id: string;
  email: string;
  username: string | null;
  display_name: string | null;
  confirmed: boolean;
  created_at: string;
  last_sign_in_at: string | null;
  receiver_status: string | null;
  devices: number;
}
export interface Device {
  id: string;
  description: string;
  category: string;
  decision: string;
  outcome: string | null;
  archived_at: string | null;
}
export interface UserDetail extends Omit<UserRow, "devices"> {
  devices: Device[];
}
export interface Receiver {
  id: string;
  user_id: string;
  email: string;
  full_name: string;
  place_name: string;
  address: string;
  status: string;
  created_at: string;
  updated_at: string;
  hours: string;
  conditions: string;
  categories: string[];
  photos: string[];
  latitude: number | null;
  longitude: number | null;
  public_whatsapp: string | null;
  review_note: string | null;
  reviewed_at: string | null;
  reviewed_by: string | null;
  legal_version: string;
  accepted_at: string;
}
export interface Point {
  id: string;
  slug: string;
  name: string;
  address: string;
  city: string;
  latitude: number;
  longitude: number;
  source_name: string;
  source_url: string;
  verified_at: string;
  accepted_categories: string[];
  accepted_details: string;
  hours_text: string;
  phone: string | null;
  whatsapp: string | null;
  location_note: string | null;
  active: boolean;
  created_at: string;
  receiver_request_id: string | null;
}
export interface Delivery {
  id: string;
  receiver_name: string;
  sender_email: string;
  receiver_email: string;
  address: string;
  status: string;
  created_at: string;
  received_at: string | null;
  completed_at: string | null;
  cancelled_at: string | null;
  cancel_reason: string | null;
  contact_confirmed_at: string | null;
  devices: number;
  items: {
    device_id: string;
    description: string;
    category: string;
    safety_state: string;
  }[];
}
export interface Audit {
  id: number;
  actor_email: string;
  action: string;
  target_id: string | null;
  note: string | null;
  created_at: string;
}
export type Row = UserRow | Receiver | Point | Delivery | Audit;
export interface Page<T = Row> {
  items: T[];
  total: number;
  offset: number;
  pageSize: number;
  checkedAt: string;
}
export interface Notice {
  kind: "success" | "error" | "info";
  message: string;
}
