# SquadUp

**SquadUp** é uma aplicação móvel Android desenvolvida em **Kotlin** para gestão de eventos desportivos, enquadrada no **Tema 2 - Gestão de Eventos Desportivos**.

O projecto permite descobrir, criar e gerir eventos/torneios desportivos, organizar equipas, gerir inscrições, acompanhar jogos e consultar informação relevante para jogadores, organizadores e administradores. A aplicação usa **Supabase** como backend e base de dados remota, com interface construída em **Jetpack Compose**.

Este README descreve apenas funcionalidades observadas no código e no enunciado fornecido.

## Funcionalidades principais

- Ecrãs de onboarding com apresentação inicial da aplicação.
- Registo, login, recuperação e alteração de palavra-passe.
- Perfis de utilizador com tipos como administrador, organizador e jogador/utilizador.
- Gestão de perfil, incluindo dados pessoais, localização, modalidades de interesse e estilo de jogo.
- Página inicial com eventos, jogos em destaque e informação contextual do utilizador.
- Exploração de eventos desportivos com pesquisa, filtros e eventos próximos.
- Visualização de eventos em mapa, com suporte a localização.
- Criação de eventos em vários passos, incluindo modalidade, formato, regras, inscrições, data/hora, localização e imagem de capa.
- Suporte a eventos públicos e privados.
- Formatos de competição como jogo único, liga, eliminatória, grupos + eliminatória e formato aberto.
- Gestão de eventos criados pelo organizador.
- Gestão de equipas, pedidos de inscrição, jogadores, jogos, classificações e estatísticas do evento.
- Criação e edição de jogos associados a eventos.
- Acompanhamento de jogos em directo, com resultado, estatísticas e timeline de acontecimentos.
- Registo de eventos de jogo, como pontuação, faltas/infrações, substituições e tempos técnicos, de acordo com a modalidade.
- Suporte parcial a funcionamento offline no acompanhamento de jogos, com cache local e operações pendentes para sincronização posterior.
- Criação e gestão de equipas.
- Convites para equipas e pedidos de adesão por código.
- Gestão de membros de equipa, incluindo capitão/membro.
- Inscrição individual ou por equipa em eventos, quando aplicável.
- Fluxo de pagamento/confirmação de inscrição e emissão de bilhete.
- Consulta de bilhetes do utilizador e detalhe de bilhete.
- Leitura/validação de QR codes de bilhetes para eventos.
- Notificações relacionadas com eventos, equipas e convites.
- Painel de administração com métricas gerais.
- Gestão de contas por administradores, incluindo criação, edição, suspensão/estado e remoção lógica.
- Suporte de interface em inglês e português de Portugal através de recursos Android.
- Testes unitários e testes instrumentados associados a estados de UI, login, filtros, contas, eventos e acesso a eventos privados.

## Tecnologias usadas

- **Kotlin**: linguagem principal da aplicação.
- **Android / Gradle**: projecto Android nativo.
- **Jetpack Compose**: construção da interface declarativa.
- **Material 3**: componentes visuais da interface.
- **Navigation Compose**: navegação entre ecrãs.
- **ViewModel, StateFlow e Lifecycle**: gestão de estado e ciclo de vida.
- **Supabase**: autenticação, PostgREST, Storage, Realtime e Functions.
- **Ktor OkHttp Client**: comunicação HTTP usada pelas bibliotecas Supabase.
- **Kotlinx Serialization**: serialização de modelos.
- **MapLibre**: mapas e selecção/visualização de localização.
- **Google Play Services Location**: localização do dispositivo.
- **Room**: persistência local para suporte offline no módulo de jogo em directo.
- **DataStore Preferences**: persistência local de preferências, como onboarding.
- **CameraX, ML Kit Barcode Scanning e ZXing**: leitura e validação de QR codes.
- **Coil**: carregamento de imagens em Compose.
- **JUnit, AndroidX Test, Espresso e Compose UI Test**: testes unitários e instrumentados.

## Arquitectura

O projecto segue uma organização por funcionalidades, com separação entre UI, estado, navegação e acesso a dados.

- **`core`** concentra elementos partilhados da aplicação, como navegação, tema, componentes de UI, enums, permissões, cliente Supabase e monitorização de rede.
- **`features`** agrupa os módulos funcionais da aplicação. Cada funcionalidade tende a ter ficheiros de `Screen`, `Route`, `ViewModel`, `UiState`, `Repository` e `Models`.
- **Repositories** encapsulam o acesso ao Supabase, operações de leitura/escrita e, em alguns casos, subscrições Realtime.
- **ViewModels** coordenam estado, validações e chamadas aos repositories.
- **Screens/Routes** implementam a interface Jetpack Compose e ligam os ecrãs à navegação.
- **Room** é usado no módulo de jogo em directo para cache local e operações offline pendentes.

### Módulos principais

| Área | Responsabilidade |
| --- | --- |
| `features/auth` | Login, registo, recuperação e reposição de palavra-passe. |
| `features/home` | Página inicial, eventos próximos e jogo em destaque. |
| `features/events` | Listagem, mapa, detalhe, criação, edição e gestão de eventos. |
| `features/events/livematch` | Acompanhamento de jogos em directo, estatísticas, timeline e suporte offline. |
| `features/events/manageevent` | Gestão operacional de eventos, equipas, inscrições, jogos, estatísticas e QR scanner. |
| `features/teams` | Equipas, criação, convites, membros e pedidos de adesão. |
| `features/profile` | Perfil, bilhetes, eventos do utilizador, edição e alteração de palavra-passe. |
| `features/payment` | Fluxo de confirmação de pagamento/inscrição e criação de bilhetes. |
| `features/notifications` | Notificações e interacção com convites/eventos. |
| `features/admin` | Dashboard administrativo e gestão de contas. |
| `features/onboarding` | Introdução inicial da aplicação. |

## Estrutura de pastas

```text
.
|-- app/
|   |-- src/
|   |   |-- main/
|   |   |   |-- java/com/example/squadup/
|   |   |   |   |-- core/          # Navegação, tema, componentes, enums, Supabase e utilitários
|   |   |   |   |-- features/      # Funcionalidades da aplicação
|   |   |   |   |-- MainActivity.kt
|   |   |   |-- res/              # Strings, temas, imagens, ícones, fontes e XML Android
|   |   |   |-- AndroidManifest.xml
|   |   |-- test/                 # Testes unitários
|   |   |-- androidTest/          # Testes instrumentados
|   |-- build.gradle.kts          # Configuração Gradle do módulo Android
|   |-- proguard-rules.pro
|-- gradle/
|   |-- libs.versions.toml        # Versões de plugins e dependências
|   |-- wrapper/                  # Gradle Wrapper
|-- build.gradle.kts              # Configuração Gradle de topo
|-- settings.gradle.kts           # Nome do projecto e módulos
|-- gradlew / gradlew.bat         # Gradle Wrapper
```

## Como executar

### Pré-requisitos

- Android Studio instalado.
- JDK compatível com o projecto Android.
- Android SDK com suporte para `compileSdk 36`.
- Emulador Android ou dispositivo físico.
- Projecto Supabase configurado com as tabelas e funções esperadas pela aplicação.

### Configurar variáveis locais

O projecto lê as credenciais do Supabase a partir do ficheiro `local.properties`, que está ignorado pelo Git.

Na raiz do repositório, criar ou actualizar `local.properties` com:

```properties
SUPABASE_URL=https://<project-id>.supabase.co
SUPABASE_ANON_KEY=<supabase-anon-key>
```

Estes valores são expostos ao código através de `BuildConfig.SUPABASE_URL` e `BuildConfig.SUPABASE_ANON_KEY`.

### Abrir no Android Studio

1. Abrir a pasta do repositório no Android Studio.
2. Aguardar pela sincronização Gradle.
3. Confirmar que `local.properties` contém as chaves do Supabase.
4. Seleccionar um emulador/dispositivo.
5. Executar a configuração `app`.

### Executar pela linha de comandos

No Windows:

```bash
gradlew.bat assembleDebug
```

Em Linux/macOS:

```bash
./gradlew assembleDebug
```

Para instalar no dispositivo/emulador ligado:

```bash
./gradlew installDebug
```

### Testes

Testes unitários:

```bash
./gradlew test
```

Testes instrumentados, com emulador ou dispositivo ligado:

```bash
./gradlew connectedAndroidTest
```

## Variáveis de ambiente / configuração

Não foram identificadas variáveis de ambiente obrigatórias no código. A configuração sensível é feita por propriedades locais:

| Chave | Onde definir | Descrição |
| --- | --- | --- |
| `SUPABASE_URL` | `local.properties` | URL do projecto Supabase. |
| `SUPABASE_ANON_KEY` | `local.properties` | Chave pública anon do Supabase usada pelo cliente Android. |

O ficheiro `local.properties` não deve ser versionado.

## Screenshots

Não foi encontrada uma pasta `docs/screenshots` neste repositório. Por isso, este README não inclui capturas de ecrã.

## Estado do projecto

Projecto académico desenvolvido para a unidade curricular de Computação Móvel, no âmbito do **Tema 2 - Gestão de Eventos Desportivos**.

A versão actual contém uma aplicação Android estruturada por módulos funcionais, com autenticação, eventos, equipas, inscrições, bilhetes, mapa, notificações, administração, jogos em directo, suporte offline parcial e testes. Algumas funcionalidades dependem da existência/configuração correcta do backend Supabase e das respectivas tabelas, funções e permissões.

## Autores

Autores/contribuidores identificados no histórico Git do repositório:

- [Simão Mendes](https://github.com/SimaoMendes30)
- [Diogo Fontes](https://github.com/Fontezilla)
- [Pedro Cruz](https://github.com/pedrojcruz)
- [Simão Sousa](https://github.com/simaosousa10)
