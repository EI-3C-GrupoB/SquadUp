


SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;


COMMENT ON SCHEMA "public" IS 'standard public schema';



CREATE EXTENSION IF NOT EXISTS "pg_stat_statements" WITH SCHEMA "extensions";






CREATE EXTENSION IF NOT EXISTS "pgcrypto" WITH SCHEMA "extensions";






CREATE EXTENSION IF NOT EXISTS "supabase_vault" WITH SCHEMA "vault";






CREATE EXTENSION IF NOT EXISTS "uuid-ossp" WITH SCHEMA "extensions";






CREATE TYPE "public"."estado_bilhete_enum" AS ENUM (
    'ativo',
    'usado',
    'cancelado'
);


ALTER TYPE "public"."estado_bilhete_enum" OWNER TO "postgres";


CREATE TYPE "public"."estado_conta_enum" AS ENUM (
    'ativo',
    'suspenso',
    'banido'
);


ALTER TYPE "public"."estado_conta_enum" OWNER TO "postgres";


CREATE TYPE "public"."estado_convite_enum" AS ENUM (
    'pendente',
    'aceite',
    'recusado'
);


ALTER TYPE "public"."estado_convite_enum" OWNER TO "postgres";


CREATE TYPE "public"."estado_evento_enum" AS ENUM (
    'rascunho',
    'publicado',
    'a_decorrer',
    'terminado',
    'cancelado'
);


ALTER TYPE "public"."estado_evento_enum" OWNER TO "postgres";


CREATE TYPE "public"."estado_inscricao_enum" AS ENUM (
    'pendente',
    'aceite',
    'recusado',
    'convidado',
    'banido'
);


ALTER TYPE "public"."estado_inscricao_enum" OWNER TO "postgres";


CREATE TYPE "public"."estado_jogo_enum" AS ENUM (
    'agendado',
    'a_decorrer',
    'intervalo',
    'terminado',
    'cancelado'
);


ALTER TYPE "public"."estado_jogo_enum" OWNER TO "postgres";


CREATE TYPE "public"."estado_pagamento_enum" AS ENUM (
    'pendente',
    'pago',
    'falhado',
    'reembolsado'
);


ALTER TYPE "public"."estado_pagamento_enum" OWNER TO "postgres";


CREATE TYPE "public"."metodo_pagamento_enum" AS ENUM (
    'cartao',
    'paypal',
    'mbway',
    'transferencia'
);


ALTER TYPE "public"."metodo_pagamento_enum" OWNER TO "postgres";


CREATE TYPE "public"."role_inscricao_enum" AS ENUM (
    'admin',
    'membro',
    'capitao'
);


ALTER TYPE "public"."role_inscricao_enum" OWNER TO "postgres";


CREATE TYPE "public"."tipo_convite_enum" AS ENUM (
    'convite',
    'pedido_adesao'
);


ALTER TYPE "public"."tipo_convite_enum" OWNER TO "postgres";


CREATE TYPE "public"."tipo_evento_enum" AS ENUM (
    'torneio',
    'liga',
    'jogo_amigavel',
    'treino',
    'outro'
);


ALTER TYPE "public"."tipo_evento_enum" OWNER TO "postgres";


CREATE TYPE "public"."tipo_notif_enum" AS ENUM (
    'jogo',
    'evento',
    'equipa',
    'sistema',
    'mensagem'
);


ALTER TYPE "public"."tipo_notif_enum" OWNER TO "postgres";

SET default_tablespace = '';

SET default_table_access_method = "heap";


CREATE TABLE IF NOT EXISTS "public"."bilhete" (
    "id" integer NOT NULL,
    "user_id" integer NOT NULL,
    "evento_id" integer NOT NULL,
    "codigo_qr" character varying(500) NOT NULL,
    "data_emissao" timestamp without time zone DEFAULT "now"(),
    "estado" "public"."estado_bilhete_enum" DEFAULT 'ativo'::"public"."estado_bilhete_enum",
    "is_partilhavel" boolean DEFAULT false
);


ALTER TABLE "public"."bilhete" OWNER TO "postgres";


CREATE SEQUENCE IF NOT EXISTS "public"."bilhete_id_seq"
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE "public"."bilhete_id_seq" OWNER TO "postgres";


ALTER SEQUENCE "public"."bilhete_id_seq" OWNED BY "public"."bilhete"."id";



CREATE TABLE IF NOT EXISTS "public"."convite" (
    "id" integer NOT NULL,
    "equipa_id" integer NOT NULL,
    "convidado_user_id" integer NOT NULL,
    "convidador_user_id" integer,
    "estado" "public"."estado_convite_enum" DEFAULT 'pendente'::"public"."estado_convite_enum",
    "tipo" "public"."tipo_convite_enum" DEFAULT 'convite'::"public"."tipo_convite_enum",
    "data_envio" timestamp without time zone DEFAULT "now"(),
    "data_resposta" timestamp without time zone
);


ALTER TABLE "public"."convite" OWNER TO "postgres";


CREATE SEQUENCE IF NOT EXISTS "public"."convite_id_seq"
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE "public"."convite_id_seq" OWNER TO "postgres";


ALTER SEQUENCE "public"."convite_id_seq" OWNED BY "public"."convite"."id";



CREATE TABLE IF NOT EXISTS "public"."equipa" (
    "id" integer NOT NULL,
    "nome" character varying(255) NOT NULL,
    "cor_principal" character varying(20),
    "cor_secundaria" character varying(20),
    "emblema" character varying(500),
    "codigo_convite" character varying(50),
    "data_criacao" timestamp without time zone DEFAULT "now"(),
    "user_id" integer
);


ALTER TABLE "public"."equipa" OWNER TO "postgres";


CREATE SEQUENCE IF NOT EXISTS "public"."equipa_id_seq"
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE "public"."equipa_id_seq" OWNER TO "postgres";


ALTER SEQUENCE "public"."equipa_id_seq" OWNED BY "public"."equipa"."id";



CREATE TABLE IF NOT EXISTS "public"."estatistica_jogador" (
    "id" integer NOT NULL,
    "user_id" integer NOT NULL,
    "modalidade_id" integer,
    "total_jogos" integer DEFAULT 0,
    "total_golos" integer DEFAULT 0,
    "total_assistencias" integer DEFAULT 0,
    "total_equipas" integer DEFAULT 0
);


ALTER TABLE "public"."estatistica_jogador" OWNER TO "postgres";


CREATE SEQUENCE IF NOT EXISTS "public"."estatistica_jogador_id_seq"
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE "public"."estatistica_jogador_id_seq" OWNER TO "postgres";


ALTER SEQUENCE "public"."estatistica_jogador_id_seq" OWNED BY "public"."estatistica_jogador"."id";



CREATE TABLE IF NOT EXISTS "public"."estatistica_jogo" (
    "id" integer NOT NULL,
    "jogo_id" integer NOT NULL,
    "equipa_id" integer NOT NULL,
    "posse_bola" numeric(5,2),
    "remates_total" integer DEFAULT 0,
    "remates_baliza" integer DEFAULT 0,
    "faltas" integer DEFAULT 0,
    "cantos" integer DEFAULT 0,
    "cartoes_amarelos" integer DEFAULT 0,
    "cartoes_vermelhos" integer DEFAULT 0,
    "fora_de_jogo" integer DEFAULT 0,
    "defesas" integer DEFAULT 0
);


ALTER TABLE "public"."estatistica_jogo" OWNER TO "postgres";


CREATE SEQUENCE IF NOT EXISTS "public"."estatistica_jogo_id_seq"
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE "public"."estatistica_jogo_id_seq" OWNER TO "postgres";


ALTER SEQUENCE "public"."estatistica_jogo_id_seq" OWNED BY "public"."estatistica_jogo"."id";



CREATE TABLE IF NOT EXISTS "public"."evento" (
    "id" integer NOT NULL,
    "titulo" character varying(255) NOT NULL,
    "descricao" "text",
    "imagem_url" character varying(500),
    "limite_participacoes" integer,
    "max_equipas" integer,
    "taxa_inscricao" numeric(10,2) DEFAULT 0,
    "preco" numeric(10,2) DEFAULT 0,
    "moeda" character varying(10) DEFAULT 'EUR'::character varying,
    "longitude" numeric(11,8),
    "latitude" numeric(10,8),
    "morada" character varying(500),
    "is_private" boolean DEFAULT false,
    "codigo_acesso" character varying(100),
    "data_inicio" timestamp without time zone,
    "data_fim" timestamp without time zone,
    "data_ini_inscricao" timestamp without time zone,
    "data_fim_inscricao" timestamp without time zone,
    "data_criacao" timestamp without time zone DEFAULT "now"(),
    "tipo_evento" "public"."tipo_evento_enum",
    "estado_evento" "public"."estado_evento_enum" DEFAULT 'rascunho'::"public"."estado_evento_enum",
    "regras" "text",
    "criador_id" integer,
    "modalidade_id" integer,
    "formato_id" integer
);


ALTER TABLE "public"."evento" OWNER TO "postgres";


CREATE SEQUENCE IF NOT EXISTS "public"."evento_id_seq"
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE "public"."evento_id_seq" OWNER TO "postgres";


ALTER SEQUENCE "public"."evento_id_seq" OWNED BY "public"."evento"."id";



CREATE TABLE IF NOT EXISTS "public"."formato" (
    "id" integer NOT NULL,
    "nome" character varying(100) NOT NULL,
    "descricao" "text"
);


ALTER TABLE "public"."formato" OWNER TO "postgres";


CREATE SEQUENCE IF NOT EXISTS "public"."formato_id_seq"
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE "public"."formato_id_seq" OWNER TO "postgres";


ALTER SEQUENCE "public"."formato_id_seq" OWNED BY "public"."formato"."id";



CREATE TABLE IF NOT EXISTS "public"."inscricao" (
    "id" integer NOT NULL,
    "data_inscricao" timestamp without time zone DEFAULT "now"(),
    "role" "public"."role_inscricao_enum" DEFAULT 'membro'::"public"."role_inscricao_enum",
    "estado_inscricao" "public"."estado_inscricao_enum" DEFAULT 'pendente'::"public"."estado_inscricao_enum",
    "is_capitao" boolean DEFAULT false,
    "equipa_id" integer,
    "evento_id" integer,
    "user_id" integer
);


ALTER TABLE "public"."inscricao" OWNER TO "postgres";


CREATE SEQUENCE IF NOT EXISTS "public"."inscricao_id_seq"
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE "public"."inscricao_id_seq" OWNER TO "postgres";


ALTER SEQUENCE "public"."inscricao_id_seq" OWNED BY "public"."inscricao"."id";



CREATE TABLE IF NOT EXISTS "public"."jogo" (
    "id" integer NOT NULL,
    "data_hora_prevista" timestamp without time zone,
    "data_hora_real" timestamp without time zone,
    "duracao_minutos" integer,
    "is_offline" boolean DEFAULT false,
    "is_sincronizado" boolean DEFAULT false,
    "estado_jogo" "public"."estado_jogo_enum" DEFAULT 'agendado'::"public"."estado_jogo_enum",
    "longitude" numeric(11,8),
    "latitude" numeric(10,8),
    "morada" character varying(500),
    "tem_intervalo" boolean DEFAULT false,
    "data_hora_intervalo" timestamp without time zone,
    "assistencia" integer DEFAULT 0,
    "evento_id" integer
);


ALTER TABLE "public"."jogo" OWNER TO "postgres";


CREATE TABLE IF NOT EXISTS "public"."jogo_equipa" (
    "equipa_id" integer NOT NULL,
    "jogo_id" integer NOT NULL,
    "resultado" character varying(50),
    "is_vencedor" boolean
);


ALTER TABLE "public"."jogo_equipa" OWNER TO "postgres";


CREATE SEQUENCE IF NOT EXISTS "public"."jogo_id_seq"
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE "public"."jogo_id_seq" OWNER TO "postgres";


ALTER SEQUENCE "public"."jogo_id_seq" OWNED BY "public"."jogo"."id";



CREATE TABLE IF NOT EXISTS "public"."lineup" (
    "id" integer NOT NULL,
    "posicao" character varying(100),
    "numero_camisola" integer,
    "is_titular" boolean DEFAULT true,
    "equipa_id" integer,
    "user_id" integer,
    "jogo_id" integer
);


ALTER TABLE "public"."lineup" OWNER TO "postgres";


CREATE SEQUENCE IF NOT EXISTS "public"."lineup_id_seq"
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE "public"."lineup_id_seq" OWNER TO "postgres";


ALTER SEQUENCE "public"."lineup_id_seq" OWNED BY "public"."lineup"."id";



CREATE TABLE IF NOT EXISTS "public"."mensagem" (
    "id" integer NOT NULL,
    "conteudo" "text" NOT NULL,
    "data_hora" timestamp without time zone DEFAULT "now"(),
    "is_lida" boolean DEFAULT false,
    "user_id" integer,
    "equipa_id" integer
);


ALTER TABLE "public"."mensagem" OWNER TO "postgres";


CREATE SEQUENCE IF NOT EXISTS "public"."mensagem_id_seq"
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE "public"."mensagem_id_seq" OWNER TO "postgres";


ALTER SEQUENCE "public"."mensagem_id_seq" OWNED BY "public"."mensagem"."id";



CREATE TABLE IF NOT EXISTS "public"."modalidade" (
    "id" integer NOT NULL,
    "nome" character varying(100) NOT NULL,
    "icon" character varying(255)
);


ALTER TABLE "public"."modalidade" OWNER TO "postgres";


CREATE SEQUENCE IF NOT EXISTS "public"."modalidade_id_seq"
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE "public"."modalidade_id_seq" OWNER TO "postgres";


ALTER SEQUENCE "public"."modalidade_id_seq" OWNED BY "public"."modalidade"."id";



CREATE TABLE IF NOT EXISTS "public"."notificacao" (
    "id" integer NOT NULL,
    "user_id" integer NOT NULL,
    "titulo" character varying(255) NOT NULL,
    "descricao" "text",
    "imagem_url" character varying(500),
    "is_lida" boolean DEFAULT false,
    "data_criacao" timestamp without time zone DEFAULT "now"(),
    "tipo" "public"."tipo_notif_enum",
    "referencia_id" integer,
    "referencia_tipo" character varying(100)
);


ALTER TABLE "public"."notificacao" OWNER TO "postgres";


CREATE SEQUENCE IF NOT EXISTS "public"."notificacao_id_seq"
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE "public"."notificacao_id_seq" OWNER TO "postgres";


ALTER SEQUENCE "public"."notificacao_id_seq" OWNED BY "public"."notificacao"."id";



CREATE TABLE IF NOT EXISTS "public"."pagamento" (
    "id" integer NOT NULL,
    "user_id" integer,
    "inscricao_id" integer,
    "valor" numeric(10,2) NOT NULL,
    "metodo_pagamento" "public"."metodo_pagamento_enum",
    "estado_pagamento" "public"."estado_pagamento_enum" DEFAULT 'pendente'::"public"."estado_pagamento_enum",
    "data_pagamento" timestamp without time zone DEFAULT "now"(),
    "referencia_externa" character varying(500)
);


ALTER TABLE "public"."pagamento" OWNER TO "postgres";


CREATE SEQUENCE IF NOT EXISTS "public"."pagamento_id_seq"
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE "public"."pagamento_id_seq" OWNER TO "postgres";


ALTER SEQUENCE "public"."pagamento_id_seq" OWNED BY "public"."pagamento"."id";



CREATE TABLE IF NOT EXISTS "public"."registo_timeline" (
    "id" integer NOT NULL,
    "minutos_jogo" integer,
    "descricao" "text",
    "isoffline" boolean DEFAULT false,
    "issincronizado" boolean DEFAULT false,
    "equipa_id" integer,
    "user_id" integer,
    "user_id_secundario" integer,
    "numero_camisola_novo" integer,
    "posicao_nova" character varying(100),
    "tipo_acao_id" integer,
    "jogo_id" integer
);


ALTER TABLE "public"."registo_timeline" OWNER TO "postgres";


CREATE SEQUENCE IF NOT EXISTS "public"."registo_timeline_id_seq"
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE "public"."registo_timeline_id_seq" OWNER TO "postgres";


ALTER SEQUENCE "public"."registo_timeline_id_seq" OWNED BY "public"."registo_timeline"."id";



CREATE TABLE IF NOT EXISTS "public"."tipo_acao" (
    "id" integer NOT NULL,
    "nome" character varying(100) NOT NULL,
    "modalidade_id" integer
);


ALTER TABLE "public"."tipo_acao" OWNER TO "postgres";


CREATE SEQUENCE IF NOT EXISTS "public"."tipo_acao_id_seq"
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE "public"."tipo_acao_id_seq" OWNER TO "postgres";


ALTER SEQUENCE "public"."tipo_acao_id_seq" OWNED BY "public"."tipo_acao"."id";



CREATE TABLE IF NOT EXISTS "public"."tipo_utilizador" (
    "id" integer NOT NULL,
    "tipo" character varying(100) NOT NULL
);


ALTER TABLE "public"."tipo_utilizador" OWNER TO "postgres";


CREATE SEQUENCE IF NOT EXISTS "public"."tipo_utilizador_id_seq"
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE "public"."tipo_utilizador_id_seq" OWNER TO "postgres";


ALTER SEQUENCE "public"."tipo_utilizador_id_seq" OWNED BY "public"."tipo_utilizador"."id";



CREATE TABLE IF NOT EXISTS "public"."token_recuperacao" (
    "id" integer NOT NULL,
    "user_id" integer NOT NULL,
    "token" character varying(500) NOT NULL,
    "data_expiracao" timestamp without time zone NOT NULL,
    "usado" boolean DEFAULT false
);


ALTER TABLE "public"."token_recuperacao" OWNER TO "postgres";


CREATE SEQUENCE IF NOT EXISTS "public"."token_recuperacao_id_seq"
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE "public"."token_recuperacao_id_seq" OWNER TO "postgres";


ALTER SEQUENCE "public"."token_recuperacao_id_seq" OWNED BY "public"."token_recuperacao"."id";



CREATE TABLE IF NOT EXISTS "public"."utilizador" (
    "id" integer NOT NULL,
    "nome" character varying(255) NOT NULL,
    "username" character varying(100) NOT NULL,
    "email" character varying(255) NOT NULL,
    "foto_avatar" character varying(500),
    "nivel_experiencia" integer DEFAULT 1,
    "data_nascimento" "date",
    "bio" "text",
    "play_style" character varying(100),
    "play_intensity" character varying(100),
    "data_registo" timestamp without time zone DEFAULT "now"(),
    "ultimo_login" timestamp without time zone,
    "is_admin_plataforma" boolean DEFAULT false,
    "estado_conta" "public"."estado_conta_enum" DEFAULT 'ativo'::"public"."estado_conta_enum",
    "auth_user_id" "uuid"
);


ALTER TABLE "public"."utilizador" OWNER TO "postgres";


CREATE SEQUENCE IF NOT EXISTS "public"."utilizador_id_seq"
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE "public"."utilizador_id_seq" OWNER TO "postgres";


ALTER SEQUENCE "public"."utilizador_id_seq" OWNED BY "public"."utilizador"."id";



CREATE TABLE IF NOT EXISTS "public"."utilizador_modalidade" (
    "user_id" integer NOT NULL,
    "modalidade_id" integer NOT NULL
);


ALTER TABLE "public"."utilizador_modalidade" OWNER TO "postgres";


CREATE TABLE IF NOT EXISTS "public"."utilizador_tipoutilizador" (
    "tipo_utilizador_id" integer NOT NULL,
    "user_id" integer NOT NULL,
    "raio_notificacao" numeric(10,2)
);


ALTER TABLE "public"."utilizador_tipoutilizador" OWNER TO "postgres";


ALTER TABLE ONLY "public"."bilhete" ALTER COLUMN "id" SET DEFAULT "nextval"('"public"."bilhete_id_seq"'::"regclass");



ALTER TABLE ONLY "public"."convite" ALTER COLUMN "id" SET DEFAULT "nextval"('"public"."convite_id_seq"'::"regclass");



ALTER TABLE ONLY "public"."equipa" ALTER COLUMN "id" SET DEFAULT "nextval"('"public"."equipa_id_seq"'::"regclass");



ALTER TABLE ONLY "public"."estatistica_jogador" ALTER COLUMN "id" SET DEFAULT "nextval"('"public"."estatistica_jogador_id_seq"'::"regclass");



ALTER TABLE ONLY "public"."estatistica_jogo" ALTER COLUMN "id" SET DEFAULT "nextval"('"public"."estatistica_jogo_id_seq"'::"regclass");



ALTER TABLE ONLY "public"."evento" ALTER COLUMN "id" SET DEFAULT "nextval"('"public"."evento_id_seq"'::"regclass");



ALTER TABLE ONLY "public"."formato" ALTER COLUMN "id" SET DEFAULT "nextval"('"public"."formato_id_seq"'::"regclass");



ALTER TABLE ONLY "public"."inscricao" ALTER COLUMN "id" SET DEFAULT "nextval"('"public"."inscricao_id_seq"'::"regclass");



ALTER TABLE ONLY "public"."jogo" ALTER COLUMN "id" SET DEFAULT "nextval"('"public"."jogo_id_seq"'::"regclass");



ALTER TABLE ONLY "public"."lineup" ALTER COLUMN "id" SET DEFAULT "nextval"('"public"."lineup_id_seq"'::"regclass");



ALTER TABLE ONLY "public"."mensagem" ALTER COLUMN "id" SET DEFAULT "nextval"('"public"."mensagem_id_seq"'::"regclass");



ALTER TABLE ONLY "public"."modalidade" ALTER COLUMN "id" SET DEFAULT "nextval"('"public"."modalidade_id_seq"'::"regclass");



ALTER TABLE ONLY "public"."notificacao" ALTER COLUMN "id" SET DEFAULT "nextval"('"public"."notificacao_id_seq"'::"regclass");



ALTER TABLE ONLY "public"."pagamento" ALTER COLUMN "id" SET DEFAULT "nextval"('"public"."pagamento_id_seq"'::"regclass");



ALTER TABLE ONLY "public"."registo_timeline" ALTER COLUMN "id" SET DEFAULT "nextval"('"public"."registo_timeline_id_seq"'::"regclass");



ALTER TABLE ONLY "public"."tipo_acao" ALTER COLUMN "id" SET DEFAULT "nextval"('"public"."tipo_acao_id_seq"'::"regclass");



ALTER TABLE ONLY "public"."tipo_utilizador" ALTER COLUMN "id" SET DEFAULT "nextval"('"public"."tipo_utilizador_id_seq"'::"regclass");



ALTER TABLE ONLY "public"."token_recuperacao" ALTER COLUMN "id" SET DEFAULT "nextval"('"public"."token_recuperacao_id_seq"'::"regclass");



ALTER TABLE ONLY "public"."utilizador" ALTER COLUMN "id" SET DEFAULT "nextval"('"public"."utilizador_id_seq"'::"regclass");



ALTER TABLE ONLY "public"."bilhete"
    ADD CONSTRAINT "bilhete_codigo_qr_key" UNIQUE ("codigo_qr");



ALTER TABLE ONLY "public"."bilhete"
    ADD CONSTRAINT "bilhete_pkey" PRIMARY KEY ("id");



ALTER TABLE ONLY "public"."convite"
    ADD CONSTRAINT "convite_pkey" PRIMARY KEY ("id");



ALTER TABLE ONLY "public"."equipa"
    ADD CONSTRAINT "equipa_codigo_convite_key" UNIQUE ("codigo_convite");



ALTER TABLE ONLY "public"."equipa"
    ADD CONSTRAINT "equipa_pkey" PRIMARY KEY ("id");



ALTER TABLE ONLY "public"."estatistica_jogador"
    ADD CONSTRAINT "estatistica_jogador_pkey" PRIMARY KEY ("id");



ALTER TABLE ONLY "public"."estatistica_jogador"
    ADD CONSTRAINT "estatistica_jogador_user_id_modalidade_id_key" UNIQUE ("user_id", "modalidade_id");



ALTER TABLE ONLY "public"."estatistica_jogo"
    ADD CONSTRAINT "estatistica_jogo_jogo_id_equipa_id_key" UNIQUE ("jogo_id", "equipa_id");



ALTER TABLE ONLY "public"."estatistica_jogo"
    ADD CONSTRAINT "estatistica_jogo_pkey" PRIMARY KEY ("id");



ALTER TABLE ONLY "public"."evento"
    ADD CONSTRAINT "evento_pkey" PRIMARY KEY ("id");



ALTER TABLE ONLY "public"."formato"
    ADD CONSTRAINT "formato_pkey" PRIMARY KEY ("id");



ALTER TABLE ONLY "public"."inscricao"
    ADD CONSTRAINT "inscricao_pkey" PRIMARY KEY ("id");



ALTER TABLE ONLY "public"."jogo_equipa"
    ADD CONSTRAINT "jogo_equipa_pkey" PRIMARY KEY ("equipa_id", "jogo_id");



ALTER TABLE ONLY "public"."jogo"
    ADD CONSTRAINT "jogo_pkey" PRIMARY KEY ("id");



ALTER TABLE ONLY "public"."lineup"
    ADD CONSTRAINT "lineup_pkey" PRIMARY KEY ("id");



ALTER TABLE ONLY "public"."mensagem"
    ADD CONSTRAINT "mensagem_pkey" PRIMARY KEY ("id");



ALTER TABLE ONLY "public"."modalidade"
    ADD CONSTRAINT "modalidade_pkey" PRIMARY KEY ("id");



ALTER TABLE ONLY "public"."notificacao"
    ADD CONSTRAINT "notificacao_pkey" PRIMARY KEY ("id");



ALTER TABLE ONLY "public"."pagamento"
    ADD CONSTRAINT "pagamento_pkey" PRIMARY KEY ("id");



ALTER TABLE ONLY "public"."registo_timeline"
    ADD CONSTRAINT "registo_timeline_pkey" PRIMARY KEY ("id");



ALTER TABLE ONLY "public"."tipo_acao"
    ADD CONSTRAINT "tipo_acao_pkey" PRIMARY KEY ("id");



ALTER TABLE ONLY "public"."tipo_utilizador"
    ADD CONSTRAINT "tipo_utilizador_pkey" PRIMARY KEY ("id");



ALTER TABLE ONLY "public"."token_recuperacao"
    ADD CONSTRAINT "token_recuperacao_pkey" PRIMARY KEY ("id");



ALTER TABLE ONLY "public"."token_recuperacao"
    ADD CONSTRAINT "token_recuperacao_token_key" UNIQUE ("token");



ALTER TABLE ONLY "public"."utilizador"
    ADD CONSTRAINT "utilizador_auth_user_id_unique" UNIQUE ("auth_user_id");



ALTER TABLE ONLY "public"."utilizador"
    ADD CONSTRAINT "utilizador_email_key" UNIQUE ("email");



ALTER TABLE ONLY "public"."utilizador_modalidade"
    ADD CONSTRAINT "utilizador_modalidade_pkey" PRIMARY KEY ("user_id", "modalidade_id");



ALTER TABLE ONLY "public"."utilizador"
    ADD CONSTRAINT "utilizador_pkey" PRIMARY KEY ("id");



ALTER TABLE ONLY "public"."utilizador_tipoutilizador"
    ADD CONSTRAINT "utilizador_tipoutilizador_pkey" PRIMARY KEY ("tipo_utilizador_id", "user_id");



ALTER TABLE ONLY "public"."utilizador"
    ADD CONSTRAINT "utilizador_username_key" UNIQUE ("username");



CREATE INDEX "idx_bilhete_evento" ON "public"."bilhete" USING "btree" ("evento_id");



CREATE INDEX "idx_bilhete_user" ON "public"."bilhete" USING "btree" ("user_id");



CREATE INDEX "idx_convite_equipa" ON "public"."convite" USING "btree" ("equipa_id");



CREATE INDEX "idx_estatistica_jogador" ON "public"."estatistica_jogador" USING "btree" ("user_id");



CREATE INDEX "idx_estatistica_jogo" ON "public"."estatistica_jogo" USING "btree" ("jogo_id");



CREATE INDEX "idx_inscricao_evento" ON "public"."inscricao" USING "btree" ("evento_id");



CREATE INDEX "idx_inscricao_user" ON "public"."inscricao" USING "btree" ("user_id");



CREATE INDEX "idx_jogo_evento" ON "public"."jogo" USING "btree" ("evento_id");



CREATE INDEX "idx_lineup_jogo" ON "public"."lineup" USING "btree" ("jogo_id");



CREATE INDEX "idx_mensagem_equipa" ON "public"."mensagem" USING "btree" ("equipa_id");



CREATE INDEX "idx_notificacao_user" ON "public"."notificacao" USING "btree" ("user_id");



CREATE INDEX "idx_timeline_jogo" ON "public"."registo_timeline" USING "btree" ("jogo_id");



ALTER TABLE ONLY "public"."bilhete"
    ADD CONSTRAINT "bilhete_evento_id_fkey" FOREIGN KEY ("evento_id") REFERENCES "public"."evento"("id") ON DELETE CASCADE;



ALTER TABLE ONLY "public"."bilhete"
    ADD CONSTRAINT "bilhete_user_id_fkey" FOREIGN KEY ("user_id") REFERENCES "public"."utilizador"("id") ON DELETE CASCADE;



ALTER TABLE ONLY "public"."convite"
    ADD CONSTRAINT "convite_convidado_user_id_fkey" FOREIGN KEY ("convidado_user_id") REFERENCES "public"."utilizador"("id") ON DELETE CASCADE;



ALTER TABLE ONLY "public"."convite"
    ADD CONSTRAINT "convite_convidador_user_id_fkey" FOREIGN KEY ("convidador_user_id") REFERENCES "public"."utilizador"("id") ON DELETE SET NULL;



ALTER TABLE ONLY "public"."convite"
    ADD CONSTRAINT "convite_equipa_id_fkey" FOREIGN KEY ("equipa_id") REFERENCES "public"."equipa"("id") ON DELETE CASCADE;



ALTER TABLE ONLY "public"."equipa"
    ADD CONSTRAINT "equipa_user_id_fkey" FOREIGN KEY ("user_id") REFERENCES "public"."utilizador"("id") ON DELETE SET NULL;



ALTER TABLE ONLY "public"."estatistica_jogador"
    ADD CONSTRAINT "estatistica_jogador_modalidade_id_fkey" FOREIGN KEY ("modalidade_id") REFERENCES "public"."modalidade"("id") ON DELETE SET NULL;



ALTER TABLE ONLY "public"."estatistica_jogador"
    ADD CONSTRAINT "estatistica_jogador_user_id_fkey" FOREIGN KEY ("user_id") REFERENCES "public"."utilizador"("id") ON DELETE CASCADE;



ALTER TABLE ONLY "public"."estatistica_jogo"
    ADD CONSTRAINT "estatistica_jogo_equipa_id_fkey" FOREIGN KEY ("equipa_id") REFERENCES "public"."equipa"("id") ON DELETE CASCADE;



ALTER TABLE ONLY "public"."estatistica_jogo"
    ADD CONSTRAINT "estatistica_jogo_jogo_id_fkey" FOREIGN KEY ("jogo_id") REFERENCES "public"."jogo"("id") ON DELETE CASCADE;



ALTER TABLE ONLY "public"."evento"
    ADD CONSTRAINT "evento_criador_id_fkey" FOREIGN KEY ("criador_id") REFERENCES "public"."utilizador"("id") ON DELETE SET NULL;



ALTER TABLE ONLY "public"."evento"
    ADD CONSTRAINT "evento_formato_id_fkey" FOREIGN KEY ("formato_id") REFERENCES "public"."formato"("id") ON DELETE SET NULL;



ALTER TABLE ONLY "public"."evento"
    ADD CONSTRAINT "evento_modalidade_id_fkey" FOREIGN KEY ("modalidade_id") REFERENCES "public"."modalidade"("id") ON DELETE SET NULL;



ALTER TABLE ONLY "public"."inscricao"
    ADD CONSTRAINT "inscricao_equipa_id_fkey" FOREIGN KEY ("equipa_id") REFERENCES "public"."equipa"("id") ON DELETE SET NULL;



ALTER TABLE ONLY "public"."inscricao"
    ADD CONSTRAINT "inscricao_evento_id_fkey" FOREIGN KEY ("evento_id") REFERENCES "public"."evento"("id") ON DELETE CASCADE;



ALTER TABLE ONLY "public"."inscricao"
    ADD CONSTRAINT "inscricao_user_id_fkey" FOREIGN KEY ("user_id") REFERENCES "public"."utilizador"("id") ON DELETE CASCADE;



ALTER TABLE ONLY "public"."jogo_equipa"
    ADD CONSTRAINT "jogo_equipa_equipa_id_fkey" FOREIGN KEY ("equipa_id") REFERENCES "public"."equipa"("id") ON DELETE CASCADE;



ALTER TABLE ONLY "public"."jogo_equipa"
    ADD CONSTRAINT "jogo_equipa_jogo_id_fkey" FOREIGN KEY ("jogo_id") REFERENCES "public"."jogo"("id") ON DELETE CASCADE;



ALTER TABLE ONLY "public"."jogo"
    ADD CONSTRAINT "jogo_evento_id_fkey" FOREIGN KEY ("evento_id") REFERENCES "public"."evento"("id") ON DELETE SET NULL;



ALTER TABLE ONLY "public"."lineup"
    ADD CONSTRAINT "lineup_equipa_id_fkey" FOREIGN KEY ("equipa_id") REFERENCES "public"."equipa"("id") ON DELETE CASCADE;



ALTER TABLE ONLY "public"."lineup"
    ADD CONSTRAINT "lineup_jogo_id_fkey" FOREIGN KEY ("jogo_id") REFERENCES "public"."jogo"("id") ON DELETE CASCADE;



ALTER TABLE ONLY "public"."lineup"
    ADD CONSTRAINT "lineup_user_id_fkey" FOREIGN KEY ("user_id") REFERENCES "public"."utilizador"("id") ON DELETE CASCADE;



ALTER TABLE ONLY "public"."mensagem"
    ADD CONSTRAINT "mensagem_equipa_id_fkey" FOREIGN KEY ("equipa_id") REFERENCES "public"."equipa"("id") ON DELETE CASCADE;



ALTER TABLE ONLY "public"."mensagem"
    ADD CONSTRAINT "mensagem_user_id_fkey" FOREIGN KEY ("user_id") REFERENCES "public"."utilizador"("id") ON DELETE SET NULL;



ALTER TABLE ONLY "public"."notificacao"
    ADD CONSTRAINT "notificacao_user_id_fkey" FOREIGN KEY ("user_id") REFERENCES "public"."utilizador"("id") ON DELETE CASCADE;



ALTER TABLE ONLY "public"."pagamento"
    ADD CONSTRAINT "pagamento_inscricao_id_fkey" FOREIGN KEY ("inscricao_id") REFERENCES "public"."inscricao"("id") ON DELETE SET NULL;



ALTER TABLE ONLY "public"."pagamento"
    ADD CONSTRAINT "pagamento_user_id_fkey" FOREIGN KEY ("user_id") REFERENCES "public"."utilizador"("id") ON DELETE SET NULL;



ALTER TABLE ONLY "public"."registo_timeline"
    ADD CONSTRAINT "registo_timeline_equipa_id_fkey" FOREIGN KEY ("equipa_id") REFERENCES "public"."equipa"("id") ON DELETE SET NULL;



ALTER TABLE ONLY "public"."registo_timeline"
    ADD CONSTRAINT "registo_timeline_jogo_id_fkey" FOREIGN KEY ("jogo_id") REFERENCES "public"."jogo"("id") ON DELETE CASCADE;



ALTER TABLE ONLY "public"."registo_timeline"
    ADD CONSTRAINT "registo_timeline_tipo_acao_id_fkey" FOREIGN KEY ("tipo_acao_id") REFERENCES "public"."tipo_acao"("id") ON DELETE SET NULL;



ALTER TABLE ONLY "public"."registo_timeline"
    ADD CONSTRAINT "registo_timeline_user_id_fkey" FOREIGN KEY ("user_id") REFERENCES "public"."utilizador"("id") ON DELETE SET NULL;



ALTER TABLE ONLY "public"."registo_timeline"
    ADD CONSTRAINT "registo_timeline_user_id_secundario_fkey" FOREIGN KEY ("user_id_secundario") REFERENCES "public"."utilizador"("id") ON DELETE SET NULL;



ALTER TABLE ONLY "public"."tipo_acao"
    ADD CONSTRAINT "tipo_acao_modalidade_id_fkey" FOREIGN KEY ("modalidade_id") REFERENCES "public"."modalidade"("id") ON DELETE SET NULL;



ALTER TABLE ONLY "public"."token_recuperacao"
    ADD CONSTRAINT "token_recuperacao_user_id_fkey" FOREIGN KEY ("user_id") REFERENCES "public"."utilizador"("id") ON DELETE CASCADE;



ALTER TABLE ONLY "public"."utilizador"
    ADD CONSTRAINT "utilizador_auth_user_id_fkey" FOREIGN KEY ("auth_user_id") REFERENCES "auth"."users"("id") ON DELETE CASCADE;



ALTER TABLE ONLY "public"."utilizador_modalidade"
    ADD CONSTRAINT "utilizador_modalidade_modalidade_id_fkey" FOREIGN KEY ("modalidade_id") REFERENCES "public"."modalidade"("id") ON DELETE CASCADE;



ALTER TABLE ONLY "public"."utilizador_modalidade"
    ADD CONSTRAINT "utilizador_modalidade_user_id_fkey" FOREIGN KEY ("user_id") REFERENCES "public"."utilizador"("id") ON DELETE CASCADE;



ALTER TABLE ONLY "public"."utilizador_tipoutilizador"
    ADD CONSTRAINT "utilizador_tipoutilizador_tipo_utilizador_id_fkey" FOREIGN KEY ("tipo_utilizador_id") REFERENCES "public"."tipo_utilizador"("id") ON DELETE CASCADE;



ALTER TABLE ONLY "public"."utilizador_tipoutilizador"
    ADD CONSTRAINT "utilizador_tipoutilizador_user_id_fkey" FOREIGN KEY ("user_id") REFERENCES "public"."utilizador"("id") ON DELETE CASCADE;



CREATE POLICY "Utilizador pode actualizar o proprio perfil" ON "public"."utilizador" FOR UPDATE USING (("auth"."uid"() = "auth_user_id")) WITH CHECK (("auth"."uid"() = "auth_user_id"));



CREATE POLICY "Utilizador pode criar o proprio perfil" ON "public"."utilizador" FOR INSERT WITH CHECK (("auth"."uid"() = "auth_user_id"));



CREATE POLICY "Utilizador pode ler o proprio perfil" ON "public"."utilizador" FOR SELECT USING (("auth"."uid"() = "auth_user_id"));



ALTER TABLE "public"."utilizador" ENABLE ROW LEVEL SECURITY;




ALTER PUBLICATION "supabase_realtime" OWNER TO "postgres";


GRANT USAGE ON SCHEMA "public" TO "postgres";
GRANT USAGE ON SCHEMA "public" TO "anon";
GRANT USAGE ON SCHEMA "public" TO "authenticated";
GRANT USAGE ON SCHEMA "public" TO "service_role";





































































































































































GRANT ALL ON TABLE "public"."bilhete" TO "anon";
GRANT ALL ON TABLE "public"."bilhete" TO "authenticated";
GRANT ALL ON TABLE "public"."bilhete" TO "service_role";



GRANT ALL ON SEQUENCE "public"."bilhete_id_seq" TO "anon";
GRANT ALL ON SEQUENCE "public"."bilhete_id_seq" TO "authenticated";
GRANT ALL ON SEQUENCE "public"."bilhete_id_seq" TO "service_role";



GRANT ALL ON TABLE "public"."convite" TO "anon";
GRANT ALL ON TABLE "public"."convite" TO "authenticated";
GRANT ALL ON TABLE "public"."convite" TO "service_role";



GRANT ALL ON SEQUENCE "public"."convite_id_seq" TO "anon";
GRANT ALL ON SEQUENCE "public"."convite_id_seq" TO "authenticated";
GRANT ALL ON SEQUENCE "public"."convite_id_seq" TO "service_role";



GRANT ALL ON TABLE "public"."equipa" TO "anon";
GRANT ALL ON TABLE "public"."equipa" TO "authenticated";
GRANT ALL ON TABLE "public"."equipa" TO "service_role";



GRANT ALL ON SEQUENCE "public"."equipa_id_seq" TO "anon";
GRANT ALL ON SEQUENCE "public"."equipa_id_seq" TO "authenticated";
GRANT ALL ON SEQUENCE "public"."equipa_id_seq" TO "service_role";



GRANT ALL ON TABLE "public"."estatistica_jogador" TO "anon";
GRANT ALL ON TABLE "public"."estatistica_jogador" TO "authenticated";
GRANT ALL ON TABLE "public"."estatistica_jogador" TO "service_role";



GRANT ALL ON SEQUENCE "public"."estatistica_jogador_id_seq" TO "anon";
GRANT ALL ON SEQUENCE "public"."estatistica_jogador_id_seq" TO "authenticated";
GRANT ALL ON SEQUENCE "public"."estatistica_jogador_id_seq" TO "service_role";



GRANT ALL ON TABLE "public"."estatistica_jogo" TO "anon";
GRANT ALL ON TABLE "public"."estatistica_jogo" TO "authenticated";
GRANT ALL ON TABLE "public"."estatistica_jogo" TO "service_role";



GRANT ALL ON SEQUENCE "public"."estatistica_jogo_id_seq" TO "anon";
GRANT ALL ON SEQUENCE "public"."estatistica_jogo_id_seq" TO "authenticated";
GRANT ALL ON SEQUENCE "public"."estatistica_jogo_id_seq" TO "service_role";



GRANT ALL ON TABLE "public"."evento" TO "anon";
GRANT ALL ON TABLE "public"."evento" TO "authenticated";
GRANT ALL ON TABLE "public"."evento" TO "service_role";



GRANT ALL ON SEQUENCE "public"."evento_id_seq" TO "anon";
GRANT ALL ON SEQUENCE "public"."evento_id_seq" TO "authenticated";
GRANT ALL ON SEQUENCE "public"."evento_id_seq" TO "service_role";



GRANT ALL ON TABLE "public"."formato" TO "anon";
GRANT ALL ON TABLE "public"."formato" TO "authenticated";
GRANT ALL ON TABLE "public"."formato" TO "service_role";



GRANT ALL ON SEQUENCE "public"."formato_id_seq" TO "anon";
GRANT ALL ON SEQUENCE "public"."formato_id_seq" TO "authenticated";
GRANT ALL ON SEQUENCE "public"."formato_id_seq" TO "service_role";



GRANT ALL ON TABLE "public"."inscricao" TO "anon";
GRANT ALL ON TABLE "public"."inscricao" TO "authenticated";
GRANT ALL ON TABLE "public"."inscricao" TO "service_role";



GRANT ALL ON SEQUENCE "public"."inscricao_id_seq" TO "anon";
GRANT ALL ON SEQUENCE "public"."inscricao_id_seq" TO "authenticated";
GRANT ALL ON SEQUENCE "public"."inscricao_id_seq" TO "service_role";



GRANT ALL ON TABLE "public"."jogo" TO "anon";
GRANT ALL ON TABLE "public"."jogo" TO "authenticated";
GRANT ALL ON TABLE "public"."jogo" TO "service_role";



GRANT ALL ON TABLE "public"."jogo_equipa" TO "anon";
GRANT ALL ON TABLE "public"."jogo_equipa" TO "authenticated";
GRANT ALL ON TABLE "public"."jogo_equipa" TO "service_role";



GRANT ALL ON SEQUENCE "public"."jogo_id_seq" TO "anon";
GRANT ALL ON SEQUENCE "public"."jogo_id_seq" TO "authenticated";
GRANT ALL ON SEQUENCE "public"."jogo_id_seq" TO "service_role";



GRANT ALL ON TABLE "public"."lineup" TO "anon";
GRANT ALL ON TABLE "public"."lineup" TO "authenticated";
GRANT ALL ON TABLE "public"."lineup" TO "service_role";



GRANT ALL ON SEQUENCE "public"."lineup_id_seq" TO "anon";
GRANT ALL ON SEQUENCE "public"."lineup_id_seq" TO "authenticated";
GRANT ALL ON SEQUENCE "public"."lineup_id_seq" TO "service_role";



GRANT ALL ON TABLE "public"."mensagem" TO "anon";
GRANT ALL ON TABLE "public"."mensagem" TO "authenticated";
GRANT ALL ON TABLE "public"."mensagem" TO "service_role";



GRANT ALL ON SEQUENCE "public"."mensagem_id_seq" TO "anon";
GRANT ALL ON SEQUENCE "public"."mensagem_id_seq" TO "authenticated";
GRANT ALL ON SEQUENCE "public"."mensagem_id_seq" TO "service_role";



GRANT ALL ON TABLE "public"."modalidade" TO "anon";
GRANT ALL ON TABLE "public"."modalidade" TO "authenticated";
GRANT ALL ON TABLE "public"."modalidade" TO "service_role";



GRANT ALL ON SEQUENCE "public"."modalidade_id_seq" TO "anon";
GRANT ALL ON SEQUENCE "public"."modalidade_id_seq" TO "authenticated";
GRANT ALL ON SEQUENCE "public"."modalidade_id_seq" TO "service_role";



GRANT ALL ON TABLE "public"."notificacao" TO "anon";
GRANT ALL ON TABLE "public"."notificacao" TO "authenticated";
GRANT ALL ON TABLE "public"."notificacao" TO "service_role";



GRANT ALL ON SEQUENCE "public"."notificacao_id_seq" TO "anon";
GRANT ALL ON SEQUENCE "public"."notificacao_id_seq" TO "authenticated";
GRANT ALL ON SEQUENCE "public"."notificacao_id_seq" TO "service_role";



GRANT ALL ON TABLE "public"."pagamento" TO "anon";
GRANT ALL ON TABLE "public"."pagamento" TO "authenticated";
GRANT ALL ON TABLE "public"."pagamento" TO "service_role";



GRANT ALL ON SEQUENCE "public"."pagamento_id_seq" TO "anon";
GRANT ALL ON SEQUENCE "public"."pagamento_id_seq" TO "authenticated";
GRANT ALL ON SEQUENCE "public"."pagamento_id_seq" TO "service_role";



GRANT ALL ON TABLE "public"."registo_timeline" TO "anon";
GRANT ALL ON TABLE "public"."registo_timeline" TO "authenticated";
GRANT ALL ON TABLE "public"."registo_timeline" TO "service_role";



GRANT ALL ON SEQUENCE "public"."registo_timeline_id_seq" TO "anon";
GRANT ALL ON SEQUENCE "public"."registo_timeline_id_seq" TO "authenticated";
GRANT ALL ON SEQUENCE "public"."registo_timeline_id_seq" TO "service_role";



GRANT ALL ON TABLE "public"."tipo_acao" TO "anon";
GRANT ALL ON TABLE "public"."tipo_acao" TO "authenticated";
GRANT ALL ON TABLE "public"."tipo_acao" TO "service_role";



GRANT ALL ON SEQUENCE "public"."tipo_acao_id_seq" TO "anon";
GRANT ALL ON SEQUENCE "public"."tipo_acao_id_seq" TO "authenticated";
GRANT ALL ON SEQUENCE "public"."tipo_acao_id_seq" TO "service_role";



GRANT ALL ON TABLE "public"."tipo_utilizador" TO "anon";
GRANT ALL ON TABLE "public"."tipo_utilizador" TO "authenticated";
GRANT ALL ON TABLE "public"."tipo_utilizador" TO "service_role";



GRANT ALL ON SEQUENCE "public"."tipo_utilizador_id_seq" TO "anon";
GRANT ALL ON SEQUENCE "public"."tipo_utilizador_id_seq" TO "authenticated";
GRANT ALL ON SEQUENCE "public"."tipo_utilizador_id_seq" TO "service_role";



GRANT ALL ON TABLE "public"."token_recuperacao" TO "anon";
GRANT ALL ON TABLE "public"."token_recuperacao" TO "authenticated";
GRANT ALL ON TABLE "public"."token_recuperacao" TO "service_role";



GRANT ALL ON SEQUENCE "public"."token_recuperacao_id_seq" TO "anon";
GRANT ALL ON SEQUENCE "public"."token_recuperacao_id_seq" TO "authenticated";
GRANT ALL ON SEQUENCE "public"."token_recuperacao_id_seq" TO "service_role";



GRANT ALL ON TABLE "public"."utilizador" TO "anon";
GRANT ALL ON TABLE "public"."utilizador" TO "authenticated";
GRANT ALL ON TABLE "public"."utilizador" TO "service_role";



GRANT ALL ON SEQUENCE "public"."utilizador_id_seq" TO "anon";
GRANT ALL ON SEQUENCE "public"."utilizador_id_seq" TO "authenticated";
GRANT ALL ON SEQUENCE "public"."utilizador_id_seq" TO "service_role";



GRANT ALL ON TABLE "public"."utilizador_modalidade" TO "anon";
GRANT ALL ON TABLE "public"."utilizador_modalidade" TO "authenticated";
GRANT ALL ON TABLE "public"."utilizador_modalidade" TO "service_role";



GRANT ALL ON TABLE "public"."utilizador_tipoutilizador" TO "anon";
GRANT ALL ON TABLE "public"."utilizador_tipoutilizador" TO "authenticated";
GRANT ALL ON TABLE "public"."utilizador_tipoutilizador" TO "service_role";









ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" IN SCHEMA "public" GRANT ALL ON SEQUENCES TO "postgres";
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" IN SCHEMA "public" GRANT ALL ON SEQUENCES TO "anon";
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" IN SCHEMA "public" GRANT ALL ON SEQUENCES TO "authenticated";
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" IN SCHEMA "public" GRANT ALL ON SEQUENCES TO "service_role";






ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" IN SCHEMA "public" GRANT ALL ON FUNCTIONS TO "postgres";
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" IN SCHEMA "public" GRANT ALL ON FUNCTIONS TO "anon";
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" IN SCHEMA "public" GRANT ALL ON FUNCTIONS TO "authenticated";
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" IN SCHEMA "public" GRANT ALL ON FUNCTIONS TO "service_role";






ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" IN SCHEMA "public" GRANT ALL ON TABLES TO "postgres";
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" IN SCHEMA "public" GRANT ALL ON TABLES TO "anon";
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" IN SCHEMA "public" GRANT ALL ON TABLES TO "authenticated";
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" IN SCHEMA "public" GRANT ALL ON TABLES TO "service_role";































