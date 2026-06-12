import { createClient } from "jsr:@supabase/supabase-js@2";

const supabaseAdmin = createClient(
  Deno.env.get("SUPABASE_URL")!,
  Deno.env.get("SUPABASE_SERVICE_ROLE_KEY")!,
);

const jsonHeaders = { "Content-Type": "application/json" };

Deno.serve(async (req: Request) => {
  if (req.method !== "POST") {
    return new Response(JSON.stringify({ success: false, error: "method_not_allowed" }), {
      status: 405,
      headers: jsonHeaders,
    });
  }

  try {
    const { email, token, newPassword } = await req.json();

    if (!email || typeof email !== "string" || !token || typeof token !== "string" ||
      !newPassword || typeof newPassword !== "string") {
      return new Response(JSON.stringify({ success: false, error: "invalid_request" }), {
        status: 400,
        headers: jsonHeaders,
      });
    }

    if (newPassword.length < 6) {
      return new Response(JSON.stringify({ success: false, error: "weak_password" }), {
        status: 400,
        headers: jsonHeaders,
      });
    }

    const { data: user, error: userError } = await supabaseAdmin
      .from("utilizador")
      .select("id, auth_user_id")
      .eq("email", email)
      .maybeSingle();

    if (userError) throw userError;

    if (!user || !user.auth_user_id) {
      return new Response(JSON.stringify({ success: false, error: "invalid_or_expired_code" }), {
        status: 400,
        headers: jsonHeaders,
      });
    }

    const { data: tokenRow, error: tokenError } = await supabaseAdmin
      .from("token_recuperacao")
      .select("id, data_expiracao")
      .eq("user_id", user.id)
      .eq("token", token)
      .eq("usado", false)
      .maybeSingle();

    if (tokenError) throw tokenError;

    if (!tokenRow || new Date(tokenRow.data_expiracao) < new Date()) {
      return new Response(JSON.stringify({ success: false, error: "invalid_or_expired_code" }), {
        status: 400,
        headers: jsonHeaders,
      });
    }

    const { error: updateError } = await supabaseAdmin.auth.admin.updateUserById(
      user.auth_user_id,
      { password: newPassword },
    );

    if (updateError) throw updateError;

    await supabaseAdmin
      .from("token_recuperacao")
      .update({ usado: true })
      .eq("id", tokenRow.id);

    return new Response(JSON.stringify({ success: true }), {
      status: 200,
      headers: jsonHeaders,
    });
  } catch (error) {
    console.error("confirm-password-reset error:", error);
    return new Response(JSON.stringify({ success: false, error: "internal_error" }), {
      status: 500,
      headers: jsonHeaders,
    });
  }
});
