import { createClient } from "jsr:@supabase/supabase-js@2";

const BREVO_API_KEY = Deno.env.get("BREVO_API_KEY")!;
const BREVO_SENDER_EMAIL = Deno.env.get("BREVO_SENDER_EMAIL")!;

const supabaseAdmin = createClient(
  Deno.env.get("SUPABASE_URL")!,
  Deno.env.get("SUPABASE_SERVICE_ROLE_KEY")!,
);

const jsonHeaders = { "Content-Type": "application/json" };

function generateCode(): string {
  return Math.floor(100000 + Math.random() * 900000).toString();
}

async function sendResetEmail(email: string, code: string) {
  const response = await fetch("https://api.brevo.com/v3/smtp/email", {
    method: "POST",
    headers: {
      "api-key": BREVO_API_KEY,
      "Content-Type": "application/json",
      "Accept": "application/json",
    },
    body: JSON.stringify({
      sender: { email: BREVO_SENDER_EMAIL, name: "SquadUp" },
      to: [{ email }],
      subject: "Código de recuperação de password - SquadUp",
      htmlContent: `
        <div style="font-family: sans-serif; padding: 24px;">
          <h2 style="color: #FF6B00;">SquadUp</h2>
          <p>Recebemos um pedido para repor a password da tua conta.</p>
          <p>O teu código de recuperação é:</p>
          <p style="font-size: 32px; font-weight: bold; letter-spacing: 4px;">${code}</p>
          <p>Este código expira em 15 minutos. Se não pediste esta recuperação, ignora este email.</p>
        </div>
      `,
    }),
  });

  if (!response.ok) {
    const errorBody = await response.text();
    throw new Error(`Brevo error (${response.status}): ${errorBody}`);
  }
}

Deno.serve(async (req: Request) => {
  if (req.method !== "POST") {
    return new Response(JSON.stringify({ success: false, error: "method_not_allowed" }), {
      status: 405,
      headers: jsonHeaders,
    });
  }

  try {
    const { email } = await req.json();

    if (!email || typeof email !== "string") {
      return new Response(JSON.stringify({ success: false, error: "invalid_request" }), {
        status: 400,
        headers: jsonHeaders,
      });
    }

    const { data: user, error: userError } = await supabaseAdmin
      .from("utilizador")
      .select("id")
      .eq("email", email)
      .maybeSingle();

    if (userError) throw userError;

    // Always respond with success, even if the email isn't registered,
    // so this endpoint can't be used to enumerate accounts.
    if (user) {
      await supabaseAdmin
        .from("token_recuperacao")
        .delete()
        .eq("user_id", user.id);

      let inserted = false;
      let lastError: unknown = null;

      for (let attempt = 0; attempt < 3 && !inserted; attempt++) {
        const code = generateCode();
        const expiresAt = new Date(Date.now() + 15 * 60 * 1000).toISOString();

        const { error: insertError } = await supabaseAdmin
          .from("token_recuperacao")
          .insert({
            user_id: user.id,
            token: code,
            data_expiracao: expiresAt,
            usado: false,
          });

        if (!insertError) {
          inserted = true;
          await sendResetEmail(email, code);
        } else {
          lastError = insertError;
        }
      }

      if (!inserted) {
        throw lastError ?? new Error("could_not_generate_code");
      }
    }

    return new Response(JSON.stringify({ success: true }), {
      status: 200,
      headers: jsonHeaders,
    });
  } catch (error) {
    console.error("request-password-reset error:", error);
    return new Response(JSON.stringify({ success: false, error: "internal_error" }), {
      status: 500,
      headers: jsonHeaders,
    });
  }
});
