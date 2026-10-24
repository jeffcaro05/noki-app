// Isolated browser fixtures: all Supabase HTTP/WebSocket requests are intercepted.
// These identities and tokens are not real and are never sent to the live backend.
import type { BrowserContext } from "@playwright/test";
const id = "00000000-0000-4000-8000-000000000001";
export const receiverId = "00000000-0000-4000-8000-000000000002";
const now = "2026-10-20T18:00:00Z";
export const receiver = {
  id: receiverId,
  user_id: receiverId,
  email: "receptor@example.com",
  full_name: "Responsable de prueba",
  place_name: "Centro de prueba accesible",
  address: "Calle de prueba 123, Medellín",
  status: "pending",
  created_at: now,
  updated_at: now,
  hours: "Lunes a viernes, 8:00 a 17:00",
  conditions: "Recibe computadores y accesorios sin baterías dañadas.",
  categories: ["computer", "accessories"],
  photos: [
    `${receiverId}/photo1.jpg`,
    `${receiverId}/photo2.jpg`,
    `${receiverId}/photo3.jpg`,
  ],
  latitude: 6.2442,
  longitude: -75.5812,
  public_whatsapp: "573001234567",
  review_note: null,
  reviewed_at: null,
  reviewed_by: null,
  legal_version: "v1",
  accepted_at: now,
};
const point = {
  id: receiverId,
  slug: "test-place",
  name: receiver.place_name,
  address: receiver.address,
  city: "Medellín",
  latitude: receiver.latitude,
  longitude: receiver.longitude,
  source_name: "Receptor de prueba",
  source_url: "https://www.openstreetmap.org",
  verified_at: "2026-10-20",
  accepted_categories: receiver.categories,
  accepted_details: receiver.conditions,
  hours_text: receiver.hours,
  phone: null,
  whatsapp: receiver.public_whatsapp,
  location_note: "Entrada principal",
  active: true,
  created_at: now,
  receiver_request_id: receiverId,
};
const user = {
  id,
  email: "persona@example.com",
  username: "Persona de prueba",
  display_name: "Persona de prueba",
  confirmed: true,
  created_at: now,
  last_sign_in_at: now,
  receiver_status: null,
  devices: 2,
};
const delivery = {
  id,
  receiver_name: receiver.place_name,
  sender_email: user.email,
  receiver_email: receiver.email,
  address: receiver.address,
  status: "completed",
  created_at: now,
  received_at: now,
  completed_at: now,
  cancelled_at: null,
  cancel_reason: null,
  contact_confirmed_at: now,
  devices: 1,
  items: [
    {
      device_id: id,
      description: "Computador portátil de prueba",
      category: "computer",
      safety_state: "normal",
    },
  ],
};
const overview = {
  users: 17,
  confirmedUsers: 15,
  receivers: 1,
  pendingReviews: 1,
  points: 3,
  pendingDeliveries: 2,
  completedDeliveries: 4,
  cancelledDeliveries: 1,
  devices: 18,
  deliveredDevices: 5,
  checkedAt: now,
  byMonth: [
    "2026-05",
    "2026-06",
    "2026-07",
    "2026-08",
    "2026-09",
    "2026-10",
  ].map((month, i) => ({ month, completed: [0, 0, 1, 0, 1, 2][i] })),
};
export async function mockBackend(
  context: BrowserContext,
  authenticated = true,
) {
  const calls: { op: string; args: Record<string, unknown> }[] = [];
  await context.routeWebSocket("**", (socket) => socket.close());
  await context.route("**/*", async (route) => {
    const url = new URL(route.request().url());
    if (url.hostname === "127.0.0.1") return route.continue();
    if (url.pathname.endsWith("/rpc/noki_admin_action")) {
      const { op, args } = route.request().postDataJSON();
      calls.push({ op, args });
      let result: unknown = { ok: true };
      if (op === "open")
        result = { id, email: "caroajeffry@gmail.com", name: "Jeffry" };
      if (op === "overview" || op === "report_export") result = overview;
      const lists: Record<string, unknown[]> = {
        users: [user],
        receivers: [receiver],
        points: [point],
        deliveries: [delivery],
        audit: [
          {
            id: 1,
            actor_email: "admin@example.com",
            action: "receiver.approved",
            target_id: receiverId,
            note: "Revisión de prueba",
            created_at: now,
          },
        ],
      };
      if (op in lists)
        result = {
          items: args.q === "inexistente" ? [] : lists[op],
          total: args.q === "inexistente" ? 0 : 1,
          pageSize: 25,
          offset: 0,
          checkedAt: now,
        };
      if (op === "user_detail")
        result = {
          ...user,
          devices: [
            {
              id,
              description: "Portátil",
              category: "computer",
              decision: "deliver",
              outcome: "delivered",
              archived_at: null,
            },
          ],
        };
      if (op === "receiver_detail") result = receiver;
      if (op === "delivery_detail") result = delivery;
      return route.fulfill({ json: result });
    }
    if (url.pathname.endsWith("/auth/v1/otp"))
      return route.fulfill({ json: {} });
    return route.fulfill({
      status: 403,
      json: { message: "Blocked: isolated browser test" },
    });
  });
  if (authenticated) {
    const jwt = [
      { alg: "HS256", typ: "JWT" },
      {
        sub: id,
        exp: Math.floor(Date.now() / 1000) + 3600,
        role: "authenticated",
      },
      "test-only-not-a-real-signature",
    ]
      .map((p, i) =>
        i < 2 ? Buffer.from(JSON.stringify(p)).toString("base64url") : p,
      )
      .join(".");
    await context.addInitScript(
      ({ id, jwt }) =>
        sessionStorage.setItem(
          "noki-admin-auth-v2",
          JSON.stringify({
            access_token: jwt,
            refresh_token: "test-only",
            expires_at: Math.floor(Date.now() / 1000) + 3600,
            expires_in: 3600,
            token_type: "bearer",
            user: {
              id,
              email: "caroajeffry@gmail.com",
              aud: "authenticated",
              app_metadata: {},
              user_metadata: {},
              created_at: "2026-10-20T00:00:00Z",
            },
          }),
        ),
      { id, jwt },
    );
  }
  return calls;
}
